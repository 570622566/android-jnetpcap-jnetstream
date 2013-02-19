/**
 * $Id$
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
package com.slytechs.utils.iosequence;

import java.util.Iterator;
import java.util.Random;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class RandomNumberGenerator<T extends Number> implements Input<T> {
	private boolean isClosed = false;
	private Random generator = new Random(System.currentTimeMillis());
	private final Class<T> c;
	
	public RandomNumberGenerator(Class<T> c) {
		this.c = c;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.jnetstream.iosequence.Input#get()
	 */
	@SuppressWarnings("unchecked")
	public T get() throws InterruptedException {
		
		if (isClosed == true) {
			throw new IllegalStateException("Current input source is closed");
		}
		
		if (c == Integer.class) {
			return (T) (Number) generator.nextInt();
		} else if (c == Float.class) {
			return (T) (Number) generator.nextFloat();
		} else if (c == Double.class) {
			return (T) (Number) generator.nextDouble();
		} else {
			return (T) (Number) generator.nextLong();
		}
	}

	/* (non-Javadoc)
	 * @see com.slytechs.jnetstream.iosequence.Input#close()
	 */
	public void close() {
		isClosed = true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private T data;
			
			public boolean hasNext() {
				try {
					data = get();
				} catch (InterruptedException e) {
				}
				return isClosed != true;
			}

			public T next() {
				return data;
			}

			public void remove() {
				throw new IllegalStateException("This is an immutable iterator");
			}
			
		};
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Input<Integer> ints = new RandomNumberGenerator<Integer>(Integer.class);

		for (Integer i: ints) {
			System.out.println("i=" + i);
		}
	}

}
