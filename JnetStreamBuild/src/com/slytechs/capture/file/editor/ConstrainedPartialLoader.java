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
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import org.jnetstream.capture.file.HeaderReader;


import com.slytechs.utils.memory.MemoryModel;
import com.slytechs.utils.memory.PartialBuffer;
import com.slytechs.utils.region.RegionSegment;

/**
 * <p>
 * ConstrainedPartialLoader works with any PartialLoader to load portion of
 * storage into memory but is further bound by limits imposed by a
 * OldRegionSegment. Although the underlying storage might be much larger, such
 * as a huge file, this loader is bound and constrained to only the section of
 * the overall storage as specified by the OldRegionSegment limits.
 * </p>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */

final public class ConstrainedPartialLoader implements PartialLoader {

	private final PartialLoader loader;

	private final RegionSegment segment;

	private final HeaderReader lengthGetter;

	/**
	 * @param loader
	 * @param segment
	 * @throws IndexOutOfBoundsException
	 *           TODO
	 */
	public ConstrainedPartialLoader(final PartialLoader loader,
	    final RegionSegment segment) throws IndexOutOfBoundsException {
		this.loader = loader;
		this.segment = segment;
		this.lengthGetter = loader.getLengthGetter();

		/*
		 * Check boundary conditions
		 */
		loader.checkBoundaryRegional(segment.getStartRegional());
		loader.checkBoundaryRegional(segment.getStartRegional(), segment
		    .getLength());
	}

	final public void checkBoundaryRegional(final long regional)
	    throws IndexOutOfBoundsException {

		if (regional < this.segment.getStartRegional()
		    || regional >= this.segment.getEndRegional()) {
			throw new IndexOutOfBoundsException("Position " + regional
			    + " out of bounds " + segment.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#checkBoundary(long,
	 *      long)
	 */
	public void checkBoundaryRegional(long regional, long length)
	    throws IndexOutOfBoundsException {
		if (length == 0) {
			checkBoundaryRegional(regional);
		} else {
			checkBoundaryRegional(regional + length - 1);
		}
	}

	public void close() throws IOException {
		this.loader.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#fetchBlock(long, int)
	 */
	public PartialBuffer fetchBlock(long regional, int length) throws IOException {
		/*
		 * Since length is actually flexible, we only check the starting position
		 */
		checkBoundaryRegional(regional);
		return loader.fetchBlock(regional, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#fetchBlock(long, int,
	 *      com.slytechs.utils.memory.MemoryModel)
	 */
	public PartialBuffer fetchBlock(long regional, int length, MemoryModel model)
	    throws IOException {
		/*
		 * Since length is actually flexible, we only check the starting position
		 */
		checkBoundaryRegional(regional);
		return loader.fetchBlock(regional, length, model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#fetchMinimum(long, int)
	 */
	public PartialBuffer fetchMinimum(long regional, int length)
	    throws IOException {
		
		/*
		 * Since length is actually flexible, we only check the starting position
		 */
		checkBoundaryRegional(regional);

		return loader.fetchMinimum(regional, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#fetchMinimum(long, int,
	 *      com.slytechs.utils.memory.MemoryModel)
	 */
	public PartialBuffer fetchMinimum(long regional, int length, MemoryModel model)
	    throws IOException {
		/*
		 * Since length is actually flexible, we only check the starting position
		 */
		checkBoundaryRegional(regional);
		
		return loader.fetchMinimum(regional, length, model);
	}

	final public int getBufferAllocation(long length) {
		return this.loader.getBufferAllocation(length);
	}

	final public ByteOrder getByteOrder() {
		return this.loader.getByteOrder();
	}

	final public long getLength() {
		return this.loader.getLength();
	}

	final public boolean isInMemory(final long position) {
		return this.loader.isInMemory(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.Readonly#isReadonly()
	 */
	public boolean isReadonly() {
		return loader.isReadonly();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#isWithinBoundary(long)
	 */
	public boolean isWithinBoundary(long position) {
		return position >= segment.getStart() && position < segment.getEnd();
	}

	final public void setBufferPrefetchSize(final int bufferAllocation) {
		this.loader.setBufferPrefetchSize(bufferAllocation);
	}

	final public void order(final ByteOrder order) {
		this.loader.order(order);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.Readonly#setReadonly(boolean)
	 */
	public boolean setReadonly(boolean state) {
		return loader.setReadonly(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.PartialLoader#transferTo(java.nio.channels.FileChannel)
	 */
	final public long transferTo(final FileChannel out) throws IOException {
		return transferTo(segment.getStartRegional(), segment.getLength(), out);
	}

	final public long transferTo(final long position, final long length,
	    final FileChannel out) throws IOException {

		return this.loader.transferTo(position, length, out);
	}

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.editor.PartialLoader#getLengthGetter()
   */
  public HeaderReader getLengthGetter() {
	  return lengthGetter;
  }
}
