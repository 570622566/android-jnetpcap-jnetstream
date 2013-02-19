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
package com.slytechs.jnetstream.packet;

import org.jnetstream.packet.DataField;

import com.slytechs.utils.crypto.Crc16;
import com.slytechs.utils.memory.BitBuffer;
import com.slytechs.utils.net.EUI48;
import com.slytechs.utils.net.Ip4Address;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public abstract class AbstractData {
	
	protected BitBuffer bits;
	protected int offset;
	
	public AbstractData(BitBuffer bits, int offset) {
		this.bits = bits;
		this.offset = offset;
	}
	
	protected byte readByte(int length) {
  	return  (byte) bits.getBits(offset, length);
	}
	
	protected byte readByte(DataField field) {
  	return  (byte) bits.getBits(offset + field.getOffset(), field.getLength());
	}
	
	protected int readInt(int length) {
  	return  bits.getBits(offset, length);
	}

	
	protected int readInt(DataField field) {
  	return  bits.getBits(offset + field.getOffset(), field.getLength());
	}
	
	protected short readShort(int length) {
  	return  (short) bits.getBits(offset, length);
	}
	
	protected short readShort(DataField field) {
  	return  (short) bits.getBits(offset + field.getOffset(), field.getLength());
	}
	
	protected Crc16 readCrc16() {
  	return  new Crc16(bits.getBits(offset, 16));
	}
	
	protected Crc16 readCrc16(DataField field) {
  	return  new Crc16(bits.getBits(offset + field.getOffset(), field.getLength()));
	}
	
	protected byte[] readByteArray(DataField field) {
		final byte[] b = new byte[field.getLength() / 8];

		bits.get(offset + field.getOffset(), b);

		return b;
	}
	
	protected EUI48 readEUI48() {
		final byte[] b = new byte[6];

		bits.get(offset, b);

		return new EUI48(b);
	}

	
	protected EUI48 readEUI48(DataField field) {
		final byte[] b = new byte[field.getLength() / 8];

		bits.get(offset + field.getOffset(), b);

		return new EUI48(b);
	}
	
	protected Ip4Address readIp4Address() {
		final byte[] b = new byte[4];

		bits.get(offset, b);

		return new Ip4Address(b);
	}
	
	protected Ip4Address readIp4Address(DataField field) {
		final byte[] b = new byte[field.getLength() / 8];

		bits.get(offset + field.getOffset(), b);

		return new Ip4Address(b);
	}

	protected byte[] readByteArray(DataField field, byte[] b) {
		bits.get(offset + field.getOffset(), b);

		return b;
	}

	public int getOffset() {
  	return this.offset;
  }
	
	public Object toUnsigned(Object value) {
		
		if (value instanceof Byte) {
			return (((Number) value).intValue() & 0xFF);
			
		} else if (value instanceof Short) {
			return (((Number) value).intValue() & 0xFFFF);
			
		} else if (value instanceof Integer) {
			return (((Number) value).longValue() & 0xFFFFFFFFL);
		}
		
		return value;
	}

}
