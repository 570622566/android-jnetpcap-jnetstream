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
 * Makes a validation check against the object. True means that the object is
 * still valid and can be used. A false means that the object is expired
 * and should not be used anymore. Invalid object may throw IllegalStateException
 * on any methods it implements (except {@link #isValid()} method which only
 * returns true or false and does not throw any exceptions. 
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public interface Validatable {
	
	/**
	 * Runs the validation check against the object. 
	 * 
	 * @return
	 *   true means the object is still valid and can be used, otherwise the
	 *   object and any of its methods can no be used.
	 */
	public boolean isValid();

}
