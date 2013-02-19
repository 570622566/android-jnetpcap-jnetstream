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

import java.io.PrintStream;



/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class PrintOutput<T> implements Output<T> {
	
	private final PrintStream out;

	public PrintOutput(PrintStream out) {
		this.out = out;
		
	}

	public void put(T data) throws InterruptedException {
		out.println(data.toString());
	}

	/* (non-Javadoc)
	 * @see com.slytechs.jnetstream.iosequence.Output#close()
	 */
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
