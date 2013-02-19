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
package com.slytechs.utils.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public class HashArrayMapList<K, E> implements MapList<K, E> {
	
	private Map<K, List<E>> store = new HashMap<K, List<E>>();

	/* (non-Javadoc)
   * @see com.slytechs.utils.collection.MapList#add(java.lang.Object, java.lang.Object)
   */
  public void add(K a, E b) {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see com.slytechs.utils.collection.MapList#get(java.lang.Object, int)
   */
  public E get(K a, int i) {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see com.slytechs.utils.collection.MapList#isEmpty()
   */
  public boolean isEmpty() {
	  return store.isEmpty();
  }

	/* (non-Javadoc)
   * @see com.slytechs.utils.collection.MapList#isEmpty(java.lang.Object)
   */
  public boolean isEmpty(K a) {
	  return store.containsKey(a) && store.get(a).isEmpty();
  }

	/* (non-Javadoc)
   * @see com.slytechs.utils.collection.MapList#remove(java.lang.Object, java.lang.Object)
   */
  public boolean remove(K a, E o) {
	  if (isEmpty(a)) {
	  	return false;
	  }
	  
	  return store.get(a).remove(o);
  }

	/* (non-Javadoc)
   * @see com.slytechs.utils.collection.MapList#remove(java.lang.Object, int)
   */
  public E remove(K a, int i) {
	  if (isEmpty(a)) {
	  	return null;
	  }
	  
	  return store.get(a).remove(i);
  }

	/* (non-Javadoc)
   * @see com.slytechs.utils.collection.MapList#size(java.lang.Object)
   */
  public int size(K a) {
	  if (store.containsKey(a) == false) {
	  	return 0;
	  }
	  
	  return store.get(a).size();
  }

	/* (non-Javadoc)
   * @see java.util.Map#clear()
   */
  public void clear() {
	  store.clear();
  }

	/* (non-Javadoc)
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  public boolean containsKey(Object key) {
	  return store.containsKey(key);
  }

	/* (non-Javadoc)
   * @see java.util.Map#containsValue(java.lang.Object)
   */
  public boolean containsValue(Object value) {
	  for (K k: store.keySet()) {
	  	if (store.get(k).contains(value)) {
	  		return true;
	  	}
	  }
	  
	  return false;
  }

	/* (non-Javadoc)
   * @see java.util.Map#entrySet()
   */
  public Set<java.util.Map.Entry<K, E>> entrySet() {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see java.util.Map#get(java.lang.Object)
   */
  public E get(Object key) {
	  return store.get(key).get(0);
  }

	/* (non-Javadoc)
   * @see java.util.Map#keySet()
   */
  public Set<K> keySet() {
	  return store.keySet();
  }

	/* (non-Javadoc)
   * @see java.util.Map#put(java.lang.Object, java.lang.Object)
   */
  public E put(K key, E value) {
	  List<E> l = store.get(key);
	  
	  if (l == null) {
	  	l = new ArrayList<E>();
	  }
	  
	  l.add(value);
	  
	  return value;
  }

	/* (non-Javadoc)
   * @see java.util.Map#putAll(java.util.Map)
   */
  public void putAll(Map<? extends K, ? extends E> m) {
  	 
  	for (K k: m.keySet()) {
  		put(k, m.get(k));
  	}
  	 
  }

	/* (non-Javadoc)
   * @see java.util.Map#remove(java.lang.Object)
   */
  public E remove(Object key) {
	  final E e = get(key);
	  store.remove(key);
	  
	  return e;
  }

	/* (non-Javadoc)
   * @see java.util.Map#size()
   */
  public int size() {
	  return store.size();
  }

	/* (non-Javadoc)
   * @see java.util.Map#values()
   */
  public Collection<E> values() {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see com.slytechs.utils.collection.MapList#getList(java.lang.Object)
   */
  public List<E> getList(K a) {
	  return store.get(a);
  }


}
