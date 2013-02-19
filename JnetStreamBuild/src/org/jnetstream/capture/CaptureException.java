/**
 * Copyright (C) 2007 Sly Technologies, Inc. This library is free software; you
 * can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version. This
 * library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.jnetstream.capture;

import java.io.IOException;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class CaptureException
    extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2927732402664836082L;

	private final Throwable cause;

	/**
	 * 
	 */
	public CaptureException() {

		this.cause = null;
	}

	/**
	 * @param message
	 */
	public CaptureException(String message) {
		super(message);

		this.cause = null;
	}

	/**
	 * @param cause
	 */
	public CaptureException(Throwable cause) {
		super(cause.getMessage());
		this.cause = cause;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CaptureException(String message, Throwable cause) {
		super(message);
		this.cause = cause;
	}

	/**
	 * @return the cause
	 */
	public final Throwable getCause() {
		return this.cause;
	}

}
