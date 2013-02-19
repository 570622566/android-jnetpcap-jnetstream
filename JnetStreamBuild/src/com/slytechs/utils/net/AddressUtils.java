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

import java.util.StringTokenizer;

import com.slytechs.utils.format.NumberUtils;

/**
 * A utility  class with a bunch of static methods for manipulating addresses.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class AddressUtils {

	/**
	 * Thread local buffer for building and returning string. This is very
	 * efficient as this is a global threadsafe static buffer.
	 */
	private static final ThreadLocal<StringBuilder> buffer = new ThreadLocal<StringBuilder>() {

		@Override
		public StringBuilder get() {
			return new StringBuilder();
		}

	};

	/**
	 * This method ORs Address object to another Address object. Every byte is
	 * ANDed with every other byte in the corresponding object. If number of
	 * bytes in the first number does not match the number in the second object
	 * then an IllegalArgumentException is thrown.
	 * 
	 * @return A new byte array is returned with mask applied to the ip number
	 *         object.
	 */
	public static byte[] INVERT(byte[] a) {

		byte[] v = new byte[a.length];

		for (int i = 0; i < v.length; i++) {
			int A = (a[i] < 0 ? a[i] + 256 : a[i]);
			v[i] = (byte) (~A); // A ~ (tilde) INVERTs the bits inside the
			// integer. A 0 becomes a 1 and 1 a 0.
		}

		return (v);
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
	public static byte[] EOR(byte[] a, byte[] b) {
		if (a.length != b.length)
			throw new IllegalArgumentException("Can AND 2 Address objects. "
					+ "Their byte counts do not match. "
					+ "Excecting Equal number of bytes in each Address object.");

		byte[] v = new byte[a.length];

		for (int i = 0; i < v.length; i++) {
			int A = (a[i] < 0 ? a[i] + 256 : a[i]);
			int B = (b[i] < 0 ? b[i] + 256 : b[i]);
			v[i] = (byte) (B ^ A);
		}

		return (v);
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
	public static byte[] OR(byte[] a, byte[] b) {
		if (a.length != b.length)
			throw new IllegalArgumentException("Can AND 2 Address objects. "
					+ "Their byte counts do not match. "
					+ "Excecting Equal number of bytes in each Address object.");

		byte[] v = new byte[a.length];

		for (int i = 0; i < v.length; i++) {
			int A = (a[i] < 0 ? a[i] + 256 : a[i]);
			int B = (b[i] < 0 ? b[i] + 256 : b[i]);
			v[i] = (byte) (B | A);
		}

		return (v);
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
	public static byte[] AND(byte[] a, byte[] b) {
		if (a.length != b.length)
			throw new IllegalArgumentException("Can AND 2 Address objects. "
					+ "Their byte counts do not match. "
					+ "Excecting Equal number of bytes in each Address object.");

		byte[] v = new byte[a.length];

		for (int i = 0; i < v.length; i++) {
			int A = (a[i] < 0 ? a[i] + 256 : a[i]);
			int B = (b[i] < 0 ? b[i] + 256 : b[i]);
			v[i] = (byte) (B & A);
		}

		return (v);
	}

	public static int toInt(byte b) {
		return (b < 0 ? b + 256 : b);
	}
	
	public static long toLong(byte[] address) {
		
		if (address.length == 0 || address.length > 8) {
			throw new IllegalArgumentException("Address length too long. Can not convert to a long");
		}
		
		int shift = (address.length - 1 ) * 8;
		
		long a = 0;
		int i = 0;
		for (; shift >= 0; shift -= 8, i++ ) {
			a |= ((address[i] < 0) ? (long) ((int) address[i] + 256) : address[i]) << shift;
		}
		return a;

	}

	/**
	 * Converts into a long for a IP V4 netmask. Throws an exception if bit
	 * count is greater than 32. This is only a V4 convention.
	 */
	public static byte[] parseBitCount(int bits) {

		if (bits > 32)
			throw new IllegalArgumentException(
					"Illegal number of bits for a IP v4 netmask. 32 bits MAX.");

		long mask = 0x0100000000L;
		mask >>= bits;
		mask -= 1;
		mask = ~mask & 0xFFFFFFFFL;

		return AddressUtils.toByteArray4Bytes(mask);
	}

	public static int countBits(byte[] byteArray) {

		int bitsInMask = 0;

		for (int j = 0; j < byteArray.length; j++) {
			int b = AddressUtils.toInt(byteArray[j]);
			for (int i = 0; i < 8; i++) {
				if (((0x80 >> i) & b) != 0)
					bitsInMask++;
			}
		}

		return (bitsInMask);
	}

	/**
	 * Compare our byte values for IP address to the object's. If length do not
	 * match then the one with smaller number of bytes is considered less then
	 * the one with more bytes (V6 is always > V4). If byte counts are the same
	 * then a byte for byte comparison is made and appropriate result return.
	 * 
	 * @param a
	 *            Address object to campare to.
	 * @param b
	 *            Address object to campare with.
	 * @return -1 means we are less then a, 0 means we are equal, 1 means we are
	 *         greater then a.
	 * @exception If
	 *                either object is not of IP v4 or V6. If their byte lengths
	 *                do not follow the standard.
	 */
	public static int compare(Address a, Address b)
			throws IllegalArgumentException {

		/**
		 * First we compare the length of the address. If we are comparing V6
		 * with V4 type addresses, always return as V6 is greater then V4, even
		 * if the actual data of the shorter V4 address would indicate
		 * otherwise. This will have the affect of sorters putting V4 address
		 * together while V6 separately.
		 * 
		 * If length of addresses are the same then do a byte for byte
		 * comparison.
		 */
		if (a.address.length < b.address.length)
			return (-1);

		if (a.address.length > b.address.length)
			return (1);

		/**
		 * Lengths are the same. If length of addresses are the same then do a
		 * byte for byte comparison. At any point if there is a difference in
		 * values immediately stop and return which one makes that bigger.
		 * 
		 * Otherwise if all bytes compared equal then return 0.
		 */
		for (int i = 0; i < a.address.length; i++) {
			if (a.byteAt(i) < b.byteAt(i))
				return -1;

			if (a.byteAt(i) > b.byteAt(i))
				return 1;
		}

		return 0;

	}

	public static int compare(Address a, byte[] b) {

		/**
		 * First we compare the length of the address. If we are comparing V6
		 * with V4 type addresses, always return as V6 is greater then V4, even
		 * if the actual data of the shorter V4 address would indicate
		 * otherwise. This will have the affect of sorters putting V4 address
		 * together while V6 separately.
		 * 
		 * If length of addresses are the same then do a byte for byte
		 * comparison.
		 */
		if (a.address.length < b.length)
			return -1;

		if (a.address.length > b.length)
			return (1);

		/**
		 * Lengths are the same. If length of addresses are the same then do a
		 * byte for byte comparison. At any point if there is a difference in
		 * values immediately stop and return which one makes that bigger.
		 * 
		 * Otherwise if all bytes compared equal then return 0.
		 */
		for (int i = 0; i < a.address.length; i++) {
			if (a.byteAt(i) < toInt(b[i]))
				return -1;

			if (a.byteAt(i) > toInt(b[i]))
				return 1;
		}

		return (0);

	}

	public static int compare(byte[] a, byte[] b) {

		/**
		 * First we compare the length of the address. If we are comparing V6
		 * with V4 type addresses, always return as V6 is greater then V4, even
		 * if the actual data of the shorter V4 address would indicate
		 * otherwise. This will have the affect of sorters putting V4 address
		 * together while V6 separately.
		 * 
		 * If length of addresses are the same then do a byte for byte
		 * comparison.
		 */
		if (a.length < b.length)
			return -1;

		if (a.length > b.length)
			return (1);

		/**
		 * Lengths are the same. If length of addresses are the same then do a
		 * byte for byte comparison. At any point if there is a difference in
		 * values immediately stop and return which one makes that bigger.
		 * 
		 * Otherwise if all bytes compared equal then return 0.
		 */
		for (int i = 0; i < a.length; i++) {
			if (toInt(a[i]) < toInt(b[i]))
				return -1;

			if (toInt(a[i]) > toInt(b[i]))
				return 1;
		}

		return (0);

	}

	/**
	 * Convert a unsigned int (32 bits) supplied as a long to byte array (4
	 * bytes).
	 * 
	 * @param address
	 *            IP address. Only first 32 bits are important
	 */
	public static byte[] toByteArray4Bytes(long longAddress) {

		byte[] ba = new byte[4];

		ba[0] = (byte) ((longAddress & 0xFF000000L) >> 24);
		ba[1] = (byte) ((longAddress & 0x00FF0000L) >> 16);
		ba[2] = (byte) ((longAddress & 0x0000FF00L) >> 8);
		ba[3] = (byte) ((longAddress & 0x000000FFL));

		return (ba);
	}

	/**
	 * Convert a unsigned int (48 bits) supplied as a long to byte array (6
	 * bytes).
	 * 
	 * @param address
	 *            MAC address. Only first 48 bits are important
	 */
	public static byte[] toByteArray6Bytes(long longAddress) {

		byte[] ba = new byte[6];

		ba[0] = (byte) ((longAddress & 0xFF0000000000L) >> 40);
		ba[1] = (byte) ((longAddress & 0x00FF00000000L) >> 32);
		ba[2] = (byte) ((longAddress & 0x0000FF000000L) >> 24);
		ba[3] = (byte) ((longAddress & 0x000000FF0000L) >> 16);
		ba[4] = (byte) ((longAddress & 0x00000000FF00L) >> 8);
		ba[5] = (byte) ((longAddress & 0x0000000000FFL));

		return (ba);
	}

	/**
	 * Return current IP address as byte array, for V4 that will be 4 bytes for
	 * V6 16.
	 * 
	 * @param address
	 *            String representation of IP address. Both V4 and V6 type
	 *            addresses are accepted.
	 * @return IP Byte array representation of the address. Check Array.length
	 *         to get the size of the array.
	 */
	public static byte[] toByteArray(String address, char separator) {
		return toByteArray(address, separator, 10);
	}

	/**
	 * Return current IP address as byte array, for V4 that will be 4 bytes for
	 * V6 16.
	 * 
	 * @param address
	 *            String representation of IP address. Both V4 and V6 type
	 *            addresses are accepted.
	 * @return IP Byte array representation of the address. Check Array.length
	 *         to get the size of the array.
	 */
	public static byte[] toByteArray(String address, char separator, int radix) {

		String[] components = address.split("\\" + separator);
		byte[] v = new byte[components.length];

		int i = 0;
		for (String t : components) {
			v[i++] = (byte) Integer.parseInt(t, radix);
		}

		return v;
	}

	
	public static byte[] toByteArray(String address) {
		
		String[] components = address.split("\\.");
		if (components.length == 0) {
			components = address.split(":");
		}
		
		byte[] v = new byte[components.length];

		int i = 0;
		for (String t : components) {
			v[i++] = (byte) Integer.parseInt(t);
		}

		return v;
	}

	/**
	 * Return current IP address as byte array, for V4 that will be 4 bytes for
	 * V6 16.
	 * 
	 * @param address
	 *            String representation of IP address. Both V4 and V6 type
	 *            addresses are accepted.
	 * @return IP Byte array representation of the address. Check Array.length
	 *         to get the size of the array.
	 */
	public static byte[] parseByteArray(String address)
			throws IllegalArgumentException {

		StringTokenizer st;
		byte[] v;

		if (address.indexOf('.') != -1) {
			st = new StringTokenizer(address, ".");
			v = new byte[4];
		} else if (address.indexOf(':') != -1) {
			st = new StringTokenizer(address, ":");
			v = new byte[16];
		} else {
			throw new IllegalArgumentException(
					"Illegal IP address format. Expected a string in either '.' or ':' notation");
		}

		for (int i = 0; i < v.length; i++) {
			String t = st.nextToken();

			if (t == null && i != v.length) {
				throw new IllegalArgumentException(
						"Illegal IP address format. String has too few byte elements.");
			}

			v[i] = (byte) Integer.parseInt(t);
		}

		return (v);
	}

	/**
	 * Parse the string representing a network entry. Network
	 * entries have a network number and a mask separator by '/' character.
	 * @param networkString String network in form 'n1.n2.n3.n4/m1.m2.m3.m4'
	 */
	public static IpNetwork parseIpNetwork(String networkString) {

		/**
		 * We need to separate the network from the netmask 
		 * n1.n2.n3.n4/m1.m2.m3.m4 where / is the separator.
		 */
		StringTokenizer st = new StringTokenizer(networkString, "/");

		String network = st.nextToken();
		String netmask = st.nextToken();

		// If netmask is made up of 3 charcters of less
		// assume its in the number of bits notation (ie. /24)
		if (netmask.length() < 4) {
			return (new IpNetwork(network, new IpNetmask(Integer
					.parseInt(netmask))));
		} else {
			return (new IpNetwork(network, new IpNetmask(netmask)));
		}
	}

	
	/**
	 * Convert internal address to a string. Use separator to convert to string.
	 */
	public static String toString(byte[] address, int radix, char separator) {

		StringBuilder buf = buffer.get();
		buf.setLength(0);

		char c = 0;
		for (byte b : address) {
			if (c != 0) {
				buf.append(c);
			}
			buf.append(Integer.toString((((int) b) & 0xFF), radix));

			c = separator;
		}

		return buf.toString();
	}

	/**
	 * Convert internal address to a string. Use format string to do the
	 * conversion. There needs to be equal number of X or x characters as there
	 * are bytes contained within the address paramter. The format string is
	 * copied character for character except in places where x or X is
	 * encountered. There the x is replaced with numerical string of the byte
	 * contained at the current offset into the address and X is repalaced with
	 * upper case version of the same number. The radix is used to convert byte
	 * values to string representation in the integer base.
	 */
	public static String toString(byte[] address, int radix, char[] format) {
		StringBuilder buf = buffer.get();
		buf.setLength(0);

		int a = 0;
		for (int i = 0; i < format.length; i++) {

			if (format[i] == 'x') {
				if (a == address.length)
					throw new IllegalArgumentException(
							"Format length does not match length of byte array.");

				if (radix == 16)
					buf.append(NumberUtils.toHexString(address[a++]));
				else
					buf.append(Integer.toString((((int) address[a++]) & 0xFF),
							radix));
			} else if (format[i] == 'X') {
				if ((a + 1) == address.length)
					throw new IllegalArgumentException(
							"Format length does not match length of byte array.");

				if (radix == 16)
					buf.append(NumberUtils.toHexString(address[a++])
							.toUpperCase());
				else
					buf.append(Integer.toString((((int) address[a++]) & 0xFF),
							radix).toUpperCase());
			} else {
				buf.append(format[i]);
			}
		}

		if (a != address.length)
			throw new IllegalArgumentException(
					"Format length does not match length of address .");

		return buf.toString();
	}

}
