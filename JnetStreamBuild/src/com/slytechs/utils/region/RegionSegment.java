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

import com.slytechs.utils.region.FlexSegmentListener.FlexSegmentSupport;

/**
 * A segment of a larger region. A region can be segmented into fragments of
 * usable elements using RegionSegments. Each segment is associated with a
 * parent region and are restricted to its bounds. Since more than 1 region can
 * exist as well, a FlexRegion maintains a list of all the regions and also
 * maintains a sequence of segments. The flex region, is a view, of all the
 * regions combined. A segment maintains 3 sets of addressing modes:
 * <ul>
 * <li>Global - global address within a FlexRegion. Each segment maintains
 * accurate global positioning, no matter how the main FlexRegion is modified.
 * </li>
 * <li>Regional - regional address within a parent region. Parent regions that
 * segments belong are fixed size and can not be resized. Therefore regional
 * addresses are persistant to changes made by FlexRegion. </li>
 * <li>Local - a local position within the segment's bounds only.
 * </ul>
 * You will find a number of getter methods that provide either global, regional
 * or local addresses. The format of the names of the methods is {checkBounds,
 * get}{Start, Last, End}{Global, Regional, Local}. Where global, regional and
 * local specify the desired address mode.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * @param <T>
 *          any user type
 */
public class RegionSegment<T> implements Segment {

	private long globalStart;

	private long length;

	private final Region<T> region;

	private long regionalStart;

	private final FlexSegmentSupport<T> support = new FlexSegmentSupport<T>(this);

	private boolean valid = true;

	public static boolean DEBUG_VERBOSE = false;

	protected RegionSegment() {
		this.region = null;
		// Empty - for ProxySegment
	}

	public RegionSegment(Changable changing, final long globalStart,
	    final long length, final T data) {
		this(new Region<T>(length, data, changing), globalStart, 0, length);
	}

	public RegionSegment(Region<T> region, final long globalStart,
	    final long regionalStart, final long length) {
		this.region = region;
		this.regionalStart = regionalStart;
		this.globalStart = globalStart;
		this.length = length;

		region.add(this);
	}

	/**
	 * Adds a new listener that will be notified of any segment changes.
	 * 
	 * @param listener
	 * @return true means addition was successful
	 */
	public boolean add(FlexSegmentListener<T> listener) {
		return this.support.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.RegionSegment#addToLength(long)
	 */
	public void addToStartGlobal(final long delta) {
		this.globalStart += delta;

		support.fireGlobalStartChange(this.globalStart - delta, this.globalStart);
	}

	/**
	 * @param replacementLength
	 */
	public void addToStartRegional(long delta) {
		this.regionalStart += delta;

		support.fireRegionalStartChange(this.regionalStart - delta,
		    this.regionalStart);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.Segment#checkBounds(long)
	 */
	public boolean checkBounds(long position) {
		return checkBoundsGlobal(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.Segment#checkBounds(long)
	 */
	public boolean checkBoundsGlobal(final long global) {
		return (global >= this.globalStart) && (global < this.getEndGlobal());
	}

	/**
	 * @param start
	 * @param oldLength
	 * @return
	 */
	public boolean checkBoundsGlobal(long global, long length) {
		if (length == 0) {
			return checkBoundsGlobal(global);

		} else {
			final long last = global + length - 1;
			return (last >= this.globalStart) && (last < this.getEndGlobal());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.RegionSegment#checkRegionalBounds(long)
	 */
	public boolean checkBoundsRegional(long regional) {
		return regional >= regionalStart && regional < (regionalStart + length);
	}

	/**
	 * Checks if the specified listener has already been added.
	 * 
	 * @param listener
	 * @return
	 */
	public boolean contains(FlexSegmentListener<T> listener) {
		return this.support.contains(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.DataSegment#getData()
	 */
	public T getData() {
		return this.region.getData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.Segment#getEnd()
	 */
	public long getEnd() {
		return getEndGlobal();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.Segment#getEnd()
	 */
	public long getEndGlobal() {
		return this.globalStart + this.length;
	}

	/**
	 * @return
	 */
	public long getEndLocal() {
		return length;
	}

	public long getEndRegional() {
		return this.regionalStart + this.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.Segment#getLast()
	 */
	public long getLast() {
		return getLastGlobal();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.Segment#getLast()
	 */
	public long getLastGlobal() {
		return this.globalStart + this.length - 1;
	}

	/**
	 * @return
	 */
	public long getLastLocal() {
		return length - 1;
	}

	public long getLastRegional() {
		return this.regionalStart + this.length - 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.Segment#getLength()
	 */
	public long getLength() {
		return this.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.RegionSegment#getRegion()
	 */
	public Region<T> getRegion() {
		return region;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.Segment#getStart()
	 */
	public long getStart() {
		return getStartGlobal();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.Segment#getStart()
	 */
	public long getStartGlobal() {
		return this.globalStart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.RegionSegment#getStartRegion()
	 */
	public long getStartRegional() {
		return regionalStart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.Segment#isEmpty()
	 */
	public boolean isEmpty() {
		return this.length == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.Validatable#isValid()
	 */
	public boolean isValid() {
		return this.valid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.RegionSegment#mapGlobalToLocal(long)
	 */
	public long mapGlobalToLocal(long global) {
		return global - this.globalStart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.RegionSegment#mapToIndex(long)
	 */
	public long mapGlobalToRegional(final long global) {
		return global - this.globalStart + this.regionalStart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.RegionSegment#mapLocalToGlobal(long)
	 */
	public long mapLocalToGlobal(long local) {
		return local + this.globalStart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.RegionSegment#mapLocalToRegional(long)
	 */
	public long mapLocalToRegional(long local) {
		return local + this.regionalStart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.RegionSegment#mapFromIndex(long)
	 */
	public long mapRegionalToGlobal(final long regional) {
		return regional - this.regionalStart + this.globalStart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.RegionSegment#mapRegionalToLocal(long)
	 */
	public long mapRegionalToLocal(long regional) {
		return regional - this.regionalStart;
	}

	/**
	 * Removes the specified listener from the list of active listeners.
	 * 
	 * @param listener
	 * @return
	 * @see java.util.ArrayList#remove(java.lang.Object)
	 */
	public boolean remove(FlexSegmentListener<T> listener) {
		return this.support.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.RegionSegment#setLength(long)
	 */
	public void setLength(final long length) {
		final long old = this.length;
		this.length = length;

		support.fireLengthChange(old, this.length);
	}

	/**
	 * @param b
	 */
	public void setValid(boolean state) {
		if (state == this.valid) {
			return; // Nothing to do
		}

		this.valid = state;

		support.fireValidChange(state);
	}

	@Override
	public String toString() {
		final Object linked = getLinkedSegment();

		if (DEBUG_VERBOSE) {
			return (linked != null ? "*" : "") + this.getData() + "["
			    + this.getStartGlobal() + "-" + this.getLastGlobal() + "/l="
			    + this.length + "/g=" + globalStart + "/r=" + regionalStart + "]"
			    + (linked != null ? linked : "");

		} else {
			return (linked != null ? "*" : "") + this.getData() + "["
			    + this.getStartRegional() + "-" + this.getLastRegional() + "r:"
			    + this.getStartGlobal() + "-" + this.getLastGlobal() + "g]"
			    + (linked != null ? "=>" + linked : "");
		}
	}

	/**
	 * 
	 */
	public Object getLinkedSegment() {
		return null;
	}

}