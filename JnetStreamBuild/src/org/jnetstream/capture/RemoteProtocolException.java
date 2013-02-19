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
package org.jnetstream.capture;

/**
 * <P>If a protocol error occures between CaptureSender and CaptureReceiver
 * this exception will contain specific information about the failure. Failures 
 * may be as a result of an IO error if cought will be reported by throwing
 * the standard IOException. If an IOException was thrown previously and stream
 * was not able to properly recover from the earlier error, any follow up operations
 * on the sender or receiver will very likely generate protocol errors.</P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class RemoteProtocolException extends Exception {

	private static final long serialVersionUID = -5216634668763532135L;

	/**
	 * 
	 */
	public RemoteProtocolException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public RemoteProtocolException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RemoteProtocolException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public RemoteProtocolException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
