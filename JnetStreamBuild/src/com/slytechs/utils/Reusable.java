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
package com.slytechs.utils;

/**
 * Interface which allows an object to be reused. Reusable objects are cached
 * and when an object of the same type needs to be created, one from the cache
 * which is typically an efficient queue, is reused. This saves the neccessity
 * of creating a new object.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public interface Reusable {

	/**
	 * Signals the object to do what ever is neccessary to release itself from
	 * any parent objects, and inform some kind of caching mechanism that this
	 * object is ready to be reused.
	 *
	 */
	public void free();
}
