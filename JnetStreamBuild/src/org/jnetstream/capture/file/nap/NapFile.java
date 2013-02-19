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
package org.jnetstream.capture.file.nap;

import java.io.IOException;

import org.jnetstream.capture.FileCapture;


/**
 * NAP capture file format from Sly Technologies folks.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface NapFile extends FileCapture<NAPPacket> {
	
	public static final long BLOCKING_FACTOR = 512 * 1024;
	public static final int HDR_RECORD_LENGTH = 4;
	public static final int HDR_TYPE = 0;
	
	/**
	 * Retrieves iterator over all Block Records within this NAP file. NAP files
	 * may contain any number of block records (file headers). Each block record
	 * contains any number of data records (packet, meta, no-op records)
	 * @return
	 * @throws IOException
	 */
	public NAPBlockRecordIterator getBlockIterator() throws IOException;

}
