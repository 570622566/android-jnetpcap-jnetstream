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
 * Allows writting of CapturePackets. The use of this class can be java IO
 * streams or some other types of implementations. The CapturePacket is
 * serialized in a propriatory way, not related java IO mechanism, and
 * deserialized into a StreamPacket. StreamPacket once the transmitted accross a
 * stream, looses its reference and its relationship to the original storage of
 * the packet and the packet once modified can not be written backout to the
 * original storage. Also notice that the packet may have come from a live
 * network capture which does not have any storage at all.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface CapturePacketOutput<T extends Packet> {

	/**
	 * Writes a CapturePacket to the underlying storage or stream. The class that
	 * implements this interface defines how the object is written.
	 * 
	 * @param packet
	 *          the packet to be written
	 * @throws IOException
	 *           any of the usual Input/Output related exceptions
	 */
	public void writePacket(T packet) throws IOException;
}
