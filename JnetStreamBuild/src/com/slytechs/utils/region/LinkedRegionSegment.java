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

class LinkedRegionSegment<T, S>
    extends RegionSegment<T> {

	protected final RegionSegment<S> source;

	/**
	 * @param region
	 * @param globalStart
	 * @param regionalStart
	 * @param length
	 */
	public LinkedRegionSegment(Region<T> region, long globalStart,
	    long regionalStart, long length, RegionSegment<S> source) {
		super(region, globalStart, regionalStart, length);
		this.source = source;
	}

	/**
	 * @param temp
	 * @param segment
	 */
	public LinkedRegionSegment(RegionSegment<T> temp, RegionSegment<S> source) {
		this(temp.getRegion(), temp.getStartGlobal(), temp.getStartRegional(), temp
		    .getLength(), source);

	}

	/* (non-Javadoc)
   * @see com.slytechs.utils.region.RegionSegment#getLinkedSegment()
   */
  @Override
  public Object getLinkedSegment() {
	  return source;
  }

}