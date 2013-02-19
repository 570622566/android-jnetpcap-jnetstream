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
package com.slytechs.utils.io;

import java.io.IOException;

/**
 * An exception that relays an IO exception as a runtime exception.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public class IORuntimeException
    extends RuntimeException {

	/**
   * 
   */
  private static final long serialVersionUID = -8736814398130241451L;
	private final IOException cause;


	/**
	 * @param cause
	 */
	public IORuntimeException(IOException cause) {
		super(cause);
		
		this.cause = cause;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public IORuntimeException(String message, IOException cause) {
		super(message, cause);
		
		this.cause = cause;
	}
	
	/**
	 * Returns the original ioException that caused this runtime counter part to
	 * be rethrown.
	 * 
	 * @return oringinal IOException that was the cause of this exception
	 */
	public IOException ioException() {
		return cause;
	}

}
