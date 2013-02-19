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
package com.slytechs.capture.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;

import org.jnetstream.capture.CaptureDevice;
import org.jnetstream.capture.CaptureType;
import org.jnetstream.capture.FilePacket;
import org.jnetstream.capture.FormatType;
import org.jnetstream.capture.InputCapture;
import org.jnetstream.capture.InputIterator;
import org.jnetstream.capture.file.BlockRecord;
import org.jnetstream.capture.file.HeaderReader;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;
import org.jnetstream.protocol.ProtocolEntry;
import org.jnetstream.protocol.ProtocolRegistry;

import com.slytechs.utils.collection.IOIterator.IteratorAdapter;
import com.slytechs.utils.io.IORuntimeException;
import com.slytechs.utils.memory.channel.BufferedReadableByteChannel;
import com.slytechs.utils.memory.channel.CountedReadableByteChannel;
import com.slytechs.utils.memory.channel.MarkableReadableByteChannel;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class AbstractStream<T extends FilePacket, B extends BlockRecord>
    implements InputCapture<T> {

	protected final Filter<ProtocolFilterTarget> filter;

	protected final CountedReadableByteChannel in;

	private final HeaderReader headerReader;

	protected final ByteOrder order;

	private CaptureDevice captureDevice;

	protected ProtocolEntry dlt;

	protected B block;

	/**
	 * @param in
	 * @param order
	 *          TODO
	 * @param filter
	 * @throws IOException
	 */
	public AbstractStream(final ReadableByteChannel in, final ByteOrder order,
	    final HeaderReader headerReader, final Filter<ProtocolFilterTarget> filter)
	    throws IOException {
		this.order = order;
		this.headerReader = headerReader;
		this.filter = filter;

		MarkableReadableByteChannel marked = new BufferedReadableByteChannel(in);
		this.in = new CountedReadableByteChannel(marked);

		this.block = readBlockRecord();
		
		this.dlt = ProtocolRegistry.lookup(getCaptureDevice().getLinkType());

	}

	/**
	 * @throws IOException
	 */
	private B readBlockRecord() throws IOException {
		in.mark(48);

		ByteBuffer b = ByteBuffer.allocate(headerReader.getMinLength());
		in.read(b);
		b.clear();

		int len = headerReader.getHeaderLength(b);
		b = ByteBuffer.allocate(len);

		in.reset();
		in.read(b);
		in.reset();

		return createBlockRecord(b);
	}

	/**
	 * @param b
	 * @return
	 */
	protected abstract B createBlockRecord(ByteBuffer b);

	public void close() throws IOException {
		this.in.close();
	}

	public Filter<ProtocolFilterTarget> getFilter() {
		return this.filter;
	}

	public InputIterator<ByteBuffer> getRawIterator() throws IOException {
		return getRawIterator(null);
	}

	public CaptureType getType() {
		return CaptureType.StreamCapture;
	}

	public boolean isMutable() {
		return false;
	}

	public abstract InputIterator<T> getPacketIterator() throws IOException;

	public Iterator<T> iterator() {

		try {
			return new IteratorAdapter<T>(getPacketIterator());
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	public InputIterator<ByteBuffer> getRawIterator(
	    Filter<RecordFilterTarget> filter) throws IOException {
		return new RawInputIterator(in, headerReader, order, filter);
	}

	public long countPackets() throws IOException {

		final InputIterator<T> i = getPacketIterator();
		long count = 0;

		while (i.hasNext()) {
			i.skip();

			count++;
		}

		return count;
	}

	/**
	 * @see InputCapture#getFormatType
	 * @return
	 */
	public abstract FormatType getFormatType();

	public String getFormatName() {
		return getFormatType().toString();
	}

	public void setCaptureDevice(final CaptureDevice captureDevice) {
		this.captureDevice = captureDevice;

	}

	public final CaptureDevice getCaptureDevice() {
		return this.captureDevice;
	}

}
