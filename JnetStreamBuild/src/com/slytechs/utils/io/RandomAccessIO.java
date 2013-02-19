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
package com.slytechs.utils.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class RandomAccessIO {

	private final RandomAccessFile io;

	/**
	 * 
	 */
	public RandomAccessIO(RandomAccessFile io) {
		this.io = io;
	}
	
	public final byte readUNibble(boolean lower, long position) throws IOException {
		io.seek(position);
		
		return readUNibble(lower);
	}
	
	public final byte readUNibble(boolean lower) throws IOException {
		int i = io.read();
		
		if (lower) {
			return (byte) (i & 0x0F);
			
		} else {
			return (byte) ((i & 0xF0) >>> 4);
		}
	}
	
	public final byte readByte(long position) throws IOException {
		io.seek(position);
		return readByte();
	}

	public final byte readByte() throws IOException {
		return io.readByte();
	}
	
	public final short readShort(long position) throws IOException {
		io.seek(position);
		return readShort();
	}
	
	public final short readShort() throws IOException {
		return io.readShort();
	}
	
	public final int readInt(long position) throws IOException {
		io.seek(position);
		return readInt();
	}
	
	public final int readInt() throws IOException {
		return io.readInt();
	}
	
	public final int readLong(long position) throws IOException {
		io.seek(position);
		return readLong();
	}
	
	public final int readLong() throws IOException {
		return readLong();
	}
	
	public final short readUByte(long position) throws IOException {
		io.seek(position);
		return readUByte();
	}
	
	public final short readUByte() throws IOException {
		return (short) io.readUnsignedByte();
	}
	
	
	public final int readUShort(long position) throws IOException {
		io.seek(position);
		return readUShort();
	}

	public final int readUShort() throws IOException {
		return io.readUnsignedShort();
	}
	
	public final long readUInt(long position) throws IOException {
		io.seek(position);
		return readUInt();
	}
	
	public final long readUInt() throws IOException {
		long n = io.readInt();
		if (n < 0) {
			n += 0x100000000L;
		}
		return n;
	}
	
	public final BigInteger readULong(long position) throws IOException {
		io.seek(position);
		return readULong();
	}
	
	public final BigInteger readULong() throws IOException {
		long l = io.readLong();
		
		byte[] b = new byte[] {
				(byte) ((l & 0xFF00000000000000L) >>> 56),
				(byte) ((l & 0x00FF000000000000L) >>> 48),
				(byte) ((l & 0x0000FF0000000000L) >>> 40),
				(byte) ((l & 0x000000FF00000000L) >>> 32),
				(byte) ((l & 0x00000000FF000000L) >>> 24),
				(byte) ((l & 0x0000000000FF0000L) >>> 16),
				(byte) ((l & 0x000000000000FF00L) >>> 8),
				(byte) ((l & 0x00000000000000FFL)),
		};
		BigInteger bi = new BigInteger(1, b);
		
		return bi;
	}
	
	public static long readUInt(RandomAccessFile io, long position) throws IOException {
		io.seek(position);

		long n = io.read();
		if (n < 0) {
			n += 0x100000000L;
		}
		return n;

	}

	public static int readInt(RandomAccessFile io, long position) throws IOException {
		io.seek(position);
		return io.readInt();
	}

	public static int readUShort(RandomAccessFile io, long position) throws IOException {
		io.seek(position);
		return io.readUnsignedShort();
	}
	
	public static short readShort(RandomAccessFile io, long position) throws IOException {
		io.seek(position);
		return io.readShort();
	}

	
	public static int readUByte(RandomAccessFile io, long position) throws IOException {
		io.seek(position);
		return io.readUnsignedByte();
	}
	
	public static byte readByte(RandomAccessFile io, long position) throws IOException {
		io.seek(position);
		return io.readByte();
	}

	/**
	 * @param position
	 * @throws IOException 
	 */
	public final void seek(long position) throws IOException {
		io.seek(position);
	}
	
	public final long getFilePointer() throws IOException {
		return io.getFilePointer();
	}

	
	public void writeByte(byte v, long position) throws IOException {
		io.seek(position);
		writeByte(v);
	}
	
	public void writeShort(short v, long position) throws IOException {
		io.seek(position);
		writeShort(v);
	}
	
	public void writeInt(int v, long position) throws IOException {
		io.seek(position);
		writeInt(v);
	}
	
	public void writeLong(long v, long position) throws IOException {
		io.seek(position);
		writeLong(v);
	}

	public void writeByte(int v) throws IOException {
		io.writeByte(v);
	}
	
	public void writeShort(short v) throws IOException {
		io.writeShort(v);
	}
	
	public void writeInt(int v) throws IOException {
		io.writeInt(v);
	}
	
	public void writeLong(long v) throws IOException {
		io.writeLong(v);
	}
	
	public void writeUUpperNibble(int v, long position) throws IOException {
		writeUNibble(v, false, position);
	}
	
	public void writeUUpperNibble(int v) throws IOException {
		writeUNibble(v, false);
	}
	
	public void writeULowerNibble(int v, long position) throws IOException {
		writeUNibble(v, true, position);
	}
	
	public void writeULowerNibble(int v) throws IOException {
		writeUNibble(v, true);
	}

	
	public void writeUNibble(int v, boolean lower, long position) throws IOException {
		io.seek(position);
		
		writeUNibble(v, lower);
	}
	public void writeUByte(byte v, long position) throws IOException {
		io.seek(position);
		writeUByte(v);
	}
	
	public void writeUShort(int v, long position) throws IOException {
		io.seek(position);
		writeUShort(v);
	}
	
	public void writeUInt(long v, long position) throws IOException {
		io.seek(position);
		writeUInt(v);
	}
	
	public void writeULong(BigInteger v, long position) throws IOException {
		io.seek(position);
		writeULong(v);
	}
	
	public void writeUNibble(int v, boolean lower) throws IOException {
		int n = io.read();
		
		if (lower) {
			v = (n & 0xF0) | (v & 0x0F) ;
		} else {
			v = (n & 0x0F) | (v & 0xF0) ;
		}
		
		io.writeByte(v);
	}

	public void writeUByte(short v) throws IOException {
		io.writeByte(v);
	}
	
	public void writeUShort(int v) throws IOException {
		io.writeShort(v);
	}
	
	public void writeUInt(long v) throws IOException {
		io.writeInt((int) v);
	}
	
	public void writeULong(BigInteger v) throws IOException {
		io.write(v.toByteArray(), 0, 8);
	}
}
