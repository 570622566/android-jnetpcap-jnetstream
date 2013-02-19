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
import java.util.Iterator;

import org.jnetstream.capture.CaptureDevice;
import org.jnetstream.capture.Captures;
import org.jnetstream.packet.Header;
import org.jnetstream.packet.HeaderCache;
import org.jnetstream.packet.Packet;
import org.jnetstream.protocol.Protocol;
import org.jnetstream.protocol.ProtocolEntry;
import org.jnetstream.protocol.ProtocolInfo;
import org.jnetstream.protocol.ProtocolRegistry;
import org.jnetstream.protocol.codec.CodecCreateException;
import org.jnetstream.protocol.codec.HeaderCodec;
import org.jnetstream.protocol.codec.PacketRuntime;
import org.jnetstream.protocol.codec.Scanner;

import com.slytechs.utils.io.IORuntimeException;
import com.slytechs.utils.memory.BitBuffer;

/**
 * Lightweight implementation of some basic packet properties
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class APacket implements PacketRuntime, Packet {

	/**
	 * DLT protocol ID
	 */
	ProtocolEntry dlt;

	/**
	 * List of headers that make up this packet
	 */
	HeaderCache headers;

	/**
	 * Heap packet constructor
	 * 
	 * @param dlt
	 *          dlt id
	 */
	protected APacket(ProtocolEntry dlt) {
		this.dlt = dlt;
	}

	/**
	 * 
	 */
	public Header[] getAllHeaders() throws IOException {
	  if (headers == null) {
	  	fullScan();
	  }
	  
	  return headers.toArray(new Header[headers.size()]);
  }

	/**
	 * @throws IOException
	 * @throws CodecCreateException
	 */
	private void quickScan() throws IOException {

		Scanner scanner =
		    ProtocolRegistry.getScanner("org.jnetstream.protocol.FastScanner");
		headers = scanner.init(getBuffer(), new FastHeaderCache());

		scanner.quickScan(this);
	}
	
	/**
	 * @throws IOException
	 * @throws CodecCreateException
	 */
	private void fullScan() throws IOException {

		Scanner scanner =
		    ProtocolRegistry.getScanner("org.jnetstream.protocol.FastScanner");
		headers = scanner.init(getBuffer(), new FastHeaderCache());

		scanner.fullScan(this);
	}

	public HeaderCache getCache() {
		return headers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CapturePacket#getCaptureDevice()
	 */
	public CaptureDevice getCaptureDevice() {
		return null; // Default, may be overriden
	}

	public BitBuffer getData() throws IOException {

		if (headers == null) {
			quickScan();
		}

		if (headers.isEmpty()) {
			return getBuffer();
		}

		BitBuffer b = getBuffer();

		Header last = headers.getHeader(headers.size() - 1);
		int p = b.position() + last.getOffset() + last.getLength();
		p = (p > b.limit() ? b.limit() : p);

		// Now position the buffer
		b.position(p);

		return b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.codec.PacketRuntime#getDlt()
	 */
	public ProtocolEntry getDlt() {
		return dlt;
	}

	@SuppressWarnings("unchecked")
	public <T extends Header> T getHeader(Class<T> c)
	    throws IllegalStateException, IOException {

		return (T) getHeader(ProtocolRegistry.lookup(c));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getHeader(org.jnetstream.protocol.Protocol)
	 */
	@SuppressWarnings("unchecked")
	public <T extends Header> T getHeader(Protocol p)
	    throws IllegalStateException, IOException {
		return (T) getHeader(ProtocolRegistry.lookup(p));
	}

	@SuppressWarnings("unchecked")
	public <T extends Header> T getHeader(ProtocolEntry p)
	    throws IllegalStateException, IOException {
		T r = (T) headers.getHeader(p);
		if (r == null) {
			int offset = headers.getOffset(p.getIndex());
			HeaderCodec<? extends Header> codec =
			    (HeaderCodec<? extends Header>) p.getCodec();

			r = (T) codec.newHeader(getBuffer(), offset);

			headers.put(p.getIndex(), 0, r);
		}

		return r;
	}

	public int getHeaderCount() throws IOException {
		if (headers == null) {
			quickScan();
		}

		return headers.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getProperty(org.jnetstream.packet.Packet.Property)
	 */
	@SuppressWarnings("unchecked")
  public <A> A getProperty(Property property) {
		return (A) "";
	}

	public <T extends Header> boolean hasCompleteHeader(Class<T> c)
	    throws CodecCreateException, IOException {

		return isTruncated() == false && hasHeader(c);
	}

	public <T extends Header> boolean hasCompleteHeader(ProtocolInfo<T> p)
	    throws CodecCreateException, IOException {
		return hasCompleteHeader(p.getType());
	}

	public <T extends Header> boolean hasHeader(Class<T> c)
	    throws CodecCreateException, IOException {
		if (headers == null) {
			quickScan();
		}

		return headers.contains(c);
	}

	public <T extends Header> boolean hasHeader(ProtocolEntry p)
	    throws CodecCreateException, IOException {
		return headers.contains(p);
	}

	public Iterator<Header> iterator() {

		if (headers == null) {
			try {
				fullScan();
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
		}

		return headers.iterator();
	}
	
	public String toString() {
		String s = Captures.stringFormatter().format(this).toString();
		
		return s;
	}

	/* (non-Javadoc)
   * @see org.jnetstream.packet.Packet#format()
   */
  public void format() throws IOException {
	  Captures.defaultFormatter().format(this);
  }
}
