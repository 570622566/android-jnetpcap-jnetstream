/**
 * Copyright (C) 2007 Sly Technologies, Inc. This library is free software; you
 * can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version. This
 * library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package com.slytechs.file.snoop;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.jnetstream.capture.FormatType;
import org.jnetstream.capture.InputIterator;
import org.jnetstream.capture.file.HeaderReader;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.capture.file.RecordType;
import org.jnetstream.capture.file.snoop.SnoopBlockRecord;
import org.jnetstream.capture.file.snoop.SnoopDLT;
import org.jnetstream.capture.file.snoop.SnoopFile;
import org.jnetstream.capture.file.snoop.SnoopInput;
import org.jnetstream.capture.file.snoop.SnoopPacket;
import org.jnetstream.capture.file.snoop.SnoopPacketRecord;
import org.jnetstream.capture.file.snoop.SnoopRecord;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;
import org.jnetstream.protocol.Protocol;

import com.slytechs.capture.DefaultCaptureDevice;
import com.slytechs.capture.file.AbstractStream;
import com.slytechs.utils.memory.BitBuffer;
import com.slytechs.utils.memory.BufferUtils;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class SnoopInputCapture
    extends AbstractStream<SnoopPacket, SnoopBlockRecord> implements SnoopInput {

	public static final HeaderReader headerReader = SnoopFile.headerReader;

	/**
	 * @param in
	 * @param filter
	 *          TODO
	 * @throws IOException
	 */
	public SnoopInputCapture(final InputStream in,
	    Filter<ProtocolFilterTarget> filter) throws IOException {
		super(Channels.newChannel(in), ByteOrder.BIG_ENDIAN, headerReader, filter);

		Protocol protocol = SnoopDLT.asConst(((SnoopBlockRecord) block)
		    .getLinktype());

		setCaptureDevice(new DefaultCaptureDevice(protocol));
	}

	/**
	 * @param in
	 * @param filter
	 *          TODO
	 * @throws IOException
	 */
	public SnoopInputCapture(final ReadableByteChannel in,
	    Filter<ProtocolFilterTarget> filter) throws IOException {
		super(in, ByteOrder.BIG_ENDIAN, headerReader, filter);

		Protocol protocol = SnoopDLT.asConst(((SnoopBlockRecord) block)
		    .getLinktype());

		setCaptureDevice(new DefaultCaptureDevice(protocol));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.InputCapture#getFormatType()
	 */
	public FormatType getFormatType() {
		return FormatType.Pcap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.InputCapture#getPacketIterator(org.jnetstream.filter.Filter)
	 */
	public InputIterator<SnoopPacket> getPacketIterator(
	    Filter<ProtocolFilterTarget> filter) throws IOException {

		/*
		 * Now convert the protocol filter into a record filter and apply it to raw
		 * iterator
		 */
		final Filter<RecordFilterTarget> rf = (filter == null ? SnoopPacketRecord.FILTER
		    : headerReader.asRecordFilter(filter, getCaptureDevice().getLinkType()));

		final InputIterator<ByteBuffer> raw = getRawIterator(rf);

		return new InputIterator<SnoopPacket>() {

			public void skip() throws IOException {
				raw.skip();
			}

			public boolean hasNext() throws IOException {
				return raw.hasNext();
			}

			public SnoopPacket next() throws IOException {
				final ByteBuffer buffer = raw.next();
				final long position = 0;

				final SnoopPacket packet = new SnoopPacketImpl(BitBuffer.wrap(
				    BufferUtils.slice(buffer)), position, dlt);

				return packet;
			}

			public void remove() throws IOException {
				throw new UnsupportedOperationException(
				    "Inputstream is readonly. Operation not supported");
			}

			public void close() throws IOException {
				raw.close();
			}

			public long getPosition() {
				return raw.getPosition();
			}

		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.InputCapture#getRecordIterator(org.jnetstream.filter.Filter)
	 */
	public InputIterator<SnoopRecord> getRecordIterator(
	    Filter<RecordFilterTarget> filter) throws IOException {

		final InputIterator<ByteBuffer> raw = getRawIterator(filter);

		return new InputIterator<SnoopRecord>() {

			public void skip() throws IOException {
				raw.skip();
			}

			public boolean hasNext() throws IOException {
				return raw.hasNext();
			}

			public SnoopRecord next() throws IOException {
				final ByteBuffer buffer = raw.next();

				final RecordType type = headerReader.readType(buffer);

				switch (type) {
					case BlockRecord:
						block = new SnoopBlockRecordImpl(buffer, getPosition());

						return block;

					case PacketRecord:
						return new SnoopPacketRecordImpl(block, buffer, getPosition());

					default:
						throw new IllegalStateException("Unknow record type encountered ["
						    + type + "]");
				}
			}

			public void remove() throws IOException {
				raw.remove();
			}

			public void close() throws IOException {
				raw.close();
			}

			public long getPosition() {
				return raw.getPosition();
			}

		};
	}

	public InputIterator<SnoopPacket> getPacketIterator() throws IOException {
		return getPacketIterator(filter);
	}

	public InputIterator<SnoopRecord> getRecordIterator() throws IOException {
		return getRecordIterator(null);

	}

	/**
	 * @param b
	 * @return
	 */
	/**
	 * Checks the format of the stream. First few bytes are compared for valid
	 * MAGIC pattern and then appropriate check is made to determine the byte
	 * order of the headers. The method consumes 8 bytes from the channel.
	 * 
	 * @param in
	 *          input channel to check
	 * @return either the byte order of the format of the header or null if this
	 *         is an invalid format and magic pattern did not match
	 * @throws IOException
	 */
	public static ByteOrder checkFormat(ReadableByteChannel in)
	    throws IOException {

		ByteBuffer b = ByteBuffer.allocate(8);
		in.read(b);
		b.clear();

		if (SnoopFile.headerReader.readType(b) != RecordType.BlockRecord) {
			return null;
		}

		return ByteOrder.BIG_ENDIAN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.AbstractStream#createBlockRecord(java.nio.ByteBuffer)
	 */
	@Override
	protected SnoopBlockRecord createBlockRecord(ByteBuffer b) {
		return new SnoopBlockRecordImpl(b, 0);
	}

}
