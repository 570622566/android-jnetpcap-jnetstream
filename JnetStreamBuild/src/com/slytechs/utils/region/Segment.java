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

import java.io.IOException;

/**
 * A segment of an overall larger storage that has a starting position and a
 * length. The end of the segment is the last position that contains segment's
 * data.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Segment {

	/**
	 * Starting position of the segment.
	 * 
	 * @return position of the first element of the segment
	 * @throws IOException
	 *           any IO errors
	 */
	public long getStart();

	/**
	 * Ending position of the record. The position is past the last element of
	 * this segment.
	 * 
	 * @return position of the last element of the segment
	 * @throws IOException
	 *           any IO errors
	 */
	public long getEnd();

	/**
	 * Position of the last element within this segment. The element is still
	 * within the bounds of this segment.
	 * 
	 * @return position of the last element of this segment
	 * @throws IOException
	 *           any IO errors
	 */
	public long getLast();

	/**
	 * Length of this segment in elements.
	 * 
	 * @return number of elements contained within this entire segment
	 * @throws IOException
	 *           any IO errors
	 */
	public long getLength();

	/**
	 * Checks if the specified position is within bounds of this segment. It must
	 * be: start <= position < end
	 * 
	 * @param position
	 *          position to check for bounds
	 * @return true if position is within boundary, otherwise false
	 */
	public boolean checkBounds(long position);

	/**
	 * Checks to see if this segment has any elements.
	 * 
	 * @return true if segment has length greater then 0
	 * @throws IOException
	 *           any IO errors
	 */
	public boolean isEmpty();

}
