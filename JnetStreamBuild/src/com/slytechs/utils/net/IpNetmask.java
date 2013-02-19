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
 * <P>Internet Protocol netmask. This is a netmask that is appart of IP networks and
 * defines how many bits are significant for routing within a routing domain.</p>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class IpNetmask extends Address {
	/* Internal attributes */

	private static final long serialVersionUID = 2953679298109616144L;
	private int bitsInMask = -1;

	/**
	 * 
	 * @param
	 * @exception
	 */
	public IpNetmask(String netmask) {

		setAddress(AddressUtils.toByteArray(netmask, '.'));
	}

	public IpNetmask(int bitsInMask) {
		setAddress(AddressUtils.parseBitCount(bitsInMask));
	}

	public IpNetmask(byte[] address) {
		setAddress(address);
	}

	@Override
	public void setAddress(byte[] address) {
		super.setAddress(address);
		this.bitsInMask = AddressUtils.countBits(toByteArray());
	}

	/**
	 * Return the number of bits turned on in a netmask. This is useful if you
	 * want to display the netmask as a slash with number of bits in mask.
	 * 
	 * @return number of bits that are on in the netmask.
	 */
	public int getBitCount() {
		return this.bitsInMask;
	}

	/**
	 * This method applies the netmask to another IpNumber object such as
	 * IpAddress or IpNetwork. Netmask is ANDed with every byte in the
	 * corresponding object. If number of bytes in the netmask does not match
	 * the number in the IpNumber object then an IllegalArgumentException is
	 * thrown.
	 * 
	 * @return A new byte array is returned with mask applied to the ip number
	 *         object.
	 */
	public byte[] applyNetmask(Address ip) {
		if (this.address.length != ip.address.length)
			throw new IllegalArgumentException("Can not apply netmask. "
					+ "Netmask and IP number byte lengths do not match. "
					+ "Excecting 4 or 16");

		return (AddressUtils.AND(this.address, ip.address));
	}

	public byte[] applyNetmask(byte[] ip) {
		if (this.address.length != ip.length)
			throw new IllegalArgumentException("Can not apply netmask. "
					+ "Netmask and IP number byte lengths do not match. "
					+ "Excecting 4 or 16");

		return AddressUtils.AND(this.address, ip);
	}


	public String toString() {
		if (bitsInMask == -1)
			bitsInMask = AddressUtils.countBits(address);

		return "" + bitsInMask;
	}
	
	public static IpNetmask valueOf(String address) {
		return new IpNetmask(address);
	}

	public static IpNetmask valueOf(Long address) {
		return new IpNetmask(AddressUtils.toByteArray4Bytes(address));
	}


} /* END OF: IpNetmask */
