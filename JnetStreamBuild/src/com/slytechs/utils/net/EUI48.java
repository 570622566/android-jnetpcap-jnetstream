/**
 * $Id$
 *
 * Copyright (C) 2006 Mark Bednarczyk, Sly Technologies, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place,
 * Suite 330, Boston,
 * MA 02111-1307 USA
 * 
 */
package com.slytechs.utils.net;



/**
 * A Class for storing MAC Addresses
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class EUI48 
	extends Address {

	/* Internal attributes */

	
	private static final long serialVersionUID = 3708471998652004154L;

	/**
	 * Main constructor taking the array of bytes as the address.
	 * Automatically sets the separator token to ':' 
	 *
	 * @param address array of bytes making up the address.
	 * @exception IllegalArgumentException if number of bytes is not 6.
	 */
	public EUI48(byte [] address) {
		super(address);

		if(address.length == 6) {
			setSeparator(':');
			setRadix(16);
		}

		else {
			throw new IllegalArgumentException("EUI48 Address can only be 48 bits long");
		}
	}

	/**
	 * <P>Create a MacAdderss for a properly formatted address string. MacAddress format
	 * require 6 hexadecimal integers separated by : character.</P>
	 * @param address Address string in the : (colon) notation.
	 */
	public EUI48(String address) {
		this(AddressUtils.toByteArray(address, ':', 16));		
	}
	
	/**
	 * Convert to dot notation string representation of the address. If hostname is
	 * known then hostname.domain is returned other wise the appropriate numerical
	 * representation of the numerical address is returned as a String.
	 *
	 * @return IP address in the dot notation.
	 */
	public String toString() {
		return(super.toString());
	}
	
	/**
	 * <P>Factory method to create a new instance of mac address from a string address.</P>
	 * @param address
	 * @return
	 */
	public static EUI48 valueOf(String address) {
		return new EUI48(address);
	}

	/**
	 * <P>Factory method to create a new instance of mac address from a long. Only first 6 bytes
	 * are significant, the remaining 2 are ignored and should be set to 0s.</P>
	 * @param address
	 * @return
	 */
	public static EUI48 valueOf(Long address) {
		return new EUI48(AddressUtils.toByteArray6Bytes(address));
	}
	
} /* END OF: UIE48 */
