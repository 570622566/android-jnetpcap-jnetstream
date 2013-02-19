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
import java.util.ArrayList;
import java.util.List;

import com.slytechs.capture.file.editor.PartialLoader;
import com.slytechs.utils.region.FlexRegion;
import com.slytechs.utils.region.RegionSegment;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class RecordPositionIndexer implements PositionIndexer {

	private final FlexRegion<RegionIndexer> indexes;

	/**
	 * @param records
	 * @throws IOException
	 */
	public RecordPositionIndexer(final FlexRegion<PartialLoader> records)
	    throws IOException {

		indexes = records.linkedRegion(new PositionToIndexTranslator());
	}

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.indexer.PositionIndexer#get(long)
   */
	@SuppressWarnings("unchecked")
	public Long get(long globalIndex) throws IOException {
		final RegionSegment<RegionIndexer> tSegment = indexes
		    .getSegment(globalIndex);
		final RegionIndexer indexer = tSegment.getData();
		final long tRegional = tSegment.mapGlobalToRegional(globalIndex);

		final long sRegional = indexer.mapIndexToPositionRegional((int) tRegional);

		final RegionSegment<PartialLoader> sSegment = (RegionSegment<PartialLoader>) tSegment
		    .getLinkedSegment();

		final long global = sSegment.mapRegionalToGlobal(sRegional);

		return global;
	}

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.indexer.PositionIndexer#size()
   */
  public long size() {
	  return indexes.getLength();
  }

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.indexer.PositionIndexer#getSegmentCount()
   */
  public int getSegmentCount() {
	  return indexes.getSegmentCount();
  }

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.indexer.PositionIndexer#keepInMemory(long, long)
   */
  public Object keepInMemory(final long start, final long length) throws IOException {
  	
  	long p = start;
  	long l = length;
  	List<Object> list = new ArrayList<Object>(5);
  	
  	for (RegionSegment<RegionIndexer> segment: indexes) {
  		RegionIndexer indexer = segment.getData();
  		
  		if (segment.checkBoundsGlobal(p)) {
  			long remaining = segment.getLength() - segment.mapGlobalToLocal(p);
  			long sl = (remaining <= l ? remaining : l);
  			list.add(indexer.keepInMemory(p, sl));
  			
  			p += sl;
  			l -= sl;
  		}
  		
  		if (l == 0) {
  			break;
  		}
  	}
  	
  	return list.toArray();
  }
}
