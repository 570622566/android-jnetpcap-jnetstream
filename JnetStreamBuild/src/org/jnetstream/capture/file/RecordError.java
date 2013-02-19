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
package org.jnetstream.capture.file;

/**
 * Holds any errors that have been encountered with any of the records within a
 * capture file. When an error is encountered, an error is generated that is
 * returned by some methods within the API. You can query the errors to see what
 * happened. For example the RawIterator.skipOverErrors() method returns an
 * array of errors that may have happened and no exceptions are thrown.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class RecordError {
	private final long position;

	private final String message;

	private Exception exception = null;

	/**
	 * @param position
	 * @param message
	 * @param exception
	 */
	public RecordError(final long position, final String message,
	    Exception exception) {
		this.position = position;
		this.message = message;
		this.exception = exception;
	}

	/**
	 * @param position
	 * @param message
	 */
	public RecordError(final long position, final String message) {
		this.position = position;
		this.message = message;
	}

	/**
   * @return the exception
   */
  public final Exception getException() {
  	return this.exception;
  }

	/**
   * @param exception the exception to set
   */
  public final void setException(Exception exception) {
  	this.exception = exception;
  }

	/**
   * @return the message
   */
  public final String getMessage() {
  	return this.message;
  }

	/**
   * @return the position
   */
  public final long getPosition() {
  	return this.position;
  }

}
