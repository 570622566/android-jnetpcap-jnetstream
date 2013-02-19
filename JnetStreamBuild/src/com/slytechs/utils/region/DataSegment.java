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
package com.slytechs.utils.region;

/**
 * In addition to being a simple segment, the DataSegment interface adds a user
 * data object that is attached to this segment. You are provided with both a
 * getter and a setter method for accessing the user data object.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface DataSegment<T> extends Segment {

	/**
	 * Retrieves the data object associated with this segment.
	 * 
	 * @return data object
	 */
	public T getData();

	/**
	 * Sets a new data object associated with this segment.
	 * 
	 * @param data
	 *          new data object to set
	 */
	public void setData(T data);

}
