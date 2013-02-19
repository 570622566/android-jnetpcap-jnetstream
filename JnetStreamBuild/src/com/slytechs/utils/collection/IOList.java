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
 * <P>
 * Special List interface that mimics all of the standard List methods but which
 * throw IOException. This allows the interface to be used for back-end
 * operations. The convention is that the method names are exactly the same as
 * in the List interface. The only difference is that all methods can throw an
 * IOException.
 * </P>
 * 
 * <P>
 * Any operation can throw an IOException including size() and isEmpty() as they
 * may need to reference data using some kind of IO operation.
 * </P>
 * 
 * <P>
 * Implementations must also provide implementations for normal List methods,
 * but without the possibility of throwing IOException. It is upto the
 * implementation exactly how to export the underlying IOException if its ever
 * thrown for any of the normal, non-io List methods.
 * </P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface IOList<T> extends IOCollection<T> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends T> c) throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection<? extends T> c)
	    throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#add(int, E)
	 */
	public void add(int index, T element) throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#clear()
	 */
	public void clear() throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#get(int)
	 */
	public T get(int index) throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#remove(int)
	 */
	public T remove(int index) throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o) throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#set(int, E)
	 */
	public T set(int index, T element) throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#subList(int, int)
	 */
	public IOList subList(int fromIndex, int toIndex) throws IOException;

}
