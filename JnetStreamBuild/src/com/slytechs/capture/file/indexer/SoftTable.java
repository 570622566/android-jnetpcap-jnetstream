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
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.slytechs.capture.file.editor.BasicRecordIterator;
import com.slytechs.capture.file.editor.PartialLoader;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class SoftTable implements IndexTable {

	@SuppressWarnings("unused")
  private static final Log logger = LogFactory.getLog(SoftTable.class);

	private final int length;

	private final long first; // The first position is hard

	private final long last; // The last position is hard

	private SoftReference<long[]> positions;

	private final PartialLoader loader;

	/**
	 * @param length
	 */
	public SoftTable(final List<Long> list, PartialLoader loader) {
		this.loader = loader;
		this.length = list.size();
		this.first = list.get(0);
		this.last = list.get(list.size() - 1);

		final long[] positions = new long[length];
		for (int i = 0; i < list.size(); i++) {
			positions[i] = list.get(i);
		}

		this.positions = new SoftReference<long[]>(positions);
	}

	public long get(int index) throws IOException {

		final long[] positions = fetchIndexes();

		return positions[index];
	}

	/**
	 * @param i
	 * @param index
	 * @throws IOException
	 */
	private long[] fetchIndexes() throws IOException {
		if (this.positions.get() != null) {
			return positions.get(); // Nothing to do
		}

		final long[] positions = new long[length];
		// Arrays.fill(positions, -1);

		final BasicRecordIterator iterator = new BasicRecordIterator(loader, loader
		    .getLengthGetter());

		iterator.setPosition(first);

		for (int i = 0; i < length; i++) {
			positions[i] = iterator.getPosition();

			if (iterator.hasNext() == false) {
				throw new IllegalStateException(
				    "Not enough records in loader, expected upto " + length);
			}

			iterator.skip();
		}

		this.positions = new SoftReference<long[]>(positions);

//		logger.info("fetched: [" + positions[0] + "-" + (positions[length - 1])
//		    + "]");

		return positions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.indexer.IndexTable#search(long)
	 */
	public long search(long position) throws IOException {
		if (position < first || position > last) {
			return -1;
		}

		;

		return Arrays.binarySearch(fetchIndexes(), position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.indexer.IndexTable#keepInMemory(long, long)
	 */
	public Object keepInMemory(long start, long length) throws IOException {
		return ((start < 0 || start > length) ? null : fetchIndexes());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.indexer.IndexTable#getLength()
	 */
	public long getLength() {
		return length;
	}
}
