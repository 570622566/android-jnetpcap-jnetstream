/**
 * Copyright (C) 2008 Sly Technologies, Inc. This library is free software; you
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
package com.slytechs.jnetstream.protocol;

import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class ProtocolUtils {

	/**
	 * @param buf
	 *          bit buffer
	 * @param position
	 *          in bits
	 * @param len
	 *          in bits
	 * @return array with data read from the buffer
	 */
	public static byte[] readByteArray(BitBuffer buf, int position, int len) {
		final byte[] a = new byte[len / 8];

		buf.get(position, a);

		return a;
	}
	
	/**
	 * 
	 * @param buf
	 * @param position
	 * @param value
	 */
	public static void writeByteArray(BitBuffer buf, int position, byte[] value) {
		buf.put(position, value, 0, value.length);
	}

	/**
   * @param bitBuffer
   * @param offset
   * @param length
   * @return
   */
  public static short readShort(BitBuffer bitBuffer, int offset, int length) {
  	
  	return (short) bitBuffer.getBits(offset, length);
  }

	/**
   * @param bitBuffer
   * @param offset
   * @param length
   * @param value
   */
  public static void writeShort(BitBuffer bitBuffer, int offset, int length,
      short value) {
  	bitBuffer.writeBits(length, (int) value);
 }
  
  

	/**
   * @param c
   * @param buffer
   * @param offset
   * @param length
   * @return
   */
  public static <T> T readValue(Class<T> c, BitBuffer buffer, int offset, int length) {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/**
   * @param buffer
   * @param offset
   * @param length
   * @return
   */
  public static int readInt(BitBuffer buffer, int offset, int length) {
   	return buffer.getBits(offset, length);
      }

	/**
   * @param class1
   * @return
   */
  public static String getSuite(Class<?> c) {
	  String s = c.getPackage().getName();
	  String e[] = s.split("\\.");
	  if (e.length == 0) {
	  	return "default";
	  }
	  
	  return e[e.length -1];
  }
}
