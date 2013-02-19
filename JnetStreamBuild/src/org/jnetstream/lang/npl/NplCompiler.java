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


import org.jnetstream.lang.Compiler;

import com.slytechs.tools.OptionChecker;

/**
 * NPL (Network Protocol Language) compiler. The interface allows NPL files to
 * be compiled for various target outputs. The compiler can compile the
 * following types of source files:
 * <ul>
 * <li> NPL header definition files</li>
 * <li> NPL table definition files</li>
 * <li> NPL binding files</li>
 * <li> NPL expression strings</li>
 * </ul>
 * The compiler can also be invoked by forwarding command line arguments for
 * parsing. This allows the compiler to be easily wrapped around a command line
 * invokable tool.
 * <p>
 * In addition to the above specified sources, the compiler can be directed to
 * produce object files for the following output types:
 * <ul>
 * <li> NPL AST (Abstract Syntax Tree) which can further be traversed using tree
 * walkers or the visitor pattern</li>
 * <li> binary Java byte code</li>
 * <li> BPF (Berkley Packet Filter) byte code</li>
 * </ul>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface NplCompiler extends OptionChecker, Compiler {
	
	public void compileNplDefinition();

}
