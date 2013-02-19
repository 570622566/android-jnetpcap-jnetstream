/**
 * $Id$
 *
 * Copyright (C) 2006 Mark Bednarczyk, Sly Technologies, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place,
 * Suite 330, Boston,
 * MA 02111-1307 USA
 * 
 */
package com.slytechs.utils.io;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class DataDirectBuffer implements DataBuffer {

	private Encoding encoding = Encoding.BIG_ENDIAN;
	
	private ByteBuffer buffer;

	/** Creates a new instance of BitDataChannel */
	public DataDirectBuffer(int size) {
		buffer = ByteBuffer.allocateDirect(size);
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.io.DataBuffer#getPosition()
	 */
	public int getPosition() {
		return buffer.position();
	}

	/**
	 * Sets the position within the buffer to new offset.
	 */
	public void setPosition(int position){
		buffer.position(position);
	}
	/* (non-Javadoc)
	 * @see com.slytechs.utils.io.DataBuffer#skip(int)
	 */
	public void skip(int skip) {
		buffer.position(buffer.position() + skip);
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.io.DataBuffer#getEncoding()
	 */
	public Encoding getEncoding() {
		return encoding;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.io.DataBuffer#setEncoding(com.slytechs.utils.io.DataDirectBuffer.Encoding)
	 */
	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
		
		switch (encoding){
		case BIG_ENDIAN:
			buffer.order(ByteOrder.BIG_ENDIAN);
			break;
		case LITTLE_ENDIAN:
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			break;
		}
	}

	public void rewind() {
		buffer.rewind();
	}
	/* (non-Javadoc)
	 * @see com.slytechs.utils.io.DataBuffer#getByte()
	 */
	public byte getByte() {

		return buffer.get();
	}
	
	public short getUnsignedByte() {
		byte b = buffer.get();

		return (short) ((b < 0)?(b & 0x00FF):(b));
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.io.DataBuffer#getByte(int)
	 */
	public byte[] getByte(int count) {

		byte[] b = new byte[count];
		buffer.get(b);

		return b;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.io.DataBuffer#getByte(byte[], int)
	 */
	public byte[] getByte(byte[] b, int offset, int count) {
		buffer.get(b, offset, count);
		return b;
	}


	/* (non-Javadoc)
	 * @see com.slytechs.utils.io.DataBuffer#getShort()
	 */
	public short getShort() {
		return buffer.getShort();
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.io.DataBuffer#getUnsignedShort()
	 */
	public int getUnsignedShort() {
		int s = getShort();
		return (s < 0)?(s & 0x0000FFFF):(s);
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.io.DataBuffer#getInt()
	 */
	public int getInt() {
		return buffer.getInt();
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.io.DataBuffer#getUnsignedInt()
	 */
	public long getUnsignedInt() {

		long i = getInt();
		return (i < 0)?(i & 0x00000000FFFFFFFFL):(i);
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.io.DataBuffer#getLong()
	 */
	public long getLong() {
		return buffer.getLong();
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.io.DataBuffer#getUnsignedLong()
	 */
	public BigInteger getUnsignedLong() {

		long l = getLong();
		
		BigInteger bi = BigInteger.valueOf(l);
		if (l < 0) {
			bi = bi.setBit(63).add(new BigInteger(new byte[] {1}));
		}
		
		return bi;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.io.DataBuffer#getBigInteger(int)
	 */
	public BigInteger getBigInteger(int count) {
		/* Position algety incremented by call to getByte() */

		BigInteger returnValue = new BigInteger(getByte(count));

		return returnValue;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.io.DataBuffer#getUnsignedBigInteger(int)
	 */
	public BigInteger getUnsignedBigInteger(int count) {
		/* Position algety incremented by call to getByte() */
		
		byte[] b = new byte[count + 1];
		b[count] = 0;
		BigInteger returnValue = new BigInteger(getByte(b, 0, count));

		return returnValue;
	}
	
	public String toString(){
		return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#put(byte)
	 */
	public ByteBuffer put(byte arg0) {
		return buffer.put(arg0);
	}

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#put(byte[], int, int)
	 */
	public ByteBuffer put(byte[] arg0, int arg1, int arg2) {
		return buffer.put(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#put(java.nio.ByteBuffer)
	 */
	public ByteBuffer put(ByteBuffer arg0) {
		return buffer.put(arg0);
	}

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#put(int, byte)
	 */
	public ByteBuffer put(int arg0, byte arg1) {
		return buffer.put(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putChar(char)
	 */
	public ByteBuffer putChar(char arg0) {
		return buffer.putChar(arg0);
	}

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putChar(int, char)
	 */
	public ByteBuffer putChar(int arg0, char arg1) {
		return buffer.putChar(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putDouble(double)
	 */
	public ByteBuffer putDouble(double arg0) {
		return buffer.putDouble(arg0);
	}

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putDouble(int, double)
	 */
	public ByteBuffer putDouble(int arg0, double arg1) {
		return buffer.putDouble(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putFloat(float)
	 */
	public ByteBuffer putFloat(float arg0) {
		return buffer.putFloat(arg0);
	}

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putFloat(int, float)
	 */
	public ByteBuffer putFloat(int arg0, float arg1) {
		return buffer.putFloat(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putInt(int, int)
	 */
	public ByteBuffer putInt(int arg0, int arg1) {
		return buffer.putInt(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putInt(int)
	 */
	public ByteBuffer putInt(int arg0) {
		return buffer.putInt(arg0);
	}

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putLong(int, long)
	 */
	public ByteBuffer putLong(int arg0, long arg1) {
		return buffer.putLong(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putLong(long)
	 */
	public ByteBuffer putLong(long arg0) {
		return buffer.putLong(arg0);
	}

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putShort(int, short)
	 */
	public ByteBuffer putShort(int arg0, short arg1) {
		return buffer.putShort(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putShort(short)
	 */
	public ByteBuffer putShort(short arg0) {
		return buffer.putShort(arg0);
	}

}
