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
package org.jnetstream.capture;

import java.io.IOException;

/**
 * A file based packet. The interface adds one important method to allow
 * retrieval of packet record position within the capture file.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface FilePacket extends CapturePacket {

	/**
	 * <p>
	 * Gets the global position of this record. There are 3 types of position
	 * addressing in jNetStream.
	 * </p>
	 * <p>
	 * The 1st is global position which reflects the address space of the main
	 * editor for a file. On an unmodified file, the global position will equal
	 * the position of the packet within the physical file. If any edits are in
	 * place, the position will differ from the files position and if the packet
	 * is based on a buffer completely in memory, such as when a new packet has
	 * been added, then the position does not have any mappings to a position
	 * related to a file.
	 * </p>
	 * <p>
	 * The 2nd type is the regional address space which has a 1 to 1 mapping with
	 * the physical location within a file. But certain regions of the file may
	 * have been mapped out and not all the regions are addressable.
	 * </p>
	 * <p>
	 * The 3rd type is local address which is the address within the returned
	 * buffer. If the buffer is larger then the actual contents of the packet,
	 * then the local address specifies offset into the data buffer of the start
	 * of packet's data.
	 * </p>
	 * 
	 * @return
	 * @throws IOException
	 */
	public long getPositionGlobal() throws IOException;

	public long getPositionRegional() throws IOException;

	public int getPositionLocal() throws IOException;


}
