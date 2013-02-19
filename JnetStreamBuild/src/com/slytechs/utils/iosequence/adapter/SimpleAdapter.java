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
package com.slytechs.utils.iosequence.adapter;

import com.slytechs.utils.iosequence.Input;
import com.slytechs.utils.iosequence.Output;
import com.slytechs.utils.iosequence.SequenceElement;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class SimpleAdapter<T extends SequenceElement> extends Adapter<T, T> {

	/**
	 * 
	 * @param in
	 * @param out
	 */
	public SimpleAdapter(Input<T> in, Output<T> out) {
		super(in, out);
		// TODO Auto-generated constructor stub
	}
	

	/**
	 * @param in
	 * @param out
	 * @param monitor
	 */
	public SimpleAdapter(Input<T> in, Output<T> out, Bridge<T, T> bridge) {
		super(in, out, bridge);
		// TODO Auto-generated constructor stub
	}



	@Override
	public T convert(T data) {
		return data;
	}

}
