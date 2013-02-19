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

/**
 * Flags that are set within the packet header. The flags are
 * used to mark the packet data to contain certain specific aspects
 * or user defined significance.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * {@see 
 */
public enum NAPFlag {
	/**
	 * Packet was imported from another file or format
	 */
	Imported			 	(0x0001),
	
	/**
	 * Packet is marked for deletion by the user
	 */
	MarkedForDeletion 		(0x0002),
	
	/**
	 * Packet data contains frame pre-amble octets
	 */
	FramePreamble		(0x0004),
	
	/**
	 * Packet data contains CRC
	 */
	FrameCRC				(0x0008),
	
	/**
	 * Packet data is marked to contain Protocol error when
	 * packet content is decoded.
	 */
	ProtocolError			(0x0010),
	
	/**
	 * Packet data is marked to contain structural errors,
	 * that is invalid CRC, corrupt headers or other such
	 * errors.
	 */
	StructuralError         (0x0020),
	
	/**
	 * The user has marked the packet. This information may
	 * be used to report on only packets marked by the user.
	 */
	UserMark                (0x1000),
	
	/**
	 * User has marked the packet to be hidden, that it should
	 * not be reported to the user, although the packet is still
	 * contained within the file.
	 */
	UserHidden              (0x2000),
	
	/**
	 * Decoding of this packet should be skipped.
	 */
	UserNoDecode            (0x4000),
	
	/**
	 * A custom flag that is user defined and has no other special
	 * meaning.
	 */
	UserCustom              (0x8000),
	;
	
	private final int flag;

	private NAPFlag(int flag) {
		this.flag = flag;
		
	}

	/**
	 * @return Returns the flag.
	 */
	public int getFlag() {
		return flag;
	}

}
