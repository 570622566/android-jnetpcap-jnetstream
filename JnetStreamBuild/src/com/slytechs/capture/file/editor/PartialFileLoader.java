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
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.List;

import org.jnetstream.capture.FileMode;
import org.jnetstream.capture.file.BufferFetchException;
import org.jnetstream.capture.file.HeaderReader;

import com.slytechs.utils.collection.WeakArrayList;
import com.slytechs.utils.memory.BitBuffer;
import com.slytechs.utils.memory.BufferBlock;
import com.slytechs.utils.memory.BufferUtils;
import com.slytechs.utils.memory.MemoryModel;
import com.slytechs.utils.memory.PartialBuffer;

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
 * You need to specify a HeaderReader who's job is to decode the length of the
 * record from the buffer's data. The custom reader has to only have enough
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
 * The only thing really required is a default instance of a HeaderReader which
 * is called on by default to help advance from record to record during
 * iteration. The default reader is used with the simple {@link #nextRecord()}
 * method. The user can also call the {@link #nextRecord(HeaderReader)} which
 * allows the default reader to be overriden and any reader can be substituted.
 * Also you may be interested in setting the default byte order of any newly
 * created buffers, although it can easily be changed by a cutom reader. It may,
 * however, be easier to set the default up once globally by calling
 * {@link #order(ByteOrder)} method. Note that, changing the byte order on the
 * ByteBuffer that is returned as a result will have a global effect of on any
 * future invocations of the and {@link #nextRecord} methods.
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
public class PartialFileLoader implements Closeable, PartialLoader {

	private int bufferSize;

	private long bufferStart;

	private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

	public final List<BufferBlock> cache = new WeakArrayList<BufferBlock>();

	private final FileChannel channel;

	final long channelSize;

	private final MemoryModel memoryModel;

	private long position;

	private FileMode mode;

	public static List<BufferBlock> globalCache = new WeakArrayList<BufferBlock>();

	private final HeaderReader lengthGetter;

	private final File file;

	/**
	 * Initializes a record channel capable of iterating over generic records that
	 * make up the structure of the channel. The reader automatically determines
	 * the best memory and buffer size for using while reading the file contents.
	 * 
	 * @param channel
	 *          channel to read records from
	 * @param mode
	 *          TODO
	 * @param file
	 *          TODO
	 * @param defaultReader
	 *          the default record header reader that can determine the record
	 *          length
	 * @throws IOException
	 *           any IO errors
	 */
	public PartialFileLoader(final FileChannel channel, FileMode mode,
	    final HeaderReader lengthGetter, File file) throws IOException {
		this.mode = mode;
		this.lengthGetter = lengthGetter;
		this.file = file;
		this.memoryModel = this.pickMemoryModel(channel.size());
		this.channel = channel;
		this.position = 0;
		this.channelSize = channel.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#checkBoundary(long)
	 */
	public void checkBoundaryRegional(long position)
	    throws IndexOutOfBoundsException {
		if (position < 0 || position > channelSize) {
			throw new IndexOutOfBoundsException("Position (" + position
			    + ") out of bounds [0 - " + channelSize + "]");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#checkBoundary(long,
	 *      long)
	 */
	public void checkBoundaryRegional(long position, long length)
	    throws IndexOutOfBoundsException {
		if (length == 0) {
			checkBoundaryRegional(position);
		} else {
			checkBoundaryRegional(position + length - 1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	public void close() throws IOException {

		channel.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#fetchBlock(long, int)
	 */
	public PartialBuffer fetchBlock(long regional, int length) throws IOException {
		PartialBuffer p = fetchFromCache(regional, length);
		final boolean fromCache;

		if (p == null) {

			final int blockSize = pickBlockSize(regional, length);
			final MemoryModel model = pickMemoryModel(blockSize);

			p = fetchFromChannelAndCache(regional, blockSize, model);
			fromCache = false;

		} else {
			fromCache = true;
		}

		try {
			p.reposition(regional, length);

		} catch (IndexOutOfBoundsException e) {
			throw new BufferFetchException("Can not reposition buffer", p,
			    regional, length, fromCache, e);
		}

		return p;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#fetchBlock(long, int,
	 *      com.slytechs.utils.memory.MemoryModel)
	 */
	public PartialBuffer fetchBlock(long regional, int length, MemoryModel model)
	    throws IOException {
		PartialBuffer p = fetchFromCache(regional, length);

		if (p == null) {
			final int blockSize = pickBlockSize(regional, length);

			p = fetchFromChannelAndCache(regional, blockSize, model);
		}

		p.reposition(regional, length);

		return p;
	}

	private PartialBuffer fetchFromCache(final long start, final int size) {
		PartialBuffer blockBuffer = null;

		final long last = start + size - 1;

		/*
		 * cache size is always fluctuating as weak references are retired, must use
		 * the dynamic for, also note that you are not allowed to use the list
		 * iterator as its disabled.
		 */
		for (int i = 0; i < cache.size(); i++) {
			blockBuffer = cache.get(i);
			if (blockBuffer == null) {
				return null;
			}

			if (start >= blockBuffer.getStartRegional()
			    && last < blockBuffer.getEndRegional()) {
				return blockBuffer;
			}
		}

		return null;
	}

	/**
	 * @param regional
	 * @param mode
	 *          sets the mode of either readonly or readwrite for buffers that are
	 *          created. In readwrite mode direct buffer modifications are allowed
	 *          and may not require a flush.
	 * @return
	 * @throws IOException
	 */
	private BufferBlock fetchFromChannel(final long regional, final int length,
	    FileMode mode, MemoryModel memoryModel) throws IOException {

		final boolean readwrite = mode.isContent();

		BufferBlock block = null;
		ByteBuffer buf;
		final int minimum = pickMinimumSize(regional, length);

		/*
		 * Needed to make sure the file is synched. There were some intermittened
		 * failures with HardRegionIndexer while it was scanning the entire file
		 * without this force. Therefore it must be here. Some portions of the file
		 * were lagging with the synch to the physical file.
		 */
		channel.force(true);

		switch (memoryModel) {
			case MappedFile:
				final MapMode mapMode = ((readwrite) ? MapMode.READ_WRITE
				    : MapMode.READ_ONLY);
				buf = this.channel.map(mapMode, regional, minimum);
				buf.clear();

				block = new BufferBlock(buf, BitBuffer.wrap(buf), regional, buf.capacity());
				block.getByteBuffer().order(this.byteOrder);

				System.gc();

				break;

			case DirectBuffer:
				System.out.flush();

				buf = ByteBuffer.allocateDirect(minimum);
				buf.clear();

				this.channel.position(regional);
				int s = this.channel.read(buf);
				buf.clear();

				buf = BufferUtils.asReadonly(buf);

				block = new BufferBlock(buf, BitBuffer.wrap(buf), regional, s);
				block.getByteBuffer().order(this.byteOrder);

				break;

			case ByteArray:

				buf = ByteBuffer.allocate(minimum);
				buf.clear();

				this.channel.position(regional);
				s = this.channel.read(buf);
				buf.clear();

				buf = BufferUtils.asReadonly(buf);

				block = new BufferBlock(buf, BitBuffer.wrap(buf), regional, s);
				block.getByteBuffer().order(this.byteOrder);

				break;

			default:
				throw new IllegalStateException("Unknown memory model encountered "
				    + memoryModel);

		}

		return block;

	}

	/**
	 * @param regional
	 * @param length
	 * @param memoryModel
	 * @return
	 * @throws IOException
	 */
	private PartialBuffer fetchFromChannelAndCache(long regional, int length,
	    MemoryModel memoryModel) throws IOException {

		final BufferBlock partial = fetchFromChannel(regional, length, mode,
		    memoryModel);
		partial.reposition(regional, length);

		cache.add(partial);
		globalCache.add(partial);

		return partial;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#fetchMinimum(long, int)
	 */
	public PartialBuffer fetchMinimum(long regional, int length)
	    throws IOException {
		PartialBuffer p = fetchFromCache(regional, length);
		if (p == null) {
			final MemoryModel model = pickMemoryModel(length);
			final int minimum = pickMinimumSize(regional, length);

			p = fetchFromChannelAndCache(regional, minimum, model);

		} else {
			final int minimum = pickMinimumSize(regional, length, p);

			p.reposition(regional, minimum);
		}

		return p;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#fetchMinimum(long, int,
	 *      com.slytechs.utils.memory.MemoryModel)
	 */
	public PartialBuffer fetchMinimum(long regional, int length, MemoryModel model)
	    throws IOException {
		PartialBuffer p = fetchFromCache(regional, length);
		if (p == null) {
			final int minimum = pickMinimumSize(regional, length);

			p = fetchFromChannelAndCache(regional, minimum, model);
			p.reposition(regional, minimum);

		} else {
			final int minimum = pickMinimumSize(regional, length, p);

			p.reposition(regional, minimum);
		}

		return p;
	}

	/**
	 * Chooses the buffer allocation for the current memory model.
	 * 
	 * @param length
	 *          length to use to choose the best memory model for the buffer
	 *          allocation decision
	 */
	public int getBufferAllocation(long length) {
		if (mode.isMap()) {
			return PartialLoader.BUFFER_MEMORY_MAP;
		}

		final long remaining = length - position;

		switch (pickMemoryModel(remaining)) {
			case ByteArray:
				return PartialLoader.BUFFER_BYTE_BUFFER;

			case DirectBuffer:
				return PartialLoader.BUFFER_DIRECT;

			case MappedFile:
				return PartialLoader.BUFFER_MEMORY_MAP;
		}

		throw new IllegalStateException("Unknown memory model. Not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#getByteOrder()
	 */
	final public ByteOrder getByteOrder() {
		return this.byteOrder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#getLength()
	 */
	public long getLength() {
		return channelSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#getMemoryModel()
	 */
	public final MemoryModel getMemoryModel() {
		return (mode.isMap()) ? memoryModel : MemoryModel.MappedFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.FileIterator#getPosition()
	 */
	public final long getPosition() throws IOException {
		return this.position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.RecordReader#isInBuffer(long)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#isInMemory(long)
	 */
	public final boolean isInMemory(final long position) {
		return (position >= this.bufferStart)
		    && (position < this.bufferStart + this.bufferSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.Readonly#isReadonly()
	 */
	public boolean isReadonly() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#isWithinBoundary(long)
	 */
	public boolean isWithinBoundary(long position) {
		return position >= 0 && position < channelSize;
	}

	private final int pickBlockSize(final long regional, final int length) {
		final long remaining = this.channelSize - regional;
		final long allocation = getBufferAllocation(length);

		return (int) ((remaining < allocation) ? remaining : allocation);
	}

	/**
	 * Picks an appropriate memory model based on the size of the file. The
	 * decision tree is as follows:
	 * 
	 * <pre>
	 *             1) readonly &amp;&amp; map - pick model based on length including map
	 *             2) readonly &amp;&amp; nomap - pick model based on length, exclude map
	 *             3) readwrite &amp;&amp; nomap - pick model based on length, exclude map
	 *             4) readwrite &amp;&amp; map - map any size block length
	 * </pre>
	 * 
	 * @param length
	 *          length of the file in octets to based our decision on
	 * @return best optimized memory model for the size of the file and current
	 *         FileMode settings
	 */
	private final MemoryModel pickMemoryModel(final long length) {

		final boolean readonly = !mode.isContent();
		final boolean readwrite = !readonly;
		final boolean map = mode.isMap();
		final boolean nomap = !map;

		/*
		 * Case #1 readonly && map
		 */
		if (readwrite && map) {
			return MemoryModel.MappedFile;

		} else if (readonly && nomap) {
			return pickMemoryModel(length, nomap);

		} else if (readwrite && nomap) {
			return pickMemoryModel(length, nomap);

		} else if (readonly && map) {
			return pickMemoryModel(length, map);

		} else {
			throw new IllegalStateException("Internal logic error, shouldn't be here");
		}

	}

	/**
	 * Picks memory model based on file size and takes into account the map flag.
	 * 
	 * @param length
	 *          length to use to base the decision about the model
	 * @param map
	 *          specifies if map should be included or excluded from the memory
	 *          model picking decision
	 * @return best memory model for the length specified
	 */
	private final MemoryModel pickMemoryModel(final long length, final boolean map) {

		if (length >= PartialLoader.BUFFER_MEMORY_MAP && map) {
			return MemoryModel.MappedFile;

		} else if (length >= PartialLoader.BUFFER_DIRECT) {
			return MemoryModel.DirectBuffer;

		} else {
			return MemoryModel.ByteArray;
		}
	}

	private final int pickMinimumSize(final long regional, final int length) {
		final long remaining = this.channelSize - regional;

		return (int) ((remaining < length) ? remaining : length);
	}

	private final int pickMinimumSize(final long regional, final int length,
	    final PartialBuffer block) {
		final long local = block.mapRegionalToLocal(regional);
		final long remaining = block.getLength() - local;

		return (int) ((remaining < length) ? remaining : length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#setBufferPrefetchSize(int)
	 */
	public final void setBufferPrefetchSize(final int bufferAllocation) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#setByteOrder(java.nio.ByteOrder)
	 */
	final public void order(final ByteOrder order) {
		this.byteOrder = order;

		/*
		 * Change the byte order in any buffers in the cache as well
		 */
		for (int i = 0; i < cache.size(); i++) {
			final ByteBuffer b = cache.get(i).getByteBuffer();
			b.order(order);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.FileIterator#setPosition(long)
	 */
	public final void setPosition(final long position) throws IOException {
		this.position = position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.Readonly#setReadonly(boolean)
	 */
	public boolean setReadonly(boolean state) {
		/*
		 * File loader is not allowed to return a writtable state. It can only
		 * return readonly buffers.
		 */
		return state == true;

	}

	public String toString() {
		return file.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#transferTo(java.nio.channels.FileChannel)
	 */
	public long transferTo(FileChannel out) throws IOException {
		return transferTo(0, channelSize, out);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#transferTo(long, long,
	 *      java.nio.channels.FileChannel)
	 */
	public long transferTo(long position, long length, FileChannel out)
	    throws IOException {
		return channel.transferTo(position, length, out);
	}

	/**
	 * @return the headerReader
	 */
	public final HeaderReader getLengthGetter() {
		return this.lengthGetter;
	}
}
