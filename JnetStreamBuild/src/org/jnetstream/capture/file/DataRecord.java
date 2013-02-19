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
package org.jnetstream.capture.file;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jnetstream.capture.FileFormatException;


/**
 * Data record is a record in a capture file that belongs to one block record, a
 * parent/child relationship, and contains some kind of data. Data records
 * usually contain packet data but other types of data records also exist in
 * certain file formats.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface DataRecord extends Record {
	
	/**
	 * Gets the parent block record this data record belongs to.
	 * Data records always belong to a block record, that is block
	 * records contain a sequential list of data records.
	 * 
	 * @return
	 *   parent block record
	 */
	public BlockRecord getBlockRecord();

	/**
	 * Gets the data buffer of this record. The record is made up
	 * of a record header and record data. The record data field does
	 * not contain the record's header. Record's header + record's data
	 * equal the entire record.
	 * 
	 * @return
	 *   the data buffer which contains only the data portion of the record
	 * @throws IOException 
	 */
	public ByteBuffer getRecordDataBuffer() throws IOException;

	/**
	 * Retrieves the length of the data within the record. This length does not
	 * contain the length of the record's header. Also if this data
	 * record is a packet record for certain file formats the length
	 * may be more then as returned by {@link Packet#getIncludedLength} due
	 * to the fact that certain records may be oversized to hold the packet
	 * data and thus there is unsused record data portion. 
	 * 
	 * @return
	 *   length in bytes of only the data portion of the record
	 * @throws IOException 
	 */
	public int getRecordDataLength() throws IOException;


	
	/**
	 * Converts java class type of a generic record into more specific record.
	 * This method does not do conversion between different types of records
	 * simply allows easy way of casting a record from more generic to more
	 * specific type. Note that a normal java typecast to more specific record
	 * type may fail as this is very implementation dependent behaviour and
	 * should not be relied upon. The proper way to change a Record object into
	 * a more specific object that is matched by the Record type is to use the
	 * {@link #asType} method. Note that the actual record object may be a stub
	 * in a remote session and the actual more specific type may have to be
	 * fetched accross a network conneciton.
	 * 
	 * @param <C>
	 *            class type to convert this record to. The record type must be
	 *            of the correct type inorder for the cast conversion to
	 *            succeed.
	 * 
	 * @param c
	 *            class instance of specific class type of a record to be
	 *            converted to
	 * 
	 * @return returns an instance pointing at the more specific record type
	 *         requested
	 * 
	 * @throws FileFormatException
	 *             if the current record is not of the correct type for the
	 *             conversion to take place
	 */
	public <C extends DataRecord> C asType(Class<C> c)
			throws FileFormatException;
}
