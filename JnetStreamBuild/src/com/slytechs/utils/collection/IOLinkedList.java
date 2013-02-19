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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class IOLinkedList<T>  implements IOList<T> {

	private static final long serialVersionUID = -954863337906838330L;
	
	private List<T> storage = new LinkedList<T>();
	
	protected List<T> getBackingList() {
		return storage;
	}

	/* (non-Javadoc)
	 * @see java.util.List#add(int, E)
	 */
	public void add(int index, T element) throws IOException {
		storage.add(index, element);
	}

	/* (non-Javadoc)
	 * @see java.util.List#add(E)
	 */
	public boolean add(T o) throws IOException  {
		return storage.add(o);
	}

	/* (non-Javadoc)
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends T> c) throws IOException  {
		return storage.addAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection<? extends T> c)  throws IOException {
		return storage.addAll(index, c);
	}

	/* (non-Javadoc)
	 * @see java.util.List#clear()
	 */
	public void clear()  throws IOException {
		storage.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o)  throws IOException {
		return storage.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c)  throws IOException {
		return storage.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.List#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return storage.equals(o);
	}

	/* (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	public T get(int index)  throws IOException {
		return storage.get(index);
	}

	/* (non-Javadoc)
	 * @see java.util.List#hashCode()
	 */
	public int hashCode() {
		return storage.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) throws IOException  {
		return storage.indexOf(o);
	}

	/* (non-Javadoc)
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty()  throws IOException {
		return storage.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(int)
	 */
	public T remove(int index)  throws IOException {
		return storage.remove(index);
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o) throws IOException  {
		return storage.remove(o);
	}

	/* (non-Javadoc)
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c)  throws IOException {
		return storage.removeAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c)  throws IOException {
		return storage.retainAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.List#set(int, E)
	 */
	public T set(int index, T element)  throws IOException {
		return storage.set(index, element);
	}

	/* (non-Javadoc)
	 * @see java.util.List#size()
	 */
	public int size()  throws IOException {
		return storage.size();
	}



	/* (non-Javadoc)
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() throws IOException  {
		return storage.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.List#toArray(T[])
	 */
	public <C> C[] toArray(C[] a) throws IOException  {
		return storage.toArray(a);
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.collection.IOIterable#iterator()
	 */
	public IOIterator<T> iterator() throws IOException {
		return new IOIterator<T>() {
			private Iterator<T> i = storage.iterator();
			
			public boolean hasNext() throws IOException {
				return i.hasNext();
			}

			public T next() throws IOException {
				return i.next();
			}

			public void remove() throws IOException {
				i.remove();
			}
			
		};
	}

	/* (non-Javadoc)
   * @see com.slytechs.utils.collection.IOList#subList(int, int)
   */
  public IOList subList(int fromIndex, int toIndex) throws IOException {
	  // TODO Auto-generated method stub
	  return null;
  }
	

}
