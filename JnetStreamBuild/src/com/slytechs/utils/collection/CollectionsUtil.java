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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * Additional Collections utilities.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class CollectionsUtil {

	private CollectionsUtil() {
		/* empty */
	}
	
	public static <T> Collection<T> asCollection(Enumeration<T> e) {
		return asList(e);
	}
	
	public static <T> List<T> asList(Enumeration<T> e) {
		List<T> l = new LinkedList<T>();
		
		while (e.hasMoreElements()) {
			l.add(e.nextElement());
		}
		
		return l;
	}
	
	
	/**
	 * Creates a list from the vararg specified items in the order they were
	 * specified.
	 * 
	 * @param <T>
	 *   type of item in the list
	 *   
	 * @param items
	 *   array of items to be added to the list
	 *   
	 * @return
	 *   a list containing all the items supplied in the order supplied order
	 */
	public static <T> List<T> newList(T ... items) {
		List<T> list =  new ArrayList<T>(items.length);
		for (T t: items) {
			list.add(t);
		}
		
		return list;
	}

}
