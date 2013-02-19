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
package com.slytechs.file.pcap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;

import org.jnetstream.capture.FormatType;
import org.jnetstream.capture.InputIterator;
import org.jnetstream.capture.file.HeaderReader;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.capture.file.RecordType;
import org.jnetstream.capture.file.pcap.PcapBlockRecord;
import org.jnetstream.capture.file.pcap.PcapDLT;
import org.jnetstream.capture.file.pcap.PcapFile;
import org.jnetstream.capture.file.pcap.PcapInput;
import org.jnetstream.capture.file.pcap.PcapPacket;
import org.jnetstream.capture.file.pcap.PcapPacketRecord;
import org.jnetstream.capture.file.pcap.PcapRecord;
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
public class PcapInputCapture
    extends AbstractStream<PcapPacket, PcapBlockRecord> implements PcapInput {

	public static final HeaderReader headerReader = PcapFile.headerReader;

	/**
	 * Checks the format of the stream. First few bytes are compared for valid
	 * MAGIC pattern and then appropriate check is made to determine the byte
	 * order of the headers. The method consumes 4 bytes from the channel.
	 * 
	 * @param in
	 *          input channel to check
	 * @return either the byte order of the format of the header or null if this
	 *         is an invalid format and magic pattern did not match
	 * @throws IOException
	 */
	public static ByteOrder checkFormat(ReadableByteChannel in)
	    throws IOException {
		ByteBuffer b = ByteBuffer.allocate(4);
		in.read(b);
		b.flip();

		if (PcapFile.headerReader.readType(b) != RecordType.BlockRecord) {
			return null;
		}

		return (b.get(0) == PcapFile.MAGIC_PATTERN_BE[0] ? ByteOrder.BIG_ENDIAN
		    : ByteOrder.LITTLE_ENDIAN);
	}

	/**
	 * @param in
	 * @param order
	 *          TODO
	 * @param filter
	 *          TODO
	 * @throws IOException
	 */
	public PcapInputCapture(final ReadableByteChannel in, ByteOrder order,
	    Filter<ProtocolFilterTarget> filter) throws IOException {
		super(in, order, headerReader, filter);

		Protocol protocol =
		    PcapDLT.asConst(((PcapBlockRecord) block).getLinktype());

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

	public InputIterator<PcapPacket> getPacketIterator() throws IOException {
		return getPacketIterator(filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.InputCapture#getPacketIterator(org.jnetstream.filter.Filter)
	 */
	public InputIterator<PcapPacket> getPacketIterator(
	    Filter<ProtocolFilterTarget> filter) throws IOException {

		/*
		 * Now convert the protocol filter into a record filter and apply it to raw
		 * iterator
		 */
		final Filter<RecordFilterTarget> rf =
		    (filter == null ? PcapPacketRecord.FILTER : headerReader
		        .asRecordFilter(filter, getCaptureDevice().getLinkType()));

		final InputIterator<ByteBuffer> raw = getRawIterator(rf);

		return new InputIterator<PcapPacket>() {


			public void close() throws IOException {
				raw.close();
			}

			public long getPosition() {
				return raw.getPosition();
			}

			public boolean hasNext() throws IOException {
				return raw.hasNext();
			}

			public PcapPacket next() throws IOException {
				final ByteBuffer buffer = raw.next();
				final long position = 0;

				final PcapPacket packet =
				    new PcapPacketImpl(BitBuffer.wrap(BufferUtils.slice(buffer)),
				        position, dlt);

				return packet;
			}

			public void remove() throws IOException {
				throw new UnsupportedOperationException(
				    "Inputstream is readonly. Operation not supported");
			}

			public void skip() throws IOException {
				raw.skip();
			}

		};
	}

	public InputIterator<PcapRecord> getRecordIterator() throws IOException {
		return getRecordIterator(null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.InputCapture#getRecordIterator(org.jnetstream.filter.Filter)
	 */
	public InputIterator<PcapRecord> getRecordIterator(
	    Filter<RecordFilterTarget> filter) throws IOException {

		final InputIterator<ByteBuffer> raw = getRawIterator(filter);

		return new InputIterator<PcapRecord>() {

			public void close() throws IOException {
				raw.close();
			}

			public long getPosition() {
				return raw.getPosition();
			}

			public boolean hasNext() throws IOException {
				return raw.hasNext();
			}

			public PcapRecord next() throws IOException {
				final long position = raw.getPosition();
				final ByteBuffer buffer = raw.next();

				final RecordType type = headerReader.readType(buffer);

				switch (type) {
					case BlockRecord:
						return new PcapBlockRecordImpl(buffer, position);

					case PacketRecord:
						return new PcapPacketRecordImpl(buffer, position, block);

					default:
						throw new IllegalStateException("Unknow record type encountered ["
						    + type + "]");
				}
			}

			public void remove() throws IOException {
				raw.remove();
			}

			public void skip() throws IOException {
				raw.skip();
			}

		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.AbstractStream#createBlockRecord(java.nio.ByteBuffer)
	 */
	@Override
	protected PcapBlockRecord createBlockRecord(ByteBuffer b) {
		return new PcapBlockRecordImpl(b, 0);
	}

}
