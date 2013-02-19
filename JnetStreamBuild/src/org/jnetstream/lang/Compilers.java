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
package org.jnetstream.lang;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jnetstream.lang.npl.NplCompiler;

import com.slytechs.utils.factory.FactoryLoader;

/**
 * Factory class which loads the default NPL compiler.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class Compilers {
	private final static Log logger = LogFactory.getLog(Compilers.class);


	/**
	 * Property name which can be used to override the default implementation for
	 * NPL compiler. The system property should contain the name of the class that
	 * provides the NplCompiler implementation.
	 */
	public static final String NPL_COMPILER = "org.jnetstream.lang.npl.compiler";

	private static final String NPL_COMPILER_DEFAULT = "com.slytechs.lang.npl.NplCompilerImpl";

	private static final FactoryLoader<NplCompiler> factory = new FactoryLoader<NplCompiler>(
	    logger, NPL_COMPILER, NPL_COMPILER_DEFAULT);

	/**
	 * Retrieves the default NPL compiler.
	 * 
	 * @return default NPL compiler
	 */
	public static NplCompiler getNplCompiler() {
		return factory.getFactory();
	}
}
