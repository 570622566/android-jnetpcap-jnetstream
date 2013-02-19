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

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public interface RecordReader {

	/**
	 * Gets the default byte order for all buffers created by this object
	 * 
	 * @return default byte ordering
	 */
	public abstract ByteOrder getByteOrder();

	/**
	 * Returns the memory model used. This memory model is used to prefetch new
	 * buffers.
	 * 
	 * @return the memoryModel existing memory model
	 */
	public abstract MemoryModel getMemoryModel();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.FileIterator#getPosition()
	 */
	public abstract long getPosition() throws IOException;

	public abstract boolean hasNextRecord() throws IOException;

	/**
	 * Checks if the data at the position is currently mapped to memory or
	 * some kind of a fetch or IO operation will need to be performed in order
	 * to pull the data into memory.
	 * 
	 * @param position
	 *    position into the record reader to check
	 * @return
	 *   true means that data at the position is in memory and will be efficiently
	 *   retrieved if required, if false an inefficient IO operation may result
	 */
	public abstract boolean isInMemory(final long position);

	/**
	 * Advanced the buffer to the next record in the buffer. The byte buffer may
	 * be the main shared buffer where its position is set to the start of the
	 * next record, and its limit is set to the end of the record. No individual
	 * ByteBuffer views are created its up to the user to create such a view if
	 * needed. This ensure optimal performance as even minimal object creation has
	 * great impact on read performance.
	 * 
	 * @return a shared buffer with its position and limit properties properly set
	 *         to include the entire record
	 * @throws BufferUnderflowException
	 *           thrown when the default buffer size is too small to accomodate
	 *           the size of the record
	 * @throws IOException
	 *           any IO errors as buffer prefetch operations are IO based
	 */
	public abstract ByteBuffer nextRecord() throws BufferUnderflowException,
	    IOException;

	/**
	 * Advanced the buffer to the next record in the buffer. The byte buffer may
	 * be the main shared buffer where its position is set to the start of the
	 * next record, and its limit is set to the end of the record. No individual
	 * ByteBuffer views are created its up to the user to create such a view if
	 * needed. This ensure optimal performance as even minimal object creation has
	 * great impact on read performance.
	 * 
	 * @param lengthReader
	 *          a LengthReader responsible for parsing the record's header
	 *          structure and calculating the length of the entire record
	 * @return a shared buffer with its position and limit properties properly set
	 *         to include the entire record
	 * @throws IOException
	 *           any IO errors while fetching data from file using IO operations
	 * @throws BufferUnderflowException
	 *           thrown when the default buffer size is too small to accomodate
	 *           the size of the record
	 */
	public abstract ByteBuffer nextRecord(final RecordLengthGetter lengthReader)
	    throws IOException, BufferUnderflowException;

	/**
	 * @param bufferAllocation
	 *          the bufferAllocation to set
	 */
	public abstract void setBufferPrefetchSize(final int bufferAllocation);

	/**
	 * Sets the default byte order of all new buffers created by this object
	 * 
	 * @param byteOrder
	 *          the requested byte order for all new buffers
	 */
	public abstract void setByteOrder(final ByteOrder byteOrder);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.FileIterator#setPosition(long)
	 */
	public abstract void setPosition(final long position) throws IOException;

	/**
   * @return
   */
  public abstract long getLength();

  public long transferTo(long position, long length, FileChannel channel) throws IOException;
}