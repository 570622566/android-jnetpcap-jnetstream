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
package org.jnetstream.capture.file;

import java.nio.ByteBuffer;

import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;


import com.slytechs.utils.memory.LengthReader;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface HeaderReader extends LengthReader, TypeReader {

	/**
	 * @param buffer
	 *          TODO
	 * @return
	 */
	public int getHeaderLength(ByteBuffer buffer);

	/**
	 * @param buffer
	 *          TODO
	 * @return
	 */
	public RecordType readType(ByteBuffer buffer);

	/**
	 * @param buffer
	 * @return
	 */
	public RecordFilterTarget readRecordFilterTarget(ByteBuffer buffer);

	/**
	 * </p>
	 * Method adapts a "protocol" filter to a "record" filter. Protocol filter is
	 * one that filters the contents of a packet. It only works with a
	 * ProtocolFilterTarget which are things such as ProtocolConst or DLT of the
	 * first protocol within the packet. While the "record" filter works with file
	 * records which may or may not have packet contents at all.
	 * </p>
	 * <p>
	 * The adaptor adapts a record filter to a packet filter by first making sure
	 * its a PacketRecord. Only packet records can be adapted, then offets the
	 * position within the buffer passed to <code>accept</code> and
	 * <code>execute</code> filter methods by the length of the record's header.
	 * Anotherwords, positions the buffer to point at the packet data, skipping
	 * past the record's header. This allows a protocol filter to be applied to
	 * records, since the record's buffer is shifted so that in reality the packet
	 * filter can be applied.
	 * </p>
	 * 
	 * @param filter
	 *          the protocol filter to adapt to a record filter
	 * @param protocol
	 *          the protocol or the DLT of the first protocol within the packet's
	 *          data
	 * @return
	 */
	public Filter<RecordFilterTarget> asRecordFilter(
	    Filter<ProtocolFilterTarget> filter, ProtocolFilterTarget protocol);

}
