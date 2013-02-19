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

import java.io.IOException;
import java.util.Iterator;

import com.slytechs.utils.io.IORuntimeException;

/**
 * Special IO Iterator that behaves and has the same contract as Iterator, but
 * because it works with IO operations it also throws an IOException. This is
 * the only difference.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface IOIterator<T> extends IORemovable {

	/**
	 * Adapter class that adapts <code>IOIterator</code> to a regular
	 * <code>java.util.Iterator</code>. The iterator's methods throw a
	 * <code>IORuntimeException</code> with the original IOException as the
	 * cause, if any errors occured while processing the adapted IOIterator.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 * @param <E>
	 *          generic type for the iterator
	 */
	public static class IteratorAdapter<E> implements Iterator<E> {

		private final IOIterator<E> adapter;

		/**
		 * @param adapter
		 */
		public IteratorAdapter(final IOIterator<E> adapter) {
			this.adapter = adapter;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			try {
				return adapter.hasNext();
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#next()
		 */
		public E next() {
			try {
				return adapter.next();
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			try {
				adapter.remove();
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	public T next() throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove() throws IOException;
}
