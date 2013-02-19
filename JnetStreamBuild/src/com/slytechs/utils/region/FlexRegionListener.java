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

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface FlexRegionListener<T> {

	static class FlexRegiontSupport<T>
	    extends ArrayList<SoftReference<FlexRegionListener<T>>> {
		private static final long serialVersionUID = 6285233278387980679L;

		private final FlexRegion<T> source;

		private final Map<FlexRegionListener<T>, Object> linkState = new HashMap<FlexRegionListener<T>, Object>();

		public FlexRegiontSupport(FlexRegion<T> source) {
			super(2); // Initialize ArrayList with just 2 elements

			this.source = source;
		}

		public void fireReplace(long start, long oldLength, long newLength, T data) {
			for (final SoftReference<FlexRegionListener<T>> f : this) {

				final FlexRegionListener<T> l = f.get();
				if (l == null) {
					continue;
				}

				final Object state = l.eventReplace(source, start, oldLength,
				    newLength, data);

				linkState.put(l, state);
			}
		}

		public void fireAppend(long length, T data) {
			for (final SoftReference<FlexRegionListener<T>> f : this) {

				final FlexRegionListener<T> l = f.get();
				if (l == null) {
					continue;
				}

				final Object state = l.eventAppend(source, length, data);

				linkState.put(l, state);
			}
		}
		
		public void fireFlatten(T data) {
			for (final SoftReference<FlexRegionListener<T>> f : this) {

				final FlexRegionListener<T> l = f.get();
				if (l == null) {
					continue;
				}

				final Object state = l.eventFlatten(source, data);

				linkState.put(l, state);
			}
		}
		
		public void fireAbortAllChanges() {
			for (final SoftReference<FlexRegionListener<T>> f : this) {

				final FlexRegionListener<T> l = f.get();
				if (l == null) {
					continue;
				}

				final Object state = l.eventAbortAllChanges(source);

				linkState.put(l, state);
			}
		}




		@SuppressWarnings("unchecked")
		public void fireLinkSegment(RegionSegment[] newSegment) {
			for (final SoftReference<FlexRegionListener<T>> f : this) {

				final FlexRegionListener<T> l = f.get();
				if (l == null) {
					continue;
				}

				final Object state = linkState.get(l);
				l.linkSegment(source, newSegment, state);
			}

			linkState.clear();
		}

		public boolean add(FlexRegionListener<T> l) {
			return add(new SoftReference<FlexRegionListener<T>>(l));
		}

		public boolean contains(FlexRegionListener<T> l) {
			return contains(new SoftReference<FlexRegionListener<T>>(l));
		}

		public boolean remove(FlexRegionListener<T> l) {
			return remove(new SoftReference<FlexRegionListener<T>>(l));
		}

	}

	public Object eventReplace(FlexRegion<T> source, long start, long oldLength,
	    long newLength, T data);

	public Object eventAppend(FlexRegion<T> source, long length, T data);
	
	public Object eventFlatten(FlexRegion<T> source, T data);
	
	public Object eventAbortAllChanges(FlexRegion<T> source);


	public Object linkSegment(FlexRegion<T> source, RegionSegment<T>[] segment,
	    Object userLinkState);

}
