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

class ProxyRegionSegment<T> extends RegionSegment<T> {
	private RegionSegment<T> delagate;
	

	/**
   * @param delagate
   */
  public ProxyRegionSegment(RegionSegment<T> delagate) {
    this.delagate = delagate;
  }

	/**
   * @param listener
   * @return
   * @see com.slytechs.utils.region.RegionSegment#add(com.slytechs.utils.region.FlexSegmentListener)
   */
  public boolean add(FlexSegmentListener<T> listener) {
    return this.delagate.add(listener);
  }

	/**
   * @param delta
   * @see com.slytechs.utils.region.RegionSegment#addToStartGlobal(long)
   */
  public void addToStartGlobal(long delta) {
    this.delagate.addToStartGlobal(delta);
  }

	/**
   * @param delta
   * @see com.slytechs.utils.region.RegionSegment#addToStartRegional(long)
   */
  public void addToStartRegional(long delta) {
    this.delagate.addToStartRegional(delta);
  }

	/**
   * @param position
   * @return
   * @see com.slytechs.utils.region.RegionSegment#checkBounds(long)
   */
  public boolean checkBounds(long position) {
    return this.delagate.checkBounds(position);
  }

	/**
   * @param global
   * @param length
   * @return
   * @see com.slytechs.utils.region.RegionSegment#checkBoundsGlobal(long, long)
   */
  public boolean checkBoundsGlobal(long global, long length) {
    return this.delagate.checkBoundsGlobal(global, length);
  }

	/**
   * @param global
   * @return
   * @see com.slytechs.utils.region.RegionSegment#checkBoundsGlobal(long)
   */
  public boolean checkBoundsGlobal(long global) {
    return this.delagate.checkBoundsGlobal(global);
  }

	/**
   * @param regional
   * @return
   * @see com.slytechs.utils.region.RegionSegment#checkBoundsRegional(long)
   */
  public boolean checkBoundsRegional(long regional) {
    return this.delagate.checkBoundsRegional(regional);
  }

	/**
   * @param listener
   * @return
   * @see com.slytechs.utils.region.RegionSegment#contains(com.slytechs.utils.region.FlexSegmentListener)
   */
  public boolean contains(FlexSegmentListener<T> listener) {
    return this.delagate.contains(listener);
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#getData()
   */
  public T getData() {
    return this.delagate.getData();
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#getEnd()
   */
  public long getEnd() {
    return this.delagate.getEnd();
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#getEndGlobal()
   */
  public long getEndGlobal() {
    return this.delagate.getEndGlobal();
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#getEndLocal()
   */
  public long getEndLocal() {
    return this.delagate.getEndLocal();
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#getEndRegional()
   */
  public long getEndRegional() {
    return this.delagate.getEndRegional();
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#getLast()
   */
  public long getLast() {
    return this.delagate.getLast();
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#getLastGlobal()
   */
  public long getLastGlobal() {
    return this.delagate.getLastGlobal();
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#getLastLocal()
   */
  public long getLastLocal() {
    return this.delagate.getLastLocal();
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#getLastRegional()
   */
  public long getLastRegional() {
    return this.delagate.getLastRegional();
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#getLength()
   */
  public long getLength() {
    return this.delagate.getLength();
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#getRegion()
   */
  public Region<T> getRegion() {
    return this.delagate.getRegion();
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#getStart()
   */
  public long getStart() {
    return this.delagate.getStart();
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#getStartGlobal()
   */
  public long getStartGlobal() {
    return this.delagate.getStartGlobal();
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#getStartRegional()
   */
  public long getStartRegional() {
    return this.delagate.getStartRegional();
  }

	/**
   * @return
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return this.delagate.hashCode();
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#isEmpty()
   */
  public boolean isEmpty() {
    return this.delagate.isEmpty();
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#isValid()
   */
  public boolean isValid() {
    return this.delagate.isValid();
  }

	/**
   * @param global
   * @return
   * @see com.slytechs.utils.region.RegionSegment#mapGlobalToLocal(long)
   */
  public long mapGlobalToLocal(long global) {
    return this.delagate.mapGlobalToLocal(global);
  }

	/**
   * @param global
   * @return
   * @see com.slytechs.utils.region.RegionSegment#mapGlobalToRegional(long)
   */
  public long mapGlobalToRegional(long global) {
    return this.delagate.mapGlobalToRegional(global);
  }

	/**
   * @param local
   * @return
   * @see com.slytechs.utils.region.RegionSegment#mapLocalToGlobal(long)
   */
  public long mapLocalToGlobal(long local) {
    return this.delagate.mapLocalToGlobal(local);
  }

	/**
   * @param local
   * @return
   * @see com.slytechs.utils.region.RegionSegment#mapLocalToRegional(long)
   */
  public long mapLocalToRegional(long local) {
    return this.delagate.mapLocalToRegional(local);
  }

	/**
   * @param regional
   * @return
   * @see com.slytechs.utils.region.RegionSegment#mapRegionalToGlobal(long)
   */
  public long mapRegionalToGlobal(long regional) {
    return this.delagate.mapRegionalToGlobal(regional);
  }

	/**
   * @param regional
   * @return
   * @see com.slytechs.utils.region.RegionSegment#mapRegionalToLocal(long)
   */
  public long mapRegionalToLocal(long regional) {
    return this.delagate.mapRegionalToLocal(regional);
  }

	/**
   * @param listener
   * @return
   * @see com.slytechs.utils.region.RegionSegment#remove(com.slytechs.utils.region.FlexSegmentListener)
   */
  public boolean remove(FlexSegmentListener<T> listener) {
    return this.delagate.remove(listener);
  }

	/**
   * @param length
   * @see com.slytechs.utils.region.RegionSegment#setLength(long)
   */
  public void setLength(long length) {
    this.delagate.setLength(length);
  }

	/**
   * @param state
   * @see com.slytechs.utils.region.RegionSegment#setValid(boolean)
   */
  public void setValid(boolean state) {
    this.delagate.setValid(state);
  }

	/**
   * @return
   * @see com.slytechs.utils.region.RegionSegment#toString()
   */
  public String toString() {
    return this.delagate.toString();
  }

	/**
   * @return the delagate
   */
  public final RegionSegment<T> getDelagate() {
  	return this.delagate;
  }

	/**
   * @param delagate the delagate to set
   */
  public final void setDelagate(RegionSegment<T> delagate) {
  	this.delagate = delagate;
  }

	/* (non-Javadoc)
   * @see com.slytechs.utils.region.RegionSegment#getLinkedSegment()
   */
  @Override
  public Object getLinkedSegment() {
	  return this.delagate.getLinkedSegment();
  }
}