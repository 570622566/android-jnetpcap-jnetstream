/**
 * Copyright (C) 2008 Sly Technologies, Inc.
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
package com.slytechs.jnetstream.packet;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;

import org.jnetstream.capture.CapturePacket;
import org.jnetstream.capture.FormatType;
import org.jnetstream.capture.InputCapture;
import org.jnetstream.capture.FormatType.Detail;
import org.jnetstream.capture.InputCapture.FormatTypeOtherFactory;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;

import com.slytechs.utils.memory.channel.BufferedReadableByteChannel;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public class DefaultFormatOtherStreamFactory implements FormatTypeOtherFactory {

	/* (non-Javadoc)
	 * @see org.jnetstream.capture.InputCapture.FormatTypeOtherFactory#formatType(java.nio.channels.ReadableByteChannel)
	 */
	public FormatType formatType(ReadableByteChannel in) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.capture.InputCapture.FormatTypeOtherFactory#formatTypeDetail(com.slytechs.utils.memory.channel.BufferedReadableByteChannel)
	 */
	public Detail formatTypeDetail(BufferedReadableByteChannel in)
	    throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.capture.InputCapture.FormatTypeOtherFactory#newInput(java.nio.channels.ReadableByteChannel, org.jnetstream.filter.Filter)
	 */
	public InputCapture<CapturePacket> newInput(ReadableByteChannel in,
	    Filter<ProtocolFilterTarget> filter) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

}
