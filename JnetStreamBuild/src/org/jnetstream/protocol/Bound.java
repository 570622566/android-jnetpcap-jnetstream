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
package org.jnetstream.protocol;

import java.io.IOException;

/**
 * Defines a method for checking a binding by value of a protocol. If a field in
 * NPL has a "linked" modifier on it, then the auto generated protocol
 * definition will also implement this Bound interface which will provide the
 * {@link #checkBinding} method. The method allows checking if the value of any
 * of the linked fields matches the supplied value.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * @param <T>
 *          linked field value type
 */
public interface Bound<T> {

	/**
	 * Checks if supplied value matches any of the fields marked as "linked" in
	 * source NPL definition file.
	 * 
	 * @param value
	 *          value to check against the field
	 * @return true if value matches any of the "linked" field, another words an
	 *         logical OR is performed on all linked fields against the supplied
	 *         value, otherwise false is returned
	 * @throws IOException 
	 */
	public boolean checkBinding(T value) throws IOException;
}
