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
package com.slytechs.utils.format;

/**
 * Interface which allows objects to be formatted. The format is specified using
 * object specific format string which describes the format of the output of 
 * the object. Some format strings are in form familiar to printf statements in
 * C, others are object/implementation specified.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public interface Formattable {
	
	public enum Justification {
		None,
		Right,
		Left,
		Center,
	}
	
	public void setFormat(String format);
	
	public void setFormat(Justification justify);
	
	public String getFormatString();
	
	public Justification getFormatJustification();
	
	public void format(Appendable out);
}
