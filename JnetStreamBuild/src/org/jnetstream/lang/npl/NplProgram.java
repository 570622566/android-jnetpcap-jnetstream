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
package org.jnetstream.lang.npl;

import java.net.URI;

/**
 * NPL based program. The program is a tree of nodes that can be traversed and
 * executed, or processed further down to a lower level representation such as
 * java byte code, BPF byte code or even native processor instructions.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface NplProgram {
	
	public URI getSourceUri();

}
