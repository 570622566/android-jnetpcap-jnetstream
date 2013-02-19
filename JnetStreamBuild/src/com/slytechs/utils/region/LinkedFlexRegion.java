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
package com.slytechs.utils.region;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.slytechs.utils.io.IORuntimeException;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
class LinkedFlexRegion<T, S>
    extends FlexRegion<T> implements FlexRegionListener<S> {

	private final FlexRegion<S> source;

	private final RegionTranslator<T, S> translator;

	/**
	 * @param name
	 * @param translator
	 */
	public LinkedFlexRegion(FlexRegion<S> source,
	    RegionTranslator<T, S> translator) {
		super(source.isReadonly(), source.isAppend());

		this.source = source;
		this.translator = translator;

		initFromSource(source, translator);

		source.add(this); // Add us as a listener
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.FlexRegion#createSegment(com.slytechs.utils.region.Changable,
	 *      long, long, java.lang.Object)
	 */
	@Override
	protected RegionSegment<T> createSegment(Changable changable, long global,
	    long length, T data) {
		return new ProxyRegionSegment<T>(super.createSegment(changable, global,
		    length, data));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.FlexRegion#createSegment(long, long,
	 *      java.lang.Object)
	 */
	@Override
	protected RegionSegment<T> createSegment(long global, long length, T data) {
		return new ProxyRegionSegment<T>(super.createSegment(global, length, data));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.FlexRegion#createSegment(com.slytechs.utils.region.Region,
	 *      long, long, long)
	 */
	@Override
	protected RegionSegment<T> createSegment(Region<T> region, long global,
	    long regional, long length) {
		return new ProxyRegionSegment<T>(super.createSegment(region, global,
		    regional, length));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.FlexRegionListener#eventAbortAllChanges(com.slytechs.utils.region.FlexRegion)
	 */
	public Object eventAbortAllChanges(FlexRegion<S> source) {
		return super.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.FlexRegionListener#eventAppend(com.slytechs.utils.region.FlexRegion,
	 *      long, java.lang.Object)
	 */
	public Object eventAppend(final FlexRegion<S> source, final long length,
	    final S data) {
		this.throwIfNotSource(source);

		final T tData = this.translator.data(this, data);

		final long tLength = this.translator.newLength(this, length, tData);

		RegionSegment[] newSegment = super.append(tLength, tData);

		return newSegment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.FlexRegionListener#eventFlatten(com.slytechs.utils.region.FlexRegion,
	 *      java.lang.Object)
	 */
	public Object eventFlatten(FlexRegion<S> source, S data) {
		this.throwIfNotSource(source);

		final T tData = this.translator.flatten(this, source, data);
		final long tLength = this.translator.getTDataLength(tData);

		RegionSegment[] newSegment = super.flatten(tData, tLength);

		return newSegment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.FlexRegionListener#eventReplace(com.slytechs.utils.region.FlexRegion,
	 *      long, long, long, java.lang.Object)
	 */
	public Object eventReplace(final FlexRegion<S> sRegion, final long sStart,
	    final long sOldLength, final long sNewLength, S sData) {

		this.throwIfNotSource(sRegion);

		// System.out.printf("--- replace(start=%d, old=%d, new=%d, data=%s) ---\n",
		// sStart, sOldLength, sNewLength, data);
		// System.out.println("S=" + source.toString());
		// System.out.println("T=" + this.toString());

		final T tData = (sData == null ? null : this.translator.data(this, sData));

		final long tStart = this.translator.start(this, sStart);

		final long tNewLength = (sNewLength == 0 ? 0 : this.translator.newLength(
		    this, sNewLength, tData));

		long tOldLength;
		try {
			tOldLength = (sOldLength == 0 ? 0 : this.translator.oldLength(this,
			    tStart, tData, sRegion, sStart, sOldLength, sData));
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}

		RegionSegment[] newSegment = super.replace(tStart, tOldLength, tNewLength,
		    tData);

		return newSegment;
	}

	private List<Region<S>> extractRegionsFromSegments(
	    List<RegionSegment<S>> segments) {

		final List<Region<S>> regions = new ArrayList<Region<S>>();
		for (final RegionSegment<S> segment : segments) {
			if (regions.contains(segment.getRegion()) == false) {
				regions.add(segment.getRegion());
			}
		}

		return regions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		this.source.remove(this); // Unregister as a listener
	}

	private void initFromSource(FlexRegion<S> source,
	    RegionTranslator<T, S> translator) {

		/*
		 * 1st pull out all the regions
		 */
		final List<Region<S>> regions = extractRegionsFromSegments(source.segments);

		/*
		 * 2nd translate source region "data" into target "data" At the same time
		 * find "initialRegion" to reutilize the same loop
		 */
		T initRegTranslation = null;
		final Map<Region<S>, Region<T>> r2r = new HashMap<Region<S>, Region<T>>();
		for (final Region<S> region : regions) {
			final S sData = region.getData();
			final T tData = translator.data(this, sData);
			final long tLength = translator.getTDataLength(tData);

			final Region<T> tRegion = new Region<T>(tLength, tData, this);

			r2r.put(region, tRegion);

			if (region == source.initialRegion) {
				initRegTranslation = tData;
			}
		}

		/*
		 * 3rd setup initial region
		 */
		long tl = translator.getTDataLength(initRegTranslation);
		final RegionSegment<T> initSeg = new RegionSegment<T>(this, 0, tl,
		    initRegTranslation);
		super.initialRegion = initSeg.getRegion();

		long global = 0;

		/*
		 * 4th translate segments
		 */
		for (final RegionSegment<S> segment : source.segments) {
			final Region<T> tRegion = r2r.get(segment.getRegion());
			final T tData = tRegion.getData();
			final long tLength = translator.getLength(tData, segment
			    .getStartRegional(), segment.getLength());
			final long tRegional = translator.mapSRegionalToTRegional(tData, segment
			    .getStartRegional());

			final RegionSegment<T> tSegment = new LinkedRegionSegment<T, S>(tRegion,
			    global, tRegional, tLength, segment);

			super.segments.add(tSegment);

			global += tLength;
		}

		/*
		 * 5th set the global length
		 */
		super.length = global;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.FlexRegionListener#linkSegment(com.slytechs.utils.region.FlexRegion,
	 *      com.slytechs.utils.region.RegionSegment, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object linkSegment(FlexRegion<S> source, RegionSegment<S>[] segments,
	    Object state) {

		throwIfNotSource(source);

		if (state == null || segments == null) {
			return null;
		}

		final RegionSegment<T>[] proxies = (RegionSegment<T>[]) state;

		for (int i = 0; i < segments.length; i++) {
			final ProxyRegionSegment<T> proxy = (ProxyRegionSegment<T>) proxies[i];
			if (proxy == null) {
				/*
				 * Proxy is null for remove type records where new length == 0, another
				 * words no segment
				 */
				continue;

			}

			final RegionSegment<T> temp = proxy.getDelagate();
			final RegionSegment<T> linked = new LinkedRegionSegment<T, S>(temp,
			    segments[i]);

			proxy.setDelagate(linked);
		}

		return null;
	}

	/**
	 * @param source2
	 */
	private final void throwIfNotSource(final FlexRegion<S> source) {
		if (this.source != source) {
			throw new IllegalStateException(
			    "Unknown region source. Linked region source binding error.");
		}
	}

}
