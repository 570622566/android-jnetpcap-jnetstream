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
package com.slytechs.jnetstream.livecapture;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapAddr;
import org.jnetpcap.PcapIf;
import org.jnetstream.capture.LiveCaptureDevice;
import org.jnetstream.capture.file.pcap.PcapFile;
import org.jnetstream.filter.Filter;
import org.jnetstream.protocol.Protocol;
import org.jnetstream.protocol.ProtocolEntry;
import org.jnetstream.protocol.ProtocolRegistry;

import com.slytechs.utils.net.Address;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class jNetPcapDevice implements LiveCaptureDevice, Serializable {

	private static final long serialVersionUID = 1574563234L;

	/**
	 * Lists all the CaptureDevices (network interfaces) available on the local
	 * system. Uses SfjPcap to lookup device names as it has better support for
	 * that
	 */
	public static LiveCaptureDevice[] listCaptureDevices() throws IOException {

		List<PcapIf> devices = new ArrayList<PcapIf>();
		StringBuilder errbuf = new StringBuilder();

		if (Pcap.findAllDevs(devices, errbuf) != Pcap.OK) {
			throw new IOException(errbuf.toString());
		}

		final LiveCaptureDevice[] live = new jNetPcapDevice[devices.size()];
		int i = 0;
		for (PcapIf d : devices) {
			live[i++] = new jNetPcapDevice(d);
		}

		return live;
	}

	/**
	 * Looks up the names and descriptions of all the network interfaces on this
	 * local system.
	 * 
	 * @return array of device names
	 * @throws IOException
	 */
	public static String[] lookupDeviceNames() throws IOException {

		List<PcapIf> devices = new ArrayList<PcapIf>();
		StringBuilder errbuf = new StringBuilder();

		if (Pcap.findAllDevs(devices, errbuf) != Pcap.OK) {
			throw new IOException(errbuf.toString());
		}

		String[] names = new String[devices.size()];
		int i = 0;
		for (PcapIf d : devices) {
			names[i++] = d.getName();
		}

		return names;
	}

	private final PcapIf device;

	private int dlt;

	private ProtocolEntry id;

	private jNetPcapDevice(PcapIf device) throws IOException {
		this.device = device;
		StringBuilder errbuf = new StringBuilder();

		Pcap pcap = Pcap.openLive(device.getName(), 1024, 0, 0, errbuf);
		if (pcap == null) {
			throw new IOException(errbuf.toString());
		}

		this.dlt = pcap.datalink();

		pcap.close();

		id =
		    ProtocolRegistry.getProtocolEntry(ProtocolRegistry.translate(
		        PcapFile.class, dlt));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CaptureDevice#getAddresses()
	 */
	public List<Address> getAddresses() {
		final List<Address> addresses = new ArrayList<Address>();

		for (final PcapAddr a : device.getAddresses()) {
			addresses.add(new Address(a.getAddr().getData()));
		}

		return addresses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CaptureDevice#getDescription()
	 */
	public String getDescription() {
		return device.getDescription();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CaptureDevice#getDisplayName()
	 */
	public String getDisplayName() {
		return device.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.LiveCaptureDevice#getFilter()
	 */
	public Filter<?> getFilter() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CaptureDevice#getLinkType()
	 */
	public Protocol getLinkType() {
		return id.getProtocol();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CaptureDevice#getName()
	 */
	public String getName() {
		return device.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CaptureDevice#getRawLinkType()
	 */
	public long getRawLinkType() {
		return dlt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CaptureDevice#getTimestampResolution()
	 */
	public TimestampResolution getTimestampResolution() {
		return TimestampResolution.MicroSecond;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CaptureDevice#getTimezone()
	 */
	public long getTimezone() {
		return TimeZone.getDefault().getRawOffset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CaptureDevice#getProtocol()
	 */
	public ProtocolEntry getProtocol() {
		return id;
	}

}
