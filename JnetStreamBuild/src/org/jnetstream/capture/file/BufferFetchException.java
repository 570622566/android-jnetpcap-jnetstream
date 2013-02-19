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

import com.slytechs.utils.memory.PartialBuffer;
import com.slytechs.utils.region.FlexRegion;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class BufferFetchException
    extends BufferException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8609730609666590018L;

	private FlexRegion flexRegion;

	/**
   * @param message
   * @param partialBuffer
   * @param regional
   * @param length
   * @param headerReader
   * @param fromCache
   */
  public BufferFetchException(String message, PartialBuffer partialBuffer, long regional, int length, HeaderReader headerReader, boolean fromCache) {
	  super(message, partialBuffer, regional, length, fromCache, headerReader);
	  // TODO Auto-generated constructor stub
  }

	/**
	 * 
	 */
	public BufferFetchException() {
	}

	/**
	 * @param message
	 */
	public BufferFetchException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param partialBuffer
	 * @param regional
	 * @param length
	 * @param fromCache
	 */
	public BufferFetchException(String message, PartialBuffer partialBuffer,
	    long regional, int length, boolean fromCache) {
		super(message, partialBuffer, regional, length, fromCache);
	}

	/**
	 * @param message
	 * @param partialBuffer
	 * @param regional
	 * @param length
	 * @param fromCache
	 * @param cause
	 */
	public BufferFetchException(String message, PartialBuffer partialBuffer,
	    long regional, int length, boolean fromCache, Exception cause) {
		super(message, partialBuffer, regional, length, fromCache, cause);
	}

	public BufferFetchException(String message, PartialBuffer partialBuffer,
	    long regional, int length, boolean fromCache, FlexRegion flexRegion,
	    Exception cause) {
		super(message, partialBuffer, regional, length, fromCache, cause);
		this.flexRegion = flexRegion;
	}

	public String toString() {
		StringBuilder b = new StringBuilder(super.toString());

		if (flexRegion != null) {
			b.append("\nFileEditor=").append(flexRegion.toString()).append(
			    "\t<= Detailed region segments of the file editor");
		}

		return b.toString();
	}

	public final FlexRegion getFlexRegion() {
		return this.flexRegion;
	}

	public final void setFlexRegion(FlexRegion flexRegion) {
		this.flexRegion = flexRegion;
	}

}
