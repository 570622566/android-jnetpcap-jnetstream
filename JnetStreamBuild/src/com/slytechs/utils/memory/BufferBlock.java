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
package com.slytechs.utils.memory;

import java.nio.ByteBuffer;

import com.slytechs.utils.collection.Readonly;

/**
 * Buffer that can provide a partial view of a larger buffer or storage object.
 * For example large files can be broken up into partial buffers where the
 * buffer is only a small fraction of the underlying file. The partial buffer
 * class maintains a start and length of the buffer in relation to the main
 * backing storage such as the file or another buffer.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class BufferBlock implements Readonly, PartialBuffer {

	public final static PartialBuffer EMPTY_BUFFER = new PartialBuffer() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.slytechs.utils.memory.BufferBlock#checkBoundsLocal(long, long)
		 */
		public boolean checkBoundsLocal(long position, long length) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.slytechs.utils.memory.BufferBlock#checkBoundsLocal(long)
		 */
		public boolean checkBoundsLocal(long position) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.slytechs.utils.memory.BufferBlock#checkBoundsRegional(long, int)
		 */
		public boolean checkBoundsRegional(long position, int length) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.slytechs.utils.memory.BufferBlock#checkBoundsRegional(long)
		 */
		public boolean checkBoundsRegional(long position) {
			return false;
		}

		public ByteBuffer getByteBuffer() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		public long getEndRegional() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		public long getLastRegional() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		public long getLength() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		public long getStartRegional() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		public boolean isReadonly() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		public long mapLocalToRegional(long local) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		public long mapRegionalToLocal(long regional) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		public void reposition(long regional, int length) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		public void setBuffer(ByteBuffer buffer) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		public void setLength(long length) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		public boolean setReadonly(boolean state) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		public void setStartRegional(long start) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		public BitBuffer getBitBuffer() {
	    // TODO Auto-generated method stub
	    throw new UnsupportedOperationException("Not implemented yet");
    }

	};

	private long start;

	private long length;

	private long end;

	private ByteBuffer bytes;

	private BitBuffer bits;

	/**
	 * Constructs a new partial buffer initialized with the actual data buffer,
	 * the starting position of the first byte within the data buffer of some
	 * larger storage and the length of this buffer. The data buffer's capacity
	 * should be equal or greater then length.
	 * 
	 * @param bytes
	 *          the data buffer holding the data
	 * @param bits TODO
	 * @param start
	 *          starting offset within some relative storage
	 * @param length
	 *          length of this buffer
	 * @throws IllegalArgumentException
	 *           if length is larger then buffer capacity
	 * @throws NullPointerException
	 *           if the buffer parameter is null
	 */
	public BufferBlock(ByteBuffer bytes, BitBuffer bits, long start, long length) {
		if (bytes == null || bits == null) {
			throw new NullPointerException("The data buffer can not be null");
		}

		this.bits = bits;
		this.bytes = bytes;
		this.start = start;
		this.length = length;
		this.end = start + length;

		if (bytes.capacity() < length) {
			throw new IllegalArgumentException(
			    "Supplied length can not be larger then data buffer capacity");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#reposition(long, int)
	 */
	public final void reposition(final long regional, final int length) {
		final int local = (int) mapRegionalToLocal(regional);
		final int end = (int) regional + length - 1;

		if (checkBoundsLocal(local, length) == false) {
			throw new IndexOutOfBoundsException(
			    "Can not reposition buffer block to [" + regional
			        + " - " + end+ "] when is " + toString());
		}

		this.bytes.limit(local + length);
		this.bytes.position(local);
		
		this.bits.limit((local + length) * 8);
		this.bits.position(local * 8);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#getBuffer()
	 */
	public final ByteBuffer getByteBuffer() {
		return this.bytes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#setBuffer(java.nio.ByteBuffer)
	 */
	public final void setBuffer(ByteBuffer buffer) {
		if (buffer == null) {
			throw new NullPointerException("The data buffer can not be null");
		}

		this.bytes = buffer;
		setLength(buffer.capacity());
		
		this.bits = BitBuffer.wrap(this.bytes);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#getLength()
	 */
	public final long getLength() {
		return this.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#setLength(long)
	 */
	public final void setLength(long length) {
		if (length > bytes.capacity()) {
			throw new IllegalArgumentException(
			    "Partial buffer length can not be greater "
			        + "then data buffer's capacity.");
		}

		this.length = length;

		this.end = start + length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#getStartRegional()
	 */
	public final long getStartRegional() {
		return this.start;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#setStartRegional(long)
	 */
	public final void setStartRegional(long start) {
		this.start = start;

		this.end = start + length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#getEndRegional()
	 */
	public long getEndRegional() {
		return end;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#getLastRegional()
	 */
	public long getLastRegional() {
		return end - 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#mapLocalToRegional(long)
	 */
	public long mapLocalToRegional(long local) {
		return local + start;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#mapRegionalToLocal(long)
	 */
	public long mapRegionalToLocal(long regional) {
		return regional - start;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#checkBoundsRegional(long)
	 */
	public boolean checkBoundsRegional(long position) {
		return position >= start && position < end;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#checkBoundsLocal(long)
	 */
	public boolean checkBoundsLocal(long position) {
		return position >= 0 && position < length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#checkBoundsLocal(long, long)
	 */
	public boolean checkBoundsLocal(long position, long length) {
		if (length == 0) {
			return checkBoundsLocal(position);
		} else {
			return checkBoundsLocal(position + length - 1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#checkBoundsRegional(long, int)
	 */
	public boolean checkBoundsRegional(long position, int length) {
		if (length == 0) {
			return checkBoundsRegional(position);
		} else {
			return checkBoundsRegional(position + length - 1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.Readonly#isReadonly()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#isReadonly()
	 */
	public boolean isReadonly() {
		return this.bytes.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.Readonly#setReadonly(boolean)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.PartialBuffer#setReadonly(boolean)
	 */
	public boolean setReadonly(boolean state) {
		throw new UnsupportedOperationException(
		    "Can not change buffer to readonly. Must change it at the source.");
	}

	public String toString() {
		final StringBuilder b = new StringBuilder(128);

		b.append("[");
		b.append(start);
		b.append(" - ");
		b.append(end);
		b.append("]");

		return b.toString();
	}

	/* (non-Javadoc)
   * @see com.slytechs.utils.memory.PartialBuffer#getBitBuffer()
   */
  public BitBuffer getBitBuffer() {
	  return bits;
  }
}
