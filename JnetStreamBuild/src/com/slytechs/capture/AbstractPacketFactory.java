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
package com.slytechs.capture;

import org.jnetstream.packet.Packet;
import org.jnetstream.packet.PacketFactory;
import org.jnetstream.protocol.Protocol;

import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class AbstractPacketFactory<T extends Packet> implements
    PacketFactory<T> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.PacketFactory#newPacket(com.slytechs.utils.memory.BitBuffer,
	 *      org.jnetstream.protocol.Protocol)
	 */
	public T newPacket(BitBuffer buffer, Protocol dlt) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

}
