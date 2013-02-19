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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jnetstream.capture.file.HeaderReader;

import com.slytechs.capture.file.editor.BasicRecordIterator;
import com.slytechs.capture.file.editor.PartialLoader;
import com.slytechs.utils.Size;
import com.slytechs.utils.event.ProgressTask;
import com.slytechs.utils.event.SuperProgressTask;
import com.slytechs.utils.io.IORuntimeException;
import com.slytechs.utils.region.FlexRegion;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class SoftRegionIndexer implements RegionIndexer {
	private static final Log logger = LogFactory.getLog(SoftRegionIndexer.class);

	private static final int LARGE_FILE_SAMPLE = 3000;

	private static final long LARGE_FILE_THRESHOLD = 10 * 1024 * 1024;

	private static final long MAX_HARD_RECORDS = 10000;

	private static final int MIN_FACTOR_SIZE = 500;

	private static final int SMALL_FILE_SAMPE = 300;

	private int factor = 0;

	private int length = 0;

	private final HeaderReader lengthGetter;

	private SuperProgressTask task;

	/*
	 * TODO add implementation of partial region indexer
	 */
	@SuppressWarnings("unused")
	private final PartialLoader loader;

	private IndexTable[] table;

	private ProgressTask scanTask;

	/**
	 * @param data
	 * @param name
	 * @throws IOException
	 */
	public SoftRegionIndexer(final FlexRegion<RegionIndexer> region,
	    final long length, final PartialLoader sData) throws IOException {

		this(sData);
		/*
		 * Just in case we have use soft indexes which will require rescanning using
		 * the partial loader
		 */
		// this.loader = sData;
		// this.lengthGetter = sData.getLengthGetter();
		//
		// RegionSegment<RegionIndexer> segment = region.getSegment(0);
		// RegionSegment linked = (RegionSegment) segment.getLinkedSegment();
		// RegionIndexer indexer = segment.getData();
		//
		// for (int i = 0; i < length; i++) {
		//
		// if (segment.checkBoundsGlobal(i) == false) {
		// segment = region.getSegment(i);
		// linked = (RegionSegment) segment.getLinkedSegment();
		// indexer = segment.getData();
		// }
		//
		// final long regional = segment.mapGlobalToRegional(i);
		//
		// final long position = linked.mapRegionalToGlobal(indexer
		// .mapIndexToPositionRegional((int) regional));
		//
		// }
	}

	/**
	 * Indexes the entire unsegmented region. The region can not be segmented
	 * 
	 * @param target
	 * @throws IOException
	 */
	public SoftRegionIndexer(final PartialLoader loader) throws IOException {

		task = new SuperProgressTask("indexer");
		scanTask = task.addTask("scan file for records", loader.getLength());

		this.loader = loader;

		this.calculateFactor(loader);

		if (loader != null) {
			this.lengthGetter = loader.getLengthGetter();
			this.table = this.createIndexTableFromLoader(loader, this.factor);

			logger.trace("table=%d" + this.table.length);
		} else {
			this.lengthGetter = null;
		}

	}

	private void calculateFactor(final PartialLoader loader) throws IOException {
		/*
		 * Calculate the soft index factor which determines how many soft indexes we
		 * keep per every hard index. For very large files take a sample of packets
		 * and calculate average size. For smaller files simply assume the minimum
		 * of 30 bytes per packet as average.
		 */
		final int ave =
		    (loader.getLength() > SoftRegionIndexer.LARGE_FILE_THRESHOLD ? this
		        .takeAverageSample(loader, SoftRegionIndexer.LARGE_FILE_SAMPLE)
		        : this
		            .takeAverageSample(loader, SoftRegionIndexer.SMALL_FILE_SAMPE));

		final long estPacketCount = loader.getLength() / ave;
		this.factor = (int) (estPacketCount / SoftRegionIndexer.MAX_HARD_RECORDS);

		/*
		 * Make sure we use a reasonable minimum to be efficient
		 */
		if ((this.factor != 0) && (this.factor < SoftRegionIndexer.MIN_FACTOR_SIZE)) {
			this.factor = SoftRegionIndexer.MIN_FACTOR_SIZE;
		}

		logger.trace("size=" + loader.getLength() + ", ave=" + ave + ", est="
		    + estPacketCount + ", factor=" + this.factor);
	}

	/**
	 * Method will iterate over the entire region full of records and index each
	 * record's position in an array. Record positions in regional address space
	 * are always constant, therefore once the region is indexed once, we don't
	 * have to worry about positions any more.
	 * 
	 * @param loader
	 * @param factor
	 *          TODO
	 * @return
	 * @throws IOException
	 */
	private IndexTable[] createIndexTableFromLoader(final PartialLoader loader,
	    final int factor) throws IOException {

		final BasicRecordIterator iterator =
		    new BasicRecordIterator(loader, this.lengthGetter);
		final int capacity = (factor == 0 ? 10000 : factor);

		final List<Long> temp = new ArrayList<Long>(capacity); // Rough estimate
		final List<IndexTable> it = new ArrayList<IndexTable>(100);

		long next = 0;
		long previous = 0;
		while (iterator.hasNext()) {
			final long regional = iterator.getPosition();
			temp.add(regional);
			iterator.skip();

			this.length++;
			if ((factor != 0) && (this.length % factor == 0)) {
				it.add(new SoftTable(temp, loader));
				temp.clear();
			}
			
			if (regional > next) {
				scanTask.update(regional - previous);
				next = regional + 200 * Size.OneMeg.bytes();
				
				previous = regional;
			}
		}
		
		scanTask.finish();

		if (temp.isEmpty() == false) {

			if (factor > 2) {
				it.add(new HardTable(temp));

			} else {
				it.add(new SoftTable(temp, loader));
			}
		}

		/*
		 * Now lets turn the list into more efficient array since regions are not
		 * mutable we can do that.
		 */
		final IndexTable[] array = it.toArray(new IndexTable[it.size()]);

		return array;
	}

	/**
	 * @return
	 */
	public long getLength() {
		return this.length;
	}

	public long mapIndexToPositionRegional(final int regional) {
		if ((regional < 0) || (regional >= this.length)) {
			throw new IndexOutOfBoundsException("Regional index [" + regional
			    + "] is out of bounds [0 - " + (this.length - 1) + "].");
		}

		final int it = (this.factor == 0 ? 0 : regional / this.factor);
		final int index = (this.factor == 0 ? regional : regional % this.factor);

		try {
			final long position = this.table[it].get(index);

			return position;

		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * @param start
	 * @return
	 */
	public long mapSRegionalToTRegional(final long sRegional) {
		long r = -1;

		try {
			for (int i = 0; i < this.table.length; i++) {
				if ((r = this.table[i].search(sRegional)) != -1) {
					r += i * this.factor;
				}
			}
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}

		return r;
	}

	/**
	 * Takes a certain sample packet average by reading the first max number of
	 * packets.
	 * 
	 * @param loader
	 * @param max
	 * @return
	 * @throws IOException
	 */
	private int takeAverageSample(final PartialLoader loader, final int max)
	    throws IOException {
		final BasicRecordIterator iterator =
		    new BasicRecordIterator(loader, loader.getLengthGetter());

		int count = 0;
		int total = 0;
		while (iterator.hasNext() && (count < max)) {
			final ByteBuffer b = iterator.next();
			total += loader.getLengthGetter().readLength(b);

			count++;
		}


		final int ave = total / count;

		return ave;
	}

	@Override
	public String toString() {
		final StringBuilder b = new StringBuilder("Idx");

		return b.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.indexer.RegionIndexer#keepInMemory(long,
	 *      long)
	 */
	public Object keepInMemory(long start, long count) throws IOException {
		count = (start + count <= this.length ? count : this.length - start);
		
		task = new SuperProgressTask("indexer");

		final int first = (this.factor == 0 ? 0 : (int) start / this.factor);
		final int end =
		    (this.factor == 0 ? 1 : (int) (start + count) / this.factor) + 1;

		scanTask = task.addTask("locking indexes into memory", end - start);

		final List<Object> list = new ArrayList<Object>(10);

		for (int i = first; i < end; i++) {
			list.add(table[i].keepInMemory(0, 1));
			
			if (i % 1000 == 0) {
				scanTask.update(1000);
			}
		}

		scanTask.finish();
		
		return list.toArray();
	}

}
