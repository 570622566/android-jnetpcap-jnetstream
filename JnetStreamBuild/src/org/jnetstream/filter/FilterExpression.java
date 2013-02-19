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
package org.jnetstream.filter;

/**
 * A filter expression interface that can access the abstract syntax tree of the
 * parsed expression in whatever syntax it was originally supplied.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface FilterExpression<C> {

	public enum FilterExpressionType {
		/**
		 * 
		 */
		Pcap,

		/**
		 * 
		 */
		NPL, ;
	}

	/**
	 * Returns the syntax in a form of enum constant that the expression string is
	 * in.
	 * 
	 * @return name constant of the syntax of this filter expression
	 */
	public FilterExpressionType getExpressionType();

	/**
	 * Gets the actual expression in form of a string that needs to be compiled or
	 * interpreted
	 * 
	 * @return the filter expression as a string
	 */
	public String getExpression();

	/**
	 * Compiles the current expression for target and returns the compiled result
	 * 
	 * @param target
	 *          target for which to compile the expression
	 * @return result of the compilation
	 * @throws FilterSyntaxError
	 *           any Syntax errors generated during the expression
	 * @throws FilterNotFoundException
	 */
	public C compile(FilterTarget target) throws FilterSyntaxError,
	    FilterNotFoundException;
}