/**
 * Copyright (C) 2007 Sly Technologies, Inc.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.slytechs.utils.region;

import java.util.Comparator;

public class Range {
	/**
	 * Accessible for reusable passage as params.
	 */
	public static Range temp1 = new Range(0, 0);

	/**
	 * Accessible for reusable passage as params.
	 */
	public static Range temp2 = new Range(0, 0);

	public long start;

	public long length;

	public Range(long start, long length) {
		this.start = start;
		this.length = length;
	}

	public final long start() {
		return start;
	}

	public final long end() {
		return start + length;
	}

	public final long length() {
		return length;
	}

	public static class RangeComparator implements Comparator<Range> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Range o1, Range o2) {

			/*
			 * o2 start is within o1 region then treat them as equal
			 */
			if (o1.start >= o2.start && o1.start <= o2.end()) {
				return 0;
			}

			if ((o1.start - o2.start) < 0) {
				return -1;
			} else if ((o1.start - o2.start) > 0) {
				return 1;
			} else {
				return (int) (o1.length - o2.length);
			}
		}

	}

	public String toString() {
		return "[" + start + "/" + length + "]";
	}
}