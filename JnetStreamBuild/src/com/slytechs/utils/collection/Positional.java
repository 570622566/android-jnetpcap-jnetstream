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
package com.slytechs.utils.collection;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Positional {

	/**
	 * Gets the current cursor position.
	 * 
	 * @return zero based position of the current cursor
	 */
	public long getPosition();

	/**
	 * Sets a new position for the cursor.
	 * 
	 * @param position
	 *          new position for the cursor
	 */
	public long setPosition(long position);

	/**
	 * Sets a new position for the cursor based on another Positional cursor.
	 * 
	 * @param position
	 *          will use {@link #getPosition()} on the supplied positional to
	 *          aquire a position
	 */
	public long setPosition(Positional position);

}
