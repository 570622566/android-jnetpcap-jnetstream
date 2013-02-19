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

package org.jnetstream.packet;

import org.jnetstream.capture.Capture;

/**
 * Allows an interested observer to monitor packet reads, writes, inserts,
 * deletions and packet modifications.
 * <PRE>
 * LiveCapture capture = Captures.openLive();
 * capture.addPacketListener(new PacketListener() {
 *   public void processReadPacket(Capture source, Packet packet) {
 *     System.out.println("New packet"); 
 *   }
 *   // ... and so on
 * });
 * </PRE>
 * 
 * Also can be registered as follows:
 * <PRE>
 * Captures.registerListener(new CaptureListener() {
 *   public void processOpenCapture(Capture source) {
 *     source.registerPacketListener(new PacketListener() {
 *       // ... and so on
 *     });
 *   }
 *   
 *   public void processCloseCapture(Capture source) {
 *     // All listeners are automatically removed after the capture session is closed
 *   }
 * });
 * </PRE>
 *
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public interface PacketListener {

	/**
	 * Notifies the listener that new packet has been captured or read
	 * from a file.
	 * 
	 * @param source
	 *   source capture session that captured the packet
	 *   
	 * @param packet
	 *   packet that captured
	 */
	public void processReadPacket(Capture source, Packet packet);
	
	/**
	 * Notifies the listener that a new packet has been written to a 
	 * capture session, either a file or possibly the raw network.
	 * 
	 * @param source
	 *   source capture that the packet was written to
	 *   
	 * @param packet
	 *   the packet that was written
	 */
	public void processWritePacket(Capture source, Packet packet);
	
	/**
	 * A new packet has been added or inserted.
	 * 
	 * @param source
	 *   source capture that the packet was added to
	 *   
	 * @param packet
	 *   the packet that was added
	 */
	public void processAddPacket(Capture source, Packet packet);
	
	/**
	 * A packet has been deleted from the capture session, i.e. from a file.
	 * 
	 * @param source
	 *   source capture that the packet was deleted from
	 *   
	 * @param packet
	 *   the packet that was deleted
	 */
	public void processRemovePacket(Capture source, Packet packet);
	
	/**
	 * Notifies that the packet data buffer has been changed, and possibly
	 * the internal structure of the packet has been altered as a result.
	 * 
	 * @param source
	 *   source capture the packet is bound to, could be null for newly created
	 *   packets that are not bound to a session
	 *   
	 * @param packet
	 *   the packet that had its buffer modified
	 */
	public void processPacketChange(Capture source, Packet packet);
}
