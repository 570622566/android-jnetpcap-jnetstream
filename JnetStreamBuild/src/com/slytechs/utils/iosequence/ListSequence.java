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
package com.slytechs.utils.iosequence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * <P>Adapts a standard java collection List as an Input sequence.</P>
 * <PRE>
 * List&lt;Integer&gt; list = new ArrayList&lt;Integer&gt;();
 * list.add(10);
 * list.add(20);
 * 
 * Input&lt;Integer&gt; in = new ListAdapter&lt;Integer&gt;(list);
 * 
 * for(Integer i: in) {
 *   System.out.println("element=" + i);
 * }
 * </PRE>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class ListSequence<T> implements Input<T> {
	private final List<T> list;
	private final Iterator<T> primary;
	private boolean closed = false;
	
	public ListSequence(List<T> list) {
		this.list = list;
		primary = list.iterator();
	}

	/**
	 * Returns the next element of the sequence.
	 * @return
	 *   Next element;
	 *   
	 * @see com.slytechs.utils.iosequence.Input#get()
	 */
	public T get() throws InterruptedException {
		if (closed) {
			throw new IllegalStateException("This input sequence is closed");
		}
		
		T data = null;
		if (primary.hasNext()) {
			data = primary.next();
		}
		
		return data;
	}

	/**
	 * Closes this sequence.
	 * 
	 * @see com.slytechs.utils.iosequence.Input#close()
	 */
	public void close() {
		closed = true;
	}

	/**
	 * Returns the iterator for this input sequence.
	 * 
	 * @return
	 *   Iterator for this input sequence.
	 *   
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator() {
		return list.iterator();
	}

	public static void main(String[] args) {
		 List<Integer> list = new ArrayList<Integer>();
		 list.add(10);
		 list.add(20);
		 Input<Integer> in = new ListSequence<Integer>(list);
		 for(Integer i: in) {
		   System.out.println("element=" + i);
		 }
		 
	}
}
