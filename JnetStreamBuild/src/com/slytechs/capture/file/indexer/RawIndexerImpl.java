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
package com.slytechs.capture.file.indexer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jnetstream.capture.file.RawIndexer;
import org.jnetstream.capture.file.RawIterator;


/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class RawIndexerImpl
    extends AbstractIndexer<ByteBuffer, ByteBuffer, Long> implements RawIndexer {

	private final RawIterator raw;

	/**
	 * @param raw
	 * @param indexer
	 * @throws IOException
	 */
	public RawIndexerImpl(RawIterator raw, PositionIndexer indexer)
	    throws IOException {
		super(indexer, raw);
		this.raw = raw;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.RawIndexer#add(long, java.nio.ByteBuffer,
	 *      java.nio.ByteBuffer)
	 */
	public void add(long index, ByteBuffer b1, ByteBuffer b2) throws IOException {
		setPosition(index);

		raw.add(b1, b2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.RawIndexer#add(long, java.nio.ByteBuffer,
	 *      boolean)
	 */
	public void add(long index, ByteBuffer buffer, boolean copy) throws IOException {
		setPosition(index);

		raw.add(buffer, copy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.RawIndexer#replace(long, java.nio.ByteBuffer,
	 *      boolean)
	 */
	public void replace(long index, ByteBuffer buffer, boolean copy)
	    throws IOException {
		setPosition(index);

		raw.replace(buffer, copy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.RawIndexer#replaceInPlace(long)
	 */
	public void replaceInPlace(long index) throws IOException {
		setPosition(index);

		raw.replaceInPlace();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.RawIndexer#resize(long, long)
	 */
	public void resize(long index, long size) throws IOException {
		setPosition(index);

		raw.resize(size);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.IndexedFileModifier#remove(java.util.Collection)
	 */
	public void removeAll(final Collection<Long> elements) throws IOException {


	}
	
	protected Collection<Long> mapR(Collection<Long> elements) throws IOException {
		final Long[] array = new Long[elements.size()];

		int i = 0;
		for (final Long c : elements) {
			array[i++] = this.indexer.get(c);
		}

		return Arrays.asList(array);
	}
	
	protected List<Long> mapR(List<Long> elements) throws IOException {
		final Long[] array = new Long[elements.size()];

		int i = 0;
		for (final Long c : elements) {
			array[i++] = this.indexer.get(c);
		}

		return Arrays.asList(array);
	}
	
	protected Long mapR(Long element) throws IOException {
		return indexer.get(element);
	}
	
	protected Long[] mapR(final Long...elements) throws IOException {
		final Long[] array = new Long[elements.length];

		for (int i = 0; i < elements.length; i++) {
			array[i] = this.indexer.get(elements[i]);
		}
		
		return array;
	}

}
