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
package com.slytechs.capture.file;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jnetstream.capture.file.HeaderReader;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;


/**
 * File capture related utility and factory methods.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public final class Files {

	public static boolean checkProtocolFilter(
	    final Filter<ProtocolFilterTarget> filter,
	    final ProtocolFilterTarget target, final ByteBuffer buffer)
	    throws IOException {

		final boolean result = filter.accept(buffer, target);

		return result;
	}

	public static boolean checkProtocolFilter(
	    final Filter<ProtocolFilterTarget> filter,
	    final ProtocolFilterTarget target, final ByteBuffer buffer,
	    final HeaderReader headerReader) throws IOException {

		final int offset = headerReader.getHeaderLength(buffer);
		return checkProtocolFilter(filter, target, buffer, offset);
	}

	public static boolean checkProtocolFilter(
	    final Filter<ProtocolFilterTarget> filter,
	    final ProtocolFilterTarget target, final ByteBuffer buffer,
	    final int offset) throws IOException {

		final int position = buffer.position();

		buffer.position(position + offset);
		final boolean result = filter.accept(buffer, target);
		buffer.position(position);

		return result;
	}

	public static boolean checkRecordFilter(
	    final Filter<RecordFilterTarget> filter, final ByteBuffer buffer,
	    final HeaderReader headerReader) throws IOException {

		final RecordFilterTarget target = headerReader
		    .readRecordFilterTarget(buffer);

		return checkRecordFilter(filter, buffer, target);
	}

	public static boolean checkRecordFilter(
	    final Filter<RecordFilterTarget> filter, final ByteBuffer buffer,
	    final RecordFilterTarget target) throws IOException {

		final boolean result = filter.accept(buffer, target);

		return result;
	}

	private Files() {
		throw new UnsupportedOperationException(
		    "This is a factory class, should not be instantiated");
	}

}
