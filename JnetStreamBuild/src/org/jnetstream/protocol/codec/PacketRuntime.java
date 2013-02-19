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
package org.jnetstream.protocol.codec;

import java.io.IOException;

import org.jnetstream.packet.HeaderCache;
import org.jnetstream.protocol.ProtocolEntry;

import com.slytechs.utils.memory.BitBuffer;

/**
 * Special interface which allows a packet codec to build a new packet. The
 * interface does not extend <code>Packet</code> interface, which allows
 * packet building process to be separated from the packet itself.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface PacketRuntime  {


	/**
   * @return
   */
  public HeaderCache getCache();

	/**
   * @return
   */
  public ProtocolEntry getDlt();

	/**
   * @return
	 * @throws IOException 
   */
  public BitBuffer getBuffer() throws IOException;

}
