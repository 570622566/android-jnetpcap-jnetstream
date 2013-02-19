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

import com.slytechs.utils.collection.IOSkippableIterator;

/**
 * <p>
 * IO based iterator over a live capture session. LiveIterator does not provide
 * as many services as for example file PacketIterator. Also note that this
 * iterator's <code>remove()</code> method is unsupported and will throw an
 * UnuspportedOperationException if invoked.
 * </p>
 * <p>
 * You can aquire a LiveIterator from a LiveCapture session. Here is an example:
 * 
 * <pre>
 * LiveCapture capture = Captures.openLive(10); // Capture only 10 packets
 * LiveIterator iterator = capture.getPacketIterator();
 * 
 * while (iterator.hasNext()) {
 * 	LivePacket packet = iterator.next();
 * 	System.out.println(packet.toString());
 * }
 * 
 * capture.close(); // Don't forget to close
 * </pre>
 * 
 * For convenience you can also use a tight foreach loop:
 * 
 * <pre>
 * for (LivePacket packet : Captures.openLive(10)) {
 * 	System.out.println(packet.toString());
 * }
 * 
 * Captures.close(); // Close the last capture returned by our factory
 * </pre>
 * 
 * Live iterator can also be interrupted from another thread, safely and
 * gracefully by a call to <code>LiveIterator.interruptNext</code>:
 * 
 * <pre>
 * LiveCapture capture = Captures.openLive(10); // Capture only 10 packets
 * LiveIterator iterator = capture.getPacketIterator();
 * 
 * new Thread(new Runnable() {
 * 	public void run() {
 * 		Thread.sleep(10000); // sleep for 10 seconds
 * 		iterator.interruptNext();
 * 	}
 * }).start();
 * 
 * while (iterator.hasNext()) {
 * 	LivePacket packet = iterator.next();
 * 
 * 	if (packet == null) {
 * 		// This means we were interrupted from another thread by a call to
 * 		// iterator.interruptNext()
 * 		// The interruption caused next() to exit early and return null. Any
 * 		// packets that may have arrived are remembered and will be returned 
 * 		// by subsequent calls to next, nothing has been lost. 
 * 		// The main thing to remember is that when there is a possibility that
 * 		// a call may be interrupted in a program, we must check for null being
 * 		// returned.
 * 	}
 * }
 * 
 * capture.close(); // Don't forget to close
 * </pre>
 * 
 * It may also be a good idea to check <code>LiveIterator.getQueueDrops()</code>
 * from time to time, especially if there was an interruption or any long delay
 * between subsequent <code>next()</code> calls. The iterator maintains a
 * queue of packets being received and is able to buffer a small amount of
 * packets, but if they overwhelm the queue, the counter will indicate if any
 * packets have been dropped due to the fact that the queue is full. Packets may
 * continue to be dropped until the queue is drained. If the queue drop counter
 * is increasing, even in a tight loop between calls to <code>next</code>,
 * this means that the packets are arriving faster then the current thread can
 * handle the processing of them.
 * </p>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface LiveIterator extends IOSkippableIterator<LivePacket>,
    Closeable {

	/**
	 * Retrieves the current value of the "queue drop" counter. The counter
	 * maintains the number of packets that this particular iterator was not able
	 * to received due to its internall packet receive queue was full. The queue
	 * becomes full when the user of the iterator is not able to quickly enough
	 * draw queued packets to make room for new packets to be received by the
	 * iterator. Other iterator's queues are independent of this iterator queue
	 * and may hold a different state.
	 * 
	 * @return number of packets dropped due to no more room on the queue
	 * @throws IOException
	 *           any IO errors
	 */
	public long getQueueDrops() throws IOException;

	/**
	 * <p>
	 * Politely interrupts the <code>next()</code> call. This method is atomic
	 * and safe to call from any thread. <code>next</code> will gracefully exit
	 * while its waiting for a packet to arrive. The value returned from the
	 * interrupted <code>next()</code> will be null so if the user expects to
	 * use this interrupt method at any time, it should check all return values
	 * from <code>next()</code> for null reference.
	 * </p>
	 * <p>
	 * Although the current next command may be interrupted, the LiveCapture
	 * session may continue capturing packets which will be queued up on this
	 * iterator. Any subsequent call to <code>next</code> after the
	 * interruption, will likely return the packet that it was waiting for
	 * originally.
	 * </p>
	 */
	public void interruptNext();

}
