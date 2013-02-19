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
package com.slytechs.capture.file;

import java.nio.ByteBuffer;

import org.jnetstream.capture.FileCapture;

import com.slytechs.capture.file.editor.EditorHandle;
import com.slytechs.capture.file.editor.FileEditor;




/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class AbstractDataRecord extends AbstractRecord {

	/**
   * @param file TODO
	 * @param editor
	 * @param handle
   */
  public AbstractDataRecord(FileCapture file, FileEditor editor, EditorHandle handle) {
	  super(file, editor, handle);
  }

	/**
   * @param buffer
   * @param position
   */
  public AbstractDataRecord(ByteBuffer buffer, long position) {
  	super(buffer, position);
  }


}
