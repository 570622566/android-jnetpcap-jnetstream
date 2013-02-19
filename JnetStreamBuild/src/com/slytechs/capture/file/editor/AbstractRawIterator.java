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
import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jnetstream.capture.file.BufferException;
import org.jnetstream.capture.file.BufferFetchException;
import org.jnetstream.capture.file.HeaderReader;
import org.jnetstream.capture.file.RawIterator;
import org.jnetstream.capture.file.RecordError;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.capture.file.SeekPattern;
import org.jnetstream.filter.Filter;

import com.slytechs.capture.file.Files;
import com.slytechs.utils.collection.IOPositional;
import com.slytechs.utils.collection.SeekResult;
import com.slytechs.utils.event.RuntimeIOException;
import com.slytechs.utils.io.AutoflushMonitor;
import com.slytechs.utils.memory.BufferBlock;
import com.slytechs.utils.memory.BufferUtils;
import com.slytechs.utils.memory.MemoryModel;
import com.slytechs.utils.memory.PartialBuffer;
import com.slytechs.utils.region.FlexRegion;
import com.slytechs.utils.region.RegionHandle;
import com.slytechs.utils.region.RegionSegment;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class AbstractRawIterator implements RawIterator {
	private static final RecordError[] EMPTY_ARRAY = new RecordError[0];

	private final static Log logger = LogFactory
	    .getLog(AbstractRawIterator.class);

	protected static final SeekResult NOT_OK = SeekResult.NotFullfilled;

	protected static final SeekResult OK = SeekResult.Fullfilled;

	protected static final int SEARCH_LENGTH = 64 * 1024;

	private AutoflushMonitor autoflush;

	private PartialBuffer blockBuffer;

	private final Closeable closeable;

	protected final FlexRegion<PartialLoader> edits;

	private final Filter<RecordFilterTarget> filter;

	private long global;

	private final HeaderReader headerReader;

	protected PartialLoader loader;

	protected SeekPattern pattern;

	protected long previousPosition;

	private RegionSegment<PartialLoader> segment;

	public AbstractRawIterator(final FlexRegion<PartialLoader> edits,
	    final HeaderReader headerReader, final AutoflushMonitor autoflush,
	    final Closeable closeable, final Filter<RecordFilterTarget> filter)
	    throws IOException {

		this.edits = edits;
		this.headerReader = headerReader;
		this.autoflush = autoflush;
		this.closeable = closeable;
		this.filter = filter;

		this.setPosition(0);

		/*
		 * Align on the first record that matches our filter
		 */
		seekFilter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#abortChanges()
	 */
	public void abortChanges() throws IOException {
		this.edits.clear();

		// Now make sure that after edits have been cleared we end up somewhere
		// on a reasonable record start.
		if (this.global > this.edits.getLength()) {
			this.setPosition(this.edits.getLength());
		} else {

			// Lets find the first record starting from the current position
			this.seek(this.global);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#add(java.nio.ByteBuffer)
	 */
	public void add(final ByteBuffer b) throws IOException {
		this.add(b, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.RawIterator#add(ByteBuffer, boolean)
	 */
	public void add(final ByteBuffer element, final boolean copy)
	    throws IOException {

		// Create one large memory cache for all elements
		final PartialLoader additions = new MemoryCacheLoader(element, copy,
		    headerReader);

		// Now do the insert
		this.edits.insert(this.global, additions.getLength(), additions);

		// Instead of skipping over all the records, its easier to simply increase
		// the position by total we just computed and be done with it.
		this.setPosition(this.global + additions.getLength());

		this.autoflush.autoflushChange(additions.getLength());
	}

	/**
	 * Adds a new record using two buffers. This method is more efficient then
	 * using {@link #addAll(ByteBuffer[])} version as the two buffers are received
	 * as normal paramters. This version of the signature is used when record's
	 * header and content reside in two separate buffers.
	 * 
	 * @param b1
	 *          first buffer containing the record's header
	 * @param b2
	 *          second buffer containing the record's content
	 * @throws IOException
	 *           any IO errors
	 */
	public void add(final ByteBuffer b1, final ByteBuffer b2) throws IOException {
		final long length = (b1.limit() - b1.position())
		    + (b2.limit() - b2.position());

		// Create a partial loader for our cache memory buffer and do the insert
		final PartialLoader record = new MemoryCacheLoader(b1, b2, headerReader);
		this.edits.insert(this.global, length, record);

		// Advance past the record we just added
		this.setPosition(this.global + length);

		this.autoflush.autoflushChange(length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#add(ByteBuffer[])
	 */
	public void addAll(final ByteBuffer... elements) throws IOException {

		// Create one large memory cache for all elements
		final PartialLoader additions = new MemoryCacheLoader(headerReader,
		    elements);

		// Now do the insert
		this.edits.insert(this.global, additions.getLength(), additions);

		// Instead of skipping over all the records, its easier to simply increase
		// the position by total we just computed and be done with it.
		this.setPosition(this.global + additions.getLength());

		this.autoflush.autoflushChange(additions.getLength());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#add(java.util.List)
	 */
	public void addAll(final List<ByteBuffer> elements) throws IOException {
		final ByteBuffer[] b = new ByteBuffer[elements.size()];
		this.addAll(elements.toArray(b));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	public void close() throws IOException {
		this.closeable.close();
	}

	/**
	 * Takes a snapshow of positions. Converts an array of global positions into
	 * handles. Since mutable operations change the positions of all subsequent
	 * elements within the editor, you must use handles which keep track of the
	 * changes and reflect accurate position after any change to previous
	 * elements.
	 * 
	 * @param elements
	 *          position elements to generate handles for
	 * @return handles for all the position elements
	 */
	public RegionHandle[] convertToHandles(final Long[] elements) {

		final RegionHandle[] handles = new RegionHandle[elements.length];

		for (int i = 0; i < elements.length; i++) {
			handles[i] = this.edits.createHandle(elements[i]);
		}

		return handles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Flushable#flush()
	 */
	public void flush() throws IOException {
		this.autoflush.flush();
	}

	/**
	 * @return
	 */
	protected final long getBoundaryEnd() {
		return edits.getLength();
	}

	protected final long getBoundaryStart() {
		return 0;
	}

	/**
	 * @return the recordFilter
	 */
	public final Filter getFilter() {
		return this.filter;
	}

	private PartialLoader getLoader(final RegionSegment<PartialLoader> segment)
	    throws IndexOutOfBoundsException {

		return segment.getData();
	}

	/**
	 * Gets the buffer at current position, does not advance and does not apply
	 * the recordFilter.
	 * 
	 * @param headerReader
	 * @return
	 * @throws IOException
	 */
	public final ByteBuffer getNoFilter(final HeaderReader lengthGetter)
	    throws IOException {

		final long regional = this.segment.mapGlobalToRegional(this.global);
		final int min = lengthGetter.getMinLength();

		if (this.blockBuffer.checkBoundsRegional(regional, min) == false) {
			this.blockBuffer = this.loader.fetchBlock(regional, min);
		} else {
			this.blockBuffer.reposition(regional, min);
		}

		final ByteBuffer buffer = this.blockBuffer.getByteBuffer();

		final int length = (int) this.getRecordLength(buffer, lengthGetter);
		final int allocation = this.loader.getBufferAllocation(length);

		if (this.blockBuffer.checkBoundsRegional(regional, length) == false) {

			if (length > allocation) {
				AbstractRawIterator.logger.error("Record's length (" + length
				    + ") is greater then prefetch buffer size (" + allocation
				    + ") at record position (" + this.global + ")");
				throw new BufferUnderflowException();
			}

			this.blockBuffer = this.loader.fetchBlock(regional, length);
		}

		try {
			this.blockBuffer.reposition(regional, length);
		} catch (final IllegalArgumentException e) {
			AbstractRawIterator.logger
			    .error("Unable to set limit and position ByteBuffer properties "
			        + "at position (" + this.global + "). Record's length (" + length
			        + ").");
			throw e;
		}

		return buffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOPositionable#getPosition()
	 */
	public long getPosition() throws IOException {
		return this.global;
	}

	/**
	 * @param buffer
	 * @return
	 */
	protected abstract int getRecordHeaderLength(ByteBuffer buffer);

	protected long getRecordLength(final ByteBuffer buffer) throws IOException {
		return this.getRecordLength(buffer, this.headerReader);
	}

	private long getRecordLength(final ByteBuffer buffer,
	    final HeaderReader lengthGetter) throws IOException {

		final long length = lengthGetter.readLength(buffer);

		if (length < 0) {
			AbstractRawIterator.logger.error("Invalid record length value (" + length
			    + ") at record position (" + this.global + ")");
			throw new BufferUnderflowException();
		}

		return length;
	}

	private long getRecordLength(final long regional,
	    final HeaderReader headerReader) throws IOException {

		final PartialBuffer bblock;
		try {
			bblock = this.loader.fetchBlock(regional, headerReader.getMinLength());

		} catch (BufferFetchException e) {
			e.setFlexRegion(edits);
			e.setMessage("Unable to read length from header");
			e.setHeaderReader(headerReader);
			throw e;
		}

		final ByteBuffer buffer = bblock.getByteBuffer();
		final long length = headerReader.readLength(buffer);

		if ((length < 0) || (length > 100000)) {
			AbstractRawIterator.logger.error("Invalid record length value (" + length
			    + ") at record position (" + this.global + ")");
			throw new BufferUnderflowException();
		}

		return length;
	}

	private RegionSegment<PartialLoader> getSegment(final long position) {
		return this.edits.getSegment(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOIterator#hasNext()
	 */
	public boolean hasNext() throws IOException {

		seekFilter(); // Make sure we are aligned to the next filtered record

		return this.global < getBoundaryEnd();
	}

	private final boolean iterateToLast() throws IOException {

		long previous = this.getBoundaryStart();
		while (this.hasNext()) {
			previous = this.getPosition();
			this.skip();
		}

		this.setPosition(previous);

		return previous != this.getBoundaryStart();
	}
	
	/* (non-Javadoc)
   * @see java.lang.Iterable#iterator()
   */
  public Iterator<ByteBuffer> iterator() {
  	final RawIterator i = this;
	  return new Iterator<ByteBuffer>() {

			/* (non-Javadoc)
       * @see java.util.Iterator#hasNext()
       */
      public boolean hasNext() {
	      try {
	        return i.hasNext();
        } catch (IOException e) {
	        throw new RuntimeIOException(e);
        }
      }

			/* (non-Javadoc)
       * @see java.util.Iterator#next()
       */
      public ByteBuffer next() {
	      try {
	        return i.next();
        } catch (IOException e) {
	        throw new RuntimeIOException(e);
        }
      }

			/* (non-Javadoc)
       * @see java.util.Iterator#remove()
       */
      public void remove() {
	      try {
	        i.remove();
        } catch (IOException e) {
	        throw new RuntimeIOException(e);
        }
      }
	  	
	  };
  }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOIterator#next()
	 */
	public ByteBuffer next() throws IOException {
		final ByteBuffer buffer = this.nextNoFilter(this.headerReader);

		/*
		 * Next, apply the filter and advance the position to the next record
		 */
		this.seekFilter();

		return buffer;
	}

	/**
	 * Does all the work and does not apply the recordFilter.
	 * 
	 * @param headerReader
	 * @return
	 * @throws IOException
	 */
	private ByteBuffer nextNoFilter(final HeaderReader lengthGetter)
	    throws IOException {

		final long regional = this.segment.mapGlobalToRegional(this.global);
		final int min = lengthGetter.getMinLength();

		if (this.blockBuffer.checkBoundsRegional(regional, min) == false) {
			this.blockBuffer = this.loader.fetchBlock(regional, min);
		} else {
			this.blockBuffer.reposition(regional, min);
		}

		final ByteBuffer buffer = this.blockBuffer.getByteBuffer();

		final int length = (int) this.getRecordLength(buffer, lengthGetter);
		final int allocation = this.loader.getBufferAllocation(length);

		if (this.blockBuffer.checkBoundsRegional(regional, length) == false) {

			if (length > SEARCH_LENGTH) {
				AbstractRawIterator.logger.warn("Record's length (" + length
				    + ") is greater then prefetch buffer size (" + allocation
				    + ") at record position (" + this.global + ")");
				if (pattern.match(buffer)) {
					logger.info("Erroneous record passes the search pattern test");
				}
				throw new BufferUnderflowException();
			}

			this.blockBuffer = this.loader.fetchBlock(regional, length);
		}

		try {
			this.blockBuffer.reposition(regional, length);
		} catch (final IllegalArgumentException e) {
			AbstractRawIterator.logger
			    .error("Unable to set limit and position ByteBuffer properties "
			        + "at position (" + this.global + "). Record's length (" + length
			        + ").");
			throw e;
		}

		this.previousPosition = this.global;
		this.setPosition(this.global + length);

		return buffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOIterator#remove()
	 */
	public void remove() throws IOException {

		if (global == edits.getLength()) {
			return; // Nothing to do
		}

		final long length = this.getRecordLength(this.segment
		    .mapGlobalToRegional(this.global), this.headerReader);

		this.edits.remove(this.global, length);

		this.setPosition(this.global);

		this.autoflush.autoflushChange(length);
	}

	/**
	 * Does an in-memory remove by replacing the region of the entire file, with
	 * the exception of the block header, with 0 length overlay.
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#removeAll()
	 */
	public void removeAll() throws IOException {
		/*
		 * Alternative to calling abortChanges() would be to remove each segment
		 * within edits by iterating over every segment and doing a remove on its
		 * region. This might be neccessary in the future if granular undo operation
		 * is supported. Currently with only the atomic undo we simply abort all in
		 * memory changes.
		 */

		// First abort all in-memory changes
		this.abortChanges();

		final long length = this.edits.getLength() - this.getBoundaryStart();

		// Next simply remove the region making up the entire physical file
		this.edits.remove(this.getBoundaryStart(), length);

		// Position the cursor to front
		this.seekFirst();

		this.autoflush.autoflushChange(length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#remove(Collection<D>)
	 */
	public void removeAll(final Collection<Long> elements) throws IOException {

		final Long[] array = elements.toArray(new Long[elements.size()]);

		this.removeAll(array);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOIterator#remove(long count)
	 */
	public void removeAll(final long count) throws IOException {
		if (count < 0) {
			throw new IllegalArgumentException("Invalid count number. Less then 0");
		}

		// Remember our current position
		final long start = this.getPosition();
		long total = 0;

		// Calculate total amount to be removed based on all records
		for (int i = 0; i < count; i++) {
			final long length = this.getRecordLength(this.segment
			    .mapGlobalToRegional(this.global), this.headerReader);
			total += length;

			if (this.hasNext() == false) {
				throw new IllegalArgumentException(
				    "Invalid count number. "
				        + "Count is larger then records remaining from the current position");
			}
			this.skip();
		}

		// Do one large remove of all the records
		this.edits.remove(start, total);

		// reinitize segment and buffer
		this.setPosition();

		this.autoflush.autoflushChange(total);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#remove(D[])
	 */
	public void removeAll(final Long... elements) throws IOException {
		Arrays.sort(elements);

		final RegionHandle[] handles = this.convertToHandles(elements);

		for (final RegionHandle handle : handles) {
			this.setPosition(handle.getPositionGlobal());
			this.remove();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#replace(java.lang.Object)
	 */
	public void replace(final ByteBuffer element) throws IOException {
		this.replace(element, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.RawIterator#replace(java.lang.Object, boolean)
	 */
	public void replace(final ByteBuffer element, final boolean copy)
	    throws IOException {
		final long length = this.getRecordLength(this.segment
		    .mapGlobalToRegional(this.global), this.headerReader);
		final PartialLoader replacement = new MemoryCacheLoader(element, copy,
		    headerReader);

		this.edits.replace(this.global, length, replacement.getLength(),
		    replacement);

		this.autoflush.autoflushChange(length + replacement.getLength());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.RawIterator#replaceInPlace()
	 */
	public void replaceInPlace() throws IOException {
		// remember the start of this record
		final long p = this.getPosition();

		// existing record from buffer
		final ByteBuffer original = this.next();

		// its length
		final int length = (int) this.getRecordLength(original);

		// create new buffer by copy of the original
		final PartialLoader loader = new MemoryCacheLoader(original, true,
		    headerReader);

		// now the replacement by region with the new buffer
		this.edits.replace(p, length, length, loader);

		this.autoflush.autoflushChange(length * 2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.RawIterator#resize(long)
	 */
	public void resize(final long size) throws IOException {

		// Check for reasonable value since we currently only allocate from heap
		if (size > Integer.MAX_VALUE) {
			throw new UnsupportedOperationException(
			    "Current implementation uses in memory allocation. "
			        + "Not enough memory to allocate for the request. "
			        + "Must use physical storage backed allocation cache.");
		}

		final long p = this.getPosition();
		final ByteBuffer b = this.next();
		final int length = (int) this.getRecordLength(b);

		if (size == length) {
			return; // Nothing to do

		} else if (size < 0) {
			/*
			 * Simply trucate the record by removing part of its region
			 */
			final int delta = length - (int) size;
			this.edits.remove(p + delta, delta);

		} else {
			/*
			 * Expand the record by replacing the original segment where the record
			 * resides, with a new segment of the new size. Copy the original record
			 * content into the new buffer that replaces it.
			 */
			final PartialLoader loader = new MemoryCacheLoader((int) size,
			    headerReader);
			final ByteBuffer dst = loader.fetchBlock(0, (int) size).getByteBuffer();

			// Do copy of or original record buffer into the new buffer
			dst.put(b);

			// Do the region replacement
			this.edits.replace(p, length, size, loader);
		}

		this.autoflush.autoflushChange(length + size);
	}

	/**
	 * Retains only the elements that are in this collection. All other records
	 * are removed. The retained elements are reorder to match the order of as
	 * specified by the collection's natural order.
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#retainAll(List)
	 */
	public void retainAll(final List<Long> elements) throws IOException {

		final Long[] array = elements.toArray(new Long[elements.size()]);

		this.retainAll(array);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#retain(D[])
	 */
	public void retainAll(final Long... elements) throws IOException {

		final ByteBuffer[] originals = new ByteBuffer[elements.length];

		int i = 0;
		for (final long element : elements) {
			this.setPosition(element);
			originals[i++] = BufferUtils.slice(this.next());
		}

		final long before = this.edits.getLength();

		// Very efficiently removes all records as a single region remap
		this.removeAll();

		final long after = this.edits.getLength();

		/*
		 * Make sure to record number of byte affected, in this case that can be
		 * freed by a flush.
		 */
		this.autoflush.autoflushChange(before - after);

		// now we add our records back, in their user supplied order
		this.addAll(originals);
	}

	/**
	 * Searches for a packet record start within the file. If the record header is
	 * not found exactly at the specified offset, the search is repeated by
	 * starting the match at the offset + 1. Incrementing the offset until a match
	 * is found or maxSearch has been reached.
	 * 
	 * @param offset
	 *          offset within the file to start the search at. This is the first
	 *          byte to search for a record header match.
	 * @param maxSearch
	 *          a limit on the search. The search will be performed within the
	 *          windows of offset <= search < (offset + maxSearch)
	 * @return exact offset into the capture file of the start of the next record
	 *         header. -1 indicates that no record header was found at the offset
	 *         and with the limits set of maxSearch bytes.
	 * @throws EOFException
	 *           end of file has been reached before the header could be matched.
	 *           This indicates that no positive match was made.
	 * @throws IOException
	 *           any IO errors
	 */
	public long searchForRecordStart(final ByteBuffer buffer, final int index,
	    final int maxSearch) throws EOFException, IOException {

		final int l = index + maxSearch - this.pattern.minLength();

		for (int i = index; i < l; i++) {
			buffer.position(i);
			buffer.mark();

			if (this.pattern.match(buffer) && verifyAdditionalRecords(buffer, 5)) {
				return i;
			}
		}

		return -1;
	}

	public boolean verifyAdditionalRecords(final ByteBuffer buffer,
	    final int count) throws EOFException, IOException {

		buffer.reset();

		final int MAX_HEADER_LENGTH = 24;
		final ByteBuffer view = BufferUtils.duplicate(buffer);
		final int capacity = view.capacity();
		boolean status = true;

		for (int i = 0; i < count && view.position() + MAX_HEADER_LENGTH < capacity; i++) {
			view.mark();
			long length = headerReader.readLength(view);
			int p = view.position() + (int) length;

			if (pattern.match(view) == false) {
				status = false;
				break;
			}
			view.reset();

			if (p + MAX_HEADER_LENGTH > view.capacity()) {
				break;
			}

			view.limit(p + MAX_HEADER_LENGTH);
			view.position(p);
		}

		return status;
	}

	public SeekResult seek(final double percentage) throws IOException {
		if ((percentage < 0.0) || (percentage > 1.0)) {
			throw new IllegalArgumentException(
			    "percentage is out of range, must be between 0.0 and 1.0");
		}

		final long global = (long) (percentage * this.edits.getLength());
		// logger.error("position=" + position);

		return this.seek(global);
	}

	/**
	 * @param filter
	 * @return
	 * @throws IOException
	 */
	public SeekResult seek(final Filter<RecordFilterTarget> filter)
	    throws IOException {
		final long length = this.edits.getLength();

		if (filter == null) {
			return (this.global < length ? OK : NOT_OK);
		}

		ByteBuffer buffer = null;

		long nextPosition = this.global;

		do {

			if (this.global >= length) {
				return NOT_OK;
			}

			nextPosition = this.getPosition();
			buffer = this.nextNoFilter(this.headerReader);

		} while (Files.checkRecordFilter(this.filter, buffer, headerReader) == false);

		this.setPosition(nextPosition);

		return (this.global < length ? OK : NOT_OK);
	}

	public SeekResult seek(long global) throws IOException {

		if (global == this.global) {
			return OK; // Nothing to DO
		}

		final RegionSegment<PartialLoader> segment = this.edits.getSegment(global);
		final long regional = segment.mapGlobalToRegional(global);

		final PartialLoader loader = segment.getData();

		/*
		 * For searches since we are skipping around the file, we supply a memory
		 * hint of ByteArray so that only SEARCH_LENGTH of bytes are brought into
		 * byte array memory. It makes no sense to bring in a 10Meg mapped buffer
		 * into memory if we only need to look at one 4K block in it. Since the
		 * cache is checked first, if the block does already exist in the memory
		 * mapped case, then the cached block will be used. Also note that the fetch
		 * version with MemoryModel, does not cache the returned buffers.
		 */
		final PartialBuffer buf = loader.fetchMinimum(regional,
		    AbstractRawIterator.SEARCH_LENGTH, MemoryModel.ByteArray);

		final int p = (int) (global - segment.mapRegionalToGlobal(buf
		    .getStartRegional()));
		int maxSearch = (int) buf.getLength() - p;
		maxSearch = (maxSearch < AbstractRawIterator.SEARCH_LENGTH) ? maxSearch
		    : AbstractRawIterator.SEARCH_LENGTH;

		/*
		 * If we don't have enough bytes in this segment for even the minLength,
		 * then no more records can be found here. The next segment, must contain a
		 * record at its begining, therefore we can simply align there and call
		 * hasNext() to confirm and apply the recordFilter if one has been defined.
		 * If hasNext() can't fullfill the request due to a recordFilter, it will
		 * return false.
		 */
		if (maxSearch < this.pattern.minLength()) {
			final long nextSegmentStart = segment.getEndGlobal();
			setPosition(nextSegmentStart);

			return (this.hasNext() ? OK : NOT_OK);
		}

		buf.getByteBuffer().limit(p + maxSearch);

		final long local = this.searchForRecordStart(buf.getByteBuffer(), p, maxSearch);
		if (local == -1) {
			/*
			 * Current segment did not contain a beginning of a packet, therefore move
			 * on to the next segment. The start of each segment should begin with a
			 * record, so this we should find the next record at startNextSegment
			 * position, although if the file is corrupt, then we might need to search
			 * through it until the next record.
			 */
			final long startNextSegment = segment.getEnd();

			if (startNextSegment == this.edits.getLength()) {
				/*
				 * No more segments, so we just searched through last segment and did
				 * not find start of record. Therefore seek to the end past the last
				 * record.
				 */
				this.seekEnd();

				return NOT_OK;
			} else {
				/*
				 * Every segment always starts with atleast 1 record. It is illegal to
				 * have segments with not records in them in any of the file formats.
				 */
				this.setPosition(startNextSegment);
				return (this.hasNext() ? OK : NOT_OK);
			}
		}

		final long reg = buf.mapLocalToRegional(local);
		@SuppressWarnings("unused")
		final long glob = segment.mapRegionalToGlobal(reg);

		// if (TestFilter.positions.contains(glob) == false) {
		// System.out.printf("Not found in positions %d\n", glob);
		// }

		this.setPosition(glob);

		return (this.hasNext() ? OK : NOT_OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOSeekableFirstLast#seekEnd()
	 */
	public SeekResult seekEnd() throws IOException {
		this.setPosition(this.edits.getLength());

		return OK;
	}

	/**
	 * Applies the recordFilter starting at the current position. If the record at
	 * the current position is acceptable, no advance is made. Otherwise the
	 * position is advanced until the next acceptable record or the end of the
	 * editor region.
	 * 
	 * @return TODO
	 * @return
	 * @throws IOException
	 */
	private SeekResult seekFilter() throws IOException {
		return seek(this.filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOSeekableFirstLast#seekFirst()
	 */
	public SeekResult seekFirst() throws IOException {
		this.setPosition(this.getBoundaryStart());

		return (this.hasNext() ? OK : NOT_OK);
	}

	public SeekResult seekSecond() throws IOException {
		seekFirst();
		skip();

		return (this.global < edits.getLength() ? OK : NOT_OK);
	}

	/**
	 * Seeks to start of file, not using any filtering
	 * 
	 * @return
	 * @throws IOException
	 */
	private SeekResult seekFirstNoFilter() throws IOException {
		this.setPosition(this.getBoundaryStart());

		return OK;
	}

	/**
	 * Searches for the last record within the file. When no recordFilter is
	 * applied, the last record is the last record within the file or seekEnd()
	 * and status will be SeekResult.NotFound. If there is a recordFilter applied,
	 * then the last record will be the last record matching the recordFilter
	 * criteria within the file or seekEnd and SeekResult.NotFound.
	 */
	public SeekResult seekLast() throws IOException {

		final long length = this.edits.getLength();

		/*
		 * If the file is empty with just the block header, then simply align to the
		 * end where first record will go.
		 */
		if (length == this.getBoundaryStart()) {
			this.setPosition(this.getBoundaryStart());
			return (this.hasNext() ? OK : NOT_OK);
		}

		if (length < AbstractRawIterator.SEARCH_LENGTH * 10) {
			this.seekFirstNoFilter();

			return (this.iterateToLast() ? OK : NOT_OK);
		}

		final double slenPercentage = (double) AbstractRawIterator.SEARCH_LENGTH
		    / (double) (length - this.getBoundaryStart());

		/*
		 * We're going to choose a percentage for back off from the back of the file
		 * which depends on the size of the file. If the percentage of our
		 * SEARCH_LENGTH (initially 4K) is less the 1%, then we simply go backwards
		 * in 1% intervals. If the percentage is more then 1% we back off in 10%
		 * intervals.
		 */
		final int delta;
		if (slenPercentage < 0.01) {
			delta = (int) (this.edits.getLength() * 0.01);

		} else {
			delta = (int) (this.edits.getLength() * 0.1);

		}

		/*
		 * For no filters set, we will always hit the last record on the first loop,
		 * but if there is a recordFilter in place, we need to back off farther and
		 * farther until possibly the beginning of the file, and good possibility
		 * that we will not find a last record at all, since the recordFilter could
		 * be too strict.
		 */
		for (long p = length - AbstractRawIterator.SEARCH_LENGTH; p > this
		    .getBoundaryStart(); p -= delta) {

			if (this.iterateToLast()) {
				return OK;
			}
		}

		return NOT_OK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.RawIterator#seekToIndex(long)
	 */
	public SeekResult seekToIndex(long recordIndex) throws IOException {
		this.seekFirst();

		while (this.hasNext() && (recordIndex > 0)) {
			recordIndex--;

			this.skip();
		}

		return (recordIndex == 0 ? OK : NOT_OK);
	}

	public void setAutoflush(final boolean state) throws IOException {
		this.autoflush.setAutoflush(state);
	}

	private void setPosition() throws IOException {
		if ((this.segment != null) && this.segment.checkBoundsGlobal(this.global)) {
			return; // We're still within the segment
		}

		/*
		 * The position is set to the end of the edits, this is 1 byte past the last
		 * byte in the entire edit session. No loader or segment is associated with
		 * this, but the position can legaly be set. Only add and seek ops are
		 * allowed, remove and others will fail.
		 */
		if (this.global == this.edits.getLength()) {
			this.segment = null;
			this.loader = null;

			return;
		}

		this.segment = this.getSegment(this.global);
		this.loader = this.getLoader(this.segment);

		this.blockBuffer = BufferBlock.EMPTY_BUFFER;
	}

	public long setPosition(final IOPositional position) throws IOException {
		return this.setPosition(position.getPosition());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOPositionable#setPosition(long)
	 */
	public long setPosition(final long global) throws IOException {
		if (global < getBoundaryStart()) {
			throw new IndexOutOfBoundsException("The position [" + global
			    + "]is outside the boundary of this bounded Iterator ["
			    + getBoundaryStart() + "-" + getBoundaryEnd() + "]");
		}

		final long old = global;
		this.global = global;

		this.setPosition();

		return old;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOSkippable#skip()
	 */
	public void skip() throws IOException {
		final long regional = this.segment.mapGlobalToRegional(this.global);
		final long length = this.getRecordLength(regional, this.headerReader);

		if (length < this.headerReader.getMinLength()) {
			throw new BufferException("Read length is less then minimum length",
			    null, regional, (int) length, false, this.headerReader);
		}
		
		this.setPosition(this.global + length);

		/*
		 * Align to the next record that matches our filter
		 */
		this.seekFilter();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.file.RawIterator#skipIgnoreErrors()
	 */
	public RecordError[] skipOverErrors() throws IOException {

		Exception exception = null;

		try {
			this.skip();
			return AbstractRawIterator.EMPTY_ARRAY;

		} catch (final BufferUnderflowException e) {
			exception = e;
		} catch (final IndexOutOfBoundsException e) {
			exception = e;
		}

		final long old = this.getPosition();
		this.seek(this.global + 1);

		final RecordError[] errors = new RecordError[1];
		errors[0] = new RecordError(old, exception.getMessage(), exception);

		return errors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#swap(java.lang.Object,
	 *      java.lang.Object)
	 */
	public void swap(final Long r1, final Long r2) throws IOException {

		if (r1 == r2) {
			return; // Nothing to do
		}

		// Flip them around, make sure r1 is always < r2
		if (r1 < r2) {
			this.swap(r2, r1);
		}

		final long p = this.getPosition(); // remember current position

		// 1st aquire the buffers for both records
		this.setPosition(r1);
		final ByteBuffer b1 = BufferUtils.slice(this.next()); // Remember the buffer

		this.setPosition(r2);
		final ByteBuffer b2 = BufferUtils.slice(this.next()); // Remember the buffer

		// Replace r2 first, as its replacement won't affect position of r1
		this.setPosition(r2);
		this.replace(b1);

		// Lastly replace r1, it might affect r2 position, but r2 has already been
		// replaced, so no big deal
		this.setPosition(r1);
		this.replace(b2);

		/*
		 * Try to get back to roughly the same position, the position might only
		 * change if r1 < position < r2 and if r1.length != r2.length, otherwise the
		 * position should still end up on same record's start position
		 */
		this.seek(p);

		this.autoflush.autoflushChange(b1.capacity() + b2.capacity());
	}
}
