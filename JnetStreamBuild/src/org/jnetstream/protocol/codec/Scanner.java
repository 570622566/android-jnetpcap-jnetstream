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
package org.jnetstream.protocol.codec;

import java.io.IOException;

import org.jnetstream.packet.HeaderCache;
import org.jnetstream.packet.Packet;

import com.slytechs.utils.memory.BitBuffer;

/**
 * Packet scanner which allows lightweight and heavyweight packet decoding. The
 * scanner interface provides to distinctly different ways of decoding packet
 * content.
 * <p>
 * The lightweight calls do not create any objects or use heavy amount storage
 * and provide a quick way to determine which headers exists within a packet.
 * The lightweight methods are invoked using a call to {@link #quickScan()}
 * which relies on {@link Scandec} interface to provide lightweight, in possibly
 * non-java way. Scandecs can be implemented using native or BPF byte code
 * librariers which do nor rely on need for creating and accessing java objects.
 * </p>
 * <p>
 * The heavyweight class produce header objects and if fully decoded sub header
 * and field objects as well. This is much heavier way of accessing packet
 * content but is required by the general jNetStream public API. For example
 * invoking the method call {@link Packet#format()} forces entire packet
 * contents to be fully decoded so that information about each piece of the
 * packet nicely formatted and displayed to the user.
 * </p>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Scanner {

	/**
	 * <p>
	 * Scanner's Decoder or Scandec for short. Is deceptively simple interface
	 * that accomplishes several tasks. Scandecs are deployed along with protocol
	 * bindings and are a merger of a protocol binding information any NPL header
	 * length formulas. In addition any assert conditions within the NPL
	 * definition are applied as well.
	 * </p>
	 * <p>
	 * The primary task of a Scandec is to determine if all the conditions of a
	 * scandec are met and the header that the scandec represents exists. The
	 * second task is to return the length of the header. If length returned by a
	 * Scandec is greater then -1, that means that the header exists or was found
	 * to exist and the value is the actual length of the header in bits. A lenth
	 * of 0 actually means that that the match was on a ProtocolGroup and its not
	 * a physical header that exists.
	 * </p>
	 * <p>
	 * Scandecs read data directly from raw buffers and do not rely on
	 * HeaderCodecs or java Protocol classes. The Scandecs may be autogenerated or
	 * custom written in java or BPF byte code. They do not instantiate any
	 * objects and are really fast at runtime to execute.
	 * </p>
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface Scandec {

		/**
		 * <p>
		 * Returns the length of the sink header for which this scandec was built. A
		 * length of -1 means that the header does not exist in the packet buffer or
		 * that 1 or more assert conditions originally defined within the source NPL
		 * definition did not pass, evaluate to true.
		 * </p>
		 * 
		 * @param buffer
		 *          packet runtime environment used to access the raw buffer and
		 *          possibly some static properties; it is not used to access codecs
		 *          or other decoded headers
		 * @param source
		 *          offset of the source header in bits from the start of the raw
		 *          buffer
		 * @param sink
		 *          bit offset into the buffer of the sink protocol under evaluation
		 * @return length of the header in bits or 0 if the header doesn't exist or
		 *         pass assert conditions
		 */
		public int getLength(BitBuffer buffer, int source, int sink);

	}

	/**
	 * Performs a full scan of the raw packet buffer, using object based API to
	 * determine to build and decode headers.
	 * 
	 * @return TODO
	 * @throws IOException
	 */
	public boolean fullScan(PacketRuntime packet) throws IOException;

	/**
	 * Performs a quick scan of the raw packet buffer, using non object based API
	 * to determine which headers exist and their lengths.
	 * 
	 * @param packetRT
	 * @return TODO
	 * @throws CodecCreateException
	 * @throws IOException
	 */
	public boolean quickScan(PacketRuntime packetRT) throws IOException;

	/**
	 * @param buffer
	 * @param cache
	 * @return
	 */
	public HeaderCache init(BitBuffer buffer, HeaderCache cache);

}