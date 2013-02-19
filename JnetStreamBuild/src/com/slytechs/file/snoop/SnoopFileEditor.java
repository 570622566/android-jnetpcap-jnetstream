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
package com.slytechs.file.snoop;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;

import org.jnetstream.capture.FileMode;
import org.jnetstream.capture.file.RawIterator;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.capture.file.snoop.SnoopFile;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;


import com.slytechs.capture.file.editor.FileEditorImpl;
import com.slytechs.capture.file.editor.FileEditor;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class SnoopFileEditor
    extends FileEditorImpl implements FileEditor {

	public SnoopFileEditor(File file, FileMode mode,
	    Filter<ProtocolFilterTarget> protocolFilter) throws IOException {
		super(file, mode, SnoopFile.headerReader, ByteOrder.BIG_ENDIAN,
		    protocolFilter, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.file.pcap.FileEditor#getRawIterator(long)
	 */
	public RawIterator getRawIterator(Filter<RecordFilterTarget> filter)
	    throws IOException {

		return new SnoopRawIterator(edits, this, this, filter);
	}
}
