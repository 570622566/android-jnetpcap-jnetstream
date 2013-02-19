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
package org.jnetstream.protocol;

/**
 * An error occured during loading, unloading or discovery
 * of a protocol binding.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class ProtocolBindingException extends ProtocolException {

	private static final long serialVersionUID = 2064849909091077418L;

	/**
	 * 
	 */
	public ProtocolBindingException() {
		super();
	}

	/**
	 * @param message
	 */
	public ProtocolBindingException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ProtocolBindingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public ProtocolBindingException(Throwable cause) {
		super(cause);
	}

}
