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

import org.jnetstream.packet.Packet;

/**
 * <P>
 * A captured network packet. The actual packet data may have been captured live
 * from a network interface or device or came from a file, streamed . That is
 * this interface does not neccessarily represent a 1-to-1 relationship with any
 * record contained within the capture file.
 * </P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface CapturePacket extends Packet {

	/**
	 * Returns information about the device that captured this packet. This is
	 * typically some network interface on this or some other system. The
	 * structure also has ProtocolConst information about the first data header
	 * within the packet.
	 * 
	 * @return device that captured this packet
	 */
	// public CaptureDevice getCaptureDevice();
	/**
	 * Gets the number of octets that were included as packet data after the
	 * packet was captured. This number may be less then the actual length of the
	 * packet as it was seen on the wire. This is sometimes also called the
	 * "snaplength" or "slice" of a packet. Truncating packet data in such a way
	 * allows greater storage efficiency when only the beginning of a packet data
	 * is needed. The beginning of a packet data contains the important protocol
	 * headers which can be used to determine the packet type and other important
	 * information. This is typically enough of accounting type application or
	 * monitoring applications.
	 * 
	 * @return number of octets that were captured and stored in the packet data
	 *         buffer
	 * @throws IOException
	 */
	public long getIncludedLength() throws IOException;

	/**
	 * Gets the number of octets of the actual packet as it was originally seen at
	 * the time of the capture or on the wire. This number may not neccessarily
	 * correspond with {@link #getIncludedLength()} method since sometimes packets
	 * are "sliced", "truncated", "snapped" etc to preserve storage space.
	 * 
	 * @return number of octets of the original packet before any truncation of
	 *         its data buffer
	 * @throws IOException
	 */
	public long getOriginalLength() throws IOException;

	/**
	 * A reference to the capture device that the packet was captured on.
	 * 
	 * @return device the packet was captured on
	 */
	public CaptureDevice getCaptureDevice();

}
