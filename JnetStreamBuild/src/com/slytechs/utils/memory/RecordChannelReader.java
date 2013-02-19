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

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;


/**
 * <P>
 * A special structure reader, structured in a sequence of records, that has the
 * capability to allocate memory as specified by the supplied memory model and
 * read sequentially record data from the channel. No actual records are
 * produced by this class, only the super high performance buffer allocation and
 * positioning are done by this class. Record's detailed structure is broken
 * down outside this class.
 * </p>
 * <p>
 * You need to specify a RecordLengthGetter who's job is to decode the length of
 * the record from the buffer's data. The custom reader has to only have enough
 * knowledge to return the length of the entire record, including any headers,
 * data and padding.
 * </p>
 * <p>
 * After the record boundaries are calculated, based on record's starting
 * position and the length returned by the custom reader, the shared buffer is
 * positioned by setting of the ByteBuffer position and limit properties to
 * contain the entire record. Once configured, the shared buffer is returned.
 * The result can easily be turned into a ByteBuffer view externally, but has
 * direct performance impact and thus this class leaves it up to the user to
 * decide if a view is required by a call to {@link java.nio.ByteBuffer#slice()}
 * method on the returned result.
 * </p>
 * <p>
 * This class is intended for traversing the structure of an entire file or
 * large portions of it. A large buffer is allocated and contents of the file
 * are read into the buffer, using very efficient low level/native algorithms,
 * and then the references with position and limit properties properly setup are
 * returned back to the user. Therefore it would be inefficient to use this
 * class to access very small number of structures, as there is a fairely large
 * overhead in the buffer allocation algorithm used to keep performance over
 * large iterations, very fast.
 * </p>
 * <p>
 * The performance and speed of iterations over the structure of the channel is
 * extremely fast. At the high extreme end, performance of atleast 6,000,000
 * records per second can be expected. Typical performance is well above
 * 1,000,000 records per second for any size file even on slow sytems and disks.
 * </p>
 * <p>
 * This class utilized 3 memory models of operation. Each model has its benefits
 * and negatives at various file sizes. The user has a choice of specifying a
 * specific memory model to use and any specific pre-fetch buffer sizes. The
 * class provides a basic constructor which calculates the best defaults for
 * memory model and default pre-fetch buffer size. The defaults should work very
 * well on any system; slow or fast.
 * </p>
 * <p>
 * The only thing really required is a default instance of a RecordLengthGetter
 * which is called on by default to help advance from record to record during
 * iteration. The default reader is used with the simple {@link #nextRecord()}
 * method. The user can also call the {@link #nextRecord(RecordLengthGetter)}
 * which allows the default reader to be overriden and any reader can be
 * substituted. Also you may be interested in setting the default byte order of
 * any newly created buffers, although it can easily be changed by a cutom
 * reader. It may, however, be easier to set the default up once globally by
 * calling {@link #setByteOrder(ByteOrder)} method. Note that, changing the byte
 * order on the ByteBuffer that is returned as a result will have a global
 * effect of on any future invocations of the and {@link #nextRecord} methods.
 * </p>
 * <p>
 * <B>Important:</b> note that the buffer's position and limit properties
 * returned can be modified at any time by another invocation of any of this
 * classes methods. Also note that because a shared buffers is used to return
 * results, none of the methods are multithread safe as each thread would
 * override any results of another thread. Multithread use must be synchronized
 * externally after the results are retrieved and recorded in external
 * variables. Only buffer properies are changed by each method invocation, none
 * of the buffer's data is overriden in anyway as the result of the iteratation
 * once returned.
 * </p>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class RecordChannelReader implements RecordReader {

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

	private int prefetchSize = 0;

	private ByteBuffer buffer;

	private int bufferSize;

	private long bufferStart;

	private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

	private final FileChannel channel;

	private long channelSize;

	private final MemoryModel memoryModel;

	private long position;

	private RecordLengthGetter recordReader;

	public RecordChannelReader(final FileChannel channel,
	    final RecordLengthGetter defaultReader) throws IOException {
		this.memoryModel = this.pickMemoryModel(channel.size());
		this.channel = channel;
		this.recordReader = defaultReader;
		this.position = 0;
		this.channelSize = channel.size();
	}

	/**
	 * @param block
	 * @throws IOException
	 */
	public RecordChannelReader(final MemoryModel model,
	    final FileChannel channel, final RecordLengthGetter defaultReader)
	    throws IOException {
		this.memoryModel = model;
		this.channel = channel;
		this.recordReader = defaultReader;
		this.position = 0;
		this.channelSize = channel.size();
	}

	/**
	 * @return
	 */
	private int getBufferAllocation() {
		if (prefetchSize != 0) {
			return prefetchSize;
		}
		switch (this.memoryModel) {
			case ByteArray:
				return RecordChannelReader.BUFFER_BYTE_BUFFER;

			case DirectBuffer:
				return RecordChannelReader.BUFFER_DIRECT;

			case MappedFile:
				return RecordChannelReader.BUFFER_MEMORY_MAP;
		}

		return 1 * 1024;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#getByteOrder()
	 */
	final public ByteOrder getByteOrder() {
		return this.byteOrder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#getMemoryModel()
	 */
	public final MemoryModel getMemoryModel() {
		return this.memoryModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.FileIterator#getPosition()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#getPosition()
	 */
	public final long getPosition() throws IOException {
		return this.position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#hasNextRecord()
	 */
	public final boolean hasNextRecord() throws IOException {
		return this.position < this.channelSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#isInBuffer(long)
	 */
	public final boolean isInMemory(final long position) {
		return (position >= this.bufferStart)
		    && (position < this.bufferStart + this.bufferSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#nextRecord()
	 */
	public final ByteBuffer nextRecord() throws BufferUnderflowException,
	    IOException {
		return this.nextRecord(this.recordReader);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#nextRecord(com.slytechs.utils.memory.RecordLengthGetter)
	 */
	public final ByteBuffer nextRecord(final RecordLengthGetter lengthReader)
	    throws IOException, BufferUnderflowException {

		int p = (int) (this.position - this.bufferStart);

		/*
		 * Make sure the next record we want to fetch resides in the existing shared
		 * buffer, otherwise we have to prefetch another buffer.
		 */
		if (p + lengthReader.getMinLength() >= this.bufferSize) {
			this.prefetchBuffer(this.position);
			p = (int) (this.position - this.bufferStart);

			if (p + lengthReader.getMinLength() > this.bufferSize) {
				throw new IllegalStateException("Unable to prefetch buffer");
			}
		}

		this.buffer.limit(p + lengthReader.getMinLength());
		this.buffer.position(p);

		final int length = lengthReader.readLength(this.buffer);
		if (p + length > this.bufferSize) {
			if (length > this.getBufferAllocation()) {
				throw new BufferUnderflowException();
			}
			this.resetBuffer();
			return this.nextRecord(lengthReader);
		}

		this.buffer.limit(p + length);
		this.buffer.position(p);

		this.position += length;

		return this.buffer;
	}

	/**
	 * Picks an appropriate memory model based on the size of the file.
	 * 
	 * @param l
	 *          length of the file in octets
	 * @return best optimized memory model for the size of the file
	 */
	private MemoryModel pickMemoryModel(final long l) {
		
		if (true) {
			return MemoryModel.ByteArray;
		}

		if (l >= RecordChannelReader.BUFFER_MEMORY_MAP) {
			return MemoryModel.MappedFile;
		} else if (l >= RecordChannelReader.BUFFER_DIRECT) {
			return MemoryModel.DirectBuffer;
		} else {
			return MemoryModel.ByteArray;
		}
	}

	/**
	 * </P>
	 * Prefetch certain amount of records upto bufferSize into bufferSize packet
	 * buffer. We aquire a shared header buffer large enough to only hold one
	 * record header and then we decode enough of it to learn the length of the
	 * record. Then we iterate using the shared header buffer tallying up the
	 * record lengths until we no longer can fit any more packets in the buffer.
	 * Allocate large buffer more accurately using the tallied record lengths.
	 * Then we read from file the entire length of the buffer while creating
	 * buffer views for each packet and create new isntances of records for each
	 * view buffer from the larger packet buffer.
	 * <P>
	 * <P>
	 * This allows us to more efficiently read file contents into our packet
	 * buffer and allocate more expensive to create java.nio direct buffers since
	 * one of them is created for many packets. Also alows us to memory map the
	 * file region for no copies at all.
	 * </P>
	 * 
	 * @param start
	 *          position of the first record
	 * @param bufferSize
	 *          size of the packet buffer to read records into
	 * @throws
	 * @throws IOException
	 * @throws IOException
	 *           any IO errors
	 * @throws CacheException
	 */
	private final void prefetchBuffer(final long start) throws IOException {
		this.prefetchBuffer(start, this.getBufferAllocation());
	}

	private final void prefetchBuffer(final long start, int size)
	    throws IOException {

		switch (this.memoryModel) {
			case MappedFile:

				if (size > this.channelSize - start) {
					size = (int) (this.channelSize - start);
				}
				this.bufferStart = start;
				this.buffer = this.channel.map(MapMode.READ_ONLY, start, size);
				this.bufferSize = this.buffer.capacity();
				this.buffer.order(this.byteOrder);
				this.buffer.clear();
				System.gc();

				break;

			case DirectBuffer:

				this.bufferStart = start;
				this.buffer = ByteBuffer.allocateDirect(size);
				this.channel.position(start);
				this.bufferSize = this.channel.read(this.buffer);
				this.buffer.flip();
				this.buffer.order(this.byteOrder);

				break;

			case ByteArray:

				this.bufferStart = start;
				this.buffer = ByteBuffer.allocate(size);
				this.channel.position(start);
				this.bufferSize = this.channel.read(this.buffer);
				this.buffer.flip();
				this.buffer.order(this.byteOrder);

				break;

			default:
				throw new IllegalStateException("Unknown memory model encountered "
				    + this.memoryModel);

		}

	}

	/**
	 * 
	 */
	private final void resetBuffer() {
		this.bufferSize = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#setBufferPrefetchSize(int)
	 */
	public final void setBufferPrefetchSize(final int bufferAllocation) {
		prefetchSize = bufferAllocation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#setByteOrder(java.nio.ByteOrder)
	 */
	final public void setByteOrder(final ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.FileIterator#setPosition(long)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#setPosition(long)
	 */
	public final void setPosition(final long position) throws IOException {
		this.position = position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#synchronize()
	 */
	public boolean synchronize() throws IOException {
		if (channelSize != channel.size()) {
			channelSize = channel.size();

			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#getLength()
	 */
	public long getLength() {
		return channelSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#transferTo(long, long,
	 *      java.nio.channels.FileChannel)
	 */
	public long transferTo(long position, long length, FileChannel out)
	    throws IOException {
		return channel.transferTo(position, length, out);
	}
}
