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
 * Allows containers that maintain position to skip over an element without
 * the element being retrieved from the container, and possibly more efficiently
 * position the container to point at next element within the container.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public interface Skippable {
	
	/**
	 * Skips to the next element in the iteration. This is more efficient then
	 * calling next() and discarding the value returned, but has the same effect.
	 */
	public void skip();
}
