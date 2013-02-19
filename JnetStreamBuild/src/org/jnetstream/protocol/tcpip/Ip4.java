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
import com.slytechs.utils.net.Ip4Address;


/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public interface Ip4 extends Header {
	
	public enum StaticField implements DataField {
		VER(0, 4),
		HLEN(4,4),
		TOS(8,8),
		LENGTH(16,16),
		ID(32,16),
		FLAGS(48,3),
		OFFSET(51,13),
		TTL(64,8),
		PROTOCOL(9 * 8,8),
		CRC(80,16),
		SOURCE(96,32),
		DESTINATION(128,32),
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
	
	public enum StaticProperty {
		
	}
	
	public enum DynamicProperty {
		
	}
	

	public byte ver();
	public void ver(byte value);
	
	public byte hlen();
	public void hlen(byte value);
	
	public byte tos();
	public void tos(byte value);
	
	public short length();
	public void length(short value);
	
	public short id();
	public void id(short value);
	
	public byte flags();
	public void flags(byte value);
	
	public short offset();
	public void offset(short value);
	
	public byte ttl();
	public void ttl(byte value);
	
	public byte protocol();
	public void protocol(byte value);
	
	public Crc16 crc();
	public void crc(short value);
	
	public Ip4Address source();
	public void source(Ip4Address value);
	
	public byte[] getSourceByteArray();
	public void setSourceByteArray(byte[] value);
	
	public Ip4Address destination();
	public void destination(Ip4Address value);
	
	public byte[] getDestinationByteArray();
	public void setDestinationByteArray(byte[] value);

}
