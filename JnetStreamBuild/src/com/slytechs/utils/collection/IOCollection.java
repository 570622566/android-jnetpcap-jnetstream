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

/**
 * Mimics the standard {@link java.util.Collection} with only difference of
 * each method throwing IOException. This is for collections that are backed
 * by IO operations.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface IOCollection<T>  {

	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	public int size()  throws IOException;

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty()  throws IOException;

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object o)  throws IOException;

	/**
	 * A rather dangerous method that uses IO operations to gets its data. Since
	 * the dataset can be of any size, the java VM may run out of memory or
	 * take extremely long time to create the array of elements.
	 * 
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray()  throws IOException;

	/**
	 * A rather dangerous method that uses IO operations to gets its data. Since
	 * the dataset can be of any size, the java VM may run out of memory or
	 * take extremely long time to create the array of elements.
	 * 
	 * @see java.util.Collection#toArray(T[])
	 */
	public <C> C[] toArray(C[] a)  throws IOException;

	/* (non-Javadoc)
	 * @see java.util.Collection#add(E)
	 */
	public boolean add(T o)  throws IOException;

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object o)  throws IOException;

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c)  throws IOException;

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends T> c)  throws IOException;

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c)  throws IOException;

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c)  throws IOException;

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	public void clear()  throws IOException;

}
