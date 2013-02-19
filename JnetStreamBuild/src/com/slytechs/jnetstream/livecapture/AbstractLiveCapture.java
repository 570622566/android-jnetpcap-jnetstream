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
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jnetstream.capture.CaptureType;
import org.jnetstream.capture.LiveCapture;
import org.jnetstream.capture.LiveCaptureDevice;
import org.jnetstream.capture.LiveIterator;
import org.jnetstream.capture.LivePacket;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;

import com.slytechs.utils.collection.IOIterator.IteratorAdapter;
import com.slytechs.utils.io.IORuntimeException;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class AbstractLiveCapture implements LiveSource,
    LiveDispatchable, LiveCapture {

	protected static final Log logger = LogFactory.getLog(LiveCapture.class);

	private static final AtomicReference<IOException> ioError =
	    new AtomicReference<IOException>();

	private static final String DISPATCHED = "DISPATCH";

	protected int count;

	protected int dispatched = 0;

	protected final LiveCaptureDevice[] devices;

	protected LiveClient client;

	protected Filter<ProtocolFilterTarget> filter;

	private boolean openState = true;

	private LiveIterator packetIterator;

	protected boolean promiscuous;

	protected int snaplen;

	protected int timeout;

	/**
	 * Currently active worker thread. If null, no thread is running
	 */
	@SuppressWarnings("unused")
	private volatile Thread[] workers;

	/**
	 * @param captureDevice
	 * @param captureCount
	 * @param snaplen
	 * @param promiscuous
	 * @param timeout
	 * @param filter
	 */
	public AbstractLiveCapture(final LiveCaptureDevice[] captureDevice,
	    int captureCount, int snaplen, boolean promiscuous, int timeout,
	    Filter<ProtocolFilterTarget> filter) {
		this.devices = captureDevice;
		this.count = captureCount;
		this.snaplen = snaplen;
		this.promiscuous = promiscuous;
		this.timeout = timeout;
		this.filter = filter;
	}

	/**
	 * Tells the open implementation dependent capture object to capture count
	 * number of packets and then return.
	 * 
	 * @param count
	 *          number of packets to capture and return or 0 for infinate
	 * @param index
	 *          TODO
	 * @throws IOException
	 *           any IO errors during the capture
	 */
	protected abstract void capture(int count, int index) throws IOException;

	/**
	 * Flags the capture closed and waits for all the workers to exit
	 */
	public void close() throws IOException {
		this.openState = false;

		for (Thread worker : workers) {
			if (worker == null) {
				continue;
			}

			try {
				worker.join();
			} catch (InterruptedException e) {
				break;
			}
		}
		
		client.getPacketQueue().clear();
	}

	/**
	 * Dispatches a packet that is ready to clients. A concurrent blocking queue
	 * is used to pass packets between this server thread and clients. Each client
	 * maintains its own queue through which the packet is exchanged. If
	 * particular clients queue is full, the packet is dropped and the appropriate
	 * counter incremented to indicate a drop, on this particular client's queue.
	 */
	public void dispatch(final LivePacket packet) {
		if (client == null) {
			return; // Nothing to do, ignore any packets received
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Received packet");
		}

		final BlockingQueue<LivePacket> queue = client.getPacketQueue();

		if (queue.remainingCapacity() == 0) {
			client.incrementDropCounter(1);

		} else {
			queue.offer(packet);
		}

		synchronized (DISPATCHED) {
			this.dispatched++;
		}
	}

	public Filter<ProtocolFilterTarget> getFilter() {
		return filter;
	}

	/**
	 * Gets the current pending IO exception which needs to be reported
	 * 
	 * @return IO exception which needs to be reported.
	 */
	public IOException getIOException() {
		return ioError.get();
	}

	public LiveCaptureDevice[] getOpenCaptureDevices() {
		return devices;
	}

	public LiveIterator getPacketIterator() throws IOException {
		return getPacketIterator(count);
	}

	public LiveIterator getPacketIterator(int count) throws IOException {

		if (packetIterator == null) {
			this.packetIterator = new LiveIteratorImpl(this);
			this.client = (LiveClient) packetIterator;

			/*
			 * Start the capture in the background
			 */
			start(count);
		}

		return packetIterator;
	}

	public int getSnaplen() {
		return snaplen;
	}

	public int getTimeout() {
		return timeout;
	}

	public CaptureType getType() {
		return CaptureType.LiveCapture;
	}

	/**
	 * Checks if there is a pending IO exception to be reported
	 * 
	 * @return true means that there is a pending exception, otherwise false
	 */
	public boolean hasIOException() {
		return ioError.get() != null;
	}

	public boolean isMutable() {
		return false;
	}

	public boolean isOpen() throws IOException {
		return openState;
	}

	public boolean isPromisuous() {
		return promiscuous;
	}

	public boolean isRunning() {
		synchronized (DISPATCHED) {
			return workers != null && (count == 0 || dispatched < count);
		}
	}

	public Iterator<LivePacket> iterator() {
		try {
			return new IteratorAdapter<LivePacket>(getPacketIterator());
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	public void setFilter(Filter<ProtocolFilterTarget> filter) throws IOException {
		this.filter = filter;
	}

	/**
	 * Captures count packets using an open capture session. Once the capture
	 * session has been closed it can not be opened again.
	 * 
	 * @param count
	 * @throws IOException
	 */
	private void start(final int count) throws IOException {
		if (isOpen() == false) {
			throw new IOException("Capture session is closed");
		}

		/**
		 * Check if we still have running workers
		 */
		if (workers != null) {
			return;
		}

		this.dispatched = 0;

		if (workers == null) {
			workers = new Thread[devices.length];

			/*
			 * Initialize with dummy data, so that the array is not full of nulls,
			 * which would break the below loop if early workers start exitting
			 * quickly and reseting workers back to null as well.
			 */
			Arrays.fill(workers, Thread.currentThread());
		}

		for (int i = 0; i < devices.length; i++) {
			final int index = i;

			final String name = "Live-" + devices[i].getDisplayName();

			/*
			 * Our capture session worker thread. Closes the LiveCapture when its done
			 */
			workers[i] = new Thread(new Runnable() {

				public void run() {

					try {
						capture(count, index);
					} catch (IOException e) {
						/*
						 * The IO exception will be reported at next available opportunity
						 * such as in LiveIterator's next or hasNext method calls.
						 */
						ioError.set(e);

					} finally {
						workers[index] = null;

						/*
						 * Now check if there are any workers left, if not then reset the
						 * workers variable, as a flag
						 */
						int j = 0;
						for (; j < workers.length; j++) {
							if (workers[j] != null) {
								break;
							}
						}
						if (j == workers.length) {
							workers = null;
						}

						Thread.yield();
					}
				}

			}, name);

			workers[i].start();
		}
	}

}
