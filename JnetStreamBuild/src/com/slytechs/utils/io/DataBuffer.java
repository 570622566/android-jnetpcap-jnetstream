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

/**
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface DataBuffer {
	
	public enum Encoding {
		BIG_ENDIAN, LITTLE_ENDIAN
	}

	public abstract int getPosition();
	
	public abstract void setPosition(int position);

	public abstract void skip(int skip);

	public abstract Encoding getEncoding();

	public abstract void setEncoding(Encoding encoding);
	
	public abstract void rewind();

	public abstract byte getByte();

	public abstract byte[] getByte(int count);

	public abstract byte[] getByte(byte[] b, int offset, int count);

	public abstract short getUnsignedByte();

	public abstract short getShort();

	public abstract int getUnsignedShort();

	public abstract int getInt();

	public abstract long getUnsignedInt();

	public abstract long getLong();

	public abstract BigInteger getUnsignedLong();

	public abstract BigInteger getBigInteger(int count);

	public abstract BigInteger getUnsignedBigInteger(int count);
	
	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#put(byte)
	 */
	public ByteBuffer put(byte arg0);
	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#put(byte[], int, int)
	 */
	public ByteBuffer put(byte[] arg0, int arg1, int arg2);
	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#put(java.nio.ByteBuffer)
	 */
	public ByteBuffer put(ByteBuffer arg0);
	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#put(int, byte)
	 */
	public ByteBuffer put(int arg0, byte arg1);
	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putChar(char)
	 */
	public ByteBuffer putChar(char arg0);
	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putChar(int, char)
	 */
	public ByteBuffer putChar(int arg0, char arg1);
	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putDouble(double)
	 */
	public ByteBuffer putDouble(double arg0);

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putDouble(int, double)
	 */
	public ByteBuffer putDouble(int arg0, double arg1);

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putFloat(float)
	 */
	public ByteBuffer putFloat(float arg0);

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putFloat(int, float)
	 */
	public ByteBuffer putFloat(int arg0, float arg1);

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putInt(int, int)
	 */
	public ByteBuffer putInt(int arg0, int arg1) ;

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putInt(int)
	 */
	public ByteBuffer putInt(int arg0);

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putLong(int, long)
	 */
	public ByteBuffer putLong(int arg0, long arg1);

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putLong(long)
	 */
	public ByteBuffer putLong(long arg0);

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putShort(int, short)
	 */
	public ByteBuffer putShort(int arg0, short arg1);

	/* (non-Javadoc)
	 * @see java.nio.ByteBuffer#putShort(short)
	 */
	public ByteBuffer putShort(short arg0);

}