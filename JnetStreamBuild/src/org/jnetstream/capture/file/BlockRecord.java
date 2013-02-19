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
import java.nio.ByteOrder;

import com.slytechs.utils.number.Version;

/**
 * A block record also sometimes refered to a file header in a capture file
 * which uniquely identifies the file as certain type with a magic pattern or
 * magic number. Block record contains, as children, one or more data records
 * which usually contain packet data.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface BlockRecord extends Record {

	/**
	 * Gets the byte-order encoding for integers of fields within the record
	 * header.
	 * 
	 * @return either big or little endian encoding
	 */
	public ByteOrder order();

	/**
	 * Returns the first file version found. There may be multiple blocks within
	 * the file at different versions.
	 * 
	 * @return version of the file
	 */
	public Version getVersion() throws IOException;

	/**
	 * Sets the version numbers for the file format.
	 * 
	 * @param major
	 *          major version of the file format
	 */
	public void setMajorVersion(long major) throws IOException;

	/**
	 * Returns the Magic number or pattern that is used to uniquely identify the
	 * file type of the capture file. The magic number is not normalized so the
	 * pattern may be returned differently on big and small endian machines.
	 * 
	 * @return pattern that is used to identify the file type
	 * @throws IOException 
	 */
	public byte[] getMagicPattern() throws IOException;
}
