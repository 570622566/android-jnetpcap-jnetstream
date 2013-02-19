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

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class WeakArrayList<T>
    extends AbstractList<T> {

	/**
	 * A utility counter that is incremented everytime a reference within the weak
	 * list has been freedup.
	 */
	public static int referenceFreeUps = 0;

	/**
	 * A utility counter that is incremented everytime a reference within the weak
	 * list has been created.
	 */
	public static int referenceCreated = 0;

	private final ReferenceQueue<T> gced = new ReferenceQueue<T>();

	private final List<Reference<T>> list = new ArrayList<Reference<T>>();

	public WeakArrayList() {

	}

	public WeakArrayList(final Collection<T> collection) {

		for (final T t : collection) {
			this.add(t);
		}
	}

	@Override
	public void add(final int index, final T element) {
		this.update();

		referenceCreated++;
		this.list.add(index, new WeakReference<T>(element, this.gced));
	}

	@Override
	public boolean add(final T o) {
		this.update();

		referenceCreated++;
		return this.list.add(new WeakReference<T>(o, this.gced));
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		this.update();

		return super.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractList#get(int)
	 */
	@Override
	public T get(final int index) {
		this.update();

		T r = null;
		try {
			while ((r = this.list.get(index).get()) == null) {
				this.update();
			}
		} catch (IndexOutOfBoundsException e) {
			return null;
		}

		return r;

	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException(
		    "Volatile list due to unsychronized reference expiration, "
		        + "iterator not supported");
	}

	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException(
		    "Volatile list due to unsychronized reference expiration, "
		        + "iterator not supported");
	}

	@Override
	public ListIterator<T> listIterator(final int index) {
		throw new UnsupportedOperationException(
		    "Volatile list due to unsychronized reference expiration, "
		        + "iterator not supported");
	}

	@Override
	public T remove(final int index) {
		this.update();

		return this.list.remove(index).get();
	}

	@Override
	public T set(final int index, final T element) {
		this.update();

		final T oldValue = this.list.get(index).get();

		this.list.add(index, new WeakReference<T>(element));

		return oldValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		this.update();

		return this.list.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] toArray() {
		this.update();

		final Object[] a = this.list.toArray();

		int count = 0;
		for (Object o : a) {
			o = ((Reference<Object>) o).get();

			if (o == null) {
				continue;
			}

			count++;
		}

		final Object[] b = new Object[count];

		int i = 0;
		for (Object o : a) {
			o = ((Reference<Object>) o).get();
			if (o == null) {
				continue; // skip
			}

			b[i++] = o;
		}

		return b;
	}

	@Override
	public <C> C[] toArray(final C[] a) {
		this.update();

		return super.toArray(a);
	}

	final private void update() {

		Reference<? extends T> r = null;

		while ((r = this.gced.poll()) != null) {
			this.list.remove(r);
			WeakArrayList.referenceFreeUps++;
		}
	}

}
