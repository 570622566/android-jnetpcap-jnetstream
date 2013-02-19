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

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.slytechs.utils.io.DataBuffer;
import com.slytechs.utils.namespace.NameResolutionException;
import com.slytechs.utils.string.StringUtils;

/**
 * A Class for storing IP Addresses Currently is only designed to utilize IPv4
 * (32bit) addresses. Class also contains utility methods for DNS lookups. There
 * are various functions for converting the IP address to a LONG. Why would you
 * want to use a long for storage of an IP address, in my case I store IP
 * address in a database as an UNSIGNED INT, java does not have unsigned numbers
 * so you have to go to next bigger primitive type to store it or do like other
 * implementations do store the address in a byte array.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class IpAddress extends Address {

	
	private static final long serialVersionUID = -7420353055502485516L;
	
	/**
	 * <P>The version of IP Address. Only two versions exist 4 and 6.</P>
	 * 
	 * <P>Version 4 addresses are made up of 4 bytes or 32 bits while version 6 addresses are
	 * made up 16 bytes or 128 bits.</P>
	 * 
	 * @author Mark Bednarczyk
	 *
	 */
	public enum IpAddressVersion {
		
		/**
		 * IP version 4 address.
		 */
		IpVersion4,
		
		/**
		 * IP version 6 address.
		 */
		IpVersion6,
		;
		
		/**
		 * Given an IP address lookup which version it is.
		 * 
		 * @param address address, to lookup version for.
		 * @return the version number for this IP address.
		 */
		public static IpAddressVersion getIpAddressVersion(IpAddress address) {
			return getIpAddressVersion(address.toByteArray());
		}
		
		/**
		 * Given an IP address lookup which version it is.
		 * 
		 * @param address address, in big endian byte encoding, to lookup version for.
		 * @return the version number for this IP address.
		 */
		public static IpAddressVersion getIpAddressVersion(byte[] address) {
			if (address.length == 4) {
				return IpVersion4;
			} else {
				return IpVersion6;
			}
		}
	}
	
	/**
	 * <P>Defines constants to describe the IP address routability through the internet as defined
	 * by the internet com.slytechs.utils.info.standards using RFCs.</P>
	 * 
	 * <P> There can be 3 types of addresses that are treated by routers differently. 
	 * <UL>
	 *  <LI> Public addresses - normal IP Address that are fully routable
	 *  <LI> Private addresses - addresses that can be used within a private network but will
	 *  not be routed by Internet Service Provider or any of the internet routers.
	 *  <LI> Automatic addresses - addresses which are used in place of DHCP servers for automatic
	 *  configuration or network devices. 
	 * </UL>
	 * </p>
	 * @author Mark Bednarczyk
	 *
	 */
	public enum IpAddressRoutability {
		
		/**
		 * True publically routable IP Address.
		 */
		PublicAddress,
		
		/**
		 * <P>(RFC1918) The following three IP ranges are reserved by 
		 * Internet Assigned Numbers Authority (IANA) according to RFC 1918.  
		 * These IP addresses are not routable over the Internet, so they will 
		 * not conflict with other sites using these ranges.  To access the Internet 
		 * with these ranges, you need a Network Address Translation (NAT) device or 
		 * proxy server, etc.</P>
		 * 
		 * <P> Private IP Addresses fall in the following ranges:
		 * <UL>
		 *  <LI> 10.0.0.0/8 - 10.255.255.255/12
		 *  <LI> 172.16.0.0/12 - 172.31.255.255/12
		 *  <LI> 192.168.0.0/16 - 192.168.255.255/16 
		 * </UL>
		 * </P>
		 */
		PrivateAddress      (new byte[][] {
				{10},
				{(byte)172, 16},
				{(byte)192, (byte)168}
		}),
		
		/**
		 * <P>(RFC3330) This range is reserved as a Link Local Block for auto-configuration or 
		 * when a DHCP server can't be found. It is not recommended for normal 
		 * configuration but is useful in determining if a connection to a DHCP 
		 * server is not working.</P>
		 */
		AutomaticAddress	(new byte[][] {
				{(byte)169, (byte)254}	
		})
		;
		
		private final byte[][] addressList;

		private IpAddressRoutability() {
			this.addressList = null;
		}
		
		private IpAddressRoutability(byte[][] addressList) {
			this.addressList = addressList;
		}
		
		public boolean equals(byte[] address) {
			
			boolean res = true;
			
			if (addressList != null) {
				
				for(byte[] ba: this.addressList) {
					
					int i = 0;
					for(byte b: ba) {
						if (address[i++] != b) {
							res = false;
							break;
						}
						
						res = true;
					}
					
					if (res == true) {
						break;
					}
				}	
			}
			
			return res;
		}

	}
		
	/**
	 * <P>Defines constants for various types of IP Addresses. IP Address can be looked up
	 * for its type.<P>
	 * 
	 * <P>For example, 0.0.0.0 is a BROADCAST address type, more specifically OldBroadcast type
	 * since all 0 broadcasts are discouraged but still exist with existing legacy networks. Another
	 * type of Address type is Unicast, meaning that an address is has an address that maps to 
	 * a single host.</P>
	 * 
	 * <P>Other type of 
	 * @author Mark Bednarczyk
	 *
	 */
	public enum IpAddressType {
		
		/**
		 * <P>IP Address consisting of all 0s. This is old style of broadcast addressing.</P>
		 * 
		 * <P>Broadcast means that destination is all hosts reachable via the destination interface
		 * of router, system, host or simply device.
		 * 
		 */
		OldBroadcast (new byte[] {0, 0, 0, 0} ),
		
		/**
		 * <P> IP Address consisting of all 1s or 255.255.255.255.</P>
		 * 
		 * <P>Broadcast means that destination is all hosts reachable via the destination interface
		 * of router, system, host or simply device.
		 */
		Broadcast    (new byte[] {(byte)255, (byte)255, (byte)255, (byte)255} ),
		
		/**
		 * <P>Device internal loopback interface for communication within its self. This is typeically
		 * completely virtual interface created by routers, switches, systems for communication
		 * with its internal sub-systems. Any device can create as many loopback interfaces as
		 * they wish within the allocated address space.</P>
		 * 
		 * <P>Address range for loopback addresses is:
		 * <UL>
		 *  <LI> 127.0.0.0/8 - 127.255.255.255/8
		 * </UL>
		 * </P>
		 */
		Loopback     (new byte[][] {
				{127}
		}),
		
		/**
		 * Multicast address type is destined to a group of devices.
		 */
		Multicast(IpAddressClass.Multicast),
		
		/**
		 * Unicast address type is bound signifies that there is a single device. Although
		 * There are some tricky implementations of certain devices that make it look like a 
		 * single device when in reality multable devices communicate using the same Unicast address.
		 */
		Unicast,
		;
		
		private final byte[] address;
		private byte[][] addressList;
		private final IpAddressClass addressClass;
		
		private IpAddressType(IpAddressClass addressClass) {
			this.addressClass = addressClass;
			this.address = null;
			this.addressList = null;
		}

		private IpAddressType() {
			address = null;
			addressList = null;
			addressClass = null;
		}
		
		private IpAddressType(byte[] address) {
			this.address = address;
			this.addressClass = null;
		}
		
		private IpAddressType(byte[][] addressList) {
			this.address = null;
			this.addressList = addressList;
			this.addressClass = null;
		}
		
		/**
		 * Tests the type of this address against this constant.
		 * 
		 * @param address address to do a test against.
		 * @return true if the supplied address is of this type, matching this constant
		 * criteria, otherwise false.
		 */
		public boolean equals(IpAddress address) {
			return equals(address.toByteArray());
		}
		
		/**
		 * Tests the type of this address against this constant.
		 * 
		 * @param address address, in big endian byte order encoding, to do a test against.
		 * @return true if the supplied address is of this type, matching this constant
		 * criteria, otherwise false.
		 */
		public boolean equals(byte[] address) {
			
			if (address.length != 4) {
				return false;
			}
			
			if (this.address == null && this.addressList == null && this.addressClass == null) {
				return true;
			}
			
			if (this.addressClass != null) {
				return addressClass.equals(address);
			}
			
			if (this.address != null) {
				return AddressUtils.compare(this.address, address) == 0;
			} else {
				
				boolean res = true;
				for(byte[] ba: this.addressList) {
					
					int i = 0;
					for(byte b: ba) {
						if (address[i++] != b) {
							res = false;
							break;
						}
						
						res = true;
					}
					
					if (res == true) {
						break;
					}
				}
				
				return res;
			}
		}
		
		/**
		 * Looks up the address against all defined types to which one the supplied
		 * address belongs to.
		 * 
		 * @param address address to lookup against all defined types.
		 * @return type that best matches this address.
		 */
		public static IpAddressType getAddressType(IpAddress address) {
			return getAddressType(address.toByteArray());
		}
		
		/**
		 * Looks up the address against all defined types to which one the supplied
		 * address belongs to.
		 * 
		 * @param address address, in big endian byte encoding, to lookup against all defined types.
		 * @return type that best matches this address.
		 */
		public static IpAddressType getAddressType(byte[] address) {
			for(IpAddressType at: values()) {
				if (at.equals(address)) {
					return at;
				}
			}
			
			return Unicast;

		}
	}
		
	/**
	 * <P>Defines Internet Protocol Address classes. Constants for prefix bits
	 * and network mask for each class of IP address is provided via getter method
	 * getPrefix() and getMask(). Also equals(IpAddress) method is provided to test if 
	 * supplied Ip address is part of this class.</P>
	 * 
	 * @author Mark Bednarczyk
	 *
	 */
	public enum IpAddressClass {
		
		/**
		 * <P>Address of this class is Class A address. All Ip v4 addresses
		 * that begin with 00XX prefix are Class A.</P>
		 * 
		 * <P>Range is from 0-127.H.H.H</P>
		 * 
		 */
		ClassA    (0x00, 0x80),
		
		/**
		 * <P>Address of this class is Class B address. All Ip v4 addresss
		 * that begin with 10XX prefix are Class B.</P>
		 * 
		 * <P>Range is from 128-191.N.H.H</P>
		 * 
		 */
		ClassB    (0x80, 0xC0),
		
		
		/**
		 * <P>Address of this class is Class C address. All Ip v4 addresss
		 * that begin with 110X prefix are ClassCB.</P>
		 * 
		 * <P>Range is from 192-223.N.N.H</P>
		 * 
		 */
		ClassC    (0xC0, 0xE0),
		
		/**
		 * <P>Address of this class is Class D (Multicast) address. All Ip v4 addresss
		 * that begin with 10XX prefix are Class B.</P>
		 * 
		 * <P>Range is from 224-247.H.H.H</P>
		 * 
		 */
		Multicast (0xE0, 0xF0),
		
		/**
		 * <P>Address of this class is Class D (Multicast) address. All Ip v4 addresss
		 * that begin with 1110 prefix are Class B.</P>
		 * 
		 * <P>Range is from 224-247.H.H.H</P>
		 * 
		 */
		ClassD    (0xE0, 0xF0),
		
		/**
		 * <P>Address of this class is Class E address. All Ip v4 addresss
		 * that begin with 1111 prefix are Class E. Currently
		 * Class E addresses are reserved and not in use.</P>
		 * 
		 * <P>Range is from 248-255.H.H.H</P>
		 * 
		 */
		ClassE    (0xF0, 0xF0),
		;
		
		private final int prefix;
		private final int mask;

		/**
		 * Construct our class enum constant using a prefix and mask.
		 * 
		 * @param prefix prefix bits within the address that need to match.
		 * @param mask mask which specifies which bits within the prefix are significant.
		 */
		private IpAddressClass(int prefix, int mask) {
			this.prefix = prefix;
			this.mask = mask;	
		}
		
		/**
		 * Getter to access the prefix defined for this address class. The prefix is that
		 * first bits within the first byte of an IP Address that must match in order to fulfill
		 * the Address class requirement.
		 * 
		 * @return integer representing the prefix bits within the first byte (most significant) of IP address.
		 */
		public int getPrefix() {
			return prefix;
		}
		
		/**
		 * Getter to access the mask defined for this address class. The mask specifies which
		 * bits within the prefix are significant and which are not (wild cards). When prefix is
		 * applied to an address only the significant bits within this mask are used for comparison.
		 * 
		 * @return integer representing the mask bits within the prefix.
		 */
		public int getMask() {
			return mask;
		}
		/**
		 * Tests against the supplied IP address for class equality.
		 * 
		 * @param address address to test against this address class.
		 * @return true if address is part of this class, otherwise false.
		 */
		public boolean equals(IpAddress address) {
			return equals(address.toByteArray());
		}
		
		/**
		 * Tests against the supplied IP address for class equality.
		 * 
		 * @param address Ip address, in big endian byte order, to test against this address class.
		 * @return true if address is part of this class, otherwise false.
		 */
		
		public boolean equals(byte[] address) {
			
			if (address.length == 4) {
				return (AddressUtils.toInt(address[0]) & mask) == prefix;
			} else if (address.length == 16 && this == Multicast) {
				return (AddressUtils.toInt(address[0]) & mask) == prefix;
			} else {
				return false;
			}
		}
		
		/**
		 * Given an IP address, returns the
		 * specific IP Address class this address is part of.
		 * 
		 * @param address Ip address, to test against 
		 * @return Address class for this address.
		 */
		public static IpAddressClass getAddressClass(IpAddress address) {
			return getAddressClass(address.toByteArray());
		}

		/**
		 * Given an IP address, returns the
		 * specific IP Address class this address is part of.
		 * 
		 * @param address Ip address, in big endian byte order, to test against 
		 * @return Address class for this address.
		 */
		public static IpAddressClass getAddressClass(byte[] address) {
			for(IpAddressClass ac: values()) {
				if (ac.equals(address)) {
					return ac;
				}
			}
			
			return null;
		}
	}

	private InetAddress inetAddress = null;

	private String canonicalName = null;

	private String domainName = null;
	
	private String hostName = null;
	
	private IpAddressType addressType = null;
	
	private IpAddressClass addressClass = null;

	/**
	 * Main constructor taking the array of bytes as the address. Automatically
	 * sets the separator token between '.' and ':' depending on number of bytes
	 * in the address. 4 bytes IPv4 address with '.' separator and 32 bytes for
	 * IPv6 address with ':' as separator.
	 * 
	 * @param address
	 *            array of bytes making up the address.
	 * @exception IllegalArgumentException
	 *                if number of bytes is not 4 or 32.
	 */
	public IpAddress(byte[] address) {
		super(address);

		if (address.length == 4)
			setSeparator('.');

		else if (address.length == 16) {
			setFormat("xx:xx:xx:xx:xx:xx:xx:xx");
			setRadix(16);
		}

		else {
			throw new IllegalArgumentException(
					"IP Address can only be either 32 or 128 bits long");
		}
	}

	/**
	 * Constructor taking a canonicalName. This is a conveniece constructor which
	 * converts the canonicalName to numerical format internally. Currently only IPv4
	 * addresses can be initialized this way due to java.net library
	 * limitations.
	 * 
	 * @param canonicalName
	 *            canonicalName or IP address in '.' notation initialize this address
	 *            with.
	 * @exception UnknownHostException
	 *                if canonicalName can not be converted to an IP address.
	 *                Addresses in IPv4 '.' notation format always convert and
	 *                this exception is never thrown.
	 */
	public IpAddress(Hostname hostname) throws NameResolutionException {

		try {
			inetAddress = InetAddress.getByName(hostname.getHostname());
		} catch (UnknownHostException e) {
			throw new NameResolutionException(e);
		}

		setAddress(inetAddress.getAddress());

		if (address.length == 4) {
			setSeparator('.');
		} else if (address.length == 16) {
			setFormat("xx:xx:xx:xx:xx:xx:xx:xx");
			setRadix(16);
		}
	}

	public IpAddress(String address) {
		
		setAddress(AddressUtils.toByteArray(address));
		
		if (address == null) {
			throw new IllegalArgumentException("Unrecognized IpAddress string [" + address + "]");
		}
		
		if (super.address.length == 4) {
			setSeparator('.');
		} else if (super.address.length == 16) {
			setFormat("xx:xx:xx:xx:xx:xx:xx:xx");
			setRadix(16);
		} else {
			throw new IllegalArgumentException("Unrecognized IpAddress string [" + address + "]");		
		}
	}

	public IpAddress(DataBuffer buffer) {
		super(buffer.getByte(4));
	}

	public IpAddress(DataBuffer buffer, int length) {
		super(buffer.getByte(length));
	}

	/**
	 * Returns the java.net.InetAddress object associated with this IpAddress.
	 * 
	 * @return InetAddress object associated with this IpAddress. If address
	 *         does not exist and can not be resolved InetAddress object will be
	 *         null and null will be returned as well.
	 * 
	 * @since JNetStream 0.2.2
	 */
	public InetAddress getInetObject() throws NameResolutionException {

		if (inetAddress == null)  {
			try {
				inetAddress = InetAddress.getByName(super.toString(false));

			} catch (UnknownHostException e) {
				throw new NameResolutionException(e);
			}
		}

		return inetAddress;
	}

	/**
	 * Convert to dot notation string representation of the address. If canonicalName
	 * is known then canonicalName.domain is returned other wise the appropriate
	 * numerical representation of the numerical address is returned as a
	 * String.
	 * 
	 * @return IP address in the dot notation.
	 */
	public String toString() {
		if (canonicalName != null) {
			return (canonicalName + "." + domainName);
		} else {
			return (super.toString());
		}
	}

	public IpAddressClass getAddressClass() {
		
		if (addressClass == null) {
			addressClass = IpAddressClass.getAddressClass(address);
		}
		return addressClass;
	}

	public IpAddressType getAddressType() {
		if (addressType == null) {
			addressType = IpAddressType.getAddressType(address);
		}
		return addressType;
	}

	public IpAddressVersion getAddressVersion() {
		return IpAddressVersion.getIpAddressVersion(this.address);
	}
	@Override
	public String getName() {
		return canonicalName;
	}

	@Override
	public boolean hasNameResolvingService() {
		return true;
	}

	@Override
	public boolean isNameResolved() {		
		return canonicalName != null;
	}

	public String getCanonicalHostname() {
		return canonicalName;
	}
	
	public String getHostname() {
		return hostName;
	}
	
	public String getDomainname() {
		return domainName;
	}
	
	@Override
	public boolean resolveName() throws NameResolutionException {
		
		if (canonicalName != null)
			return true;

		if (inetAddress == null) {
			try {
				inetAddress = InetAddress.getByName(super.toString(false));
			} catch (UnknownHostException e) {
				throw new NameResolutionException(e);
			}
		}

		canonicalName = inetAddress.getCanonicalHostName();
		hostName = inetAddress.getHostName();
		
		String[] path = canonicalName.split("\\.");
		
		if (path.length == 1) {
			domainName = "";
		} else {
			String s = "";
			for (int i = 0; i < path.length -1; i ++) {
				domainName += s + path[i];
				s = ".";
			}
		}
		

		if (canonicalName == null) {
			canonicalName = toString(false);
			hostName = canonicalName;
		}

		return true;
	}

	public static IpAddress valueOf(String address) {
		return new IpAddress(address);
	}

	public static IpAddress valueOf(Long address) {
		return new IpAddress(AddressUtils.toByteArray4Bytes(address));
	}

	public static boolean validateV4(String adr) {
		return adr.matches("[0..9]+\\.[0..9]+\\.[0..9]+\\.[0..9]+");
	}
	
	public static boolean validateV6(String adr) {
		
		int count = StringUtils.countChars(adr, ':');
		return count > 1;
	}
	
	
} /* END OF: IpAddress */
