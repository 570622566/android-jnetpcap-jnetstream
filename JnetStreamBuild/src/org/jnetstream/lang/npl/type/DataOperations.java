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

import java.util.Set;

/**
 * Interface that defines various operations that can be performed on a
 * particular data type.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface DataOperations {

	/**
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public enum DataOperation {
		ADD, S, MULTIPLY, DIVIDE, MODULO, INCREMENT,

		BXOR, BOR, BAND, BNOR, BNOT, BRSHIFT, BRRSHIFT, BLSHIFT, BLLSHIFT,

		LOR, LAND, LNOT, LEQ, LLE, LGT,
		
		CAST,
	}

	/**
	 * Returns a set of capabilities that this DataOperations object can perform
	 * on specified data type by use of this interface do* operation. Translated
	 * operations are the ones that are performed by calling of one of the methods
	 * supplied by this interface as opposed to java native operations which do
	 * not require a special call, but simply be used directly in java.
	 * 
	 * @param <O>
	 *          right operand type
	 * @param c
	 *          class of the right operand
	 * @return set of capabilities that this data type can perform on the operand
	 */
	public <O extends DataType> Set<DataOperation> getTranslatedCapability(
	    Class<O> c);

	public <O extends DataType> Set<DataOperation> getNativeCapability(Class<O> c);

	public <R extends DataType, O extends DataType> R doAdd(O operand);
}
