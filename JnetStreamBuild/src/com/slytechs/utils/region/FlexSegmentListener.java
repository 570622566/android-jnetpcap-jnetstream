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

import java.util.ArrayList;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface FlexSegmentListener<T> {

	static class FlexSegmentSupport<T>
	    extends ArrayList<FlexSegmentListener<T>> {
		private static final long serialVersionUID = 6285233278387980678L;

		private final RegionSegment<T> source;

		/**
		 * @param source
		 */
		public FlexSegmentSupport(final RegionSegment<T> source) {
			super(2); // Initilize the ArrayList with just 2 elements

			this.source = source;
		}

		/**
		 * @param oldLength
		 * @param newLength
		 */
		public void fireLengthChange(long oldLength, long newLength) {
			for (final FlexSegmentListener<T> l : this) {
				l.changeSegmentLength(source, oldLength, newLength);
			}

		}

		/**
     * @param l
     * @param regionalStart
     */
    public void fireRegionalStartChange(long oldStart, long newStart) {
			for (final FlexSegmentListener<T> l : this) {
				l.changeSegmentRegionalStart(source, oldStart, newStart);
			}
    }

		/**
     * @param l
     * @param globalStart
     */
    public void fireGlobalStartChange(long oldStart, long newStart) {
			for (final FlexSegmentListener<T> l : this) {
				l.changeSegmentGlobalStart(source, oldStart, newStart);
			}
    }

		/**
     * @param b
		 * @param state
     */
    public void fireValidChange(boolean newState) {
			for (final FlexSegmentListener<T> l : this) {
				l.changeSegmentValid(source, newState);
			}
    }
	}

	/**
	 * 
	 * @param source
	 * @param oldLength
	 * @param newLength
	 */
	public void changeSegmentLength(RegionSegment<T> source, long oldLength,
	    long newLength);

	/**
	 * 
	 * @param source
	 * @param oldStart
	 * @param newStart
	 */
	public void changeSegmentRegionalStart(RegionSegment<T> source, long oldStart,
	    long newStart);
	
	/**
	 * 
	 * @param source
	 * @param oldStart
	 * @param newStart
	 */
	public void changeSegmentGlobalStart(RegionSegment<T> source, long oldStart,
	    long newStart);

	public void changeSegmentValid(RegionSegment<T> source, boolean newState);

}
