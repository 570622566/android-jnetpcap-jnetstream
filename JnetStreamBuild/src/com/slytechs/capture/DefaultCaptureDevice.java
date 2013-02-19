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
package com.slytechs.capture;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;

import org.jnetstream.capture.CaptureDevice;
import org.jnetstream.capture.file.pcap.PcapDLT;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;
import org.jnetstream.protocol.Protocol;
import org.jnetstream.protocol.ProtocolEntry;

import com.slytechs.utils.net.Address;
import com.slytechs.utils.net.IpAddress;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class DefaultCaptureDevice implements CaptureDevice {

	public static String[] lookupDevicesJavaNet() throws SocketException {
  	Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
  
    
    List<String> list = new ArrayList<String>();
    
    while (e.hasMoreElements()) {
    	NetworkInterface n = e.nextElement();
    	
    	list.add(n.getDisplayName());
    }
    
    System.out.println(list);
    
    return list.toArray(new String[list.size()]);
  }

	private Protocol protocolLinkType;

	protected String displayName;

	protected List<Address> addresses = new ArrayList<Address>();

	protected String name;

	protected TimestampResolution timestampResolution = TimestampResolution.MicroSecond;

	protected int timezome;

	protected Filter<ProtocolFilterTarget> filter;

	protected String description;

	protected long rawLinkType;
	
	protected DefaultCaptureDevice() {
		// Empty
	}

	/**
   * @param protocolLinkType
   */
  public DefaultCaptureDevice(Protocol protocolLinkType) {
  	if (protocolLinkType == null) {
  		throw new NullPointerException("Null protocol constant");
  	}
	  this.protocolLinkType = protocolLinkType;
  }

	public String getDisplayName() {
		return displayName;
	}

	public Filter<ProtocolFilterTarget> getFilter() {
		return filter;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public String getName() {
		return name;
	}

	public Protocol getLinkType() {
		return protocolLinkType;
	}

	public TimestampResolution getTimestampResolution() {
		return timestampResolution;
	}

	public long getTimezone() {
		return timezome;
	}

	public String toString() {
		String s = "";

		s += "[" + getName() + ", " + getDisplayName() + ", link="
		    + protocolLinkType + "]";

		return s;
	}

	public String getDescription() {
		return description;
	}

	public long getRawLinkType() {
		return rawLinkType;
	}

	/**
	 * @param linkLayerType
	 */
	protected void setRawLinkType(int linkLayerType) {
		this.rawLinkType = linkLayerType;

		protocolLinkType = PcapDLT.asConst(rawLinkType);
	}

	public void setLinkType(Protocol dlt) {
		this.rawLinkType = PcapDLT.asPcap(dlt).intValue();
	}

	protected void initFromJavaNetNetworkInterface(String device)
	    throws SocketException {
		NetworkInterface netint = NetworkInterface.getByName(device);

		name = netint.getName();
		displayName = netint.getDisplayName();
		timezome = TimeZone.getDefault().getRawOffset();

		Enumeration<InetAddress> e = netint.getInetAddresses();
		while (e.hasMoreElements()) {
			addresses.add(new IpAddress(e.nextElement().getAddress()));
		}

	}

	/* (non-Javadoc)
   * @see org.jnetstream.capture.CaptureDevice#getProtocol()
   */
  public ProtocolEntry getProtocol() {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

}
