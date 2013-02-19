/**
 * $Id$
 *
 * Copyright (C) 2006 Mark Bednarczyk, Sly Technologies, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place,
 * Suite 330, Boston,
 * MA 02111-1307 USA
 * 
 */
package com.slytechs.utils.net;

import java.net.InetSocketAddress;
import java.sql.Timestamp;

import com.slytechs.utils.io.DataBuffer;

/**
 * Utility class for dispatching captured PDU
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class NetPdu {
	DataBuffer buffer;

	InetSocketAddress remoteSocket;

	InetSocketAddress localSocket;

	Timestamp captureTime;

	/**
	 * @param buffer
	 * @param socket
	 * @param socket2
	 */
	public NetPdu(DataBuffer buffer, InetSocketAddress remote,
			InetSocketAddress local) {
		// TODO Auto-generated constructor stub
		this.buffer = buffer;
		localSocket = local;
		remoteSocket = remote;
		captureTime = new Timestamp(System.currentTimeMillis());
	}
	
	public void clear() {
		buffer = null;
		localSocket = null;
		remoteSocket = null;
		captureTime = null;
	}

	/**
	 * @return Returns the buffer.
	 */
	public DataBuffer getBuffer() {
		return buffer;
	}

	/**
	 * @param buffer
	 *            The buffer to set.
	 */
	public void setBuffer(DataBuffer buffer) {
		this.buffer = buffer;
	}

	/**
	 * @return Returns the localSocket.
	 */
	public InetSocketAddress getLocalSocket() {
		return localSocket;
	}

	/**
	 * @param localSocket
	 *            The localSocket to set.
	 */
	public void setLocalSocket(InetSocketAddress localSocket) {
		this.localSocket = localSocket;
	}

	/**
	 * @return Returns the remoteSocket.
	 */
	public InetSocketAddress getRemoteSocket() {
		return remoteSocket;
	}

	/**
	 * @param remoteSocket
	 *            The remoteSocket to set.
	 */
	public void setRemoteSocket(InetSocketAddress remoteSocket) {
		this.remoteSocket = remoteSocket;
	}

	/**
	 * @return Returns the captureTime.
	 */
	public Timestamp getCaptureTime() {
		return captureTime;
	}

	/**
	 * @param captureTime
	 *            The captureTime to set.
	 */
	public void setCaptureTime(Timestamp captureTime) {
		this.captureTime = captureTime;
	}
	
}