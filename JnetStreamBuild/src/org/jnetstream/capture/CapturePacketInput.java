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
 * CapturePacketInput interface reads CapturePacket objects from underlying
 * storage or stream which is defined by the class implementing this interface.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface CapturePacketInput<T extends CapturePacket> {

	/**
	 * Read and return a StreamPacket object. The class that implements this
	 * interface defines where the object is "read" from.
	 * 
	 * @return the CapturePacket object read from storage or stream
	 * @throws IOException
	 *           if any of the usual InputOutput related exceptions occur
	 */
	public T readPacket() throws IOException;
}
