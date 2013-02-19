/**
 * Copyright (C) 2007 Sly Technologies, Inc.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.slytechs.utils.memory;

import java.nio.ByteBuffer;
import java.util.Comparator;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public interface PartialBuffer {

	/**
	 * A comparator that can be used to sort and compare to partial buffers by the
	 * start position of the buffer.
	 */
	public final static Comparator<? super BufferBlock> sortByStart = new Comparator<BufferBlock>() {

		public int compare(BufferBlock o1, BufferBlock o2) {
			return (int) (o1.getStartRegional() - o2.getStartRegional());
		}

	};

	/**
	 * Repositions the ByteBuffer properties "position" and "limit" to the bounds
	 * specified by regional and length. The regional position is remapped to a
	 * local posiiton and the buffer properites are reset.
	 * 
	 * @param regional
	 *          regional position to set the ByteBuffer "position" property
	 * @param length
	 *          the length of the requested region which is also used to calculate
	 *          the ByteBuffer's limit property value
	 */
	public abstract void reposition(final long regional, final int length);

	/**
	 * Gets the data buffer
	 * 
	 * @return data buffer
	 */
	public abstract ByteBuffer getByteBuffer();
	
	/**
	 * Gets the data buffer
	 * 
	 * @return data buffer
	 */
	public abstract BitBuffer getBitBuffer();

	/**
	 * Sets a new data buffer. The length property of this partial buffer is set
	 * to the capacity of the buffer.
	 * 
	 * @param buffer
	 *          new data buffer
	 */
	public abstract void setBuffer(ByteBuffer buffer);

	/**
	 * Gets the length of the data buffer that is independent of the buffer
	 * capacilty or limit properties
	 * 
	 * @return user specified length of the partial buffer
	 */
	public abstract long getLength();

	/**
	 * Sets a new length of the partial buffer. the length is independent of the
	 * buffer capacity. The specified length can not be greater then the buffer
	 * capacity ensuring that the entire length of this partial buffer can somehow
	 * be stored within the data buffer.
	 * 
	 * @param length
	 *          length of the partial buffer, independent of the buffer capacity
	 */
	public abstract void setLength(long length);

	/**
	 * Starting offset of this partial buffer relative to the backing storage or
	 * another buffer.
	 * 
	 * @return starting offset within the related backing store
	 */
	public abstract long getStartRegional();

	/**
	 * Sets a new starting offset within a related backging store or another
	 * buffer.
	 * 
	 * @param start
	 *          new starting offset
	 */
	public abstract void setStartRegional(long start);

	/**
	 * Calculates the end of the buffer using the formula of start + length.
	 * 
	 * @return ending offset of the last byte of the data buffer within the
	 *         underlying store
	 */
	public abstract long getEndRegional();

	public abstract long getLastRegional();

	public abstract long mapLocalToRegional(long local);

	public abstract long mapRegionalToLocal(long regional);

	/**
	 * @param regional
	 * @return
	 */
	public abstract boolean checkBoundsRegional(long position);

	public abstract boolean checkBoundsLocal(long position);

	public abstract boolean checkBoundsLocal(long position, long length);

	/**
	 * @param position
	 * @param length
	 * @return
	 */
	public abstract boolean checkBoundsRegional(long position, int length);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.Readonly#isReadonly()
	 */
	public abstract boolean isReadonly();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.Readonly#setReadonly(boolean)
	 */
	public abstract boolean setReadonly(boolean state);

}