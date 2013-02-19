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
 * This interface allows generic objects which maintain position to be
 * repositioned easily to the first, last elements and the end, past the last
 * element. The positional object could be an Iterator or some other kind of
 * object that maintains a position.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface IOSeekableFirstLast<T> {

	/**
	 * Changes the position to the first element.
	 * 
	 * @return result of the seek operation
	 * @throws IOException
	 *           any IO errors
	 */
	public SeekResult seekFirst() throws IOException;

	/**
	 * Changes the position to the last element.
	 * 
	 * @return result of the seek operation
	 * @throws IOException
	 *           any IO errors
	 */
	public SeekResult seekLast() throws IOException;

	/**
	 * Changes the position to just past the last element or the end.
	 * 
	 * @return result of the seek operation
	 * @throws IOException
	 *           any IO errors
	 */
	public SeekResult seekEnd() throws IOException;
}
