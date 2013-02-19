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

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jnetstream.capture.FileCapture;


import com.slytechs.utils.collection.Validatable;

/**
 * <P>
 * A record within a capture file. Capture files contain multiple records which
 * contain data. This interface provides access to a generic Record. Each record
 * has at minimum, a header and content area. You can use various methods in
 * this interface to access and manipulate these standard fields within the
 * record.
 * <P>
 * <P>
 * Record consists of a header and record content. Records retrieve the header
 * and content into separate buffers. Also when you modify the record you may
 * supply each of the two buffers separately. You may use
 * {@link #getHeaderBuffer} and {@link #getContentBuffer} method calls to
 * retrieve raw buffers.
 * </P>
 * <P>
 * The header contains information about the record. The most important of which
 * is the type of record and record's overall length. Use the
 * {@link #getRecordType} method to get the records type and
 * {@link #getRecordLength} to get the entire record's length.
 * </P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Record extends Validatable {

	public interface RecordHeaderField {

		public <T> T read();

		public <T> T read(ByteBuffer buffer);

		public <T> T read(ByteBuffer buffer, int start);

		public <T> void write(T value);

		public <T> void write(ByteBuffer buffer, T value);

		public <T> void write(ByteBuffer buffer, int start, T value);

		public String toStringDebug();
	}

	/**
	 * Gets the current FileCapture that this record is part of. If the record is
	 * not attached to a file a null will be returned.
	 * 
	 * @return current FileCapture session this record is attached to or null if
	 *         none
	 */
	public FileCapture getFileCapture();

	/**
	 * Returns the type of this record. There are certain standard record types
	 * defined, plus each file type may define addtional record types beyond the
	 * standard ones.
	 * 
	 * @return Type of this record.
	 */
	public RecordType getRecordType();

	/**
	 * Returns the current active buffer that stores this record header. The
	 * buffer's position and limit properties are set to point exactly at the
	 * header's content.
	 * 
	 * @return the buffer for this record's header
	 * @throws IOException
	 */
	public ByteBuffer getRecordHeaderBuffer() throws IOException;

	/**
	 * Length of the header of this record. The record length is file format
	 * specific althogh it is usually constant for each record type.
	 * 
	 * @return length of this record's header
	 */
	public int getRecordHeaderLength();

	/**
	 * Returns the length of the entire record.
	 * 
	 * @return length of the record in octets
	 * @throws IOException
	 */
	public long getRecordLength() throws IOException;

	public ByteBuffer getRecordBuffer() throws IOException;

	public long getPositionGlobal() throws IOException;
	
	public void edit() throws IOException;
	
	/**
	 * Converts the current record to more specific type of 'c'. The user must
	 * ensure that the record type is record is actually the correct type.
	 * 
	 * @param <T>
	 * @param c
	 * @return
	 */
	public <T extends Record> T asType(Class<T> c);
}
