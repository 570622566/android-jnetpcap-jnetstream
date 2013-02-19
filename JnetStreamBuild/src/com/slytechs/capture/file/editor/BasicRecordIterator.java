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
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import org.jnetstream.capture.file.HeaderReader;


import com.slytechs.utils.collection.IOSkippableIterator;
import com.slytechs.utils.collection.Positional;
import com.slytechs.utils.memory.PartialBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class BasicRecordIterator implements IOSkippableIterator<ByteBuffer>,
    Positional {

	private final PartialLoader loader;

	private final HeaderReader lengthGetter;

	private long regional = 0;

	private final int min;

	private final long length;

	private PartialBuffer blockBuffer;

	/**
	 * @param loader
	 * @param headerReader
	 */
	public BasicRecordIterator(final PartialLoader loader,
	    final HeaderReader lengthGetter) {
		this.loader = loader;
		this.lengthGetter = lengthGetter;
		this.min = lengthGetter.getMinLength();
		this.length = loader.getLength();
		
		this.regional = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOSkippable#skip()
	 */
	public void skip() throws IOException {
		next();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOIterator#hasNext()
	 */
	public boolean hasNext() throws IOException {
		return regional < length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOIterator#next()
	 */
	public ByteBuffer next() throws IOException {
		
		if (this.blockBuffer == null
		    || this.blockBuffer.checkBoundsRegional(regional, min) == false) {
			
			this.blockBuffer = this.loader.fetchBlock(regional, min);
			
		} else {
			this.blockBuffer.reposition(regional, min);
		}

		final ByteBuffer buffer = this.blockBuffer.getByteBuffer();

		final int length = (int) lengthGetter.readLength(buffer);
		final int allocation = this.loader.getBufferAllocation(length);

		if (this.blockBuffer.checkBoundsRegional(regional, length) == false) {

			if (length > allocation) {
				throw new BufferUnderflowException();
			}

			this.blockBuffer = this.loader.fetchBlock(regional, length);
		}

		try {
			this.blockBuffer.reposition(regional, length);
		} catch (final IllegalArgumentException e) {
			throw e;
		}

		this.regional += length;

		return buffer;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOIterator#remove()
	 */
	public void remove() throws IOException {
		throw new UnsupportedOperationException(
		    "This optional operation is not supported by this basic iterator");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.Positional#getPosition()
	 */
	public long getPosition() {
		return regional;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.Positional#setPosition(long)
	 */
	public long setPosition(long position) {
		final long old = this.regional;

		loader.checkBoundaryRegional(position);

		this.regional = position;

		return old;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.Positional#setPosition(com.slytechs.utils.collection.Positional)
	 */
	public long setPosition(Positional position) {
		return setPosition(position.getPosition());
	}

}
