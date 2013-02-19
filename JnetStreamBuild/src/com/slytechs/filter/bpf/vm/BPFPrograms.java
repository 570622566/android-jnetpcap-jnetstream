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
package com.slytechs.filter.bpf.vm;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jnetstream.filter.bpf.BPFProgram;
import org.jnetstream.filter.bpf.IllegalInstructionException;
import org.jnetstream.filter.bpf.PcapDebugFilter;
import org.jnetstream.protocol.Protocol.Builtin;

/**
 * BPF programs generated with WinDump and tcpdump as follows:
 * 
 * <pre>
 *  windump -dd -y EN10MB recordFilter
 * </pre>
 * 
 * Where "-i 2" is the interface and 2 is second interface which is Ethernet.
 * This specifies the DLT of ethernet, otherwise the primary PPP interface and
 * DLT are chosen.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum BPFPrograms {

	/**
	 * 
	 */
	Ethernet("{ 0x6, 0, 0, 0x00000400 },"),

	/**
	 * 
	 */
	IPv4("{ 0x28, 0, 0, 0x0000000c },\r\n" + "{ 0x15, 0, 1, 0x00000800 },\r\n"
	    + "{ 0x6, 0, 0, 0x00000400 },\r\n" + "{ 0x6, 0, 0, 0x00000000 },"),

	/**
	 * 
	 */
	IPv6("{ 0x28, 0, 0, 0x0000000c },\r\n" + "{ 0x15, 0, 1, 0x000086dd },\r\n"
	    + "{ 0x6, 0, 0, 0x00000060 },\r\n" + "{ 0x6, 0, 0, 0x00000000 },"),

	/**
	 * 
	 */
	TCP("{ 0x28, 0, 0, 0x0000000c },\r\n" + 
			"{ 0x15, 0, 2, 0x000086dd },\r\n" + 
			"{ 0x30, 0, 0, 0x00000014 },\r\n" + 
			"{ 0x15, 3, 4, 0x00000006 },\r\n" + 
			"{ 0x15, 0, 3, 0x00000800 },\r\n" + 
			"{ 0x30, 0, 0, 0x00000017 },\r\n" + 
			"{ 0x15, 0, 1, 0x00000006 },\r\n" + 
			"{ 0x6, 0, 0, 0x00000060 },\r\n" + 
			"{ 0x6, 0, 0, 0x00000000 },"
	    + "{ 0x6, 0, 0, 0x00000000 },"),

	/**
	 * 
	 */
	UDP("{ 0x28, 0, 0, 0x0000000c },\r\n" + "{ 0x15, 0, 2, 0x000086dd },\r\n"
	    + "{ 0x30, 0, 0, 0x00000014 },\r\n" + "{ 0x15, 3, 4, 0x00000011 },\r\n"
	    + "{ 0x15, 0, 3, 0x00000800 },\r\n" + "{ 0x30, 0, 0, 0x00000017 },\r\n"
	    + "{ 0x15, 0, 1, 0x00000011 },\r\n" + "{ 0x6, 0, 0, 0x00000060 },\r\n"
	    + "{ 0x6, 0, 0, 0x00000000 },"),

	/**
	 * 
	 */
	ARP("{ 0x28, 0, 0, 0x0000000c },\r\n" + "{ 0x15, 0, 1, 0x00000806 },\r\n"
	    + "{ 0x6, 0, 0, 0x00000400 },\r\n" + "{ 0x6, 0, 0, 0x00000000 },"),

	/**
	 * 
	 */
	ICMP("{ 0x28, 0, 0, 0x0000000c },\r\n" + "{ 0x15, 0, 3, 0x00000800 },\r\n"
	    + "{ 0x30, 0, 0, 0x00000017 },\r\n" + "{ 0x15, 0, 1, 0x00000001 },\r\n"
	    + "{ 0x6, 0, 0, 0x00000400 },\r\n" + "{ 0x6, 0, 0, 0x00000000 },"),

	;

	private final Log logger = LogFactory.getLog(BPFPrograms.class);

	private final BPFProgram program;
	
	private final PcapDebugFilter filter;

	private final String source;

	private BPFPrograms(String tcpoutput) {
		
		PcapDebugFilter filter = null;
		BPFProgram program = null;

		this.source = tcpoutput;
		try {
			program = new BPFProgram(tcpoutput);
			filter = new PcapDebugFilter(tcpoutput, Builtin.Null);
			
		} catch (IllegalInstructionException e) {
			logger.error(e.toString());
		}
		
		this.program = program;
		this.filter = filter;

	}

	/**
	 * Gets the program that can be used by a BpfVM
	 * 
	 * @return compiled program ready for use
	 */
	public final BPFProgram getProgram() {
		return this.program;
	}

	/**
	 * @return the source
	 */
	public final String getSource() {
		return this.source;
	}

	public final PcapDebugFilter getFilter() {
  	return this.filter;
  }
}
