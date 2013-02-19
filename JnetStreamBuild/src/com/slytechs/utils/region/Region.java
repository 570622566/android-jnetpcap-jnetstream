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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class Region<T> {
	private LinkedList<RegionSegment<T>> segments = new LinkedList<RegionSegment<T>>();

	private final long length;

	private Region<T> forward = null;

	private final T data;

	private final Changable changling;

	/**
	 * @param length
	 */
	public Region(final long length, T data, Changable changling) {
		this.length = length;
		this.data = data;
		this.changling = changling;
	}

	public boolean checkRegionalBounds(long regional) {

		for (RegionSegment<T> s : segments) {
			if (s.checkBoundsRegional(regional)) {
				return true;
			}
		}

		return false;
	}

	public boolean add(RegionSegment<T> s) {
		return segments.add(s);
	}

	public boolean remove(RegionSegment<T> s) {
		return segments.remove(s);
	}

	public final Region<T> getForward() {
		return this.forward;
	}

	public final void setForward(Region<T> forward) {
		this.forward = forward;
	}

	public final RegionSegment<T> getSegment(long regional) {
		if (segments.size() == 1) {
			final RegionSegment<T> first = segments.getFirst();
			if (first.checkBoundsRegional(regional) == false) {
				throw new IndexOutOfBoundsException("Regional position [" + regional
				    + "] is out of bounds for region " + first.toString());
			}

			return first;

		} else if (segments.isEmpty()) {
			throw new IndexOutOfBoundsException(
			    "Can not locate segment for this position [" + regional + "]");
		}

		for (RegionSegment<T> s : segments) {
			if (s.checkBoundsRegional(regional)) {
				return s;
			}
		}

		throw new IndexOutOfBoundsException(
		    "Can not locate segment for this position [" + regional + "]");
	}

	public final long getLength() {
		return this.length;
	}

	/**
	 * @return
	 */
	public T getData() {
		return this.data;
	}

	boolean isChanged(long changeId) {
		return changling.isChanged(changeId);
	}

	/**
   * @return
   */
  public List<RegionSegment> getSegments() {
	  return Collections.<RegionSegment>unmodifiableList(segments);
  }
  
  public long mapRegionalToGlobal(long regional) {
  	final RegionSegment<T> segment = getSegment(regional);
  	
  	return segment.mapRegionalToGlobal(regional);
  }
}
