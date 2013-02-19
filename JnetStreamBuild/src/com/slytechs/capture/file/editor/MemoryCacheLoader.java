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
package com.slytechs.capture.file.editor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import org.jnetstream.capture.file.HeaderReader;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.capture.file.RecordType;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;

import com.slytechs.utils.memory.BitBuffer;
import com.slytechs.utils.memory.BufferBlock;
import com.slytechs.utils.memory.BufferUtils;
import com.slytechs.utils.memory.MemoryModel;
import com.slytechs.utils.memory.PartialBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class MemoryCacheLoader implements PartialLoader {

	private ByteBuffer buffer;

	private final int length;

	private final PartialBuffer partial;

	private boolean readonly = false;

	private final HeaderReader lengthGetter;

	public MemoryCacheLoader(HeaderReader lengthGetter,
	    final ByteBuffer... buffers) {
		this.lengthGetter = overrideOffset(lengthGetter);
		// Calculate total number of bytes in all the elements buffers
		int total = 0;
		for (final ByteBuffer b : buffers) {
			total += (b.limit() - b.position());
		}

		this.length = total;

		this.buffer = this.allocate(this.length);
		this.partial =
		    new BufferBlock(this.buffer, BitBuffer.wrap(this.buffer), 0,
		        this.length);

		// Copy contents from all the elements buffer into our large buffer
		for (final ByteBuffer b : buffers) {
			this.buffer.put(b);
		}
	}

	/**
	 * Allocates a memory based buffer and if the copy flag is true, copies the
	 * content of the supplied buffer into the newly allocated one. The content is
	 * copied from buffer position upto the limit properties. The length is also
	 * calculated by the difference between the limit and position properties.
	 * 
	 * @param buffer
	 *          buffer to copy contents into our internally allocated buffer
	 * @param copy
	 *          if true, the contents of the buffer are copied into a new buffer,
	 *          otherwise the supplied buffer is used
	 * @param headerReader
	 *          TODO
	 */
	public MemoryCacheLoader(final ByteBuffer buffer, final boolean copy,
	    HeaderReader lengthGetter) {
		this.lengthGetter = overrideOffset(lengthGetter);
		this.length = buffer.limit() - buffer.position();

		if (copy) {
			this.buffer = this.allocate(this.length);
			this.buffer.order(buffer.order());
			this.buffer.put(buffer);
		} else {
			if ((buffer.position() != 0) || (buffer.limit() != buffer.capacity())) {
				this.buffer = BufferUtils.slice(buffer);
			} else {
				this.buffer = buffer;
			}
		}

		this.partial =
		    new BufferBlock(this.buffer, BitBuffer.wrap(this.buffer), 0,
		        this.length);
	}

	/**
	 * <p>
	 * Allocates a memory based on two buffers copies their into a newly allocated
	 * one. The content is copied from buffers position upto the limit properties.
	 * The length is also calculated by the difference between the limit and
	 * position properties of the combined two buffers.
	 * </p>
	 * <p>
	 * This version of the constructor is primarily used when you have the records
	 * header and content in two separate buffers and they need to be merged
	 * together.
	 * </p>
	 * 
	 * @param buffer1
	 *          buffer to copy contents into our internally allocated buffer
	 * @param buffer1
	 * @param buffer2
	 *          second buffer to copy contents into our internally allocated
	 *          buffer
	 * @param buffer2
	 * @param headerReader
	 *          TODO
	 */
	public MemoryCacheLoader(final ByteBuffer buffer1, final ByteBuffer buffer2,
	    HeaderReader lengthGetter) {
		this.lengthGetter = overrideOffset(lengthGetter);
		this.length =
		    (buffer1.limit() - buffer1.position())
		        + (buffer2.limit() - buffer2.position());

		this.buffer = this.allocate(this.length);
		this.buffer.put(buffer1);
		this.buffer.put(buffer2);

		this.partial =
		    new BufferBlock(this.buffer, BitBuffer.wrap(this.buffer), 0,
		        this.length);
	}

	/**
	 * Allocates a memory based and cached loader with the specified buffer
	 * length. The buffer is uninitialized and needs to be initialized externally.
	 * 
	 * @param length
	 *          number of bytes to allocate for the buffer
	 * @param headerReader
	 *          TODO
	 */
	public MemoryCacheLoader(final int length, HeaderReader lengthGetter) {
		this.length = length;
		this.lengthGetter = overrideOffset(lengthGetter);
		this.buffer = this.allocate(length);

		this.partial =
		    new BufferBlock(this.buffer, BitBuffer.wrap(this.buffer), 0, length);
	}

	private static HeaderReader overrideOffset(final HeaderReader lengthGetter) {

		if (true) {
			return lengthGetter;
		}

		final HeaderReader newGetter = new HeaderReader() {
			final HeaderReader delagate = lengthGetter;

			/**
			 * @return
			 * @see org.jnetstream.capture.file.HeaderReader#getOffset()
			 */
			public int getOffset() {
				return 0;
			}

			/**
			 * @return
			 * @see org.jnetstream.capture.file.HeaderReader#getMinLength()
			 */
			public int getMinLength() {
				return this.delagate.getMinLength();
			}

			/**
			 * @param b
			 * @return
			 * @see org.jnetstream.capture.file.HeaderReader#readLength(java.nio.ByteBuffer)
			 */
			public long readLength(ByteBuffer b) {
				return this.delagate.readLength(b);
			}

			public RecordType readType(ByteBuffer buffer) {
				return this.delagate.readType(buffer);
			}

			public int getHeaderLength(ByteBuffer buffer) {
				return this.delagate.getHeaderLength(buffer);
			}

			public RecordFilterTarget readRecordFilterTarget(ByteBuffer buffer) {
				return this.delagate.readRecordFilterTarget(buffer);
			}

			public Filter<RecordFilterTarget> asRecordFilter(
			    Filter<ProtocolFilterTarget> filter, ProtocolFilterTarget protocol) {
				return this.delagate.asRecordFilter(filter, protocol);
			}
		};

		return newGetter;
	}

	private final ByteBuffer allocate(final int length) {
		// return Malloc.getDefault().allocateBuffer(length);

		return ByteBuffer.allocate(length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#checkBoundary(long)
	 */
	public void checkBoundaryRegional(final long regional)
	    throws IndexOutOfBoundsException {
		final int local = (int) this.partial.mapRegionalToLocal(regional);

		if ((local < 0) || (local > this.buffer.capacity())) {
			throw new IndexOutOfBoundsException("Index out of bounds [0 - "
			    + this.buffer.capacity() + "]");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#checkBoundary(long,
	 *      long)
	 */
	public void checkBoundaryRegional(final long regional, final long length)
	    throws IndexOutOfBoundsException {
		if (length == 0) {
			this.checkBoundaryRegional(regional);
		} else {
			this.checkBoundaryRegional(regional + length - 1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	public void close() throws IOException {
		// Empty, nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#fetchBlock(long, int)
	 */
	public PartialBuffer fetchBlock(final long regional, final int length)
	    throws IOException {
		this.checkBoundaryRegional(regional, length);

		this.partial.reposition(regional, length);

		return this.partial;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#fetchBlock(long, int,
	 *      com.slytechs.utils.memory.MemoryModel)
	 */
	public PartialBuffer fetchBlock(final long regional, final int length,
	    final MemoryModel model) throws IOException {
		return this.fetchBlock(regional, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#fetchMinimum(long, int)
	 */
	public PartialBuffer fetchMinimum(final long regional, final int length)
	    throws IOException {

		final int minimum = pickMinimum(regional, length);

		partial.reposition(regional, minimum);

		return partial;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#fetchMinimum(long, int,
	 *      com.slytechs.utils.memory.MemoryModel)
	 */
	public PartialBuffer fetchMinimum(final long regional, final int length,
	    final MemoryModel model) throws IOException {

		return fetchMinimum(regional, length);
	}

	private int pickMinimum(final long regional, final int length) {
		final int remaining = this.length - (int) regional;

		return ((remaining < length) ? remaining : length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#getBufferAllocation()
	 */
	public int getBufferAllocation(long length) {
		return this.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#getByteOrder()
	 */
	public ByteOrder getByteOrder() {
		return this.buffer.order();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#getLength()
	 */
	public long getLength() {
		return this.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#getMemoryModel()
	 */
	public MemoryModel getMemoryModel() {
		return MemoryModel.DirectBuffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#isInMemory(long)
	 */
	public boolean isInMemory(final long position) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.Readonly#isReadonly()
	 */
	public boolean isReadonly() {
		return this.readonly;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#isWithinBoundary(long)
	 */
	public boolean isWithinBoundary(final long index) {
		if ((index >= 0) && (index < this.length)) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#setBufferPrefetchSize(int)
	 */
	public void setBufferPrefetchSize(final int bufferAllocation) {
		// Empty, does nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#setByteOrder(java.nio.ByteOrder)
	 */
	public void order(final ByteOrder order) {
		this.buffer.order(order);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.Readonly#setReadonly(boolean)
	 */
	public boolean setReadonly(final boolean state) {

		if (state != this.readonly) {

			if (state == true) {
				this.buffer = BufferUtils.asReadonly(this.buffer);
				this.partial.setBuffer(this.buffer);
			}

		}

		this.readonly = state;

		return true;
	}

	@Override
	public String toString() {
		return "Heap";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#transferTo(java.nio.channels.FileChannel)
	 */
	public long transferTo(final FileChannel out) throws IOException {
		return this.transferTo(0, this.length, out);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#transferTo(long, long,
	 *      java.nio.channels.FileChannel)
	 */
	public long transferTo(final long position, final long length,
	    final FileChannel out) throws IOException {

		this.buffer.position((int) position);
		this.buffer.limit((int) (position + length));

		return out.write(this.buffer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#getLengthGetter()
	 */
	public HeaderReader getLengthGetter() {
		return lengthGetter;
	}

}
