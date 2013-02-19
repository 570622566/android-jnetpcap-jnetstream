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
package org.jnetstream.capture.file.snoop;

import org.jnetstream.protocol.Protocol;
import org.jnetstream.protocol.ProtocolRegistry;

/**
 * Pcap data link type that specified the type of the first protocol found
 * within the packet's content buffer.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum SnoopDLT {
	IEEE802DOT3(0),
	IEEE802DOT4(1),
	IEEE802DOT5(2),
	IEEE802DOT6(3),
	Ethernet(4),
	HDLC(5),
	CharSynchronous(6),
	IBMChannel2Channel(7),
	FDDI(8),
	Other(9),
	
	;

	private final int intValue;

	private SnoopDLT(int v) {
		intValue = v;
	}

	/**
	 * Returns the integer representation of this DLT
	 * 
	 * @return DLT as a PCAP compatible integer
	 */
	public long intValue() {
		return intValue;
	}

	/**
	 * Returns the mapped jNS protocol constant for this PcapDLT
	 * @return
	 */
	public Protocol asProtocol() {
		return ProtocolRegistry.translate(SnoopFile.class, intValue);
	}

	/**
	 * Converts a jNS protocol constant into a PcapDLT specific value
	 * 
	 * @param p
	 *          jNS protocol constant
	 * @return corresponding PcapDLT value
	 */
	public static SnoopDLT asPcap(Protocol p) {
		int raw = ProtocolRegistry.translate(SnoopFile.class, p);
		
		for (SnoopDLT snoop : values()) {
			if (snoop.intValue == raw) {
				return snoop;
			}
		}

		return null;
	}

	/**
   * @param raw
   * @return
   */
  public static Protocol asConst(long raw) {
  	
  	return ProtocolRegistry.translate(SnoopFile.class, (int) raw);
  }

}
