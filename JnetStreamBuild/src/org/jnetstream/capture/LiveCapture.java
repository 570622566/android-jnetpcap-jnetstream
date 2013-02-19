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

import java.io.Closeable;
import java.io.IOException;

import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;


/**
 * <P>
 * A network packet cature on a live network interface. To obtain an instance of
 * a class implementing this interface, use one of the {@link Captures#openLive}
 * methods which open up a network interface live capture.
 * 
 * <PRE>
 * 
 * LiveCapture capture = Captures.openLive(); while (capture.hasNext()) {
 * CapturePacket packet = capture.next(); // Do something with the packet now }
 * 
 * </PRE>
 * 
 * </P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface LiveCapture extends Capture<LivePacket>, Closeable {

	/**
	 * Options which can be set on open captures. The options are only hints and
	 * may be ignored due to other reasons, such as some other capture has already
	 * put the interface into Promiscous mode, a condition which results in all
	 * packets being captured for all sessions even this one.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public enum Option {

		/**
		 * Forces all the packets to be captured on an interface, even if the
		 * packets are not destined for the network interface or the system.
		 */
		Promiscous,

		/**
		 * Captures only packets that are destined for the network interface.
		 */
		NonPropmiscous
	}

	/**
	 * The default option when none are specified by the user.
	 */
	public static final Option DEFAULT_CAPTURE_OPTION = Option.Promiscous;

	/**
	 * <p>
	 * An iterator that iterates over captured LivePackets. The iterator provides
	 * 2 additional methods, <code>skip</code> and <code>close</code>. Skip
	 * efficiently skips over the current packet discarding any data that was
	 * captured and close closes the capture session associated with this
	 * iterator.
	 * </p>
	 * <p>
	 * Each iterator maintains its own queue of packets from which it draws
	 * packets to be returned via its <code>next</code> method. If the user
	 * instantiates more than one LiveIterator, each iterator will return packets
	 * irrispective of any other iterator. If the iterator's packet queue becomes
	 * full and no more packets can be received, the packets will not be added to
	 * the iterator's queue, but may be added to other iterators' queues it there
	 * are others and the iterator's queue drop counter will be incremented.
	 * </p>
	 */
	public LiveIterator getPacketIterator() throws IOException;

	/**
	 * Applies a filter to a network interface globally. The filter will be
	 * applied to the open network interfaces by this live capture session. The
	 * underlying operating system applies the filter to each interface globally.
	 * Meaning that if other live capture sessions or even other programs applied
	 * a filter to an interface that filter is active for all other live capture
	 * sessions on the same interface.
	 * 
	 * @param filter
	 *          packet filter to apply all open network interfaces
	 * @throws IOException
	 *           any IO errors while applying the filter.
	 */
	public void setFilter(Filter<ProtocolFilterTarget> filter) throws IOException;

	/**
	 * <p>
	 * Returns the state of the currently open network interface. Determines if
	 * the network interface is currently set in promiscuous mode. Normally
	 * network interfaces reject packets that are not broadcast or have their
	 * destination MAC address set to the address of the network's interface. In
	 * promiscuous mode, all packets are captured by the network interface,
	 * allowing greater inspection of network traffic.
	 * </p>
	 * <p>
	 * Note: This version of implementation uses libraries which can no check the
	 * actual status of the interface, but will return the status of the flag as
	 * it was originally invoked by the user. Future versions will have capability
	 * to check promiscuous mode status directly with the network interface. This
	 * will allow detection of promiscuous mode, even when this particular
	 * LiveCapture did not enable it. The promiscuous flag is a global on a
	 * network interface, meaning that it can be enabled by any process and will
	 * affect all other processes capturing that interface. A promisuous flag can
	 * change state from false to true in unexpected ways and unpredictable times.
	 * </p>
	 * 
	 * @return
	 */
	public boolean isPromisuous();

	/**
	 * Gets the current snap length that this capture session is using. Snap len
	 * truncates the length of the packets from their original length as they were
	 * captured on live network. Since its not always neccessary to have entire
	 * packet, including its data portions, for analysis, it is common practice to
	 * trucate the length of the actual data retained, to some length that
	 * captures enough data to decode the packet headers only.
	 * 
	 * @return the maximum number of bytes that will be retained by this captures
	 *         session on a per packet basis
	 */
	public int getSnaplen();

	/**
	 * Gets the timeout value that before this capture session stop capturing
	 * packets. The capture session is not closed upon timeout, it simply stop
	 * capturing packets at which time it can be instructed to capture more
	 * packets or possibly close the capture session.
	 * 
	 * @return maximum number of milli seconds to wait for a packet to arrive
	 *         before stopping packet capture and unblocking any waiting
	 *         operations for new packets to arrive
	 */
	public int getTimeout();

	/**
	 * Gets the list of currently open capture devices by this capture session.
	 * LiveCapture object allow more than one capture device to be open for
	 * capture at the same time.
	 * 
	 * @return an array of currently open capture devices
	 */
	public LiveCaptureDevice[] getOpenCaptureDevices();

	/**
	 * Gets the currently active filter that has been applied to all the currently
	 * open capture devices.
	 * 
	 * @return currently active filter or null if none was set
	 */
	public Filter<ProtocolFilterTarget> getFilter();
}
