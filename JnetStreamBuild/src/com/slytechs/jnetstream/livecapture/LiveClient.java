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

import java.util.concurrent.BlockingQueue;

import org.jnetstream.capture.LivePacket;


/**
 * Iterface implemented by clients of the live source. Provides methods for a
 * source registering certain events with the client.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface LiveClient {

	/**
	 * Allows a live source to increment the queue drop counter if the queue was
	 * full and a packet had to be dropped from its queue.
	 * 
	 * @param count
	 *          amount by which to increment the counter which is typically 1 but
	 *          could be other number if more than 1 packet could not be queued
	 */
	public void incrementDropCounter(int count);

	/**
	 * Retrieves the client's packet queue which the live source can use to
	 * enqueue new packet arrivals to.
	 * 
	 * @return a queue for new packets
	 */
	public BlockingQueue<LivePacket> getPacketQueue();
}
