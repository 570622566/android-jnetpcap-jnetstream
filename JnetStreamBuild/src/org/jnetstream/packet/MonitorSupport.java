package org.jnetstream.packet;

import java.util.List;


/**
 * Utility class to help manage listeners
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * @param <T>
 *          property value type
 */
public class MonitorSupport<T> {

	private List<MonitorListener<T>> listeners;

	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(MonitorListener<T> e) {
		return this.listeners.add(e);
	}

	/**
	 * @see java.util.List#clear()
	 */
	public void clear() {
		this.listeners.clear();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return this.listeners.remove(o);
	}

	/**
	 * @return
	 * @see java.util.List#size()
	 */
	public int size() {
		return this.listeners.size();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return this.listeners.contains(o);
	}
}