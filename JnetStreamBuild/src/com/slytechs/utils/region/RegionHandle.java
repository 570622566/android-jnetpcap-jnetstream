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

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class RegionHandle<T> {

	private Region<T> region;

	private long regional;

	private RegionSegment<T> cachedSegment = null;

	private Changable changing;

	private long changeId;

	/**
	 * @param segment
	 */
	private RegionHandle(final Region<T> segment, final long regional) {
		this.region = segment;
		this.regional = regional;
	}

	/**
	 * @param s
	 * @param changing
	 *          TODO
	 * @param l
	 */
	public RegionHandle(final RegionSegment<T> s, final long regional,
	    Changable changing) {
		this(s.getRegion(), regional);
		this.changing = changing;
		this.changeId = changing.getChangeId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.OldRegionHandle#getData()
	 */
	public T getData() {
		/*
		 * Remember the change ID since we are aquiring fresh data which will
		 * dereference any region forwards and reflect any buffer substitutions.
		 */
		changeId = changing.getChangeId();

		return getRegion().getSegment(regional).getData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.Validatable#isValid()
	 */
	public boolean isValid() {
		if (cachedSegment != null && region.getForward() == null
		    && cachedSegment.checkBoundsRegional(regional)) {
			return true;

		}

		try {
			final RegionSegment<T> s = getSegment();

			return s != null;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.FlexiblePosition#getCurrentPosition()
	 */
	public long getPositionGlobal() {
		return getSegment().mapRegionalToGlobal(regional);
	}

	public long getPositionRegional() {
		return regional;
	}

	public long getPositionLocal() {
		return getSegment().mapRegionalToLocal(regional);
	}

	public String toString() {
		return "[" + getPositionGlobal() + ", " + getData().toString() + "]";
	}

	private Region<T> getRegion() {
		final Region<T> forward = region.getForward();

		if (forward != null) {

			final RegionSegment<T> s = region.getSegment(regional);
			if (s == null) {
				throw new IndexOutOfBoundsException(
				    "The position within the region at index [" + regional
				        + "] is not loger valid. "
				        + "Please use check isValid to avoid this exception");
			}

			/*
			 * Our old global position is the new regional position of the forwarded
			 * segment within the new region
			 */
			this.regional = s.mapRegionalToGlobal(regional);
			this.region = forward;

			return getRegion();
		}

		return region;
	}

	private RegionSegment<T> getSegment() {
		if (cachedSegment != null && region.getForward() == null
		    && cachedSegment.checkBoundsRegional(regional)) {
			return cachedSegment;
		}

		final Region<T> region = getRegion();
		this.cachedSegment = region.getSegment(regional);

		return cachedSegment;
	}

	/**
	 * @return
	 */
	public long getRegionalPosition() {
		return regional;
	}

	/**
	 * @return
	 */
	public final boolean isChanged() {
		return changing.isChanged(changeId);
	}
}
