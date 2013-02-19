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
package org.jnetstream.capture.file.pcap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.Set;

import org.jnetstream.capture.CapturedProperty;
import org.jnetstream.capture.file.RecordFilterTarget;



/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum PCAPRecordType implements RecordFilterTarget {
	BlockRecord("File header containing global file information",
			"",
			EnumSet.of(
					CapturedProperty.EntityTimezone,
					CapturedProperty.FileMagicNumber,
					CapturedProperty.FileVersion,
					CapturedProperty.EntityTimezone,
					CapturedProperty.PacketProtocol)),
	PacketRecord("Record containing packet buffer and capture timestamp",
			"",
			EnumSet.of(
					CapturedProperty.PacketBuffer,
					CapturedProperty.CaptureTimestampSeconds,
					CapturedProperty.CaptureTimestampMicros)),
	;
	
	private final String description;
	private final Set<CapturedProperty> capabilities;
	private final URI spec;

	private PCAPRecordType(String description, String spec, EnumSet<CapturedProperty> capabilities) {
		this.description = description;
		try {
			this.spec = new URI(spec);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Internal error: this should not happen", e);
		}
		this.capabilities = capabilities;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return Returns the capabilities.
	 */
	public Set<CapturedProperty> getCapabilities() {
		return capabilities;
	}

	/**
	 * @return Returns the spec.
	 */
	public URI getSpec() {
		return spec;
	}

}
