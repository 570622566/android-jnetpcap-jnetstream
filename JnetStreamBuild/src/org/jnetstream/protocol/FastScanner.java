/**
 * Copyright (C) 2008 Sly Technologies, Inc. This library is free software; you
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
package org.jnetstream.protocol;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jnetstream.packet.Header;
import org.jnetstream.packet.HeaderCache;
import org.jnetstream.protocol.codec.HeaderCodec;
import org.jnetstream.protocol.codec.PacketRuntime;
import org.jnetstream.protocol.codec.Scanner;
import org.jnetstream.protocol.lan.Ethernet2;
import org.jnetstream.protocol.lan.Lan;
import org.jnetstream.protocol.tcpip.Ip4;
import org.jnetstream.protocol.tcpip.Tcpip;

import com.slytechs.jnetstream.packet.AbstractData;
import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class FastScanner
    extends AbstractData implements Scanner {
	private final static Log logger = LogFactory.getLog(FastScanner.class);
	private static final ProtocolEntry ARP = gete(Lan.Arp);

	private static final boolean DEBUG = true;

	private static final ProtocolEntry ETHERNET2 = gete(Lan.Ethernet2);

	private static final ProtocolEntry ICMP = gete(Tcpip.Icmp);

	private static final ProtocolEntry IEEE802dot3 = gete(Lan.IEEE802dot3);

	private static final ProtocolEntry LLC2 = gete(Lan.IEEE802dot2);

	private static final ProtocolEntry SNAP = gete(Lan.Snap);

	private static final ProtocolEntry IP4 = gete(Tcpip.Ip4);

	private static final ProtocolEntry TCP = gete(Tcpip.Tcp);

	private static final ProtocolEntry UDP = gete(Tcpip.Udp);

	private static ProtocolEntry gete(Protocol p) {
		ProtocolEntry entry = ProtocolRegistry.getProtocolEntry(p);

		return entry;
	}

	private HeaderCache cache;

	private boolean full = false;

	// private BitBuffer bits;

	private int r1;

	/**
	 * @param bits
	 * @param offset
	 */
	public FastScanner() {
		super(null, 0);
	}

	// private int offset = 0;

	/**
	 * @param bits
	 * @param offset
	 */
	public FastScanner(BitBuffer bits, int offset) {
		super(bits, offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.codec.Scanner#fullScan()
	 */
	public boolean fullScan(PacketRuntime packet) throws IOException {

		this.full = true;
		this.bits = packet.getBuffer();
		this.cache = packet.getCache();

		return scan(packet.getDlt());
	}

	/**
	 * @return the cache
	 */
	public final HeaderCache getCache() {
		return this.cache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.codec.Scanner#init(com.slytechs.utils.memory.BitBuffer,
	 *      org.jnetstream.packet.HeaderCache)
	 */
	public HeaderCache init(BitBuffer buffer, HeaderCache cache) {
		this.cache = cache;
		this.bits = buffer;
		this.offset = buffer.position();

		return cache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.codec.Scanner#quickScan(org.jnetstream.protocol.codec.PacketRuntime)
	 */
	public boolean quickScan(PacketRuntime packetRT) throws IOException {
		this.full = false; // not a full scan
		this.bits = packetRT.getBuffer();
		this.cache = packetRT.getCache();

		return scan(packetRT.getDlt());
	}

	public final void reset() {
		this.offset = 0;
		this.cache.clear();
	}

	public boolean scan(Protocol p) {

		/*
		 * A list of builtin scans
		 */
		if (p == Lan.Ethernet2)
			return scanEthernet();
		if (p == Lan.Arp)
			return scanArp();
		if (p == Tcpip.Ip4)
			return scanIp4();
		if (p == Tcpip.Icmp)
			return scanIcmp();
		if (p == Tcpip.Tcp)
			return scanTcp();
		if (p == Tcpip.Udp)
			return scanUdp();
		if (p == Lan.IEEE802dot2)
			return scanLLC2();
		if (p == Lan.Snap)
			return scanSNAP();

		/*
		 * If not builtin use the bindings a
		 */

		return true;
	}

	/**
   * 
   * @param p
   * @return
   */
	public boolean scan(ProtocolEntry p) {

		final int i = p.getIndex();

		/*
		 * A list of builtin scans
		 */
		if (i == ETHERNET2.getIndex())
			return scanEthernet();
		
		if (i == ARP.getIndex())
			return scanArp();

		if (i == IP4.getIndex())
			return scanIp4();

		if (i == ICMP.getIndex())
			return scanIcmp();

		if (i == IEEE802dot3.getIndex())
			return scanIEEE802dot3();

		if (i == TCP.getIndex())
			return scanTcp();

		if (i == UDP.getIndex())
			return scanUdp();
		
		if (i == LLC2.getIndex())
			return scanLLC2();
		
		if (i == SNAP.getIndex())
			return scanSNAP();

		/*
		 * If not builtin use protocol's bindings TODO: add the binding loop scan
		 */

		return true;
	}
	
	/**
	 * 
	 * @param e
	 */
	private final void addToCache(final ProtocolEntry e) {
		cache.add(e.getIndex(), offset, (full) ? ((HeaderCodec<? extends Header>) e.getCodec())
		    .newHeader(bits, offset) : null);
	}

  /**
   * @return
   */
  public boolean scanArp() {
		addToCache(ARP);
		if (DEBUG)
			logger.debug(String.format("Arp p=%d\n", offset));

		return true;
  }

	public boolean scanEthernet() {
		r1 = readShort(Ethernet2.StaticField.TYPE);
		if (DEBUG)
			logger.debug(String.format("Ethernet p=%d, r1=%d\n", offset, r1));

		if (r1 <= 0x600) {
			return scanIEEE802dot3();
		}

		return scanEthernet2();
	}

	public boolean scanEthernet2() {
		addToCache(ETHERNET2);
		r1 = readShort(Ethernet2.StaticField.TYPE);
		if (DEBUG)
			logger.debug(String.format("Ethernet2 p=%d, r1=%d\n", offset, r1));
		offset += 48 * 2 + 16;

		switch (r1) {
			case 0x800:
				return scanIp4();
				
			case 0x0806:
				return scanArp();
		}

		return true;
	}

	/**
	 * 
	 */
	public boolean scanIcmp() {
		addToCache(ICMP);
		r1 = bits.getBits(offset, 8); // Type
		if (DEBUG)
			logger.debug(String.format("ICMP p=%d, r1=%d\n", offset, r1));

		switch (r1) {
			case 3: // Unreachable
				offset += 128;
				return scanIp4();

			case 11: // Time exceeded
				offset += 128;
				return scanIp4();
		}

		return true;
	}

	/**
	 * 
	 */
	public boolean scanIEEE802dot3() {
		addToCache(IEEE802dot3);
		if (DEBUG) {
			logger.debug(String.format("IEEE803dot3 p=%d, r1=%d\n", offset, r1));
		}
		offset += 48 * 2 + 16;

		return scanLLC2();
	}

	/**
	 * 
	 */
	public boolean scanIp4() {
		addToCache(IP4);
		r1 = readByte(Ip4.StaticField.PROTOCOL);
		if (DEBUG) {
			logger.debug(String.format("Ip4 p=%d, r1=%d\n", offset, r1));
		}
		offset += bits.getBits(offset + 4, 4) * 4 * 8 + 32;

		switch (r1) {
			case 1:
				return scanIcmp();

			case 6:
				return scanTcp();

			case 17:
				return scanUdp();
		}

		return true;
	}

	/**
	 * 
	 */
	public boolean scanLLC2() {
		addToCache(LLC2);
		r1 = BitBuffer.tou(bits.getByte(offset));
		if (DEBUG)
			logger.debug(String.format("LLC2 p=%d, r1=%x\n", offset, r1));
		offset += 32;
		
		switch (r1) {
			case 0xAA:
				return scanSNAP();
		}

		return true;
	}

	/**
   * @return
   */
  public boolean scanSNAP() {
		addToCache(SNAP);
		r1 = bits.getBits(offset, 24);
		int r2 = bits.getBits(offset + 24, 16);
		if (DEBUG)
			logger.debug(String.format("LLC2 p=%d, r1=%x r2=%x\n", offset, r1, r2));
		offset += 40;
		
		if (r1 != 0) {
			return true;
		}
		
		switch (r2) {
			case 0x800:
				return scanIp4();
		}

		return true;
  }

	/**
	 * 
	 */
	public boolean scanTcp() {
		addToCache(TCP);
		r1 = bits.getShort(offset + 16);
		if (DEBUG)
			logger.debug(String.format("Tcp p=%d, r1=%d\n", offset, r1));
		offset += ((bits.getBits(offset + 12, 4) & 0xF0) >> 4) * 4 * 8;

		return true;
	}

	/**
	 * 
	 */
	public boolean scanUdp() {
		addToCache(UDP);
		r1 = bits.getShort(offset + 16);
		if (DEBUG)
			logger.debug(String.format("Udp p=%d, r1=%d\n", offset, r1));
		offset += 64;

		return true;
	}

	/**
	 * @param buffer
	 *          the buffer to set
	 */
	public final void setBuffer(BitBuffer buffer) {
		this.bits = buffer;
		this.offset = buffer.position();
	}

	/**
	 * @param cache
	 *          the cache to set
	 */
	public final void setCache(HeaderCache cache) {
		this.cache = cache;
		reset();
	}
}
