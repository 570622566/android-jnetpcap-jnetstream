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

import org.jnetstream.capture.InputCapture;
import org.jnetstream.capture.InputIterator;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;


/**
 * A stream formatted to comform to Pcap file format.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface PcapInput extends InputCapture<PcapPacket> {

	/**
	 * An interator which iterates over PCAP packets within the stream.
	 */
	public InputIterator<PcapPacket> getPacketIterator() throws IOException;

	/**
	 * Gets an iterator that will iterate and return PcapPacket objects.
	 * 
	 * @param filter
	 *          a packet filter to apply to the interator
	 * @retrun iterator of CapturePacket objects or its subclassed cousins
	 * @throws IOException
	 *           any IO errors
	 */
	public InputIterator<PcapPacket> getPacketIterator(
	    Filter<ProtocolFilterTarget> filter) throws IOException;

	/**
	 * An iterator which iterates over PCAP records within the stream.
	 */
	public InputIterator<PcapRecord> getRecordIterator()
	    throws IOException;
	
	/**
	 * Gets an iterator that will iterate and return Record objects, just like a
	 * normal file based capture would produce except these records are read-only.
	 * 
	 * @param filter
	 *          filter to apply to the iterator
	 * @return iterator of record
	 * @throws IOException
	 *           any IO errors
	 */
	public InputIterator<PcapRecord> getRecordIterator(
	    Filter<RecordFilterTarget> filter) throws IOException;

}
