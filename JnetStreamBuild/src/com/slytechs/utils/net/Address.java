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

import java.io.Serializable;

import com.slytechs.utils.namespace.NameResolutionException;
import com.slytechs.utils.namespace.ResolvableName;

/**
 * <P>A Class for storing opaque Addresses. Any type of address can be stored
 * at any length.</P>
 * 
 *  <P>Typically this class is subclassed and more specific implementations such
 *  as IpAddress and MacAddress add additional functionality but use the underlying
 *  mechanics of Address for storage and manipulation of address.</P>
 *  
 *  <P>A several 
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class Address implements Serializable, ResolvableName, Comparable {

	private static final long serialVersionUID = -3475961391258142778L;

	public static final int DEFAULT_NUMBER_BASE = 10; // Base is the default.


	/**
	 * Array which stores the IP address bytes. This is a variable array so that
	 * both v4 and v6 addresses.
	 */
	protected byte[] address = null;

	protected char separator = '.';

	protected char[] format = null;

	protected int radix = DEFAULT_NUMBER_BASE;

	/**
	 * Special internal use only constructor that does not initialize the
	 * Address right away but later.
	 * 
	 * @param stringAddress
	 *            takes the IP address as a string.
	 */
	protected Address() {
		/* Empty */
	}

	/**
	 * Main constructor taking the array of bytes as the address
	 */
	public Address(byte[] address) {
		this.address = address;
	}

	/**
	 * Main constructor taking the array of bytes as the address
	 */
	public Address(Address address) {
		this.address = address.address;
		this.separator = address.separator;
		this.format = address.format;
	}

	public boolean isNameResolved() {
		return false;
	}
	
	public boolean hasNameResolvingService() {
		return false;
	}
	
	/**
	 * Returns either the resolved name or address as string.
	 * 
	 * @return Resolved name or address as string.
	 */
	public String getName() {
		return toString(true);
	}

	public boolean resolveName() throws NameResolutionException {
		return false;
	}

	public void setAddress(byte[] address) {
		this.address = address;
	}

	public void setAddress(String address, char separator) {
		this.address = AddressUtils.toByteArray(address, separator);
	}

	public void setSeparator(char separator) {
		this.separator = separator;
		format = null;
	}

	public void setFormat(String format) {
		this.format = format.toCharArray();
	}

	public char[] getFormat() {
		return format;
	}

	public void setFormat(char[] format) {
		this.format = format;
	}

	public void setRadix(int radix) {
		this.radix = radix;
	}

	public int getRadix() {
		return radix;
	}

	/**
	 * Convenience method that converts a signed byte to unsigned byte and
	 * returns it as an int so that sign doesn't flip.
	 * 
	 * @param index offset into the byte array of this address. Same as
	 * toByteArray()[index]
	 * @return Unsigned byte value as integer of the address at the index offset.
	 */
	protected int byteAt(int index) {
		return (address[index] < 0 ? address[index] + 256 : address[index]);
	}

	/**
	 * Compare our byte values for IP address to the object's. If length do not
	 * match then the objects are considered unequal. If byte counts are the
	 * same then a byte for byte comparison is made and appropriate result
	 * return.
	 * 
	 * @param o
	 *            Address object to campare to.
	 * @return true means both objects are equal. This means both Addresss have
	 *         the same number of bytes and each of those bytes is equal (==).
	 * @exception If
	 *                either object is not of IP v4 or V6. If their byte lengths
	 *                do not follow the standard.
	 */
	public boolean equals(Object o) {

		if (o instanceof Address) {

			/**
			 * Let the compare function figure it out.
			 */
			if (compareTo(o) == 0)
				return true;
			else
				return false;
		}

		return false;
	}
	
	public int hashCode() {
		return 0;
	}

	public int compareTo(Object o) {

		if (o instanceof Address) {
			return AddressUtils.compare(this, (Address) o);
		}

		throw new ClassCastException();
	}

	/**
	 * This method EORs (Exclusive OR) Address object to another Address object.
	 * Every byte is ANDed with every other byte in the corresponding object. If
	 * number of bytes in the first number does not match the number in the
	 * second object then an IllegalArgumentException is thrown.
	 * 
	 * @return A new byte array is returned with mask applied to the ip number
	 *         object.
	 */
	public byte[] eor(Address b) {
		return AddressUtils.EOR(address, b.toByteArray());
	}

	/**
	 * This method INVERTs (Java statment <CODE>~</CODE>) Address object to
	 * another Address object. Every byte is ANDed with every other byte in the
	 * corresponding object. If number of bytes in the first number does not
	 * match the number in the second object then an IllegalArgumentException is
	 * thrown.
	 */
	public byte[] invert() {
		return AddressUtils.INVERT(address);
	}

	/**
	 * This method ORs Address object to another Address object. Every byte is
	 * ANDed with every other byte in the corresponding object. If number of
	 * bytes in the first number does not match the number in the second object
	 * then an IllegalArgumentException is thrown.
	 * 
	 * @return A new byte array is returned with mask applied to the ip number
	 *         object.
	 */
	public byte[] or(Address b) {
		return AddressUtils.OR(address, b.toByteArray());
	}


	/**
	 * This method ANDs Address object to another Address object. Every byte is
	 * ANDed with every other byte in the corresponding object. If number of
	 * bytes in the first number does not match the number in the second object
	 * then an IllegalArgumentException is thrown.
	 * 
	 * @return A new byte array is returned with mask applied to the ip number
	 *         object.
	 */
	public byte[] and(Address b) {
		return AddressUtils.AND(address, b.toByteArray());
	}



	/**
	 * Returns a byte[] representing this Address.
	 * 
	 * @return address of this number.
	 */
	public byte[] toByteArray() {
		return address;
	}

	/**
	 * Covert internal IpV4 address to a long
	 * @return
	 */
	public long toLong() {
		
		long a = 0;
		a |= ((address[0] < 0)?(long)((int)address[0] + 256):address[0]) << 24;
		a |= ((address[1] < 0)?(long)((int)address[1] + 256):address[1]) << 16;
		a |= ((address[2] < 0)?(long)((int)address[2] + 256):address[2]) << 8;
		a |= ((address[3] < 0)?(long)((int)address[3] + 256):address[3]);
		
		return a;
	}


	/**
	 * Returns as string representation of an address. If address
	 * has been resolved to a name through some kind of service,
	 * and getResolvedName is true,
	 * then name is returned otherwise the address is converted to
	 * a string using current formatting properties. In either case
	 * a valid string is returned.
	 * 
	 * @param getResolvedName When true, checks to see if name has been resolved
	 * and returnes the resolved name, if the name has not been resolved returns 
	 * address as string instead. When false, always returns address as string and
	 * does not returned any resolved names even if they are found.
	 * 
	 * @return IP address in the dot notation.
	 */
	public String toString(boolean getResolvedName) {
		
		if (getResolvedName) {
			if (isNameResolved()) {
				return getName();
			} 
		} 

		if (format != null) {
			return AddressUtils.toString(address, radix, format);
		} else {
			return AddressUtils.toString(address, radix, separator);
		}

	}
	
	/**
	 * Returns as string representation of an address. If address
	 * has been resolved to a name through some kind of service, then
	 * then name is returned otherwise the address is converted to
	 * a string using current formatting properties. In either case
	 * a valid string is returned.
	 * 
	 * @return Either a resolved name or address converted to string is retruned.
	 */
	public String toString() {
		return toString(true);
	}

} /* END OF: Address */
