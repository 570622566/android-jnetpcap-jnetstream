/**
 * Copyright (C) 2008 Sly Technologies, Inc. This library is free software; you
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
package com.slytechs.jnetstream.packet;

import java.nio.ByteBuffer;

import org.jnetstream.capture.LiveCaptureDevice;
import org.jnetstream.capture.LivePacket;
import org.jnetstream.capture.LivePacketFactory;
import org.jnetstream.protocol.Protocol;
import org.jnetstream.protocol.ProtocolEntry;

import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class DefaultLivePacketFactory implements LivePacketFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.PacketFactory#newPacket(com.slytechs.utils.memory.BitBuffer,
	 *      org.jnetstream.protocol.Protocol)
	 */
	public LivePacket newPacket(BitBuffer buffer, Protocol dlt) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.LivePacketFactory#newLivePacket(org.jnetstream.protocol.Protocol,
	 *      java.nio.ByteBuffer, com.slytechs.utils.memory.BitBuffer, long, int,
	 *      int, int, org.jnetstream.capture.LiveCaptureDevice)
	 */
	public LivePacket newLivePacket(ProtocolEntry dlt, ByteBuffer bytes,
	    BitBuffer bits, long seconds, int useconds, int caplen, int len,
	    LiveCaptureDevice device) {

		return new DefaultLivePacket(dlt, bits, seconds, useconds, caplen, len,
		    device);

	}

}
