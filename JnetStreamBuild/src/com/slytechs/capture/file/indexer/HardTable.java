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
import java.util.Arrays;
import java.util.List;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class HardTable implements IndexTable {

	private final int length;

	private final long[] positions;

	/**
	 * @param length
	 */
	public HardTable(final List<Long> list) {
		this.length = list.size();

		positions = new long[length];
		for (int i = 0; i < list.size(); i++) {
			positions[i] = list.get(i);
		}
	}

	public long get(int index) throws IOException {
		return positions[index];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.indexer.IndexTable#search(long)
	 */
	public long search(long regional) {
		if (regional < positions[0] || regional > positions[positions.length - 1]) {
			return -1;
		}

		return Arrays.binarySearch(positions, regional);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.indexer.IndexTable#keepInMemory(long, long)
	 */
	public Object keepInMemory(long start, long length) {
		return ((start < positions[0] || start > positions[this.length - 1]) ? null
		    : positions);

	}

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.indexer.IndexTable#getLength()
   */
  public long getLength() {
	  return length;
  }

}
