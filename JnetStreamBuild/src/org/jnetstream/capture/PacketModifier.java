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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

import org.jnetstream.packet.Packet;
import org.jnetstream.protocol.Protocol;


/**
 * <P>
 * ElementModifier interface allows a capture session that is mutable (see
 * {@link Capture#isMutable}) to add, insert, swap and remove packets from the
 * underlying packet dataset of the capture session. For example if the capture
 * session is file based, such as FileCapture then packets are added, inserted
 * or removed from the underlying capture file in response to the operations
 * performed by this interface.
 * </P>
 * <h3>Adding new packets</h3>
 * <p>
 * New packets can be added in several different ways. Use one of the
 * {@link #add} methods to add the packet at the current position which is not
 * determined by this interface. (Usually ElementModifier objects are iterators
 * that maintain their own position.) There are several overloaded various of
 * the {@link #add} methods. You can use the add method to add a previously
 * captured packet, or you can specify the ByteBuffer containing the packet's
 * data and supply additional information so that appropriate packet
 * representation is created for the underlying capture session. The
 * {@link #add} methods are used for both appending a new packet at the end of
 * the dataset and inserting it if the current position is not at the end of the
 * dataset.
 * </p>
 * <h3>Removing packets</h3>
 * The interface also provides several {@link #remove} and {@link #retain}
 * methods which can be used to remove packets from the current capture session.
 * These methods only take CapturePackets that came from the underlying capture
 * session, otherwise IllegalArgumentException will be thrown. If you want to
 * delete a packet, you can use the following approaches.
 * <ul>
 * <li> use the {@link #remove()} method to remove the packet at the current
 * location</li>
 * <li> use the bulk {@link #remove(Collection)} method which will remove
 * several packet at the same time, found in the collection, from the underlying
 * capture session.</li>
 * <li> use the bulk {@link #retain(Collection)} which will remove all packets
 * from the capture session, other then the ones found in the supplied
 * collection.</li>
 * </ul>
 * <h3>Efficiency</h3>
 * <p>
 * Please note that it is much more efficient to utilize bulk methods as opposed
 * to accomplishing the same task single packet at a time. The implementation is
 * optimized to be as efficient as possible given the supplied information and
 * the request.
 * </p>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface PacketModifier<T extends CapturePacket> {
	/**
	 * Adds a new packet to the current capture session. The new packet is built
	 * from the supplied values which include raw buffer data, the protocol number
	 * of the first header within the packet buffer, and timestamp comprised of
	 * seconds and nanosecond fraction.
	 * 
	 * @param b
	 *          packet buffer which contains the packet's content. The packet data
	 *          retrieved from the ByteBuffer.position() upto the
	 *          ByteBuffer.limit() properties. The included and original lengths
	 *          are calculated from these values.
	 * @param p
	 *          protocol number of the first protocol within the packet buffer
	 * @param s
	 *          capture timestamp of when the packet was first captured in seconds
	 * @param n
	 *          capture timestamp of when the packet was first captured in
	 *          nanosecond fraction
	 * @return new packet that was created from the supplied values
	 * @throws IOException
	 *           any IO errors
	 */
	public T add(ByteBuffer b, Protocol p, long s, long n)
	    throws IOException;

	/**
	 * Adds a new packet to the current capture session. The new packet is built
	 * from the supplied values which include raw buffer data, the protocol number
	 * of the first header within the packet buffer, and timestamp comprised of
	 * seconds and nanosecond fraction.
	 * 
	 * @param b
	 *          packet buffer which contains the packet's content. The packet data
	 *          retrieved from the ByteBuffer.position() upto the
	 *          ByteBuffer.limit() properties. The included length is calculated
	 *          from these values.
	 * @param p
	 *          protocol number of the first protocol within the packet buffer
	 * @param s
	 *          capture timestamp of when the packet was first captured in seconds
	 * @param n
	 *          capture timestamp of when the packet was first captured in
	 *          nanosecond fraction
	 * @param o
	 *          original length of the packet data
	 * @return new packet that was created from the supplied values
	 * @throws IOException
	 *           any IO errors
	 */
	public T add(ByteBuffer b, Protocol p, long s, long n, int o)
	    throws IOException;

	/**
	 * Bulk add, which adds several packets at the same time to the capture
	 * session. All packets within the collection must be of type CapturePacket as
	 * capture timestamp is extracted from the packets themselves.
	 * 
	 * @param elements
	 *          collection of packets to add to the capture session
	 * @return a list of newly created packets by this operation
	 * @throws IOException
	 *           any IO errors
	 */
	public List<T> add(List<? extends CapturePacket> elements) throws IOException;

	/**
	 * Adds the supplied packet after the current position. The capture timestamp
	 * is extracted from the Packet.
	 * 
	 * @param element
	 *          the packet which contains the packet buffer
	 * @return new packet that was created from the supplied values
	 * @throws IOException
	 *           any IO errors
	 */
	public T add(Packet element) throws IOException;

	/**
	 * Adds the supplied packet after the current position.
	 * 
	 * @param element
	 *          the packet which contains the packet buffer
	 * @param s
	 *          capture timestamp in seconds
	 * @param n
	 *          capture timestamp in nanosecond fraction with value between 0 and
	 *          999,999,999
	 * @return new packet that was created from the supplied values
	 * @throws IOException
	 *           any IO errors
	 */
	public T add(Packet element, long s, long n) throws IOException;

	/**
	 * Removes the current element. The position is maintained by the underlying
	 * object that implements this interface, but typically that is an interator
	 * type object that maintains a current position. In that particular case the
	 * remove would happen at the current position of the iterator.
	 * 
	 * @throws IOException
	 *           any IO errors
	 */
	public void remove() throws IOException;

	/**
	 * <p>
	 * Bulk remove, that removes several packets from the collection. The packets
	 * that are part of the collection, must have been created by the underlying
	 * capture session associated with this interface. For example FilePackets
	 * retain certain information about the position within the file, the packet
	 * came from and this information is needed to efficiently perform the
	 * operation.
	 * <p>
	 * 
	 * @param elements
	 *          collection of packets to be removed from the underlying capture
	 *          session
	 * @throws IOException
	 *           any IO errors
	 */
	public void remove(Collection<T> elements) throws IOException;

	/**
	 * <p>
	 * Bulk remove, that removes all packets other then the ones found in the
	 * collection. The packets that are part of the collection, must have been
	 * created by the underlying capture session associated with this interface.
	 * For example FilePackets retain certain information about the position
	 * within the file, the packet came from and this information is needed to
	 * efficiently perform the operation.
	 * <p>
	 * 
	 * @param elements
	 *          collection of packets to be removed from the underlying capture
	 *          session
	 * @throws IOException
	 *           any IO errors
	 */
	public void retain(Collection<T> elements) throws IOException;

	/**
	 * Swaps the two elements so that first element occupies second element's
	 * space while second element occupies previous first element's space.
	 * Record's in between first and second may need to be moved in order to
	 * accomodate the change.
	 * 
	 * @param first
	 *          element to swap with 'second'
	 * @param second
	 *          element to swap with 'first'
	 * @throws IOException
	 *           any IO errors
	 */
	public void swap(T first, T second) throws IOException;

}
