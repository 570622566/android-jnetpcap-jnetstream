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
package org.jnetstream.capture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Queue;

/**
 * Transmitter which transmits or send low level packets using the link layer
 * of the open network interface. 
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public interface NetTransmitter {
	
	/**
	 * <P>Transmits the specified buffer using the currently
	 * open network interface. The transmition is done using link layer. 
	 * the supplied buffer must contain valid data-link header otherwise 
	 * an error will occur.</P>
	 * 
	 * <P>The buffer position and limit properties mark the area of the buffer
	 * that will be transmitted.</P>
	 * 
	 * @param buffer
	 *   the buffer to transmit
	 *   
	 * @exception IOException
	 *   any IO errors during the transmittion of the buffer
	 */
	public void send(ByteBuffer buffer) throws IOException;
	
	/**
	 * <P>Transmits the specified buffer using the currently
	 * open network interface. The transmition is done using link layer. 
	 * the supplied buffer must contain valid data-link header otherwise 
	 * an error will occur.</P>
	 * 
	 * <P>The buffer position and limit properties mark the area of the buffer
	 * that will be transmitted.</P>
	 * 
	 * @param buffer
	 *   the buffer to transmit
	 * @param count
	 *   Number of times the packet is to be repeatadily resent. The packet
	 *   will be sent count number of times, as quickly as the kernel and the
	 *   hardware are capable of. This is a very efficient way to generate
	 *   large number of packets on a network for testing purposes.
	 *   
	 * @exception IOException
	 *   any IO errors during the transmittion of the buffer
	 */
	public void send(ByteBuffer buffer, int count) throws IOException;
	
	/**
	 * <P>Transmits the specified queue with buffers in it using the currently
	 * open network interface. The transmition is done using link layer and
	 * raw packets. The supplied buffers on the queue must contain valid 
	 * data-link headers.</P>
	 * 
	 * <P>Each buffer's property position and limit on the queue, mark the area
	 * of the buffer that will be transmitted, one buffer after the other.
	 * The order of the buffers is retrieved using {@link Queue#poll}
	 * method.</P>
	 * 
	 * <P>Note that it is much more efficient to transmit a set of packets 
	 *  
	 * @param queue
	 *   queue of buffers to be transmitted
	 *   
	 * @throws IOException
	 *   any IO errors
	 */
	public void send(Queue<ByteBuffer> queue) throws IOException;

}
