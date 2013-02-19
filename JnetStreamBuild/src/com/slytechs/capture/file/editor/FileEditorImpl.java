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
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Iterator;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jnetstream.capture.FileMode;
import org.jnetstream.capture.file.HeaderReader;
import org.jnetstream.capture.file.RawIterator;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;

import com.slytechs.capture.file.RawIteratorBuilder;
import com.slytechs.utils.collection.IOIterator.IteratorAdapter;
import com.slytechs.utils.io.AutoflushMonitor;
import com.slytechs.utils.io.IORuntimeException;
import com.slytechs.utils.memory.PartialBuffer;
import com.slytechs.utils.region.FlexRegion;
import com.slytechs.utils.region.RegionSegment;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class FileEditorImpl implements FileEditor, Closeable,
    AutoflushMonitor, Iterable<ByteBuffer> {

	public static final long AUTOFLUSH_AMOUNT = 1000000;

	public boolean autoflush = true;

	public ByteOrder order;

	public FileChannel channel;

	public final FlexRegion<PartialLoader> edits;

	protected File file;

	public final HeaderReader headerReader;

	@SuppressWarnings("unused")
	private final Log logger = LogFactory.getLog(FileEditorImpl.class);

	private final FileMode mode;

	public long totalChange = 0;

	protected final Filter<ProtocolFilterTarget> protocolFilter;

	private final RawIteratorBuilder rawBuilder;

	/**
	 * @param file
	 * @param mode
	 *          TODO
	 * @param headerReader
	 * @param order TODO
	 * @param protocolFilter
	 *          TODO
	 * @param rawBuilder TODO
	 * @throws IOException
	 */
	public FileEditorImpl(final File file, final FileMode mode,
	    final HeaderReader headerReader,
	    ByteOrder order, Filter<ProtocolFilterTarget> protocolFilter, RawIteratorBuilder rawBuilder) throws IOException {

		this.file = file;
		this.order = order;
		this.protocolFilter = protocolFilter;
		this.rawBuilder = rawBuilder;
		this.channel = new RandomAccessFile(file, (mode.isContent()
		    || mode.isAppend() ? "rw" : "r")).getChannel();
		this.mode = mode;
		this.headerReader = headerReader;

		final boolean readonly = !mode.isStructure();
		final boolean append = mode.isAppend();

		final PartialLoader loader = new PartialFileLoader(channel, mode,
		    headerReader, file);
		this.edits = new FlexRegion<PartialLoader>(readonly, append,
		    channel.size(), loader);
	}

	/**
	 * 
	 */
	public void abortChanges() {
		this.edits.clear();
		this.totalChange = 0;
	}

	public void add(final ByteBuffer b, final long global) throws IOException {
		final long length = b.limit() - b.position();

		// Create a partial loader for our cache memory buffer and do the insert
		final PartialLoader record = new MemoryCacheLoader(b, true, headerReader);
		this.edits.insert(global, length, record);

		this.autoflushChange(length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.io.AutoflushMonitor#autoflushChange(long)
	 */
	public void autoflushChange(final long delta) throws IOException {
		this.totalChange += delta;

		if (this.autoflush
		    && (this.totalChange > FileEditorImpl.AUTOFLUSH_AMOUNT)) {
			this.flush();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	public void close() throws IOException {
		try {
			this.flush();
		} finally {

			if (this.channel.isOpen()) {
				this.channel.close();
			}

			/*
			 * Need to clear the region and run GC as memory mapped buffers may still
			 * remain and hold the channel open. This may cause that associated file
			 * can not be removed. Running GC seems to help, although officially Sun
			 * says that memory mapped buffers are not unmappable and may remain in
			 * memory until VM terminates and even beyond that.
			 */
			this.edits.clear();
			System.gc();
		}
		
		edits.close();
	}

	/**
	 * @param headerReader
	 * @param l
	 * @param length
	 * @throws IOException
	 */
	private PartialBuffer fetchPartialBuffer(final HeaderReader lengthGetter,
	    final long global, final int minLength) throws IOException {

		final RegionSegment<PartialLoader> segment = this.edits.getSegment(global);
		final PartialLoader loader = segment.getData();
		final long regional = segment.mapGlobalToRegional(global);

		final PartialBuffer blockBuffer = loader.fetchBlock(regional, minLength);

		final int p = (int) (regional - blockBuffer.getStartRegional());

		/*
		 * Make sure the next record we want to fetch resides in the existing shared
		 * buffer, otherwise we have to prefetch another buffer.
		 */
		if ((p < 0)
		    || (blockBuffer.checkBoundsRegional(regional, minLength) == false)) {

			throw new IllegalStateException("Unable to prefetch buffer [" + regional
			    + "/" + minLength + "]");
		}

		final ByteBuffer buffer = blockBuffer.getByteBuffer();

		buffer.limit(p + lengthGetter.getMinLength());
		buffer.position(p);

		return blockBuffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		if (this.channel.isOpen()) {
			this.close();
		}
	}

	/**
	 * <p>
	 * Flushes all the changes made so far to the underlying file. If no changes
	 * have been made, nothing happens. Changes are flushed to the underlying file
	 * and the edits hierarchy is flattened. Any previously aquired change
	 * iterators should be discarded and new ones aquired. Any attempt to use them
	 * will throw InvalidRegionException since all changes have been invalidated.
	 * Forwarding to the new edits tree will forward any RegionHandles to new
	 * RegionOverlays which now contain the flattened changes.
	 * </p>
	 * <p>
	 * The algorithm in this flush method is optimized for changes that simply
	 * append record to the end. If anyother type of changes, besides the appended
	 * records, have been applied, the changes are flushed using a more generic
	 * algrorithm that ensure entegrity of the entire file.
	 * </p>
	 * 
	 * @see java.io.Flushable#flush()
	 */
	public void flush() throws IOException {

		if (this.isModified() == false) {

			return;
		}

		if (this.edits.isModifiedByAppendOnly()) {
			this.flushByAppendInPlace();
		} else {
			/*
			 * We flatten inside inorder to release any Memory MAPPED regions before
			 * closing the source channel
			 */
			this.flushByCopy();
		}

		final PartialLoader loader = new PartialFileLoader(this.channel, this.mode,
		    this.headerReader, file);
		loader.order(this.order);
		this.edits.flatten(loader);

		System.gc();

		this.totalChange = 0;
	}

	/**
	 * @throws IOException
	 */
	private void flushByAppendInPlace() throws IOException {
		/*
		 * Position channel cursor at the end of the file, ready for append.
		 */
		this.channel.position(this.channel.size());

		/*
		 * Skip and align to the first change
		 */
		final Iterator<RegionSegment<PartialLoader>> i = this.edits.iterator();
		if (i.hasNext() == false) {
			throw new IllegalStateException(
			    "Editor has no changes, can not flush in place");
		}

		i.next(); // Skip over the first region, the second is the first change

		while (i.hasNext()) {
			final RegionSegment<PartialLoader> segment = i.next();
			final PartialLoader loader = new ConstrainedPartialLoader(segment
			    .getData(), segment);

			loader.transferTo(this.channel);
		}
	}

	/**
	 * <p>
	 * Flushes all the changes that currently exist in the "edits" buffer into a
	 * temporary secondary file. After the copy the original file is removed and
	 * the temporary file is renamed back to the original file which now contains
	 * the contents of the "edits" buffer.
	 * </p>
	 * <p>
	 * All the regions and their overlays are iterated over one segment at a time,
	 * this includes the big segment consiting of the original file content, and
	 * their reader's are asked to copy their buffers out to the temporary file's
	 * channel in their individual smaller segments.
	 * </p>
	 * 
	 * @throws IOException
	 *           any IO errors with either the source file or the temporary file
	 *           operation's during the flush
	 */
	private void flushByCopy() throws IOException {

		/*
		 * Create a temp file
		 */

		final File temp = File.createTempFile(this.file.getName(), null);

		final FileChannel tempChannel = new RandomAccessFile(temp, "rw")
		    .getChannel();

		/*
		 * Copy entire edits tree, including the root file, to temp file
		 */
		for (final RegionSegment<PartialLoader> segment : this.edits) {
			final PartialLoader loader = new ConstrainedPartialLoader(segment
			    .getData(), segment);

			loader.transferTo(tempChannel);
		}
		this.channel.close();
		tempChannel.close();

		System.gc();

		/*
		 * We're done with the original file. All changes are now in the temp file
		 * Try rename first, if it doesn't exist then do it by copy
		 */
		if (file.delete() == false) {
			throw new IOException(
			    "Unable to delete original file during flushByCopy()");
		}
		if (temp.renameTo(file) == false) {
			throw new IOException(
			    "Unable to move temporary file during flushByCopy()");
		}

		final String accessMode = (mode.isContent() ? "rw" : "r");

		/*
		 * Now we need to reopen the channel
		 */
		this.channel = new RandomAccessFile(file, accessMode).getChannel();
	}


	/**
	 * Creates a handle that keeps track of position and buffer forwards after the
	 * editor is flushed.
	 * 
	 * @param global
	 *          position for which to generate the handle for.
	 * @return handle which will keep track of buffer at the specified position
	 */
	public EditorHandle generateHandle(final long global) {

		final EditorHandle handle = new EditorHandleImpl(this.edits
		    .createHandle(global), headerReader);

		return handle;
	}

	/**
	 * @param global
	 * @param headerReader
	 * @return
	 * @throws IOException
	 */
	public ByteBuffer get(final long global, final HeaderReader blockGetter)
	    throws IOException {

		final PartialBuffer min = this.fetchPartialBuffer(blockGetter, global,
		    blockGetter.getMinLength());

		final long length = blockGetter.readLength(min.getByteBuffer());
		final int regional = (int) min.getStartRegional();

		final PartialBuffer partial;
		if (min.checkBoundsRegional(regional, regional + (int) length) == false) {
			partial = fetchPartialBuffer(headerReader, global, (int) length);
		} else {
			partial = min;
		}

		partial.reposition(regional, (int) length);

		return partial.getByteBuffer();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.io.Autoflushable#getAutoflush()
	 */
	public boolean getAutoflush() {
		return this.autoflush;
	}

	public FileChannel getChannel() {
		return this.channel;
	}

	public File getFile() {
		return this.file;
	}

	/**
	 * @return
	 */
	public long getLength() {
		return this.edits.getLength();
	}

	public RawIterator getRawIterator() throws IOException {
		return this.getRawIterator(null);
	}

	public RawIterator getRawIterator(Filter<RecordFilterTarget> filter)
	    throws IOException {
		return rawBuilder.createRawIterator(filter);
	}

	/**
	 * @return
	 */
	public boolean isModified() {
		return this.totalChange != 0;
	}

	/**
	 * @return
	 */
	public boolean isMutable() {
		return mode.isAppend() || mode.isContent();
	}

	public boolean isOpen() {
		return channel.isOpen();
	}

	public Iterator<ByteBuffer> iterator() {

		final RawIterator raw;

		try {
			raw = this.getRawIterator();
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}

		return new IteratorAdapter<ByteBuffer>(raw);

	}

	public final ByteOrder order() {
		return this.order;
	}

	public final void order(final ByteOrder order) {
		this.order = order;

		/*
		 * Change the byte order on all segments
		 */
		for (final RegionSegment<PartialLoader> segment : this.edits) {
			segment.getData().order(order);
		}

		/*
		 * Notify the main editor that change happened to data and that in our case
		 * RO buffer may have been replaced with a RW buffer
		 */
		this.edits.changeHappened();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.RawIterator#replaceInPlace()
	 */
	public void replaceInPlace(final long global, final boolean copy) throws IOException {

		// existing record from buffer
		final ByteBuffer original = this.get(global, headerReader);

		// its length
		final int length = original.limit() - original.position();

		// create new buffer by copy of the original
		final PartialLoader loader = new MemoryCacheLoader(original, copy,
		    headerReader);

		// now the replacement by region with the new buffer
		this.edits.replace(global, length, length, loader);

		this.autoflushChange(length * (copy ? 2 : 1));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.io.Autoflushable#setAutoflush(boolean)
	 */
	public void setAutoflush(final boolean state) throws IOException {
		this.autoflush = state;

		this.autoflushChange(0);
	}

	/**
	 * @return
	 */
	public HeaderReader getLengthGetter() {
		return headerReader;
	}

	/**
	 * @return
	 */
	public FlexRegion<PartialLoader> getFlexRegion() {
		return edits;
	}
}
