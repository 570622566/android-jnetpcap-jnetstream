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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import com.slytechs.utils.number.Version;

/**
 * Common interface for tools that can be invoked from a program. A tool is
 * traditionally a command line program such as a compiler. The set of tools
 * available with a platform is defined by the vendor.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Tool {

	/**
	 * Run the tool with the given I/O channels and arguments. By convention a
	 * tool returns 0 for success and nonzero for errors. Any diagnostics
	 * generated will be written to either <code>out</code> or <code>err</code>
	 * in some unspecified format.
	 * 
	 * @param in
	 *          "standard" input; use System.in if null
	 * @param out
	 *          "standard" output; use System.out if null
	 * @param err
	 *          "standard" error; use System.err if null
	 * @param arguments
	 *          arguments to pass to the tool
	 * @return 0 for success; nonzero otherwise
	 * @throws NullPointerException
	 *           if the array of arguments contains any null elements.
	 */
	public int run(InputStream in, OutputStream out, OutputStream err,
	    String... arguments);

	/**
	 * Gets the source versions of the tools programming or scripting language
	 * supported by this tool.
	 * 
	 * @return a set of supported source versions
	 */
	public Set<Version> getSourceVersions();

}
