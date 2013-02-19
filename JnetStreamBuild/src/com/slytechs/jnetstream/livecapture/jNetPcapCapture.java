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
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapHandler;
import org.jnetstream.capture.LiveCaptureDevice;
import org.jnetstream.capture.LivePacket;
import org.jnetstream.capture.LivePacketFactory;
import org.jnetstream.capture.file.pcap.PcapDLT;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;
import org.jnetstream.protocol.ProtocolEntry;
import org.jnetstream.protocol.ProtocolRegistry;

import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class jNetPcapCapture
    extends AbstractLiveCapture {

	/**
	 * Number of milliseconds to timeout of distpatch loop to allow a check for
	 * breakloop.
	 */
	public final static int DEFAULT_BREAK_LOOP_CHECK = 1; // 1 ms

	/**
	 * Our active backend Pcap capture sessions.
	 */
	private final Pcap pcaps[];

	private LivePacketFactory factory;

	/**
	 * Initialize a Pcap captures for a number of capture devices simultaneously.
	 * 
	 * @param captureDevice
	 *          a list of devices to prepare for capture on
	 * @param captureCount
	 *          how many packets the user would like to capture total
	 * @param snaplen
	 *          snaplen
	 * @param promiscuous
	 *          put in promiscuous mode
	 * @param timeout
	 *          timeout for the capture
	 * @param filter
	 *          any filters
	 * @throws IOException
	 */
	public jNetPcapCapture(LiveCaptureDevice[] captureDevice, int captureCount,
	    int snaplen, boolean promiscuous, int timeout,
	    Filter<ProtocolFilterTarget> filter) throws IOException {
		super(captureDevice, captureCount, snaplen, promiscuous, timeout, filter);

		pcaps = new Pcap[captureDevice.length];

		/*
		 * Now open up all the specified interfaces
		 */
		final StringBuilder errbuf = new StringBuilder();
		for (int i = 0; i < captureDevice.length; i++) {
			LiveCaptureDevice device = captureDevice[i];
			pcaps[i] =
			    Pcap.openLive(device.getName(), snaplen, (promiscuous ? 1 : 0),
			        DEFAULT_BREAK_LOOP_CHECK, errbuf);

			if (pcaps[i] == null) {
				throw new IOException("Unable to open capture device "
				    + device.getName() + ". Error: " + errbuf.toString());
			}
		}

		this.factory =
		    ProtocolRegistry.getPacketFactory(LivePacketFactory.class,
		        "com.slytechs.jnetstream.packet.DefaultLivePacketFactory");
	}

	/**
	 * This method is called to start capturing and the number of packets that are
	 * requested to be captured. This method is called in a worker thread and not
	 * the same thread that Constructor and others. The method is blocking and
	 * will block until the requested number of packets have been captured.
	 * 
	 * @see com.slytechs.jnetstream.livecapture.AbstractLiveCapture#capture(int,
	 *      int)
	 * @param count
	 *          number of packets to capture
	 * @param index
	 *          index of the capture device that was supplied by the user, to
	 *          capture on
	 */
	@Override
	protected void capture(int count, int index) throws IOException {

		/*
		 * Check for this serious condition which should not occur. This will
		 * indicate a most likely a bug in jNetStream.
		 */
		if (index < 0 || index >= pcaps.length || pcaps[index] == null) {
			throw new IllegalStateException(
			    "Trying to use an unopen Pcap capture session");
		}

		final int dlt = pcaps[index].datalink(); // Get DLT type
		final ProtocolEntry id =
		    ProtocolRegistry.getProtocolEntry(PcapDLT.asConst(dlt));

		/*
		 * The handler that will receive packets from Pcap and create LivePackets
		 * out of the received data. Then through AbstractLiveCapture.dispatch
		 * method will forward those packets over to the client which should be the
		 * live capture object that the user called from Capture.openLive.
		 */
		final PcapHandler<LiveCaptureDevice> handler =
		    new PcapHandler<LiveCaptureDevice>() {

			    public void nextPacket(final LiveCaptureDevice device, long seconds,
			        int useconds, int caplen, int len, ByteBuffer buffer) {

				    /*
						 * Use factory methods for transparancy to create a LivePacket
						 * object and initialize it
						 */
				    final LivePacket packet =
				        factory.newLivePacket(id, buffer, BitBuffer.wrap(buffer),
				            seconds, useconds, caplen, len, device);

				    dispatch(packet); // Calls AbstractLiveCapture.dispatch()
			    }
		    };

		// final int r = pcaps[index].loop(count, handler, devices[index]);
		// if (r == Pcap.NOT_OK) {
		// throw new IOException(pcaps[index].getErr());
		// }

		int r;
		long ts = System.currentTimeMillis();
		do {
			r = pcaps[index].dispatch(count, handler, devices[index]);

			if (r == Pcap.NOT_OK) {
				throw new IOException(pcaps[index].getErr());

			} else if (timeout != 0 && System.currentTimeMillis() - ts >= timeout) {
				break;
			}

		} while (r >= 0);

		// System.out.printf("r=%d\n", r);

		/*
		 * Status 0 is OK and status -2 means breakloop was used, but that would be
		 * at a request of the client through use of an early close() call for
		 * example, so this is not an error
		 */
		pcaps[index].close();
		pcaps[index] = null;
	}

	/**
	 * Called from super class to clean up any remaining open capture sessions.
	 * This is done when super decides enough packets have been captured and we
	 * are done.
	 */
	protected void cleanup() {

		for (Pcap pcap : pcaps) {
			if (pcap != null) {
				pcap.close();
			}
		}

		Arrays.fill(pcaps, null);

	}

	@Override
	public void close() throws IOException {

//		System.out.println("jNetPcapCapture:close()");

		for (int i = 0; i < pcaps.length; i++) {
			Pcap pcap = pcaps[i];
			if (pcap == null) {
				continue;
			}

			pcap.breakloop();
		}

		super.close();
	}

}
