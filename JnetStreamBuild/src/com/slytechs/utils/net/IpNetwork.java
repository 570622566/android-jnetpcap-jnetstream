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
 * This class manipulates a IP address as a network address
 * it requires 2 values, address and netmask. Netmask is applied 
 * to the various operations that this class supports such as comparisons
 * and sorting.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class IpNetwork extends Address implements Comparable {

	private static final long serialVersionUID = 3110157426646623032L;

	private IpNetmask netmask = null;

	/**
	 * This form initializes the network number by creating
	 * a netmask from the number of bits within the mask. Mask value
	 * will be created. (ie. 24 = 255.255.255.0)
	 * @note long (64bit) values are used since Java does not have a concept
	 * of an unsigned value and always treats 32bit integers as signed.
	 * @param network The network number IP address
	 * @param bitsInMask 
	 */
	public IpNetwork(String network, int bitsInMask) {
		super(AddressUtils.toByteArray(network, '.'));

		setNetmask(new IpNetmask(bitsInMask));
	}

	public IpNetwork(String network, IpNetmask netmask) {
		super(AddressUtils.toByteArray(network, '.'));

		setNetmask(netmask);
	}

	public IpNetwork(byte[] byteAddress, IpNetmask mask) {
		super(byteAddress);

		setNetmask(mask);
	}

	public IpNetwork(Address ip, int bitsInMask) {
		super(ip);

		setNetmask(new IpNetmask(bitsInMask));
	}

	public IpNetwork(Address ip, IpNetmask mask) {
		super(ip);

		setNetmask(mask);
	}

	public IpNetmask getNetmask() {
		return netmask;
	}
	
	public void setNetmask(IpNetmask netmask) {
		this.netmask = netmask;
				
		setAddress(netmask.applyNetmask(this));
	}

	/**
	 * A getter method which returns lower network boundary based on
	 * the netmasked network address.
	 *
	 * @retrun Lower address boundary
	 */
	public Address getLowerBounds() {
		return this;
	}

	/**
	 * A getter moethd which returns the upper network boundary based on
	 * the netmasked networked address.
	 *
	 * @retrun Upper address boundary
	 */
	public Address getUpperBounds() {
		byte[] invertedMask = AddressUtils.INVERT(netmask.toByteArray());
		
		return new IpAddress(AddressUtils.OR(toByteArray(), invertedMask));
	}


	public boolean equals(Object o) {

		if (o instanceof Address) {
			return (compareTo(o) == 0);
		} else {
			return false;
		}
	}

	public boolean isPartOf(Object o) {

		if (o instanceof IpNetwork) {
			IpNetwork no = (IpNetwork) o;

			byte[] temp = netmask.applyNetmask(no.toByteArray());

			if (compareTo(temp) == 0) {
				if (netmask.getBitCount() <= no.getNetmask().getBitCount()) {
					return true;
				}
			}
		}
		
		return false;
	}


	public String toString() {
		String s = "";

		s += "";
		s += super.toString();
		s += "/" + netmask.toString();
		s += "";

		return s;
	}

	public int compareTo(byte[] comp) {

		int c = AddressUtils.compare(toByteArray(), comp);

		return c;
	}

	public int compareTo(Object o) {


		if ((o instanceof IpNetwork) == false) {
			throw new ClassCastException("Expecting IpNetwork class");
		}

		IpNetwork in = (IpNetwork) o;

		int c = AddressUtils.compare(toByteArray(), in.toByteArray());
		if (c == 0) {
			return netmask.compareTo(in.netmask);
		}

		return c;
	}

} /* END OF: IpNetwork */
