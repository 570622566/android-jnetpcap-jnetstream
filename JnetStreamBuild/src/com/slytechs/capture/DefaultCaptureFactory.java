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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.apache.commons.logging.Log;
import org.jnetstream.capture.Capture;
import org.jnetstream.capture.CaptureListener;
import org.jnetstream.capture.CapturePacket;
import org.jnetstream.capture.Captures;
import org.jnetstream.capture.FileCapture;
import org.jnetstream.capture.FileMode;
import org.jnetstream.capture.FilePacket;
import org.jnetstream.capture.FormatType;
import org.jnetstream.capture.PacketIterator;
import org.jnetstream.capture.Captures.LocalFactory;
import org.jnetstream.packet.Packet;

import com.slytechs.utils.collection.IOSkippableIterator;

/**
 * Default implementation of Captures interface that delegates to Capture
 * implementation within the package.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class DefaultCaptureFactory
    extends StreamFactory implements LocalFactory {
	
	private static final Log logger = Captures.logger;
	
	private final List<CaptureListener> captureListeners = new ArrayList<CaptureListener>();

	/**
	 * 
	 */
	public DefaultCaptureFactory() {
		// Empty
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.CaptureFactory#append(com.slytechs.capture.Capture,
	 *      com.slytechs.capture.file.FileCapture)
	 */
	@SuppressWarnings("unchecked")
  public long append(final FileCapture<? extends FilePacket> dst,
	    final Capture... srcs) throws IOException {
		
		if (logger.isTraceEnabled()) {
			logger.trace(dst + ", src=" + Arrays.asList(srcs));
		}


		final PacketIterator<? extends FilePacket> i = dst.getPacketIterator();
		i.seekEnd();

		final int bulkCount = 100;
		final List<Packet> list = new ArrayList<Packet>(bulkCount);

		long count = 0; // How many packets are appended
		for (Capture<CapturePacket> capture : srcs) {

			IOSkippableIterator<? extends CapturePacket> si = capture
			    .getPacketIterator();

			/*
			 * Adds packets in bulk instead of individual, its much faster
			 */
			while (si.hasNext()) {
				final CapturePacket packet = si.next();

				list.add(packet);
				count++;

				if (list.size() % bulkCount == 0) {
					i.addAll(list);
					list.clear();
				}
			}

			/*
			 * Don't forget the remainder
			 */
			if (list.isEmpty() == false) {
				i.addAll(list);
				list.clear();
			}

		}

		return count;
	}

	@SuppressWarnings("unchecked")
  public long catFile(FormatType type, final File dst, final Capture... srcs)
	    throws IOException {
		final FileCapture<? extends FilePacket> dstCapture;
		
		if (logger.isTraceEnabled()) {
			logger.trace(type + "=" + dst + ", src=" + Arrays.asList(srcs));
		}

		/*
		 * First check if dst exists, create it if not.
		 */
		if (dst.exists() == false) {
			dstCapture = newFile(type, dst, null);
		} else {
			dstCapture = openFile(dst, FileMode.ReadWrite, null);
		}

		long count = 0;

		/*
		 * Next we need to iterate through each of the source captures and append
		 * their packets to the dst file
		 */
		for (Capture<? extends CapturePacket> source : srcs) {
			count += append(dstCapture, source);
		}

		dstCapture.close();

		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.CaptureFactory#catFile(java.io.File,
	 *      java.io.File...)
	 */
	public long catFile(FormatType type, final File dst, final File... files)
	    throws FileNotFoundException, IOException {
		if (files.length == 0) {
			return 0; // Nothing to do
		}

		final FileCapture<? extends FilePacket> dstCapture;

		/*
		 * First check if dst exists, create it if not.
		 */
		if (dst.exists() == false) {
			dstCapture = newFile(type, dst, null);
		} else {
			dstCapture = openFile(dst, FileMode.ReadWrite, null);
		}

		long count = 0;

		/*
		 * Next we need to iterate through each of the source files and append their
		 * packets to the dst file
		 */
		for (File source : files) {
			final FileCapture<? extends FilePacket> capture = openFile(source);

			try {
				count += append(dstCapture, capture);
			} catch (IOException e) {
				capture.close();
				throw e;
			}
		}

		dstCapture.close();

		return count;
	}

	protected <T extends CapturePacket> void fireCloseCapture(final Capture<T> capture) {
		for (final CaptureListener l : this.captureListeners) {
			l.processCloseCapture(this, capture);
		}
	}

	protected <T extends CapturePacket> void fireOpenCapture(final Capture<T> capture) {
		for (final CaptureListener l : this.captureListeners) {
			l.processOpenCapture(this, capture);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.Captures.Factory#registerListener(org.jnetstream.capture.CaptureListener)
	 */
	public void registerListener(final CaptureListener listener) {
		this.captureListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.Captures.Factory#removeRegisteredListener(org.jnetstream.capture.CaptureListener)
	 */
	public void removeRegisteredListener(final CaptureListener listener) {
		this.captureListeners.remove(listener);
	}

}
