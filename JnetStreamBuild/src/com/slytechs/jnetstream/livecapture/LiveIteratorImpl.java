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

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jnetstream.capture.LiveIterator;
import org.jnetstream.capture.LivePacket;


/**
 * An iterator which works with LiveSources which capture live packets and
 * enqueue those packets on this iterator BlockingQueue. The iterator mantains a
 * counter of queue drop count which is incremented everytime the LiveSource
 * could not enqueue a new packet arrival due to the queue being full. The
 * iterator provides easy to use {@link #hasNext()}, {@link #next()} and
 * {@link #skip()} methods which check, return and skip packets captured from
 * the live network. The hasNext method will always return true as long as the
 * capture source is open or there are still packets on the queue.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class LiveIteratorImpl implements LiveIterator, LiveClient, Closeable {

	/**
	 * Default timeout in microsecond between checks with the source. Any skip or
	 * next operation is timed out every {@value #DEFAULT_IS_OPEN_CHECK}
	 * microseconds and status of the source is checked. If the source has been
	 * closed, then the currently in progress operation is gracefully interrupted
	 * and the closure reported using a {@link java.io.IOException} exception.
	 */
	public static final long DEFAULT_IS_OPEN_CHECK = 10000;

	/**
	 * Default packet queue capacity. The clients are responsible to pull packets
	 * from the queue in a timely manner using the {@link #next} or
	 * {@link #skip()} methods. It makes no sense to queue a lot of packets since
	 * they take up quiet a bit of memory. Its best to put the burden on the user
	 * to retrieve them in a timely manner.
	 */
	private static final int DEFAULT_PACKET_QUEUE_CAPACITY = 10;

	/**
	 * Default time units for IS_OPEN_CHECK
	 */
	private static final TimeUnit TIME_UNIT = TimeUnit.MICROSECONDS;

	/**
	 * Packets a offered to the queue by LiveSource or LiveCapture, but if the
	 * queue becomes full the source will increment this counter using the
	 * {@link #incrementDropCounter(int)} method.
	 */
	private long queueDrops;

	/**
	 * Packet are offered to this queue by a LiveSource.
	 */
	private final BlockingQueue<LivePacket> queue;

	/**
	 * LiveSource that is placing packets on our queue. We use the LiveSource
	 * interface to check if the source is still open and to close it if we're
	 * called by our {@link #close} method.
	 */
	private final LiveSource source;

	/*
	 * An atomic flag which can be changed any time from any thread to let us know
	 * to stop running. next() method call wakes up periodically and checks this
	 * flag. If set to false, it will exit prematurely but gracefully returning a
	 * null.
	 */
	private final AtomicBoolean keepRunnging = new AtomicBoolean(true);

	/**
	 * @param source
	 *          LiveSource that is queueing packets to our queue.
	 */
	public LiveIteratorImpl(final LiveSource source) {
		this.queue = new LinkedBlockingQueue<LivePacket>(
		    DEFAULT_PACKET_QUEUE_CAPACITY);
		this.source = source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.LiveIterator#getQueueDrops()
	 */
	public final long getQueueDrops() throws IOException {
		return queueDrops;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOSkippable#skip()
	 */
	public final void skip() throws IOException {
		next();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOIterator#hasNext()
	 */
	public final boolean hasNext() throws IOException {

		/*
		 * Check for any IO errors that need to be reported
		 */
		if (source.hasIOException()) {
			throw source.getIOException();
		}

		/*
		 * Allow dispatcher thread to exit if it has completed processing the
		 * required amount of packets. Without this yield we sometimes check
		 * isRunning while the isRunning is still exitting in the live source. When
		 * that happens this hasNext() may return true while next() will report an
		 * IO exception because dispatcher has stopped running in the mean time.
		 */
		Thread.yield();

		/*
		 * Now do our check. If we still have packets on the queue, no matter if the
		 * dispatcher is running or not, next() still has something to return.
		 * Otherwise if the queue is empty and the dispatcher has exitted, that
		 * means we have no more packets to return. Although if the dispatcher
		 * starts up again, we might be back in business, but currently that is not
		 * allowed.
		 */
		return (queue.isEmpty() && !source.isRunning() ? false : true);
	}

	/**
	 * Waits for a new packet to arrive from the network capture source. If the
	 * source is closed in the middle of this operation, this operation will
	 * gracefully throw an IOException and abort.
	 * 
	 * @see com.slytechs.utils.collection.IOIterator#next()
	 */
	public final LivePacket next() throws IOException {
		try {
			LivePacket packet = null;

			while ((packet = queue.poll(DEFAULT_IS_OPEN_CHECK, TIME_UNIT)) == null) {

				if (source.isRunning() == false) {
					throw new IOException("LiveCapture source has stopped capturing "
					    + "and next() operation has been interrupted");
				}

				/*
				 * Check if there are any pending IO exception that should be reported
				 * on behalf of the dispatcher, since it can't do it as its running in a
				 * background thread.
				 */
				if (source.hasIOException()) {
					throw source.getIOException();
				}
				
				/*
				 * Lastly check and make sure that we haven't been requested to interrupt
				 * gracefully. If so simply return null as an indication that we were
				 * asked to leave early.
				 */
				if (keepRunnging.get() == false) {
					keepRunnging.set(true); // reset the flag
					return null; // We were asked to leave politely
				}
			}

			/*
			 * Everything went well, we've got a live one.
			 */
			return packet;

		} catch (InterruptedException e) {
			/*
			 * Someone didn't let us finish the get call.
			 */
			throw new IOException(e.getLocalizedMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOIterator#remove()
	 */
	public final void remove() throws IOException {
		throw new UnsupportedOperationException(
		    "LiveCapture does not allow mutable operations");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	public final void close() throws IOException {
		this.source.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.LiveClient#incrementDropCounter(long)
	 */
	public final void incrementDropCounter(int count) {
		this.queueDrops += count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.LiveClient#getPacketQueue()
	 */
	public final BlockingQueue<LivePacket> getPacketQueue() {
		return queue;
	}
	
	/**
	 * Politely ask next to interrupt and exit prematurely. next() will return
	 * null.
	 *
	 */
	public final void interruptNext() {
		this.keepRunnging.set(false);
	}

}
