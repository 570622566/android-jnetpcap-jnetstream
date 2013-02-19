/**
 * $Id$
 *
 * Copyright (C) 2006 Mark Bednarczyk, Sly Technologies, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place,
 * Suite 330, Boston,
 * MA 02111-1307 USA
 * 
 */
package com.slytechs.utils.namespace;

import java.util.List;

/**
 * Interface designed to allow retrieval of object name. Since this is a common
 * paradim, to have some kind of name reside inside an object and getName()
 * method are common, this interface structures API interaction.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Named {

	/**
	 * Retries the name assigned to this object.
	 * 
	 * @return name assigned to the object
	 */
	public String getName();

	/**
	 * A set of utility classes that operate on Named objects.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 * 
	 */
	public static class Util {

		/**
		 * Gets the length of the largest name in the list. The list is iterated and
		 * each name accessed and compared for length of characters. The length of
		 * the largest name is returned.
		 * 
		 * @param list
		 *          list of named objects to check for longest name
		 * 
		 * @return the length in characters of the largest name
		 */
		public static int getLargestLength(List<? extends Named> list) {
			return getLargestLength(list, 0);
		}

		/**
		 * Gets the length of the largest name in the list. The list is iterated and
		 * each name accessed and compared for length of characters. The length of
		 * the largest name is returned. The specified <CODE>"minimum"</CODE>
		 * parameter is the minimum length to be returned. If no name within the
		 * list is larger then the length specified by the "minimum" parameter, then
		 * the value of "minimum" is returned.
		 * 
		 * @param list
		 *          list of named objects to check for longest name
		 * 
		 * @param minimum
		 *          minimum length to be returned
		 * 
		 * @return the length in characters of the largest name or minimum, which
		 *         ever is greater
		 */
		public static int getLargestLength(List<? extends Named> list, int minimum) {

			int largest = 0;

			for (Named n : list) {
				if (n.getName().length() > largest) {
					largest = n.getName().length();
				}
			}

			if (largest < minimum) {
				largest = minimum;
			}

			return largest;
		}
		
		/**
		 * Gets the length of the largest name in the list. The list is iterated and
		 * each name accessed and compared for length of characters. The length of
		 * the largest name is returned. The specified <CODE>"minimum"</CODE>
		 * parameter is the minimum length to be returned. If no name within the
		 * list is larger then the length specified by the "minimum" parameter, then
		 * the value of "minimum" is returned.
		 * 
		 * @param list
		 *          list of named objects to check for longest name
		 * 
		 * @param minimum
		 *          minimum length to be returned
		 * 
		 * @return the length in characters of the largest name or minimum, which
		 *         ever is greater
		 */
		public static int getLargestLength(Named[] list, int minimum) {

			int largest = 0;

			for (Named n : list) {
				if (n == null) {
					continue;
				}
				
				if (n.getName().length() > largest) {
					largest = n.getName().length();
				}
			}

			if (largest < minimum) {
				largest = minimum;
			}

			return largest;
		}
		
		/**
		 * Gets the length of the smallest name in the list. The list is iterated and
		 * each name accessed and compared for length of characters. The length of
		 * the smallest name is returned. 
		 * 
		 * @param list
		 *          list of named objects to check for longest name
		 * 

		 * 
		 * @return the length in characters of the largest name or minimum, which
		 *         ever is greater
		 */
		public static int getSmallestLength(List<? extends Named> list) {
			return getSmallestLength(list, Integer.MAX_VALUE);
		}

		/**
		 * Gets the length of the smallest name in the list. The list is iterated and
		 * each name accessed and compared for length of characters. The length of
		 * the smallest name is returned. The specified <CODE>"maximum"</CODE>
		 * parameter is the maximum length to be returned. If no name within the
		 * list is smaller then the length specified by the "maximum" parameter, then
		 * the value of "maximum" is returned.
		 * 
		 * @param list
		 *          list of named objects to check for longest name
		 * 
		 * @param maximum
		 *          minimum length to be returned
		 * 
		 * @return the length in characters of the largest name or minimum, which
		 *         ever is greater
		 */
		public static int getSmallestLength(List<? extends Named> list, int maximum) {

			int smallest = Integer.MAX_VALUE;

			for (Named n : list) {
				if (n.getName().length() < smallest) {
					smallest = n.getName().length();
				}
			}

			if (smallest > maximum) {
				smallest = maximum;
			}

			return smallest;
		}
	}
}
