package com.slytechs.capture.file.indexer;

import java.io.IOException;

import com.slytechs.capture.file.editor.PartialLoader;
import com.slytechs.utils.io.IORuntimeException;
import com.slytechs.utils.region.FlexRegion;
import com.slytechs.utils.region.RegionSegment;
import com.slytechs.utils.region.RegionTranslator;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public final class PositionToIndexTranslator implements
    RegionTranslator<RegionIndexer, PartialLoader> {

	@SuppressWarnings("unchecked")
	public static Long get(FlexRegion<RegionIndexer> indexes, long globalIndex)
	    throws IOException {

		final RegionSegment<RegionIndexer> tSegment = indexes.getSegment(globalIndex);
		final RegionIndexer indexer = tSegment.getData();
		final long tRegional = tSegment.mapGlobalToRegional(globalIndex);

		final long sRegional = indexer.mapIndexToPositionRegional((int) tRegional);

		final RegionSegment<PartialLoader> sSegment = (RegionSegment<PartialLoader>) tSegment
		    .getLinkedSegment();

		final long global = sSegment.mapRegionalToGlobal(sRegional);

		return global;
	}

	/**
	 */
	public PositionToIndexTranslator() {
	}

	public RegionIndexer data(FlexRegion<RegionIndexer> target, PartialLoader source) {

		RegionIndexer indexer;
		try {
			indexer = new SoftRegionIndexer(source);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}

		return indexer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.RegionTranslator#flatten(com.slytechs.utils.region.FlexRegion,
	 *      com.slytechs.utils.region.FlexRegion, java.lang.Object)
	 */
	public RegionIndexer flatten(FlexRegion<RegionIndexer> target,
	    FlexRegion<PartialLoader> source, PartialLoader sData) {

		/*
		 * The current indexes are correct for the flattened state, therefore we're
		 * not going to rebuild the index table by scanning the source, simply
		 * create 1 large index region and copy the indexes as global indexes into
		 * this single large index region.
		 */

		RegionIndexer init;
    try {
	    init = new SoftRegionIndexer(target, target.getLength(), sData);
    } catch (IOException e) {
	    throw new IORuntimeException(e);
    }

		return init;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.RegionTranslator#getLength(java.lang.Object,
	 *      long, long)
	 */
	public long getLength(RegionIndexer data, long sStart, long sLength) {
		final long si = data.mapSRegionalToTRegional(sStart);
		final long ei = data.mapSRegionalToTRegional(sStart + sLength);

		return (ei < 0 ? (data.getLength() - si) : (ei - si));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.RegionTranslator#getLength(java.lang.Object)
	 */
	public long getTDataLength(RegionIndexer tData) {
		return tData.getLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.RegionTranslator#mapSRegionalToTRegional(long)
	 */
	public long mapSRegionalToTRegional(RegionIndexer data, long regionalPosition) {
		return data.mapSRegionalToTRegional(regionalPosition);
	}

	public long newLength(FlexRegion<RegionIndexer> target, long length, RegionIndexer indexer) {
		return indexer.getLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.RegionTranslator#oldLength(com.slytechs.utils.region.FlexRegion,
	 *      long, java.lang.Object, com.slytechs.utils.region.FlexRegion, long,
	 *      long, java.lang.Object)
	 */
	public long oldLength(FlexRegion<RegionIndexer> tRegion, long tStart, RegionIndexer tData,
	    FlexRegion<PartialLoader> sRegion, long sStart, long sLength,
	    PartialLoader sData) throws IOException {

		final long sEnd = sStart + sLength;

		for (long i = tStart; i < tRegion.getLength(); i++) {
			if (get(tRegion, i) == sEnd) {
				return i - tStart;
			}
		}

		return tRegion.getLength() - tStart;
	}

	@SuppressWarnings("unchecked")
	public long start(FlexRegion<RegionIndexer> target, long sGlobal) {

		for (RegionSegment<RegionIndexer> tSegment : target.getSegmentIterable()) {
			final RegionSegment<PartialLoader> sSegment = (RegionSegment<PartialLoader>) tSegment
			    .getLinkedSegment();

			if (sSegment.checkBoundsGlobal(sGlobal)) {
				final RegionIndexer indexer = tSegment.getData();
				final long tStart = tSegment.getStartRegional();
				final long tEnd = tSegment.getEndRegional();
				final long sRegional = sSegment.mapGlobalToRegional(sGlobal);

				for (long tRegional = tStart; tRegional < tEnd; tRegional++) {
					final long mapped = indexer
					    .mapIndexToPositionRegional((int) tRegional);

					if (sRegional == mapped) {
						final long tGlobal = tSegment.mapRegionalToGlobal(tRegional);
						return tGlobal;
					}
				}
			}
		}

		throw new IllegalArgumentException(
		    "Unable to map position from source region to target region ["
		        + sGlobal + "]");
	}
}