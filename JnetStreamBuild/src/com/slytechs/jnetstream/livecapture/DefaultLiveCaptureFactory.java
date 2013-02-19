/**
 * Copyright (C) 2007 Sly Technologies, Inc.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.slytechs.jnetstream.livecapture;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collection;

import org.jnetstream.capture.CaptureDevice;
import org.jnetstream.capture.LiveCapture;
import org.jnetstream.capture.LiveCaptureDevice;
import org.jnetstream.capture.NetTransmitter;
import org.jnetstream.capture.Captures.LiveCaptureFactory;
import org.jnetstream.filter.Filter;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public class DefaultLiveCaptureFactory implements LiveCaptureFactory {

	/* (non-Javadoc)
	 * @see org.jnetstream.capture.Captures.LiveCaptureFactory#listCaptureDevices()
	 */
	public LiveCaptureDevice[] listCaptureDevices() throws IOException {
		return jNetPcapDevice.listCaptureDevices();
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.capture.Captures.LiveCaptureFactory#newCaptureDevice()
	 */
	public CaptureDevice newCaptureDevice() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openLive()
	 */
	public LiveCapture openLive() throws IOException {
		return new jNetPcapCapture(listCaptureDevices(), 0, 64 * 1024, true, 0, null);
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openLive(org.jnetstream.capture.CaptureDevice[])
	 */
	public LiveCapture openLive(CaptureDevice... nics) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openLive(java.util.Collection)
	 */
	public LiveCapture openLive(Collection<CaptureDevice> nics)
	    throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openLive(org.jnetstream.filter.Filter)
	 */
	public LiveCapture openLive(Filter fiter) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openLive(org.jnetstream.filter.Filter, org.jnetstream.capture.CaptureDevice[])
	 */
	public LiveCapture openLive(Filter filter, CaptureDevice... nics)
	    throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openLive(org.jnetstream.filter.Filter, java.util.Collection)
	 */
	public LiveCapture openLive(Filter filter, Collection<CaptureDevice> nics)
	    throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openLive(long)
	 */
	public LiveCapture openLive(long count) throws IOException {
		return new jNetPcapCapture(listCaptureDevices(), (int) count, 64 * 1024, true, 0, null);
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openTransmitter()
	 */
	public NetTransmitter openTransmitter() throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.capture.Captures.LiveCaptureFactory#openTransmitter(java.net.NetworkInterface)
	 */
	public NetTransmitter openTransmitter(NetworkInterface netInterface)
	    throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

}
