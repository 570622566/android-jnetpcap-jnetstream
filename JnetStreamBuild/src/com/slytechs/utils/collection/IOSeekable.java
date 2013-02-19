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
 * This interface allows generic object which maintain a position to be
 * repositioned easily. The generic position maintaining object could be a
 * bidirectional iterator or something else.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface IOSeekable<T> {

	/**
	 * <p>
	 * Allows a seek or position the iterator to the supplied percentage as a
	 * hole. Whatever the underlying storage of this position object its aligned
	 * at the nearest element at the calculated position based on the percentage
	 * supplied.
	 * </p>
	 * <P>
	 * In certain situations it may not be possible to seek to the requested
	 * position due to underlying structure the iterator iterates over.
	 * </P>
	 * 
	 * @param percentage
	 *          percentage in range of 0.0 to 1.0. Any values out of this range
	 *          will throw an IllegalArgumentException as the argument is out of
	 *          allowed range.
	 * @return Result of the seek to the requested percentage. It may not be
	 *         possible or not known for certain that the position of the iterator
	 *         has been accurately reached. The SeekResult will return status of
	 *         the result of this operation.
	 * @throws IOException
	 *           any IO errros
	 */
	public SeekResult seek(double percentage) throws IOException;

	/**
	 * Seeks a record starting at the specified position. The actual position set
	 * may end up being different then the position specified. The position
	 * specified is only used as a starting point of a search for a record. The
	 * position will be set to the starting point of an actual record found.
	 * 
	 * @param position
	 *          position from which to start a search for a record
	 * @return result of the seek
	 * @throws IOException
	 *           any IO errors
	 */
	public SeekResult seek(long position) throws IOException;

}
