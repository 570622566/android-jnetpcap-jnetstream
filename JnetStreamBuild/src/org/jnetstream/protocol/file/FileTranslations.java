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
package org.jnetstream.protocol.file;

import org.jnetstream.capture.file.pcap.PcapDLT;
import org.jnetstream.capture.file.pcap.PcapFile;
import org.jnetstream.capture.file.snoop.SnoopDLT;
import org.jnetstream.capture.file.snoop.SnoopFile;
import org.jnetstream.protocol.Protocol;
import org.jnetstream.protocol.ProtocolRegistry;
import org.jnetstream.protocol.lan.Lan;

import com.slytechs.utils.module.Initializer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public class FileTranslations implements Initializer {
	
	public enum Pcap {
		m1(PcapDLT.EN10, Lan.Ethernet2),
		;
		
		private final PcapDLT pcap;
		private final Protocol proto;
		private int value;

		private Pcap(PcapDLT pcap, Protocol proto) {
			this.pcap = pcap;
			this.value = (int) pcap.intValue();
			this.proto = proto;
			
		}
	}
	
	public enum Snoop {
		m1(SnoopDLT.Ethernet, Lan.Ethernet2),
		;
		
		private final SnoopDLT pcap;
		private final Protocol proto;
		private int value;

		private Snoop(SnoopDLT pcap, Protocol proto) {
			this.pcap = pcap;
			this.value = (int) pcap.intValue();
			this.proto = proto;
			
		}
	}



	/* (non-Javadoc)
   * @see com.slytechs.utils.module.Initializer#init()
   */
  public void init() {
		for (Pcap p: Pcap.values()) {
			ProtocolRegistry.addTranslation(PcapFile.class, p.proto, p.value);
		}
		
		for (Snoop p: Snoop.values()) {
			ProtocolRegistry.addTranslation(SnoopFile.class, p.proto, p.value);
		}  }
}
