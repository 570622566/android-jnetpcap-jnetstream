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
package com.slytechs.jnetstream.packet;

import java.io.IOException;
import java.sql.Timestamp;

import org.jnetstream.capture.CaptureDevice;
import org.jnetstream.capture.LiveCaptureDevice;
import org.jnetstream.capture.LivePacket;
import org.jnetstream.packet.Header;
import org.jnetstream.protocol.Protocol;
import org.jnetstream.protocol.ProtocolEntry;

import com.slytechs.utils.memory.BitBuffer;


/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class DefaultLivePacket
    extends APacket implements LivePacket {

	public final static int DEFAULT_BUF_SIZE = 1524;

	BitBuffer bits;

	int ilength; // Included length

	int nseconds; // Timestamp nano-seconds

	int olength; // Original length

	long seconds; // Timestamp seconds

	private LiveCaptureDevice device;

	public DefaultLivePacket(ProtocolEntry protocol) {
		super(protocol);

		this.bits = BitBuffer.allocate(DEFAULT_BUF_SIZE);

		long ts = System.currentTimeMillis();
		int nf = (int) System.nanoTime();

		this.seconds = ts / 1000; // millis to seconds
		this.nseconds = nf;
	}

	/**
	 * @param protocol
	 */
	public DefaultLivePacket(ProtocolEntry protocol, BitBuffer bits, int ilength, int olength,
	    long secs, int nsecs) {
		super(protocol);
		this.bits = bits;
		this.seconds = secs;
		this.nseconds = nsecs;
	}

	/**
	 * @param protocol
	 * @param nseconds
	 * @param caplen
	 * @param len
	 * @param device
	 * @param buffer2
	 * @param wrap
	 * @param seconds2
	 */
	public DefaultLivePacket(ProtocolEntry protocol, BitBuffer bits, long seconds,
	    int nseconds, int caplen, int len, LiveCaptureDevice device) {
		super(protocol);
		this.bits = bits;
		this.seconds = seconds;
		this.ilength = caplen;
		this.olength = len;
		this.device = device;
		this.nseconds = nseconds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getBuffer()
	 */
	public BitBuffer getBuffer() throws IOException {

		return bits;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CapturePacket#getIncludedLength()
	 */
	public long getIncludedLength() throws IOException {
		return ilength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CapturePacket#getOriginalLength()
	 */
	public long getOriginalLength() throws IOException {
		return olength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getTimestampNanos()
	 */
	public long getTimestampNanos() throws IOException {
		return nseconds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getTimestampSeconds()
	 */
	public long getTimestampSeconds() throws IOException {
		return seconds;
	}

	public boolean isTruncated() throws IOException {
		return ilength != olength;
	}

	public String toString() {
		final StringBuilder out = new StringBuilder();
		
		out.append("[len=").append(ilength).append('/').append(olength);
		
		long ms = seconds * 1000;
		
		Timestamp ts = new Timestamp(ms);
		
		out.append(", ").append(ts.toString());
		out.append(", ").append(device.getName());
	
		out.append(']');
		
		return out.toString();
	}


}
