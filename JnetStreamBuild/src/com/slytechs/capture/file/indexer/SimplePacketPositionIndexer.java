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

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class SimplePacketPositionIndexer implements PacketPositionIndexer {

	private final PositionIndexer recordIndexer;

	/**
	 * @param records
	 * @throws IOException
	 */
	public SimplePacketPositionIndexer(final PositionIndexer recordIndexer) {
		this.recordIndexer = recordIndexer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.indexer.RecordPositionIndexer#get(long)
	 */
	public Long get(long globalIndex) throws IOException {
		return recordIndexer.get(globalIndex + 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.indexer.RecordPositionIndexer#size()
	 */
	public long size() {
		return recordIndexer.size() - 1;
	}

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.indexer.PositionIndexer#getSegmentCount()
   */
  public int getSegmentCount() {
	  return recordIndexer.getSegmentCount();
  }

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.indexer.PositionIndexer#keepInMemory(long, long)
   */
  public Object keepInMemory(long start, long length) throws IOException {
	  return recordIndexer.keepInMemory(start, length);
  }

}
