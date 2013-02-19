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

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Defines constants for Meta Record Sub Type field.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum NAPMetaType {

	/**
	 * Meta Record is invalid and corrupt
	 */
	Invalid("Record type is invalid. This event should be logged to the user.",
			"file:///E:/Documents%20and%20Settings/markbe/My%20Documents/jNetPCAP/docs/draft-slytechs-network-nap-00.htm#_Toc134508395"),
	
	/**
	 * Contains a table of properties for this block
	 */
	Properties("Record contains user and NAP system properties.",
			"file:///E:/Documents%20and%20Settings/markbe/My%20Documents/jNetPCAP/docs/draft-slytechs-network-nap-00.htm#_Toc134508396"),
	
	/**
	 * Contains a information table about Capture Systems. 
	 * A Capture System is an interface + system information.
	 * Local and remote Capture systems can exist capturing to the
	 * same capture file and block.
	 */
	CaptureSystems("Contains information about the capture system that captured packets. Could be interface or entire remote system.",
			"file:///E:/Documents%20and%20Settings/markbe/My%20Documents/jNetPCAP/docs/draft-slytechs-network-nap-00.htm#_Toc134508397"),
	
	/**
	 * Contains counter values for specified capture system.
	 */
	InterfaceCounters("Contains counter statistics for the packet that follows at the time of the packet capture.", ""),
	
	/**
	 * Contains protocol binding information that can aid in decoding the Packet data.
	 */
	ProtocolBindings("Protocol binding information as specified by the user during capture or decoding.", 
			"file:///E:/Documents%20and%20Settings/markbe/My%20Documents/jNetPCAP/docs/draft-slytechs-network-nap-00.htm#_Toc134508399"),
	
	/**
	 * Contains entire protocol definitions.
	 */
	ProtocolDefinitions("Protocol definitions for various protocols within the PacketRecord contents", ""),
	
	/**
	 * A table containing log of events that were recorded by the implementing program
	 */
	Event("Log of events during or after the packet capture or post processing", ""),
	
	/**
	 * Routing table information for a particular capture system.
	 */
	Routing("Routing table information during the packet capture or at certain intervals while packet was captured", ""),
	
	/**
	 * Expert analysis data from post processing and analysis of captured data.
	 */
	Expert("Expert analysis objects or data that contains analysis of the captured packet contents", ""),
	;
	
	private final String description;
	private final URI spec;
	
	private NAPMetaType(String description, String spec) {
		this.description = description;
		try {
			this.spec = new URI(spec);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Internal error: this shouldn't happen", e);
		}

	}
	
	public String getDescription() {
		return description;
	}

	/**
	 * @return Returns the spec.
	 */
	public URI getSpec() {
		return spec;
	}

}
