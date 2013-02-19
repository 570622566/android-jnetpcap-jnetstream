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

import org.jnetstream.capture.file.FileIndexer;
import org.jnetstream.packet.Packet;
import org.jnetstream.protocol.Protocol;


/**
 * <p>
 * All getter methods, that return a packet return subclass of CapturePacket
 * while all mutable methods, methods that take packets, simply require any type
 * of packet.
 * </p>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * @param <I>
 *          type returned from interator methods such as IOIterator.next().
 */
public interface PacketIndexer<I extends CapturePacket> extends
    FileIndexer<I, Packet, I> {
	/*
	 * Iterate over CapturePacket objects, but add and remove more generic Packets
	 */

	/**
	 * <p>
	 * Adds a new packet to the capture file at current cursor position. The
	 * packet data is contained within the supplied <tt>data</tt> buffer where
	 * current position and limit properties define which section of the buffer
	 * will be copied to the capture file. The dlt (data link type) is used to
	 * specify the protocol of the first header within the data buffer.
	 * </p>
	 * <p>
	 * If the underlying file type supports other properties within the packet
	 * header, those properties will be initialized to some default initial value
	 * as determined by the format itself. If you need to specify additional
	 * properties you must use one of the more format specific methods found
	 * within the format package.
	 * </p>
	 * 
	 * @param data
	 *          buffer containing the packet's data
	 * @param dlt
	 *          protocol of the first header within the packet
	 * @param original
	 *          length of the original packet before any truncations if any took
	 *          place
	 * @param seconds
	 *          capture timestamp in seconds
	 * @param nanos
	 *          Capture timestamp in nano seconds as a fraction of a second. The
	 *          valid value is between 0 and 999,999,999.
	 * @throws IOException
	 *           any IO errors
	 */
	public void add(long index, ByteBuffer data, Protocol dlt, long original,
	    long seconds, long nanos) throws IOException;

	/**
	 * <p>
	 * Adds a new packet to the capture file at current cursor position. The
	 * packet data is contained within the supplied <tt>data</tt> buffer where
	 * current position and limit properties define which section of the buffer
	 * will be copied to the capture file. This verion allows one to specify the
	 * raw DLT type. The dlt (data link type) is used to specify the protocol of
	 * the first header within the data buffer. This raw DLT type will not have
	 * any transformations done on it and will be written into the packet record
	 * header as is.
	 * </p>
	 * <p>
	 * If the underlying file type supports other properties within the packet
	 * header, those properties will be initialized to some default initial value
	 * as determined by the format itself. If you need to specify additional
	 * properties you must use one of the more format specific methods found
	 * within the format package.
	 * </p>
	 * 
	 * @param data
	 *          buffer containing the packet's data
	 * @param dlt
	 *          protocol of the first header within the packet
	 * @param original
	 *          length of the original packet before any truncations if any took
	 *          place
	 * @throws IOException
	 *           any IO errors
	 */
	public void add(long index, ByteBuffer data, int dlt, long original, long seconds,
	    long nanos) throws IOException;

	/**
	 * <p>
	 * Adds a new packet to the capture file at current cursor position. The
	 * packet data is contained within the supplied <tt>data</tt> buffer where
	 * current position and limit properties define which section of the buffer
	 * will be copied to the capture file. The dlt (data link type) is used to
	 * specify the protocol of the first header within the data buffer.
	 * </p>
	 * <p>
	 * The capture timestamp is initialized to the current timestamp.
	 * </p>
	 * <p>
	 * If the underlying file type supports other properties within the packet
	 * header, those properties will be initialized to some default initial value
	 * as determined by the format itself. If you need to specify additional
	 * properties you must use one of the more format specific methods found
	 * within the format package.
	 * </p>
	 * 
	 * @param data
	 *          buffer containing the packet's data
	 * @param dlt
	 *          protocol of the first header within the packet
	 * @param original
	 *          length of the original packet before any truncations if any took
	 *          place
	 * @throws IOException
	 *           any IO errors
	 */
	public void add(long index, ByteBuffer data, Protocol dlt, long original)
	    throws IOException;

	/**
	 * <p>
	 * Adds a new packet to the capture file at current cursor position. The
	 * packet data is contained within the supplied <tt>data</tt> buffer where
	 * current position and limit properties define which section of the buffer
	 * will be copied to the capture file. The dlt (data link type) is used to
	 * specify the protocol of the first header within the data buffer.
	 * </p>
	 * <p>
	 * The capture timestamp is initialized to the current timestamp. The original
	 * packet length is initialized to be equal to the included length.
	 * </p>
	 * <p>
	 * If the underlying file type supports other properties within the packet
	 * header, those properties will be initialized to some default initial value
	 * as determined by the format itself. If you need to specify additional
	 * properties you must use one of the more format specific methods found
	 * within the format package.
	 * </p>
	 * 
	 * @param data
	 *          buffer containing the packet's data
	 * @param dlt
	 *          protocol of the first header within the packet
	 * @param original
	 *          length of the original packet before any truncations if any took
	 *          place
	 * @throws IOException
	 *           any IO errors
	 */
	public void add(long index, ByteBuffer data, Protocol dlt) throws IOException;

	/**
	 * <p>
	 * Adds a new packet to the capture file at current cursor position. The
	 * packet data is contained within the supplied <tt>data</tt> buffer where
	 * current position and limit properties define which section of the buffer
	 * will be copied to the capture file.
	 * </p>
	 * <p>
	 * The capture timestamp is initialized to the current timestamp. The original
	 * packet length is initialized to be equal to the included length. The DLT is
	 * intialized to {@link org.jnetstream.protocol.Protocol#Ethernet} constant.
	 * </p>
	 * <p>
	 * If the underlying file type supports other properties within the packet
	 * header, those properties will be initialized to some default initial value
	 * as determined by the format itself. If you need to specify additional
	 * properties you must use one of the more format specific methods found
	 * within the format package.
	 * </p>
	 * 
	 * @param data
	 *          buffer containing the packet's data
	 * @param dlt
	 *          protocol of the first header within the packet
	 * @param original
	 *          length of the original packet before any truncations if any took
	 *          place
	 * @throws IOException
	 *           any IO errors
	 */
	public void add(long index, ByteBuffer data) throws IOException;
}
