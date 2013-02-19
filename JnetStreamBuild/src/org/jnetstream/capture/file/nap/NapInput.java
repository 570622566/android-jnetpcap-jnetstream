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
package org.jnetstream.capture.file.nap;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jnetstream.capture.InputCapture;
import org.jnetstream.capture.InputIterator;


/**
 * A stream formatted to comform to Nap file format.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public interface NapInput extends InputCapture<NAPPacket>{
	/**
	 * An interator which iterates over Nap packets within the stream.
	 */
	public InputIterator<NAPPacket> getPacketIterator() throws IOException;

	/**
	 * An iterator which iterates over Nap records within the stream.
	 */
	public InputIterator<? extends NAPRecord> getRecordIterator()
	    throws IOException;

	/**
	 * An iterator which iterates over Nap records within the stream and returns
	 * them as raw ByteBuffers. The limit and position properties within the
	 * returned ByteBuffer mark the beginning and end of the record.
	 */
	public InputIterator<ByteBuffer> getRawIterator() throws IOException;

}
