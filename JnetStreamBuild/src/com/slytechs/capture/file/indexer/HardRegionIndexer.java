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
import java.util.Arrays;
import java.util.List;

import org.jnetstream.capture.file.HeaderReader;


import com.slytechs.capture.file.editor.BasicRecordIterator;
import com.slytechs.capture.file.editor.PartialLoader;
import com.slytechs.utils.io.IORuntimeException;
import com.slytechs.utils.region.FlexRegion;
import com.slytechs.utils.region.RegionSegment;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class HardRegionIndexer implements RegionIndexer {

	private final Long[] positions;

	private final HeaderReader lengthGetter;

	/*
	 * TODO add implementation of partial region indexer
	 */
	@SuppressWarnings("unused")
	private final PartialLoader loader;

	private BasicRecordIterator iterator;

	/**
	 * Indexes the entire unsegmented region. The region can not be segmented
	 * 
	 * @param target
	 * @throws IOException
	 */
	public HardRegionIndexer(PartialLoader loader) throws IOException {

		this.loader = loader;

		if (loader != null) {
			this.lengthGetter = loader.getLengthGetter();
			this.positions = createIndexTableFromLoader(loader, 0L);
		} else {
			this.lengthGetter = null;
			this.positions = new Long[0];
		}

	}

	/**
	 * @param data
	 * @param name
	 */
	public HardRegionIndexer(FlexRegion<HardRegionIndexer> region, long length,
	    PartialLoader sData) {
		/*
		 * Just in case we have use soft indexes which will require rescanning using
		 * the partial loader
		 */
		this.loader = sData;
		this.lengthGetter = sData.getLengthGetter();

		/*
		 * For testing purposes. Leave this false as its more efficient to build the
		 * new index region from previous FlexRegion instead of IO limitted partial
		 * loader scanning. But both methods should work equally well and produce
		 * the same result. So for sanity checking try both.
		 */
		if (false) {
			try {
				/*
				 * Note - MWB: this works most of the time. Sometimes it fails. This is
				 * due to the fact that we typically are called here after a FileCapture
				 * flush and it seems that sometimes the flushByCopy hasn't been fully
				 * written to the physical file. Atleast sometimes its not fast enough.
				 * Then we start parsing a file that is partially incomplete still in
				 * the process of being written. Yikes!!!
				 */

				/*
				 * Update - MWB: PartialFileLoader now uses Channel.force(true)
				 * everytime it fetches a block. This ensures that channel is synched
				 * with the file before we read it. No more failures - YEAH!!!!
				 */
				this.positions = createIndexTableFromLoader(loader, 0L);
				if (positions.length != length) {
					throw new IllegalStateException("Lengths don't match: "
					    + positions.length + " != " + length);
				}
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}

			return;
		}

		this.positions = new Long[(int) length];

		RegionSegment<HardRegionIndexer> segment = region.getSegment(0);
		RegionSegment linked = (RegionSegment) segment.getLinkedSegment();
		RegionIndexer indexer = segment.getData();

		for (int i = 0; i < length; i++) {

			if (segment.checkBoundsGlobal(i) == false) {
				segment = region.getSegment(i);
				linked = (RegionSegment) segment.getLinkedSegment();
				indexer = segment.getData();
			}

			long regional = segment.mapGlobalToRegional(i);

			long position = linked.mapRegionalToGlobal(indexer
			    .mapIndexToPositionRegional((int) regional));

			positions[i] = position;
		}
	}

	/**
	 * Method will iterate over the entire region full of records and index each
	 * record's position in an array. Record positions in regional address space
	 * are always constant, therefore once the region is indexed once, we don't
	 * have to worry about positions any more.
	 * 
	 * @param loader
	 * @return
	 * @throws IOException
	 */
	private Long[] createIndexTableFromLoader(PartialLoader loader, long offset)
	    throws IOException {

		final long length = loader.getLength();
		iterator = new BasicRecordIterator(loader, lengthGetter);
		final int capacity = (int) (length / 32) + 1; // Estimate capacity needed

		// TODO temporary restriction to 1000000 record indexes
		if (capacity > 1000000) {
			throw new UnsupportedOperationException(
			    "The indexer currently can not index regions "
			        + "with than 1,000,000 packets");
		}

		final List<Long> temp = new ArrayList<Long>(capacity); // Rough estimate

		while (iterator.hasNext()) {
			final long regional = iterator.getPosition();
			temp.add(regional);
			iterator.skip();
		}

		/*
		 * Now lets turn the list into more efficient array since regions are not
		 * mutable we can do that.
		 */
		final Long[] array = temp.toArray(new Long[temp.size()]);

		return array;
	}

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.indexer.RegionIndexer#mapIndexToPositionRegional(int)
   */
	public long mapIndexToPositionRegional(int regional) {
		if (regional < 0 || regional >= positions.length) {
			throw new IndexOutOfBoundsException("Regional index [" + regional
			    + "] is out of bounds [0 - " + (positions.length - 1) + "].");
		}

		return positions[regional];
	}

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.indexer.RegionIndexer#getLength()
   */
	public long getLength() {
		return positions.length;
	}

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.indexer.RegionIndexer#mapSRegionalToTRegional(long)
   */
	public long mapSRegionalToTRegional(long sRegional) {

		// final int i = Arrays.binarySearch(positions, (sRegional < offset ? offset
		// : sRegional));
		final int i = Arrays.binarySearch(positions, sRegional);

		return i;
	}

	public String toString() {
		final StringBuilder b = new StringBuilder("Idx");

		return b.toString();
	}

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.indexer.RegionIndexer#keepInMemory(long, long)
   */
  public Object keepInMemory(long p, long l) {
	  return positions;
  }

}
