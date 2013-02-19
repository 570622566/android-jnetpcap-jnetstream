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
package org.jnetstream.lang.npl.type;

import java.util.Collections;
import java.util.Set;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class AbstractDataType implements DataType {

	/**
	 * Default implementation always throws UnsupportOperationException. Must
	 * override inorder to implement this operation.
	 * 
	 * @see org.jnetstream.lang.npl.type.DataOperations#doAdd(org.jnetstream.lang.npl.type.DataType)
	 */
	public <R extends DataType, O extends DataType> R doAdd(O operand) {
		throw new UnsupportedOperationException("Operation not supported by "
		    + getNplName() + " datatype");
	}

	/**
	 * Default abstract implementation always returns an empty set. Must override
	 * in order to return something different.
	 * 
	 * @see org.jnetstream.lang.npl.type.DataOperations#getNativeCapability(java.lang.Class)
	 */
	public <O extends DataType> Set<DataOperation> getNativeCapability(Class<O> c) {
		return Collections.emptySet();
	}

	/**
	 * Default abstract implementation always returns an empty set. Must override
	 * in order to return something different.
	 * 
	 * @see org.jnetstream.lang.npl.type.DataOperations#getTranslatedCapability(java.lang.Class)
	 */
	public <O extends DataType> Set<DataOperation> getTranslatedCapability(
	    Class<O> c) {
		return Collections.emptySet();
	}

}
