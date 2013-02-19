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
package org.jnetstream.capture;

import org.jnetstream.capture.file.nap.NapFile;
import org.jnetstream.capture.file.pcap.PcapFile;
import org.jnetstream.capture.file.snoop.SnoopFile;

import com.slytechs.utils.namespace.Named;

/**
 * Some standard supplied file types with some convenience methods. The enum
 * structure implements the required CaptureType which all factory methods
 * require and you can use these constants directly to specify the file type.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum FormatType implements Named {
	/**
	 * TcpDump.org Pcap file type
	 */
	Pcap(PcapFile.class, ".cap", ".pcap", ".data"),

	/**
	 * jNetStream's own Network cAPture file format
	 */
	Nap(NapFile.class, ".nap"),
	
	/**
	 * Snoop file format
	 */
	Snoop(SnoopFile.class, ".snoop"),
	
	/**
	 * Format type that does not have a "hard" implementation distributed part of
	 * the core "capture framework" distribution. This is typically NPL based file
	 * format that has an NPL based definition. These types of formats are only
	 * supported by CaptureInput sessions.
	 */
	Other(null, "n/a");
	;


	/**
	 * Detailed information about a specific format.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface Detail {
		
		/**
		 * Retrieves the core format type. Core formats are supplied with jNetStream
		 * distribution.
		 * 
		 * @return enum constant of file type.
		 */
		public FormatType getType();
		
		/**
		 * Retrieves the detailed name for this format. Subtypes are not
		 * neccessarily supplied with the default distribution of jNetStream but may
		 * be provided by other means.
		 * 
		 * @return name of the subtype even if its not part of jNetStream core
		 *         distribution
		 */
		public String getDetailedName();
		
	}
	
	private Class<? extends FileCapture<? extends FilePacket>> typeClass;

	private final String[] extensions;

	private FormatType(Class<? extends FileCapture<? extends FilePacket>> c, String... extensions) {
		this.typeClass = c;
		this.extensions = extensions;
	}

	/**
	 * Gets the main class that provides all the functionality for this type of
	 * file
	 * 
	 * @return main implementing class
	 * @see org.jnetstream.capture.CaptureType#getTypeClass()
	 */
	public Class<? extends FileCapture<? extends FilePacket>> getTypeClass() {
		return typeClass;
	}

	/**
	 * Gets the string name of the file type.
	 * 
	 * @return name of this file type
	 * @see com.slytechs.utils.namespace.Named#getName()
	 */
	public String getName() {
		return toString();
	}

	/**
	 * Retrieves common extensions for this file format.
	 * 
	 * @return array of common extensions for this file format, including the
	 *         preceding typical "dot"
	 */
	public String[] getCommonExtensions() {
		return extensions;
	}

	/**
   * @return
   */
  public String getDetailedName() {
	  return getName();
  }
}
