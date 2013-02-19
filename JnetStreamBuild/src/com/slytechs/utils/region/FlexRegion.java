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
import java.nio.ReadOnlyBufferException;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.slytechs.utils.collection.Readonly;
import com.slytechs.utils.region.FlexRegionListener.FlexRegiontSupport;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class FlexRegion<T> implements Iterable<RegionSegment<T>>, Changable {

	public static final Log logger = LogFactory.getLog(FlexRegion.class);

	private final boolean append;

	private long changeId = System.currentTimeMillis();

	protected Region<T> initialRegion;

	protected long length = 0;

	private final boolean readonly;

	protected final LinkedList<RegionSegment<T>> segments = new LinkedList<RegionSegment<T>>();

	private final FlexRegiontSupport<T> support = new FlexRegiontSupport<T>(this);

	private boolean modifiedAtLeastOnce = false;

	/**
	 * @param readonly
	 * @param append
	 */
	protected FlexRegion(final boolean readonly, final boolean append) {
		this.readonly = readonly;
		this.append = append;
	}

	/**
	 * @param readonly
	 *          creates a readonly flex region. No mutable operations are
	 *          supported and will throw an immediate Readonly exception if any
	 *          are used.
	 * @param append
	 *          TODO
	 * @param length
	 * @param data
	 */
	public FlexRegion(final boolean readonly, final boolean append,
	    final long length, final T data) {
		this.readonly = readonly;
		this.append = append;

		if (logger.isTraceEnabled()) {
			logger.trace("readonly=" + readonly + ", append=" + append + ", length="
			    + length + ", data=" + data);
		}

		init(length, data);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	public boolean add(final FlexRegionListener<T> o) {
		return this.support.add(o);
	}

	public final RegionSegment[] append(final long length, final T data) {
		this.throwIfNoAppend();

		if (length == 0) {
			return null; // Nothing to do
		}
		this.modifiedAtLeastOnce = true;

		this.support.fireAppend(length, data);

		this.changeHappened();

		// The last element of data is at (length - 1), so we append after the last
		// byte
		final long position = this.length;
		final RegionSegment<T> newSegment = createSegment(position, length, data);
		this.segments.addLast(newSegment);

		this.length += length;

		final RegionSegment[] newSegments = new RegionSegment[1];
		newSegments[0] = newSegment;

		this.support.fireLinkSegment(newSegments);

		if (logger.isTraceEnabled()) {
			logger.trace("length=" + length + ", data=" + data + " AFTER:"
			    + toString());
		}

		return newSegments;
	}

	private final void changeGlobalStart(final int index, final long delta) {

		final Iterator<RegionSegment<T>> i = this.segments.listIterator(index);

		while (i.hasNext()) {
			final RegionSegment<T> s = i.next();

			s.addToStartGlobal(delta);
		}
	}

	public final void changeHappened() {
		if (this.changeId == Long.MAX_VALUE) {
			this.changeId = Long.MIN_VALUE;
		} else {
			this.changeId++;
		}
	}

	public final boolean checkBoundsGlobal(final long global) {
		return (global >= 0) && (global < this.length);
	}

	public final RegionSegment[] clear() {
		if (this.isModified() == false) {
			return null; // Nothing to do
		}

		support.fireAbortAllChanges();

		this.segments.clear();

		final RegionSegment<T> s = createSegment(this.initialRegion, 0, 0,
		    this.initialRegion.getLength());

		this.segments.add(s);

		this.length = this.initialRegion.getLength();

		this.changeHappened();

		final RegionSegment[] newSegments = new RegionSegment[] { s };

		support.fireLinkSegment(newSegments);

		return newSegments;
	}

	/**
	 * 
	 */
	public void close() {
		/*
		 * We really don't need to do anything here, except may be print out a debug
		 * message.
		 */

		if (logger.isDebugEnabled() && modifiedAtLeastOnce
		    || logger.isTraceEnabled()) {
			logger.debug("       " + toString() + (modifiedAtLeastOnce ? "*Modified" : ""));
		}
	}

	/**
	 * @param listener
	 * @return
	 * @see java.util.ArrayList#contains(java.lang.Object)
	 */
	public boolean contains(final FlexRegionListener<T> listener) {
		return this.support.contains(listener);
	}

	/**
	 * @param i
	 * @return
	 */
	public final RegionHandle<T> createHandle(final long global) {

		final RegionSegment<T> s = this.getSegment(global);
		final long regional = s.mapGlobalToRegional(global);

		final RegionHandle<T> h = new RegionHandle<T>(s, regional, this);

		return h;
	}

	/**
	 * @param changable
	 * @param i
	 * @param length
	 * @param data
	 * @return
	 */
	protected RegionSegment<T> createSegment(Changable changable, long global,
	    long length, T data) {
		final RegionSegment<T> segment = new RegionSegment<T>(this, 0, length, data);

		return segment;
	}

	protected RegionSegment<T> createSegment(long global, long length, T data) {
		final RegionSegment<T> segment = new RegionSegment<T>(this, global, length,
		    data);

		return segment;

	}

	/**
	 * @param region
	 * @param endGlobal
	 * @param thirdStartRegional
	 * @param thirdLength
	 * @return
	 */
	protected RegionSegment<T> createSegment(Region<T> region, long global,
	    long regional, long length) {
		final RegionSegment<T> segment = new RegionSegment<T>(region, global,
		    regional, length);

		return segment;
	}

	public final RegionSegment[] flatten(final T data) {
		return this.flatten(data, this.length);
	}

	public final RegionSegment[] flatten(final T data, final long length) {

		if (logger.isDebugEnabled()) {
			logger.debug("BEFORE:" + toString());

		}

		// final RegionSegment<T> initial = new RegionSegment<T>(this, 0, length,
		// data);

		final RegionSegment<T> initial = createSegment((Changable) this, 0, length,
		    data);

		support.fireFlatten(data);

		/*
		 * Set forward references for all segments. Each segment will use its
		 * current GLOBAL address as a REGIONAL address into the new "initial"
		 * segment.
		 */
		for (final RegionSegment<T> segment : this.segments) {
			final Region<T> region = segment.getRegion();

			if (region.getForward() != initial.getRegion()) {
				region.setForward(initial.getRegion());

				/*
				 * Tell each data element within each region that its underlying content
				 * should be set to readonly, since we do not want any more changes to
				 * the region's data. All changes should be propagated to the forwarded
				 * region and its data.
				 */
				final T d = region.getData();
				if (d instanceof Readonly) {
					((Readonly) d).setReadonly(true);
				}
			}

		}

		/*
		 * Remove all old segments
		 */
		this.segments.clear();

		/*
		 * Now add the replacement "init" segment which is the entire updated file
		 */
		this.segments.add(initial);

		this.initialRegion = initial.getRegion();

		this.changeHappened();

		final RegionSegment[] newSegments = new RegionSegment[] { initial };

		support.fireLinkSegment(newSegments);

		if (logger.isDebugEnabled()) {
			logger.debug(" AFTER:" + toString());

		}

		return newSegments;
	}

	public final long getChangeId() {
		return this.changeId;
	}

	public final T getData(final long global) {
		final RegionSegment<T> s = this.getSegment(global);

		return s.getData();
	}

	public final long getLength() {
		return this.length;
	}

	/**
	 * @param index
	 * @return
	 * @throws IOException
	 */
	public Object getRegionalData(final int global) throws IOException {
		final RegionSegment<T> segment = this.getSegment(global);
		final long regional = segment.mapGlobalToRegional(global);
		final T data = segment.getData();

		if (data instanceof RegionDataGetter) {
			final RegionDataGetter getter = (RegionDataGetter) data;
			return getter.get(regional);
		}

		throw new UnsupportedOperationException(
		    "This operation is not supported by the data object. "
		        + "The data object needs to implement the RegionDataGetter "
		        + "interface.");
	}

	public final RegionSegment<T> getSegment(final long position) {
		/*
		 * Optimize for 1 single segment list, no need to initiate an Iterator
		 * object
		 */

		final RegionSegment<T> first = this.segments.getFirst();
		if (first.checkBoundsGlobal(position)) {
			return first;
		}
		final int s = this.segments.size();

		/*
		 * Optimize for very large number of segments in the list Last else does a
		 * direct search for list sizes under 1000
		 */

		if (s >= 1000000) {
			return this.getSegment(position, 0, 1000000);
		} else if (s >= 100000) {
			return this.getSegment(position, 0, 100000);
		} else if (s >= 10000) {
			return this.getSegment(position, 0, 10000);
		} else if (s >= 1000) {
			return this.getSegment(position, 0, 1000);
		} else {
			return this.getSegment(position, 0);
		}
	}

	public final RegionSegment<T> getSegment(final long global, final int start) {

		int i = 0;
		final Iterator<RegionSegment<T>> l = this.segments.listIterator(start);
		while (l.hasNext()) {
			final RegionSegment<T> ds = l.next();
			if (ds.checkBoundsGlobal(global)) {
				return ds;
			}

			i++;
		}

		throw new IndexOutOfBoundsException("Position out of bounds [" + global
		    + "]");
	}

	private final RegionSegment<T> getSegment(final long position,
	    final int start, final int power) {
		final int s = this.segments.size();

		int l = start;

		if (power == 100) {
			return this.getSegment(position, start);
		}

		for (int i = start + power; i < s; i += power) {
			if (position < this.segments.get(i).getStartGlobal()) {
				break;
			}

			l = i;
		}

		return this.getSegment(position, l, power / 10);
	}

	public final int getSegmentCount() {
		return this.segments.size();
	}

	private final int getSegmentIndex(final long position) {

		/*
		 * Optimize for 1 single segment list, no need to initiate an Iterator
		 * object
		 */
		if ((this.segments.size() == 1)
		    && this.segments.getFirst().checkBoundsGlobal(position)) {
			return 0;
		}

		int i = 0;
		for (final RegionSegment<T> ds : this.segments) {
			if (ds.checkBoundsGlobal(position)) {
				return i;
			}

			i++;
		}

		throw new IndexOutOfBoundsException("Position out of bounds [" + position
		    + "]");
	}

	/**
	 * @return
	 */
	public Iterable<RegionSegment<T>> getSegmentIterable() {
		return new Iterable<RegionSegment<T>>() {

			public Iterator<RegionSegment<T>> iterator() {
				return segments.iterator();
			}

		};
	}

	protected void init(final long length, final T data) {
		this.length = length;

		final RegionSegment<T> ds = new RegionSegment<T>(this, 0, length, data);
		this.initialRegion = ds.getRegion();

		this.segments.add(ds);
	}

	public final void insert(final long position, final long length, final T data) {

		if (length == 0) {
			return; // Nothing to do
		}

		/*
		 * Special case: append at the end - the position is actually outside any
		 * active region, therefore only if we are positioned just 1 byte past the
		 * end of the last active region, we do an append instead of an insert.
		 */
		if (position == this.length) {
			this.append(length, data);

		} else {
			this.replace(position, 0, length, data);
		}
	}

	/**
	 * @return
	 */
	public boolean isAppend() {
		return this.append;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.region.Changable#isChanged(long)
	 */
	public final boolean isChanged(final long changeId) {
		return this.changeId != changeId;
	}

	public final boolean isModified() {
		return this.segments.size() != 1;
	}

	public final boolean isModifiedByAppendOnly() {
		final RegionSegment<T> first = this.segments.getFirst();

		return (this.segments.size() > 1)
		    && (first.getRegion() == this.initialRegion)
		    && (first.getLength() == this.initialRegion.getLength());

	}

	/**
	 * @return
	 */
	public boolean isReadonly() {
		return this.readonly;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public final Iterator<RegionSegment<T>> iterator() {
		return this.segments.iterator();
	}

	/**
	 * Creates a new region which may contain different data and who is actively
	 * synched with this region. Any changes to this region will be propagated and
	 * translated via the supplied translator in the linked region.
	 * 
	 * @param <C>
	 *          new type prarameter of the new region
	 * @param translator
	 *          translator which will translate various even properties from one
	 *          region type <T> to linked region <C>.
	 * @return new region that is linked to this region
	 */
	public <C> FlexRegion<C> linkedRegion(final RegionTranslator<C, T> translator) {

		final FlexRegion<C> linked = new LinkedFlexRegion<C, T>(this, translator);

		return linked;
	}

	/**
	 * Removes the specified listener from the active listeners list. The listener
	 * will no longer be notified of any events.
	 * 
	 * @param listener
	 *          listener to remove
	 * @return true if found and removed, otherwise false
	 */
	public boolean remove(final FlexRegionListener<T> listener) {
		return this.support.remove(listener);
	}

	public final void remove(final long position, final long length) {
		if (length == 0) {
			return; // Nothing to do
		}

		this.replace(position, length, 0, null);
	}

	/**
	 * @param start
	 *          global start
	 * @param oldLength
	 *          number of elements to be replaced
	 * @param newLength
	 *          number of elements to replace the old segment
	 * @param data
	 *          data associated with this replacement
	 * @return TODO
	 */
	public final RegionSegment[] replace(final long start, final long oldLength,
	    final long newLength, final T data) {

		this.throwIfReadonly();

		if ((oldLength == 0) && (newLength == 0)) {
			return null; // Nothing to do
		}
		this.modifiedAtLeastOnce = true;

		this.support.fireReplace(start, oldLength, newLength, data);

		this.changeHappened();

		final int i = this.getSegmentIndex(start);
		final RegionSegment<T> first = this.segments.get(i);

		if ((first.checkBoundsGlobal(start) == false)
		    || (first.checkBoundsGlobal(start, oldLength) == false)) {
			throw new IndexOutOfBoundsException("Replacement request [" + start
			    + "] falls outside the segment's [" + first.toString()
			    + "] boundaries");
		}

		final long startLocal = first.mapGlobalToLocal(start);
		final long firstEndLocal = first.getEndLocal();
		final long endLocal = startLocal + oldLength;

		final RegionSegment[] newSegment;

		if (startLocal == 0) {
			newSegment = new RegionSegment[1];
			newSegment[0] = this.replaceFront(first, start, oldLength, newLength,
			    data);
		} else if (endLocal == firstEndLocal) {
			newSegment = new RegionSegment[1];
			newSegment[0] = this
			    .replaceBack(first, start, oldLength, newLength, data);
		} else {
			newSegment = this.replaceMiddle(first, i, start, oldLength, newLength,
			    data);
		}

		this.support.fireLinkSegment(newSegment);

		if (logger.isTraceEnabled()) {
			logger.trace("start" + start + ", old=" + oldLength + ", new="
			    + newLength + ", data=" + data + " AFTER:" + toString());
		}

		return newSegment;
	}

	/**
	 * @param start
	 * @param oldLength
	 * @param newLength
	 * @param data
	 */
	private final RegionSegment<T> replaceBack(final RegionSegment<T> first,
	    final long start, final long oldLength, final long newLength, final T data) {

		final long delta = newLength - oldLength;
		final int i = this.segments.indexOf(first);
		this.changeGlobalStart(i + 1, delta);

		this.length += delta;

		final long firstOldLength = first.getLength();
		first.setLength(firstOldLength + delta);

		if (newLength != 0) {
			final RegionSegment<T> newSegment = createSegment(first.getEndGlobal(),
			    newLength, data);

			this.segments.add(i + 1, newSegment);
			return newSegment;
		}

		return null;
	}

	/**
	 * @param start
	 * @param replacementLength
	 * @param newLength
	 * @param data
	 */
	private final RegionSegment<T> replaceFront(final RegionSegment<T> first,
	    final long start, final long replacementLength, final long newLength,
	    final T data) {

		final long firstOldLength = first.getLength();
		final int i = this.segments.indexOf(first);
		final long delta = newLength - replacementLength;
		this.changeGlobalStart(i + 1, delta);

		this.length += delta;

		/*
		 * Check if this is a total remove
		 */
		if (newLength == 0) {
			if (replacementLength == firstOldLength) {
				/*
				 * Removing entire segment?
				 */

				this.segments.remove(first);

			} else {
				/*
				 * Removing only the front portion of the segment. So shrink length and
				 * push out the regional start. Local and global starts are not affected
				 */
				first.setLength(firstOldLength - replacementLength);
				first.addToStartRegional(replacementLength);

			}
		} else {
			/*
			 * Ok, we're replacing a portion of the first segment with a new region
			 */

			final RegionSegment<T> newSegment = createSegment(start, newLength, data);

			/*
			 * Check if we are replacing the entire segment
			 */
			if ((newLength == replacementLength) && (first.getLength() == newLength)) {
				first.setValid(false);
				this.segments.remove(i);
				this.segments.add(i, newSegment);

			} else {
				first.addToStartRegional(replacementLength);
				first.addToStartGlobal(newLength);
				first.setLength(firstOldLength - replacementLength);

				this.segments.add(i, newSegment);
			}

			return newSegment;
		}

		return null;
	}

	/**
	 * @param start
	 * @param replacedLength
	 * @param newLength
	 * @param data
	 */
	private final RegionSegment[] replaceMiddle(final RegionSegment<T> first,
	    final int i, final long start, final long replacedLength,
	    final long newLength, final T data) {

		final long firstOldLength = first.getLength();
		final long firstNewLength = first.mapGlobalToRegional(start
		    - first.getStartRegional());
		first.setLength(firstNewLength);

		final long thirdStartRegional = first.getEndRegional() + replacedLength;
		final long thirdLength = firstOldLength - firstNewLength - replacedLength;

		final RegionSegment<T> second = createSegment(first.getEndGlobal(),
		    newLength, data);

		final RegionSegment<T> third = createSegment(first.getRegion(), second
		    .getEndGlobal(), thirdStartRegional, thirdLength);

		final long globalDelta = newLength - replacedLength;
		this.length += globalDelta;

		this.changeGlobalStart(i + 1, globalDelta);

		final RegionSegment[] newSegments;

		if (second.getLength() == 0) {
			this.segments.add(i + 1, third);
			second.getRegion().remove(second);

			newSegments = new RegionSegment[1];
			newSegments[0] = third;

		} else {
			this.segments.add(i + 1, second);
			this.segments.add(i + 2, third);

			newSegments = new RegionSegment[2];
			newSegments[0] = second;
			newSegments[1] = third;
		}

		return newSegments;
	}

	private final void throwIfNoAppend() {
		if (this.append) {
			return;
		}

		throw new ReadOnlyBufferException();
	}

	private final void throwIfReadonly() {
		if (this.readonly == false) {
			return;
		}

		throw new ReadOnlyBufferException();
	}

	/**
	 * <p>
	 * Prints the current FlexRegion and its segments. The output will have the
	 * following format:
	 * 
	 * <pre>
	 *                                       [[A.0 - A.1/l=2/g=0/r=0], [B.2 - B.8/l=7/g=2/r=1]]
	 * </pre>
	 * 
	 * Note: A and B are user supplied T type parameters supplied to the region
	 * and the operations on the region.
	 * </p>
	 * <p>
	 * The output shows the entire FlexRegion enclosed in the outter []. It has 2
	 * segments, each enclosed in inner []. First segment, is associated with a
	 * region who's user data parameter was "A". The segments range is from
	 * abstract position 0 to 1, inclusive. The other paramters:
	 * <ul>
	 * <li>l - length or number of elements within the segment (2)
	 * <li>g - global position of the first element within the segment (0)
	 * <li>r - regional position of the first element within the segment (0)1
	 * </ul>
	 * The second segment shows the same parameters but with differnt values.
	 * Range is from 2 to 8, inclusive:
	 * <ul>
	 * <li>l - length or number of elements within the segment (72)
	 * <li>g - global position of the first element within the segment (2)
	 * <li>r - regional position of the first element within the segment (1)
	 * </ul>
	 * </p>
	 */
	@Override
	public String toString() {
		return this.segments.toString();
	}
}
