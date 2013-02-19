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

import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.jnetstream.capture.InputIterator;
import org.jnetstream.capture.file.HeaderReader;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.filter.Filter;


import com.slytechs.utils.collection.SeekResult;
import com.slytechs.utils.memory.BufferBlock;
import com.slytechs.utils.memory.BufferUtils;
import com.slytechs.utils.memory.channel.CountedReadableByteChannel;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class RawInputIterator implements InputIterator<ByteBuffer> {

	/**
	 * Default buffer size for caching record data from the uni-directional byte
	 * channel or stream.
	 */
	public static final int BUFFER_SIZE = 65 * 1024;

	protected static final SeekResult NOT_OK = SeekResult.NotFullfilled;

	protected static final SeekResult OK = SeekResult.Fullfilled;

	private final CountedReadableByteChannel in;

	private final HeaderReader headerReader;

	private final Filter<RecordFilterTarget> filter;

	private BufferBlock rwBlock;

	private BufferBlock roBlock;

	private int regional;

	private final int min;

	private ByteBuffer buffer;

	private ByteBuffer view;

	private ByteBuffer prefetch = null;

	private final ByteOrder order;

	public RawInputIterator(final CountedReadableByteChannel in,
	    final HeaderReader headerReader, ByteOrder order,
	    final Filter<RecordFilterTarget> filter) {
		this.in = in;
		this.headerReader = headerReader;
		this.order = order;
		this.filter = filter;
		this.min = headerReader.getMinLength();

		setupBuffer(BUFFER_SIZE, null);
	}

	private void setupBuffer(int cap, ByteBuffer old) {

		this.buffer = ByteBuffer.allocate(cap);
		this.buffer.order(order);
		this.rwBlock = new BufferBlock(this.buffer, null, 0, cap);
		this.buffer.limit(0);

		this.view = BufferUtils.asReadonly(buffer);
		this.roBlock = new BufferBlock(this.view, null, 0, cap);
		this.view.limit(0);

		/*
		 * Lastly copy any remnants left in the old view buffer into our new buffer.
		 * Typically the header is partially read from the stream then if the length
		 * of the entire record doesn't fit, a new buffer is setup and the header
		 * portion from the old buffer needs to be copied over. View is RO therefore
		 * we copy directly to the array.
		 */

		if (old != null) {
			buffer.limit(old.remaining());
			buffer.put(old);
			this.regional = old.remaining();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOSkippable#skip()
	 */
	public void skip() throws IOException {
		prefetch = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOIterator#hasNext()
	 */
	public boolean hasNext() throws IOException {
		if (prefetch != null) {
			return true;
		}

		try {
			seekFilter();
			return true;

		} catch (EOFException e) {
			return false;
		}

	}

	public SeekResult seekFilter() throws IOException {
		return seek(filter);
	}

	public SeekResult seek(final Filter<RecordFilterTarget> filter)
	    throws IOException {

		if (filter == null) {
			prefetch = nextWithoutFilter();
			return (in.isOpen() ? OK : NOT_OK);
		}

		if (prefetch != null) {
			if (Files.checkRecordFilter(this.filter, prefetch, headerReader)) {
				return OK;
			}
		}

		do {
			prefetch = this.nextWithoutFilter();

		} while (Files.checkRecordFilter(this.filter, prefetch, headerReader) == false);

		return (in.isOpen() ? OK : NOT_OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOIterator#next()
	 */
	public ByteBuffer next() throws IOException {
		ByteBuffer r = prefetch;
		prefetch = null;

		return r;
	}

	private ByteBuffer nextWithoutFilter() throws IOException {

		if (this.rwBlock.checkBoundsRegional(regional, min) == false) {
			setupBuffer(BUFFER_SIZE, buffer);
		}

		/*
		 * Reset the view buffer and fetch minimum to read the length from the
		 * header
		 */
		this.rwBlock = fetchMinimum(regional, min);

		final ByteBuffer buffer = this.rwBlock.getByteBuffer();
		final int length = (int) headerReader.readLength(buffer);

		if (length < 0 || length > 65 * 1024) {
			throw new BufferUnderflowException();
		}

		if (this.rwBlock.checkBoundsRegional(regional, length) == false) {
			setupBuffer(BUFFER_SIZE, buffer);
		}

		this.rwBlock = fetchMinimum(regional + min, length - min); // read
		// additional
		// bytes

		try {
			this.rwBlock.reposition(regional, length);
			this.roBlock.reposition(regional, length);
		} catch (final IllegalArgumentException e) {
			throw e;
		}

		regional += length;

		return roBlock.getByteBuffer();
	}

	/**
	 * @param regional
	 *          TODO
	 * @param length
	 * @return
	 * @throws IOException
	 */
	private BufferBlock fetchMinimum(int regional, int length) throws IOException {

		this.buffer.limit(regional + length);
		this.buffer.position(regional);

		/*
		 * Read data from our channel into the buffer
		 */
		final int read = this.in.read(this.buffer);

		if (read == -1) {
			throw new EOFException();
		}

		if (read < length) {
			throw new BufferUnderflowException();
		}

		this.buffer.position(regional);

		/*
		 * bblock is tied to the backend readonly view of the this.buffer which is
		 * backed by "array"
		 */
		return rwBlock;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOIterator#remove()
	 */
	public void remove() throws IOException {
		throw new UnsupportedOperationException(
		    "Inputstream is readonly. Operation not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	public void close() throws IOException {
		in.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.InputIterator#getPosition()
	 */
	public long getPosition() {
		return in.getCounter();
	}

}
