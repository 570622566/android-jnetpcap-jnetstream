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
package com.slytechs.utils.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class ArrayAsLinkedList<T>
    extends LinkedList<T> implements List<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9163605484359263926L;

	private final List<T> delagate;

	/**
	 * 
	 */
	public ArrayAsLinkedList() {
		this.delagate = new ArrayList<T>();
	}

	/**
	 * @param c
	 */
	public ArrayAsLinkedList(Collection<T> c) {

		this.delagate = new ArrayList<T>(c);
	}
	
	public ArrayAsLinkedList(int capacity) {
		this.delagate = new ArrayList<T>(capacity);
	}

	/**
	 * @param index
	 * @param element
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, T element) {
		this.delagate.add(index, element);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(T o) {
		return this.delagate.add(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends T> c) {
		return this.delagate.addAll(c);
	}

	/**
	 * @param index
	 * @param c
	 * @return
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection<? extends T> c) {
		return this.delagate.addAll(index, c);
	}

	/* (non-Javadoc)
   * @see java.util.LinkedList#addFirst(java.lang.Object)
   */
  @Override
  public void addFirst(T o) {
	  this.delagate.add(0, o);
  }

	/* (non-Javadoc)
   * @see java.util.LinkedList#addLast(java.lang.Object)
   */
  @Override
  public void addLast(T o) {
	  this.delagate.add(delagate.size() - 1, o);
  }

	/**
	 * @see java.util.List#clear()
	 */
	public void clear() {
		this.delagate.clear();
	}

	/* (non-Javadoc)
   * @see java.util.LinkedList#clone()
   */
  @Override
  public Object clone() {
	  ArrayAsLinkedList<T> c = new ArrayAsLinkedList<T>(this.delagate);
	  
	  return c;
  }

	/**
	 * @param o
	 * @return
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return this.delagate.contains(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		return this.delagate.containsAll(c);
	}

	/* (non-Javadoc)
   * @see java.util.LinkedList#element()
   */
  @Override
  public T element() {
	  return delagate.get(0);
  }

	/**
	 * @param o
	 * @return
	 * @see java.util.List#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return this.delagate.equals(o);
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.List#get(int)
	 */
	public T get(int index) {
		return this.delagate.get(index);
	}

	/* (non-Javadoc)
   * @see java.util.LinkedList#getFirst()
   */
  @Override
  public T getFirst() {
	  return delagate.get(0);
  }

	/* (non-Javadoc)
   * @see java.util.LinkedList#getLast()
   */
  @Override
  public T getLast() {
	  return delagate.get(delagate.size() -1);
  }

	/**
	 * @return
	 * @see java.util.List#hashCode()
	 */
	public int hashCode() {
		return this.delagate.hashCode();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		return this.delagate.indexOf(o);
	}

	/**
	 * @return
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return this.delagate.isEmpty();
	}

	/**
	 * @return
	 * @see java.util.List#iterator()
	 */
	public Iterator<T> iterator() {
		return this.delagate.iterator();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o) {
		return this.delagate.lastIndexOf(o);
	}

	/**
	 * @return
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<T> listIterator() {
		return this.delagate.listIterator();
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<T> listIterator(int index) {
		return this.delagate.listIterator(index);
	}

	/* (non-Javadoc)
   * @see java.util.LinkedList#offer(java.lang.Object)
   */
  @Override
  public boolean offer(T o) {
	  delagate.add(delagate.size() -1, o);
	  
	  return true;
  }

	/* (non-Javadoc)
   * @see java.util.LinkedList#peek()
   */
  @Override
  public T peek() {
	  return delagate.get(0);
  }

	/* (non-Javadoc)
   * @see java.util.LinkedList#poll()
   */
  @Override
  public T poll() {
	  return delagate.remove(0);
  }

	/* (non-Javadoc)
   * @see java.util.LinkedList#remove()
   */
  @Override
  public T remove() {
	  return delagate.remove(0);
  }

	/**
	 * @param index
	 * @return
	 * @see java.util.List#remove(int)
	 */
	public T remove(int index) {
		return this.delagate.remove(index);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return this.delagate.remove(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		return this.delagate.removeAll(c);
	}

	/* (non-Javadoc)
   * @see java.util.LinkedList#removeFirst()
   */
  @Override
  public T removeFirst() {
	  return delagate.remove(0);
  }

	/* (non-Javadoc)
   * @see java.util.LinkedList#removeLast()
   */
  @Override
  public T removeLast() {
	  return delagate.remove(delagate.size() -1);
  }

	/**
	 * @param c
	 * @return
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		return this.delagate.retainAll(c);
	}

	/**
	 * @param index
	 * @param element
	 * @return
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public T set(int index, T element) {
		return this.delagate.set(index, element);
	}

	/**
	 * @return
	 * @see java.util.List#size()
	 */
	public int size() {
		return this.delagate.size();
	}

	/**
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 * @see java.util.List#subList(int, int)
	 */
	public List<T> subList(int fromIndex, int toIndex) {
		return this.delagate.subList(fromIndex, toIndex);
	}

	/**
	 * @return
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() {
		return this.delagate.toArray();
	}

	/**
	 * @param <T>
	 * @param a
	 * @return
	 * @see java.util.List#toArray(T[])
	 */
	public <C> C[] toArray(C[] a) {
		return this.delagate.toArray(a);
	}

}
