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
package org.jnetstream.capture.file;

import org.jnetstream.capture.FilePacket;
import org.jnetstream.packet.PacketFactory;
import org.jnetstream.protocol.Protocol;

import com.slytechs.utils.memory.BitBuffer;

/**
 * A factory for file based packets. A more specialized factory that allows
 * packet to be created that are linked to a file.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface FilePacketFactory<T extends FilePacket> extends
    PacketFactory<T> {

	/**
	 * @param buffer
	 * @param position
	 * @param device
	 * @return
	 */
	public T newPacket(BitBuffer buffer, Protocol dlt, long position);

}
