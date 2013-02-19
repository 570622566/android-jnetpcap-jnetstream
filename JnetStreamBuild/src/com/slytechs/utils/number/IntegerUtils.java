/**
 * Copyright (C) 2007 Sly Technologies, Inc.
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
package com.slytechs.utils.number;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class IntegerUtils {

	public final static short swapBytes(short i) {
		short r = 0;
		r |= (i & 0x00FF0000) >>> 8;
		r |= (i & 0x0000FF00) << 8;

		return r;
	}

	public final static int swapBytes(int i) {
		int r = 0;
		r = ((i & 0xFF000000) >>> 24);
		r |= ((i & 0x00FF0000) >>> 8);
		r |= ((i & 0x0000FF00) << 8);
		r |= ((i & 0x000000FF) << 24);

		return r;
	}
	
	public final static byte readByte(byte[] b, int offset) {
		return b[offset];
	}
	public final static int readUByte(byte[] b, int offset) {
		int v = b[offset];
		
		return (v < 0) ? v + 256: v;
	}
	
	public final static byte readByte(ByteBuffer b, int offset) {
		return b.get(offset);
	}
	
	public final static int readUByte(ByteBuffer b, int offset) {
		int v = b.get(offset);
		
		return (v < 0) ? v + 256: v;
	}

	public final static int readUShort(byte[] b, int offset, ByteOrder encoding) {
		if (b.length - offset < 4) {
			throw new IllegalArgumentException(
					"Can not read unsigned int from byte array smaller then 4 bytes from offset");
		}

		if (encoding == ByteOrder.BIG_ENDIAN) {
			return readUShortBE(b, offset);
		} else {
			return readUShortLE(b, offset);
		}
	}

	public final static int readUShortBE(byte[] b, int offset) {
		if (b.length - offset < 4) {
			throw new IllegalArgumentException(
					"Can not read unsigned int from byte array smaller then 4 bytes from offset");
		}

		int v = 0;

		v |= readUByte(b, offset + 0) << 8;
		v |= readUByte(b, offset + 1);

		return v;
	}
	
	public final static int readUShortLE(byte[] b, int offset) {
		if (b.length - offset < 4) {
			throw new IllegalArgumentException(
					"Can not read unsigned int from byte array smaller then 4 bytes from offset");
		}

		int v = 0;

		v |= readUByte(b, offset + 1) << 8;
		v |= readUByte(b, offset + 0);

		return v;
	}


	public final static int readUShort(ByteBuffer b, int offset) {
		if (b.capacity() - offset < 4) {
			throw new IllegalArgumentException(
					"Can not read unsigned int from byte array smaller then 4 bytes from offset");
		}

		int v = b.getShort(offset);
		if (v < 0) {
			v += Short.MAX_VALUE * 2 + 1;
		}

		return v;
	}

	public final static long readUInt(byte[] b, int offset, ByteOrder encoding) {
		if (encoding == ByteOrder.BIG_ENDIAN) {
			return readUIntBE(b, offset);
		} else {
			return readUIntLE(b, offset);
		}
	}

	public final static long readUIntBE(byte[] b, int offset) {
		if (b.length - offset < 4) {
			throw new IllegalArgumentException(
					"Can not read unsigned int from byte array smaller then 4 bytes from offset");
		}

		long v = 0;
		v |= readUByte(b, offset + 0) << 24;
		v |= readUByte(b, offset + 1) << 16;
		v |= readUByte(b, offset + 2) << 8;
		v |= readUByte(b, offset + 3);

		return v;
	}
	
	public final static long readUIntLE(byte[] b, int offset) {
		if (b.length - offset < 4) {
			throw new IllegalArgumentException(
					"Can not read unsigned int from byte array smaller then 4 bytes from offset");
		}

		long v = 0;
		v |= readUByte(b, offset + 3) << 24;
		v |= readUByte(b, offset + 2) << 16;
		v |= readUByte(b, offset + 1) << 8;
		v |= readUByte(b, offset + 0);

		return v;
	}


	public final static long readUInt(ByteBuffer b, int offset) {
		if (b.capacity() - offset < 4) {
			throw new IllegalArgumentException(
					"Can not read unsigned int from byte array smaller then 4 bytes from offset");
		}

		long v = b.getInt(offset);
		if (v < 0) {
			v += Integer.MAX_VALUE * 2 + 1;
		}

		return v;
	}

	/**
	 * @param buffer
	 * @param i
	 * @param length
	 */
	public final static void putUInt(ByteBuffer buffer, int offset, long length) {
		buffer.put(offset + 0, (byte) ((length & 0xFF000000) >>> 24));
		buffer.put(offset + 1, (byte) ((length & 0x00FF0000) >>> 16));
		buffer.put(offset + 2, (byte) ((length & 0x0000FF00) >>> 8));
		buffer.put(offset + 3, (byte) ((length & 0x000000FF)));
	}

	public final static long readLong(byte[] b) {
		return readLong(b, 0);
	}

	public final static long readLong(byte[] b, int offset) {

		return (((long) b[0] << 56) + ((long) (b[1] & 255) << 48)
				+ ((long) (b[2] & 255) << 40) + ((long) (b[3] & 255) << 32)
				+ ((long) (b[4] & 255) << 24) + ((b[5] & 255) << 16)
				+ ((b[6] & 255) << 8) + ((b[7] & 255) << 0));
	}

	public final static long readLong(ByteBuffer b, int offset) {
		return b.getLong(offset);
	}
}
