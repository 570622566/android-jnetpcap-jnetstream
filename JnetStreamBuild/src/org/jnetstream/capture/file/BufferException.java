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

import com.slytechs.utils.memory.PartialBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class BufferException
    extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4941664138858214297L;

	private PartialBuffer partialBuffer = null;

	private long regional = -1;

	private boolean fromCache = false;

	private int length = -1;

	private String override = null;

	private HeaderReader headerReader = null;

	/**
	 * 
	 */
	public BufferException() {
		super();
	}

	/**
	 * @param message
	 */
	public BufferException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param partialBuffer
	 * @param regional
	 * @param length
	 * @param fromCache
	 */
	public BufferException(String message, PartialBuffer partialBuffer,
	    long regional, int length, boolean fromCache) {
		super(message);
		this.partialBuffer = partialBuffer;
		this.regional = regional;
		this.length = length;
		this.fromCache = fromCache;
	}
	/**
	 * @param message
	 * @param partialBuffer
	 * @param regional
	 * @param length
	 * @param fromCache
	 */
	public BufferException(String message, PartialBuffer partialBuffer,
	    long regional, int length, boolean fromCache, HeaderReader headerReader) {
		super(message);
		this.partialBuffer = partialBuffer;
		this.regional = regional;
		this.length = length;
		this.fromCache = fromCache;
		this.headerReader = headerReader;
	}

	/**
	 * @param message
	 * @param partialBuffer
	 * @param regional
	 * @param length
	 * @param fromCache
	 * @param e
	 */
	public BufferException(String message, PartialBuffer partialBuffer,
	    long regional, int length, boolean fromCache, Exception cause) {
		this(message, partialBuffer, regional, length, fromCache);

		initCause(cause);
	}

	public final boolean isFromCache() {
		return this.fromCache;
	}

	public final void setFromCache(boolean fromCache) {
		this.fromCache = fromCache;
	}

	public final PartialBuffer getPartialBuffer() {
		return this.partialBuffer;
	}

	public final void setPartialBuffer(PartialBuffer partialBuffer) {
		this.partialBuffer = partialBuffer;
	}

	public final long getRegional() {
		return this.regional;
	}

	public final void setRegional(long regional) {
		this.regional = regional;
	}

	public String toString() {
		final String m = (override == null ? super.toString() : override);
		final StringBuilder b = new StringBuilder(m);

		if (partialBuffer != null) {
		b.append("\nbuffer=").append(partialBuffer.toString()).append(
		    "\t<= Range of partial buffer out of a bigger region");
		}

		b.append("\ncache=").append(fromCache).append(
		    "\t<= Did buffer come from cache?");

		b.append("\nregional=").append(this.regional).append(
		    "\t<= Starting position for the operation");

		b.append("\nlength=").append(this.length).append(
		    "\t<= Length of read operation in bytes");

		b.append("\nreader=").append(this.headerReader.toString()).append(
		    "\t<= Header reader which was used to parsing the record header");

		return b.toString();
	}

	public final int getLength() {
		return this.length;
	}

	public final void setLength(int length) {
		this.length = length;
	}

	public void setMessage(String message) {
		this.override = "BufferException: " + message;
	}

	public String getMessage() {
		return (override == null ? super.getMessage() : override);
	}

	public String getLocalizedMessage() {
		return (override == null ? super.getLocalizedMessage() : override);
	}

	public final HeaderReader getHeaderReader() {
		return this.headerReader;
	}

	public final void setHeaderReader(HeaderReader headerReader) {
		this.headerReader = headerReader;
	}
}
