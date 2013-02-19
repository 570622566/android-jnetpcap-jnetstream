/**
 * Copyright (C) 2007 Sly Technologies, Inc.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.slytechs.utils.namespace;

/**
 * Utility class which provides string path manipulation methods. The class
 * allows to break up a single string path into invididual elements.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public class PathUtilities {
	
	/**
	 * Default delimiter for all paths that separates individual elements of
	 * a path.
	 */
	public static final String DEFAULT_DELIMITER = ".";
	
	/**
	 * Parses the supplied path using the default delimiter into individual
	 * elements.
	 * 
	 * @param path
	 *   path to parse
	 *   
	 * @return
	 *   array of individual elements of the path
	 */
	public static String[] parse(String path) {
		return parse(path, DEFAULT_DELIMITER);
	}
	
	/**
	 * Parses the supplied path using the specified delimiter into individual
	 * elements.
	 * 
	 * @param path
	 *   path to parse
	 *   
	 * @param delimiter
	 *   parse the path using this regex as delimiter
	 *   
	 * @return
	 *   array of individual elements of the path
	 */
	public static String[] parse(String path, String delimiter) {
		String[] e = path.split(delimiter);
		
		return e;
	}

}
