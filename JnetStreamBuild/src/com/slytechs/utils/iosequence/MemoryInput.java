/**
 * $Id$
 * Copyright (C) 2006 Mark Bednarczyk
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
package com.slytechs.utils.iosequence;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;



/**
 * @author Mark Bednarczyk
 *
 */
public class MemoryInput<T> implements Input<T> {
	
	private final BlockingQueue<T> queue = new ArrayBlockingQueue<T>(100, true);
	
	public MemoryInput(Class<T> c) throws HandlerNotFound {
	}

	public T get() throws InterruptedException {
		return queue.take();
	}

	public void addBuffer(ByteBuffer buffer) {
	}
	
	public void addBuffer(byte[] buffer) {
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			public boolean hasNext() {
				return true;
			}

			public T next() {
				try {
					return queue.take();
				} catch (InterruptedException e) {
				}
				
				return null;
			}

			public void remove() {
				throw new IllegalStateException("This is an immutable iterator");
			}
			
		};
	}

	/* (non-Javadoc)
	 * @see com.slytechs.jnetstream.iosequence.Input#close()
	 */
	public void close() {
		queue.clear();
	}
}
