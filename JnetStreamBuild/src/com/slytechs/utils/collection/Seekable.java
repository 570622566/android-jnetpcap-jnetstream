/**
 * Copyright (C) 2007 Sly Technologies, Inc.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.slytechs.utils.collection;


	/**
	 * Defines methods for seeking forward to an element or as a percentage of
	 * the overal container capacity.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface Seekable<T>  {


		/**
		 * <P>
		 * (Optional) Allows a seek or position the iterator to the supplied
		 * percentage as a hole. Whatever the underlying storage of this iterator
		 * its aligned at the nearest element at the calculated position based on
		 * the percentage supplied.
		 * </P>
		 * 
		 * <P>
		 * In certain situations it may not be possible to seek to the requested
		 * position due to underlying structure the iterator iterates over.
		 * </P>
		 * 
		 * @param percentage
		 *          percentage in range of 0.0 to 1.0. Any values out of this range
		 *          will throw an IllegalArgumentException as the argument is out of
		 *          allowed range.
		 * 
		 * @return Result of the seek to the requested percentage. It may not be
		 *         possible or not known for certain that the position of the
		 *         iterator has been accurately reached. The SeekResult will return
		 *         status of the result of this operation.
		 * 
		 */
		public SeekResult seek(double percentage);

		/**
		 * <P>
		 * Advances the iterator to the requested element. The iterator will be
		 * positioned such that
		 * {@link com.slytechs.utils.collections.IOIterator#next} will return the
		 * supplied element. The returned SeekResult will specify if the result has
		 * been accurately located, estimated or simply not found.
		 * 
		 * @param element
		 *          element to search for and position the iterator at
		 * 
		 * @return result of the seek operation as there is no certaintly that the
		 *         operation will succeed accurately or at all
		 */
		public SeekResult seek(T element);
}
