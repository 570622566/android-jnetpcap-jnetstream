/**
 * Copyright (C) 2008 Sly Technologies, Inc. This library is free software; you
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
package com.slytechs.file.snoop;

import java.io.IOException;

import org.jnetstream.capture.file.snoop.SnoopPacket;
import org.jnetstream.protocol.ProtocolEntry;

import com.slytechs.capture.file.AbstractFilePacketFactory;
import com.slytechs.capture.file.editor.EditorHandle;
import com.slytechs.capture.file.editor.FileEditor;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class DefaultSnoopPacketFactory
    extends AbstractFilePacketFactory<SnoopPacket> implements
    SnoopPacketFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.file.snoop.SnoopPacketFactory#newPacket(com.slytechs.capture.file.editor.FileEditor,
	 *      com.slytechs.capture.file.editor.EditorHandle,
	 *      org.jnetstream.protocol.ProtocolEntry)
	 */
	public SnoopPacket newPacket(FileEditor editor, EditorHandle handle,
	    ProtocolEntry dlt) throws IOException {
		
		return new SnoopPacketImpl(editor, handle, dlt);
	}
}
