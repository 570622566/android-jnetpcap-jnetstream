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
package org.jnetstream.capture.file.pcap;

import java.io.IOException;

import org.jnetstream.capture.FilePacket;


/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface PcapPacket extends FilePacket {

	/**
	 * Sets the capture timestamp seconds field within the Pcap packet record
	 * header.
	 * 
	 * @param seconds
	 *          timestamp in seconds
	 * @throws IOException
	 *           any IO errros
	 */
	public void setTimestampSeconds(long seconds) throws IOException;

	/**
	 * Sets the capture timestamp fraction of a second in micros within the Pcap
	 * packet record header. The number of microseconds fraction at the time the
	 * packet was captured.
	 * 
	 * @param micros
	 *          fraction of a second in microsecond with valid value of 0 and
	 *          999,999 inclusive.
	 * @throws IOException
	 *           any IO errors
	 */
	public void setTimestampMicros(int micros) throws IOException;

}
