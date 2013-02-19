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
package com.slytechs.utils.memory;

import java.nio.ByteBuffer;

/**
 * Reads the length of the current structure based on header information in the
 * supplied buffer. The implementation determines the exact structure of the
 * header and where the length of the structure should be read from.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface RecordLengthGetter {

	/**
	 * Determines the entire length of the structure. The method must return the
	 * length of the structure including its header and any overhead or padding.
	 * The length is used to calculate the started of the next structure offset
	 * from the beginning of this structure.
	 * 
	 * @param b
	 *          buffer from which to read the structure's length
	 * @return length of the entire structure in octets from the beginning of this
	 *         structure's start position
	 */
	public int readLength(ByteBuffer b);

	/**
	 * Specifies the minimum number of bytes required in the buffer in order to
	 * retrieve the length of the structure
	 * 
	 * @return
	 */
	public int getMinLength();
	
	public int getOffset();
}
