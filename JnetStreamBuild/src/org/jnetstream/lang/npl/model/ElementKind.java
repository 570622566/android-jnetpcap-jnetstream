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
package org.jnetstream.lang.npl.model;

/**
 * The <code>kind</code> of an NPL element.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum ElementKind {

	/**
	 * A protocols' header type.
	 */
	HEADER,

	/**
	 * A table type. Tables are similar to enum but contain additional information
	 * such as descriptive labels.
	 */
	TABLE,

	/**
	 * A constant within a table.
	 */
	TABLE_CONSTANT,

	/**
	 * A field within a header.
	 */
	FIELD,

	/**
	 * A method declaration.
	 */
	METHOD,

	/**
	 * A package declaration.
	 */
	PACKAGE,

	/**
	 * NPL or java import statement.
	 */
	IMPORT,

	/**
	 * A parameter of a method or constructor.
	 */
	PARAMETER,

	/**
	 * A local variable.
	 */
	LOCAL_VARIABLE,

	/**
	 * A non-shared property exported with the packet, header or field.
	 */
	LOCAL_PROPERTY,

	/**
	 * A shared property exported with the packet, header or a field. Shared
	 * properties are shared amongst all instances of the same type of element.
	 * For example All <code>Ethernet</code> headers will have the exact same
	 * instance of all the same shared properties. Therefore any modification to
	 * the shared property will be reflected in all of the existing or future
	 * instances of <code>Ethernet</code> header.
	 */
	SHARED_PROPERTY,

}
