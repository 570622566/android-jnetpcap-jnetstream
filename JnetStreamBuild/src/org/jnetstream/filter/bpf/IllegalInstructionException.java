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
package org.jnetstream.filter.bpf;

import org.jnetstream.filter.FilterException;

/**
 * An illegal BPF byte-code instruction has been encountered.
 *  
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class IllegalInstructionException extends FilterException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5274996672193519270L;

	/**
	 * 
	 */
	public IllegalInstructionException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public IllegalInstructionException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public IllegalInstructionException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public IllegalInstructionException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
