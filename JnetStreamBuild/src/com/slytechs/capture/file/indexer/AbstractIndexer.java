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
package com.slytechs.capture.file.indexer;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jnetstream.capture.file.FileIndexer;
import org.jnetstream.capture.file.FileIterator;


import com.slytechs.utils.io.IORuntimeException;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class AbstractIndexer<G, S, R> implements FileIndexer<G, S, R>,
    Iterable<G> {

	protected final PositionIndexer indexer;

	protected final FileIterator<G, S, R> iterator;

	public AbstractIndexer(final PositionIndexer indexer,
	    final FileIterator<G, S, R> raw) throws IOException {
		this.iterator = raw;
		this.indexer = indexer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.IndexedFileModifier#abortChanges()
	 */
	public void abortChanges() throws IOException {
		this.iterator.abortChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.IndexedFileModifier#add(java.lang.Object)
	 */
	public void add(final S element) throws IOException {

		// TODO handle seek unfullfilled error
		this.iterator.seekEnd();

		this.iterator.add(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.IndexedFileModifier#add(long, S[])
	 */
	public void add(final long index, final S... elements) throws IOException {
		this.setPosition(index);

		this.iterator.addAll(elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.RawIndexer#add(long, java.nio.ByteBuffer,
	 *      java.nio.ByteBuffer)
	 */
	@SuppressWarnings("unchecked")
	public void add(final long index, final S b1, final S b2) throws IOException {
		this.setPosition(index);

		this.iterator.addAll(b1, b2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.IndexedFileModifier#add(long, java.util.List)
	 */
	public void addAll(final long index, final List<S> elements) throws IOException {
		this.setPosition(index);

		this.iterator.addAll(elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	public void close() throws IOException {
		this.iterator.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Flushable#flush()
	 */
	public void flush() throws IOException {
		this.iterator.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileIndexer#get(long)
	 */
	public G get(final long index) throws IOException {
		this.setPosition(index);

		return this.iterator.next();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.IndexedFileModifier#remove(java.util.Collection)
	 */

	public void removeAll(final Collection<R> elements) throws IOException {

		final Collection<R> translated = mapR(elements);

		this.iterator.removeAll(translated);
	}

	/**
	 * No-OP translation. Same source is returned. May be overriden in subclass to
	 * implement subclass specific translation.
	 * 
	 * @param source
	 * @return
	 * @throws IOException
	 */
	protected Collection<R> mapR(Collection<R> source) throws IOException {
		return source;
	}

	/**
	 * No-OP translation. Same source is returned. May be overriden in subclass to
	 * implement subclass specific translation.
	 * 
	 * @param source
	 * @return
	 * @throws IOException
	 */
	protected List<R> mapR(List<R> source) throws IOException {
		return source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.IndexedFileModifier#remove(long)
	 */
	public void remove(final long index) throws IOException {
		this.setPosition(index);

		this.iterator.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.IndexedFileModifier#remove(long, long)
	 */
	public void removeAll(final long index, final long count) throws IOException {
		this.setPosition(index);

		this.iterator.removeAll(count);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.IndexedFileModifier#remove(R[])
	 */
	public void removeAll(final R... elements) throws IOException {
		final R[] translated = mapR(elements);

		this.iterator.removeAll(translated);
	}

	protected R[] mapR(final R... elements) throws IOException {
		return elements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.IndexedFileModifier#removeAll()
	 */
	public void removeAll() throws IOException {
		this.iterator.removeAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.IndexedFileModifier#replace(long,
	 *      java.lang.Object)
	 */
	public void replace(final long index, final S element) throws IOException {
		this.setPosition(index);

		this.iterator.replace(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.IndexedFileModifier#retain(java.util.List)
	 */
	public void retainAll(final List<R> elements) throws IOException {
		final List<R> translated = mapR(elements);

		this.iterator.retainAll(translated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.IndexedFileModifier#retain(R[])
	 */
	public void retainAll(final R... elements) throws IOException {
		final R[] array = mapR(elements);

		this.iterator.retainAll(array);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.IndexedFileModifier#set(long,
	 *      java.lang.Object)
	 */
	public void set(final long index, final S element) throws IOException {
		this.setPosition(index);

		this.iterator.replace(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileIndexer#setAutoflush(boolean)
	 */
	public void setAutoflush(final boolean state) throws IOException {
		this.iterator.setAutoflush(state);
	}

	/**
	 * Sets the position of the raw iterator to the position of the record at
	 * index.
	 * 
	 * @param index
	 *          index of the record to position to
	 * @throws IOException
	 *           any IO errors
	 */
	protected void setPosition(final long index) throws IOException {
		final long globalPosition = this.indexer.get(index);

		this.iterator.setPosition(globalPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.IndexedFileModifier#swap(java.lang.Object,
	 *      java.lang.Object)
	 */
	public void swap(final R dst, final R src) throws IOException {

		final R dt = mapR(dst);
		final R sc = mapR(src);

		this.iterator.swap(dt, sc);
	}

	protected R mapR(R element) throws IOException {
		return element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileIndexer#size()
	 */
	public long size() throws IOException {
		return indexer.size();
	}

	/**
	 * Iterator that will iterate over all packets of this indexer. Since any the
	 * iterator relying on IO operations to aquire various data, a special
	 * IORuntimeException may be thrown which is a proxy for the originating
	 * IOExceptions that may have been thrown in order to aquire data.
	 * 
	 * @return iterator that will iterate over all indexes within this indexer and
	 *         return as if get() had been called
	 * @throws IORuntimeException
	 *           a runtime counter part to normal IOException
	 */
	public Iterator<G> iterator() {
		return new Iterator<G>() {

			private long i = 0;

			public boolean hasNext() {
				try {
					return i < size();
				} catch (IOException e) {
					throw new IORuntimeException(e);
				}
			}

			public G next() {
				try {
					return get(i++);
				} catch (IOException e) {
					throw new IORuntimeException(e);

				}
			}

			public void remove() {
				try {
					setPosition(--i);
					iterator.remove();
				} catch (IOException e) {
					throw new IORuntimeException(e);
				}
			}

		};
	}

	public long mapIndexToPosition(long index) throws IOException {
		final long globalPosition = this.indexer.get(index);

		return globalPosition;
  }
	
	public Object keepInMemory(long start, long length) throws IOException {
		return indexer.keepInMemory(start, length);
	}
}
