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


/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class FilterIO<D> implements Input<D>, Output<D> {
	
	private final Output<D> out;
	private final Input<D> in;
	protected final String filter;

	public FilterIO(Input<D> in, String filter) {
		this.in = in;
		this.out = null;
		this.filter = filter;
		
	}
	
	public FilterIO(Output<D> out, String filter) {
		this.in = null;
		this.out = out;
		this.filter = filter;
	}

	public D get() throws InterruptedException {
		D i;
		do {
			i = in.get();
		
		} while (filter(i) == false);
		
		return i;
		
	}

	protected boolean filter(D i) {
		return true;
	}

	public void put(D data) throws InterruptedException {
		if (filter(data) == false) {
			return;
		}
		
		out.put(data);
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<D> iterator() {
		return new Iterator<D>() {

			private D data;
			public boolean hasNext() {
				try {
					data = get();
				} catch (InterruptedException e) {
				}
				
				return data != null;
			}

			public D next() {
				return data;
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
	}

}
