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
 * Protocol contains valid NPL syntax, but declaration is
 * in corrent in the corresponding circumstance. For example
 * Multiple protocol defintions contained within the same file,
 * or some other kind of statement encountered instead of 
 * expected NPL header statement.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class ProtocolDeclarationException extends ProtocolException {

	
	private static final long serialVersionUID = 5939973681605039340L;

	/**
	 * 
	 */
	public ProtocolDeclarationException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ProtocolDeclarationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ProtocolDeclarationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public ProtocolDeclarationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
