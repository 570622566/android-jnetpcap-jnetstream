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

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Readonly {

	/**
	 * Tells if the entity is readonly. Only the object's getter method will be
	 * available. All mutating methods are optional and may or may not throw some
	 * kind of Readonly exception or simply ignore the operation.
	 * 
	 * @return true means the object is readonly, otherwise false
	 */
	public boolean isReadonly();

	/**
	 * Changes the readonly state of the object. When set to false, its mutating
	 * methods are disabled.
	 * 
	 * @param state
	 *          true means that all mutating methods will be disabled otherwise
	 *          mutating methods will not
	 * @return true if the request was fulfilled and the underlying object was
	 *         able to change state, otherwise false is return that the state was
	 *         not changed
	 */
	public boolean setReadonly(boolean state);

}
