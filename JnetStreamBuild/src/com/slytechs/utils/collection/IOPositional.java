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

import java.io.IOException;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface IOPositional {

	/**
	 * Gets the current cursor position.
	 * 
	 * @return zero based position of the current cursor
	 * @throws IOException
	 *           any IO errors
	 */
	public long getPosition() throws IOException;

	/**
	 * Sets a new position for the cursor.
	 * 
	 * @param position
	 *          new position for the cursor
	 * @return old cursor position
	 * @throws IOException
	 *           any IO errors
	 */
	public long setPosition(long position) throws IOException;

	/**
	 * Sets a new position for the cursor based on another Positional cursor.
	 * 
	 * @param position
	 *          will use {@link #getPosition()} on the supplied positional to
	 *          aquire a position
	 * @return old cursor position
	 * @throws IOException
	 *           any IO errors
	 */
	public long setPosition(IOPositional position) throws IOException;

}
