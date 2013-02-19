/**
 * Copyright (C) 2008 Sly Technologies, Inc.
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
package org.jnetstream.protocol.lan;

import org.jnetstream.protocol.Protocol;
import org.jnetstream.protocol.ProtocolBinding;
import org.jnetstream.protocol.ProtocolRegistry;
import org.jnetstream.protocol.tcpip.Ip4;
import org.jnetstream.protocol.tcpip.Tcpip;

import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public enum LanBindings implements ProtocolBinding {
	/**
	 * Ip4 binding #1
	 */
	IP4_BINDING1(Lan.Ethernet2, Tcpip.Ip4) {
		public int resolve(BitBuffer buffer, int source, int sink) {
			final int type =
			    buffer.getBits(Ethernet2.StaticField.TYPE.getOffset() + sink,
			        Ethernet2.StaticField.TYPE.getLength());
			if (type != 0x800) {
				return -1;
			}

			final int ver =
			    buffer.getBits(Ip4.StaticField.VER.getOffset() + source,
			        Ip4.StaticField.VER.getLength());
			if (ver != 4) {
				return -1;
			}

			final int hlen =
			    buffer.getBits(Ip4.StaticField.HLEN.getOffset() + source,
			        Ip4.StaticField.HLEN.getLength());

			return hlen * 4 * 8; // In bits
		}

	},
	;

	private final Protocol source;
	private final Protocol sink;

	private LanBindings(Protocol sink, Protocol source) {
		this.source = source;
		this.sink = sink;
	}

	/**
   * @return the source
   */
  public final Protocol getSource() {
  	return this.source;
  }

	/**
   * @return the sink
   */
  public final Protocol getSink() {
  	return this.sink;
  }
}
