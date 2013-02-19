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

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import org.jnetstream.capture.file.HeaderReader;


import com.slytechs.utils.collection.Readonly;
import com.slytechs.utils.memory.MemoryModel;
import com.slytechs.utils.memory.PartialBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface PartialLoader extends Closeable, Readonly {

	/**
	 * Default memory allocation size for byte array based buffers
	 */
	public final static int BUFFER_BYTE_BUFFER = 4 * 1024;

	/**
	 * Default memory allocation size for direct buffers
	 */
	public final static int BUFFER_DIRECT = 512 * 1024;

	/**
	 * Default memory allocation size for memory mapped buffers
	 */
	public final static int BUFFER_MEMORY_MAP = 10 * 1024 * 1024;

	/**
	 * Checks the position if its within any boundary conditions and the
	 * underlying storage space.
	 * 
	 * @param regional
	 *          position to check
	 * @throws IndexOutOfBoundsException
	 *           TODO
	 */
	public abstract void checkBoundaryRegional(long regional)
	    throws IndexOutOfBoundsException;

	public abstract void checkBoundaryRegional(long regional, long length)
	    throws IndexOutOfBoundsException;

	/**
	 * Fetches a block of atleast length bytes starting at position using the best
	 * buffering and memory model based on the storage properties.
	 * 
	 * @param regional
	 *          regional position of the requested starting position
	 * @param length
	 *          minimum length of the block
	 * @return partial buffer containing the requested data
	 * @throws IOException
	 *           any IO errors
	 */
	public abstract PartialBuffer fetchBlock(long regional, int length)
	    throws IOException;

	/**
	 * Fetches a block of atleast length bytes starting at position using the best
	 * buffering using the specified memory model and based on the storage
	 * properties.
	 * 
	 * @param regional
	 *          regional position of the requested starting position
	 * @param length
	 *          minimum length of the block
	 * @param model
	 *          the memory model to use instead of picking one automatically
	 * @return partial buffer containing the requested data
	 * @throws IOException
	 *           any IO errors
	 */
	public abstract PartialBuffer fetchBlock(long regional, int length,
	    MemoryModel model) throws IOException;

	/**
	 * <p>
	 * Fetches only the minimum amount of data as specified by length parameter.
	 * The length parameter is used to pick the memory model automatically. If the
	 * requested amount of data already exists in cache then the block is pulled
	 * from the cache and therefore may still be larger then the length requested.
	 * The data block will never be smaller though.
	 * </p>
	 * 
	 * @param regional
	 * @param length
	 * @return
	 * @throws IOException
	 */
	public abstract PartialBuffer fetchMinimum(long regional, int length)
	    throws IOException;

	/**
	 * <p>
	 * Fetches only the minimum amount of data as specified by length parameter.
	 * The length parameter is used to pick the memory model automatically. If the
	 * requested amount of data already exists in cache then the block is pulled
	 * from the cache and therefore may still be larger then the length requested.
	 * The data block will never be smaller though.
	 * </p>
	 * 
	 * @param regional
	 *          regional position to pull data from
	 * @param length
	 *          minimum amount of data to pull
	 * @param model
	 *          memory model to use instead of picking one based on storage
	 *          properties
	 * @return
	 * @throws IOException
	 */
	public abstract PartialBuffer fetchMinimum(long regional, int length,
	    MemoryModel model) throws IOException;

	/**
	 * @param length TODO
	 * @return
	 */
	public abstract int getBufferAllocation(long length);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#getByteOrder()
	 */
	public abstract ByteOrder getByteOrder();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#getLength()
	 */
	public abstract long getLength();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#isInBuffer(long)
	 */
	public abstract boolean isInMemory(final long position);

	public abstract boolean isWithinBoundary(long position);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#setByteOrder(java.nio.ByteOrder)
	 */
	public abstract void order(final ByteOrder order);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#setBufferPrefetchSize(int)
	 */
	public abstract void setBufferPrefetchSize(final int bufferAllocation);

	/**
	 * Transfers entire contents of the loader's storage or any portion if it has
	 * constraints/bounds defined.
	 * 
	 * @param out
	 *          channel to append or write from the current channels position this
	 *          loaders contents
	 * @return number of bytes transfered to the output channel
	 * @throws IOException
	 *           any IO errors
	 */
	public abstract long transferTo(FileChannel out) throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#transferTo(long, long,
	 *      java.nio.channels.FileChannel)
	 */
	public abstract long transferTo(long position, long length, FileChannel out)
	    throws IOException;
	
	
	public HeaderReader getLengthGetter();

}