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
package com.slytechs.utils.number;

/**
 * Class provides only a single static method which generates a unique id. The
 * value is guarranteed to be unique on this java VM. Since an integer is used,
 * the value may eventually overflow and repeat. Therefore it is important to
 * utilize this method only for ids that don't require more than 4 billion
 * combinations.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class UniqueID {

	private static long lastId = 0;

	/**
	 * Generates a unique ID. The returned ID may be positive or negative.
	 * 
	 * @return a unique ID on this java VM
	 */
	public final static int generate() {
		return (int) ++lastId;
	}
}
