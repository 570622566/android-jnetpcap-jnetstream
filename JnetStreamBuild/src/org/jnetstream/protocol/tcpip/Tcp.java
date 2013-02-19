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
package org.jnetstream.protocol.tcpip;

import org.jnetstream.packet.DataField;
import org.jnetstream.packet.Header;

import com.slytechs.utils.crypto.Crc16;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public interface Tcp extends Header {
	public enum StaticField implements DataField {
		SOURCE(0, 16),
		DESTINATION(16,16),
		SEQUENCE(32,32),
		ACK(64,32),
		OFFSET(96, 4),
		RESERVED(100, 6),
		FLAGS(106,6),
		WINDOW(112, 16),
		CRC(128, 16),
		URGENT(144, 16)
		;
		private final int offset;
		private final int length;

		private StaticField(int offset, int length) {
			this.offset = offset;
			this.length = length;
			
		}

		/* (non-Javadoc)
     * @see com.slytechs.jnetstream.packet.DataField#getLength()
     */
    public int getLength() {
	    return length;
    }

		/* (non-Javadoc)
     * @see com.slytechs.jnetstream.packet.DataField#getOffset()
     */
    public int getOffset() {
	    return offset;
    }
	}

	public short source();
	public short destination();
	public int sequence();
	public int ack();
	public byte offset();
	public byte reserved();
	public byte flags();
	public short window();
	public Crc16 crc();
	public short urgent();
}
