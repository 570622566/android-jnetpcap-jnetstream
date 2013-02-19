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
package com.slytechs.utils.options;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class Options implements Set<Option> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9058996828354957279L;

	/**
	 * Convenience method which creates a new Options set from the collection of
	 * arguments supplied.
	 * 
	 * @param c
	 *          variable length collection of options
	 * @return new option set
	 */
	public static Options set(Option... c) {
		return new Options(c);
	}
	
	private final TreeSet<Option> delagate;

	/**
	 * 
	 */
	public Options() {
		this.delagate = new TreeSet<Option>();
	}

	/**
	 * @param c
	 */
	public Options(final Collection<Option> c) {
		this.delagate = new TreeSet<Option>(c);
	}

	/**
	 * @param c
	 */
	public Options(final Comparator<? super Option> c) {
		this.delagate = new TreeSet<Option>(c);
	}

	/**
	 * @param c
	 */
	public Options(final Option... c) {
		this.delagate = new TreeSet<Option>(Arrays.asList(c));
	}

	/**
	 * @param s
	 */
	public Options(final SortedSet<Option> s) {
		this.delagate = new TreeSet<Option>(s);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public boolean add(final Option o) {
		return this.delagate.add(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	public boolean addAll(final Collection<? extends Option> c) {
		return this.delagate.addAll(c);
	}

	/**
	 * @see java.util.Set#clear()
	 */
	public void clear() {
		this.delagate.clear();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	public boolean contains(final Object o) {
		return this.delagate.contains(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	public boolean containsAll(final Collection<?> c) {
		return this.delagate.containsAll(c);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		return this.delagate.equals(o);
	}

  public <T extends Option> T getValue(Class<T> t, T def) {
		
		for (Option o: delagate) {
			if (t.isInstance(o)) {
				return t.cast(o);
			}
		}
		
		return def;
	}
	
  public <T extends Option> Set<T> getSet(Class<T> t, Set<T> def) {
		
		final Set<T> s = new TreeSet<T>();
		
		for (Option o: delagate) {
			if (t.isInstance(o)) {
				s.add(t.cast(o));
			}
		}
		
		return (s.isEmpty() ? def : s);
	}
  
  public <T extends Option> Set<T> getSet(Class<T> t, T...def) {
		
		final Set<T> s = new TreeSet<T>();
		
		for (Option o: delagate) {
			if (t.isInstance(o)) {
				s.add(t.cast(o));
			}
		}
		
		if (s.isEmpty()) {
			s.addAll(Arrays.asList(def));
		}
		
		return s;
	}


	/**
	 * @return
	 * @see java.util.Set#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.delagate.hashCode();
	}

	/**
	 * @return
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		return this.delagate.isEmpty();
	}

	/**
	 * @return
	 * @see java.util.Set#iterator()
	 */
	public Iterator<Option> iterator() {
		return this.delagate.iterator();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	public boolean remove(final Object o) {
		return this.delagate.remove(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	public boolean removeAll(final Collection<?> c) {
		return this.delagate.removeAll(c);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	public boolean retainAll(final Collection<?> c) {
		return this.delagate.retainAll(c);
	}

	/**
	 * @return
	 * @see java.util.Set#size()
	 */
	public int size() {
		return this.delagate.size();
	}

	/**
	 * @return
	 * @see java.util.Set#toArray()
	 */
	public Object[] toArray() {
		return this.delagate.toArray();
	}

	/**
	 * @param <Option>
	 * @param a
	 * @return
	 * @see java.util.Set#toArray(Option[])
	 */
	public <C> C[] toArray(final C[] a) {
		return this.delagate.toArray(a);
	}
}
