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

import java.util.List;

import org.jnetstream.protocol.Protocol;
import org.jnetstream.protocol.ProtocolEntry;

import com.slytechs.utils.namespace.Named;
import com.slytechs.utils.net.Address;

/**
 * <P>
 * Describes the capture device that captured the packet. Individual captured
 * packets maintain a reference to a capture device that captured them. The
 * CaptureDevice differes from java.net.NetworkInterface as it is essentially a
 * snapshot of the current or past state of the interface. The CaptureDevices
 * created are usually not cached by are new instances that contain certain
 * state at the time they were initialized.
 * </P>
 * <P>
 * For example a capture device was created and initialized at 8:00am. 5 minutes
 * later the network interface configuration was altered and another IP
 * addresses alias added to the interface. When the device was opened at 8:10am
 * a new CaptureDevice created and initialized and now it there are two
 * instances of CaptureDevice associated with that same interface both
 * containing slightly different IP addresses for that physical network
 * interface. Another common way for a new alternate CaptureDevice to be created
 * when a new filter is applied in a middle of a already open capture. At the
 * time the filter is applied a new CaptureDevice is created with new filter
 * state and from then on any packets captured and returned from LiveCapture
 * sessions will reference the new CaptureDevice with the new filter state,
 * while the old packets (packets already returned prior to the filter being
 * applied) will maintain a reference to the original CaptureDevice object.
 * "Nap" (see {@link http://jnetpcap.sf.net?q=napspec}) file format stores all
 * of this information in the capture file. Other formats such as PCAP and SNOOP
 * can not.
 * </P>
 * <P>
 * It is important to first check the capabilities of the capture device using
 * the {@link #getCapabilities} method before accessing or setting new values.
 * The capabilities are of the source of the capture, that is if the packet is
 * comming from a live network interface, the capabilities are of the network
 * interface that did the capturing and the operating system on which the
 * capture took place. If the source of the captured packet is a file, then the
 * information supplied by this interface is file type specific. File formats
 * such as Snoop and Pcap have limited capabilities to store information about
 * capturing devices, while Nap file format can store all of this information.
 * </P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface CaptureDevice extends Named {

	/**
	 * Specified the maximum supported timestamp resolution of the capture device.
	 * Since all timestamps are always recorded with NanoSecond precision even
	 * when the capturing device is only capable of MicroSecond precision, this
	 * constant provides the actual capability of the device, regardless of the
	 * precision recorded.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public enum TimestampResolution {
		/**
		 * Timestamp has microsecond resolution
		 */
		MicroSecond,

		/**
		 * Timestamp has milli second resolution
		 */
		MilliSecond,

		/**
		 * Timestamp has nanosecond resolution
		 */
		NanoSecond
	}

	/**
	 * Gets a more human readable name for the interface if one is available. If
	 * no display name is set, this method returns null.
	 * 
	 * @return human readable display name for the interface or null if one is not
	 *         available
	 */
	public String getDisplayName();

	/**
	 * Retruns device description if one is available. If no description is
	 * available, it returns a formatted string based on the display name or the
	 * name.
	 * 
	 * @return a detailed description of the device or null if description is not
	 *         available
	 */
	public String getDescription();

	/**
	 * Gets a list of addresses that are/were set on the interface.
	 * 
	 * @return list of addresses set on the interface
	 */
	public List<Address> getAddresses();

	/**
	 * Gets the name as returned by the operating system of the network interface
	 * this CaptureDevice describes. If the capture device was created based on
	 * information found in capture files, the name is typically not available and
	 * will be returned as null.
	 * 
	 * @return OS assigned name of the network interface or null if name was not
	 *         available
	 */
	public String getName();

	/**
	 * Data encapsulation used by this capture device
	 * 
	 * @return data encapsulation enum constant
	 */
	public Protocol getLinkType();

	/**
	 * Returns a refrence to a specific protocol of the DLT for this capture
	 * device.
	 * 
	 * @return
	 */
	public ProtocolEntry getProtocol();

	/**
	 * Returns the raw link type as an 32-bit signed integer as returned by the
	 * underlying implemenation of the interface or library interfacing with the
	 * device.
	 * 
	 * @return 32-bit signed integer containing the raw value
	 */
	public long getRawLinkType();

	/**
	 * Returns the resolution that the capturing device is capable of. This is
	 * usually hardware dependent.
	 * 
	 * @return an enum constant describing the clock resolution of the capturing
	 *         device
	 */
	public TimestampResolution getTimestampResolution();

	/**
	 * Gets the timezone as retrieved from the operating system at the time of the
	 * packet capture. This allows the capture timestamp as reported by the
	 * CapturePacket to be put in the correct timezone perspective.
	 * 
	 * @return timezone of the device where the packet was captured on
	 */
	public long getTimezone();

}
