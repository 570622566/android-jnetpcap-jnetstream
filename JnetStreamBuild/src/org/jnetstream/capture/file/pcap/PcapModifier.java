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
package org.jnetstream.capture.file.pcap;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jnetstream.capture.file.FileModifier;


/**
 * Extends the more generic FileModifier that only has capability to remove
 * records from a capture file, and supplied methods to create and add new
 * records to existing file.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface PcapModifier extends FileModifier<ByteBuffer, PcapPacket> {


	/**
	 * Adds a new packet record to the Pcap file. The included packet length is
	 * calculated based on the size of data in the buffer. The formula uses
	 * ByteBuffer's position and limit properties to calculate this value.
	 * 
	 * @param b
	 *          buffer containing the packet content
	 * @param s
	 *          capture timestamp in seconds
	 * @param n
	 *          capture timestamp in nanosecond fraction of a second with value
	 *          value of 0 to 999,999,999
	 * @param o
	 *          original packet length in octets as it was seen on the network
	 *          wire included packet length
	 * @throws IOException
	 */
	public void addPacketRecord(ByteBuffer b, long s, int n, int o)
	    throws IOException;


}
