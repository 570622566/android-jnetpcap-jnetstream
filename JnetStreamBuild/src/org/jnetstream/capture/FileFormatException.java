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
package org.jnetstream.capture;

import java.io.File;
import java.io.IOException;

/**
 * <P>Thrown when Invalid format has been encountered in the capture file.</P>
 * 
 * <P>The exception can also provide additional information about the file and exactly
 * the position within the file that the invalid format was encountered.</P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class FileFormatException extends IOException {

	private static final long serialVersionUID = 2436226880588709668L;
	private File file;
	private long position;

	/**
	 * 
	 */
	public FileFormatException() {
		super();
	}

	/**
	 * @param message
	 */
	public FileFormatException(String message) {
		super(message);
	}

	
	public FileFormatException(String message, File file, long position) {
		super(message);
		this.file = file;
		this.position = position;
		
	}

	/**
	 * File that the invalid format was encountered in.
	 * 
	 * @return Returns the file.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * The position within the file where the invalid format was encountred.
	 * 
	 * @return Returns the position.
	 */
	public long getPosition() {
		return position;
	}
	
	public String toString() {
		String s = super.toString();
		
		if (file != null) {
			s += " (File=" + file.toString() + "@" + position + ")";
		}
		
		return s;
	}

}
