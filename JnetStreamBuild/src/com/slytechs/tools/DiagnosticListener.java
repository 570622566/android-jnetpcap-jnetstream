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
package com.slytechs.tools;

/**
 * Interface for receiving diagnostics from tools.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * @param <S>
 *          the type of source objects used by diagnostics received by this
 *          listener
 */
public interface DiagnosticListener<S> {

	/**
	 * Invoked when a problem is found.
	 * 
	 * @param diagnostic
	 *          a diagnostic representing the problem that was found
	 * @throws NullPointerException
	 *           if the diagnostic argument is null and the implementation cannot
	 *           handle null arguments
	 */
	public void report(Diagnostic<? extends S> diagnostic);
}
