/**
 * Copyright (C) 2008 Sly Technologies, Inc. This library is free software; you
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
package org.jnetstream.packet;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A specialized properties map that maintains a set of static global and
 * dynamic properties. Global properties are immutable while dynamic properties
 * are mutable and the dynamic portion of the map can be modified. The property
 * keys can only be enum constants, nothing else is allowed. In some instances
 * dynamic properties can override or hide the static global properites. In
 * cases where method does operation on both dynamic and global properties, the
 * dynamic properties are always operated on first, then the global ones.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class EnumProperties<D extends Enum<D>, S extends Enum<S>> {
	
	private final Enum<D>[] dnames;
	
	private final Enum<S>[] snames;

	private final Object[] statics;

	private final Object[] dynamic;

	public EnumProperties(Object[] statics, Enum<D>[] dnames, Enum<S>[] snames) {
		this.statics = statics;
		this.dnames = dnames;
		this.snames = snames;
		dynamic = new Object[dnames.length];
	}

	/**
	 * @see java.util.Map#clear()
	 */
	public void clearDynamic() {
		Arrays.fill(dynamic, null);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Map#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return this.dynamic.equals(o) || this.statics.equals(o);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object getDynamic(D key) {
		return getDynamic(key.ordinal());
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object getStatic(S key) {
		return getStatic(key.ordinal());
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public Object getDynamic(int index) {
		if (index < 0 || index >= dynamic.length) {
			throw new IndexOutOfBoundsException();
		}

		return dynamic[index];
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public Object getStatic(int index) {
		if (index < 0 || index >= statics.length) {
			throw new IndexOutOfBoundsException();
		}

		return statics[index];
	}

	/**
	 * @return
	 * @see java.util.Map#hashCode()
	 */
	public int hashCode() {
		return this.dynamic.hashCode() ^ this.statics.hashCode();
	}

	/**
	 * 
	 * @param index
	 * @param value
	 * @return
	 */
	public Object putDynamic(int index, Object value) {
		if (index < 0 || index >= dynamic.length) {
			throw new IndexOutOfBoundsException();
		}
		dynamic[index] = value;

		return value;
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object putDynamic(D key, Object value) {
		return putDynamic(key.ordinal(), value);
	}

	/**
	 * @param t
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAllDynamic(Map<? extends D, ? extends Object> t) {
		for (Entry<? extends D, ? extends Object> e: t.entrySet()) {
			putDynamic(e.getKey(), e.getValue());
		}
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public Object remove(int index) {
		if (index < 0 || index >= dynamic.length) {
			throw new IndexOutOfBoundsException();
		}
		Object old = dynamic[index];
		dynamic[index] = null;
		
		return old;
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(D key) {
		return remove(key.ordinal());
	}

	/**
   * @param key
   * @return
   */
  public Object getByName(String key) {
	  for (Enum<D> d: dnames) {
	  	return getDynamic(d.ordinal());
	  }
	  
	  for (Enum<S> s: snames) {
	  	return getStatic(s.ordinal());
	  }
	  
	  return null;
  }
  
  public Enum<D>[] getDynamicNames() {
  	return dnames;
  }
  
  public Enum<S>[] getStaticNames() {
  	return snames;
  }
}
