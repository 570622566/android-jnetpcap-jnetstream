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
package org.jnetstream.capture.file;

import java.nio.ByteBuffer;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jnetstream.capture.Messages;
import org.jnetstream.packet.Packet;
import org.jnetstream.protocol.Protocol;

/**
 * Factory methods for working with the jNetStream packet package and individual
 * packets.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class Files {

	private static final Log logger = LogFactory.getLog(Files.class);

	/**
	 * Factory interface that actually does all the work.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface Factory {

		/**
		 * Creates a new packet with internally allocated buffer specified length.
		 * The buffer can be retrieved using <code>Packet.getBuffer()</code>
		 * method call. The packet included length will be set to the length of the
		 * buffer. The buffer can be later resized but may invalidate any buffer
		 * references returned prior to the resize call.
		 * 
		 * @param length
		 *          length of the backing buffer for this packet
		 * @param dlt
		 *          data-link-type, first protocol in the packet
		 * @return new packet
		 */
		public Packet newPacket(Protocol dlt, int length);

		/**
		 * Create a new packet with user supplied buffer. The supplied buffer
		 * properties <code>position</code> and <code>limit</code> determine the
		 * boundaries for the packet data.
		 * 
		 * @param buffer
		 *          user supplied buffer to hold the packet contents
		 * @param dlt
		 *          data-link-type, first protocol in the packet
		 * @return new packet
		 */
		public Packet newPacket(Protocol dlt, ByteBuffer buffer);
	}

	private static Factory local;

	public static Factory getLocal() {
		if (local == null) {
			try {
				local = (Factory) Class.forName(
				    Messages.getString("com.slytechs.packet.DefaultPacketFactory"))
				    .newInstance();
			} catch (final Exception e) {
				logger.error(Messages
				    .getString("Can't find the default packet factory"));
				throw new IllegalStateException(e);
			}
		}
		return local;
	}

	/**
	 * Creates a new packet with internally allocated buffer specified length. The
	 * buffer can be retrieved using <code>Packet.getBuffer()</code> method
	 * call. The packet included length will be set to the length of the buffer.
	 * The buffer can be later resized but may invalidate any buffer references
	 * returned prior to the resize call.
	 * 
	 * @param length
	 *          length of the backing buffer for this packet
		 * @param dlt
		 *          data-link-type, first protocol in the packet
	 * @return new packet
	 */
	public static Packet newPacket(Protocol dlt, int length) {
		return getLocal().newPacket(dlt, length);
	}

	/**
	 * Create a new packet with user supplied buffer. The supplied buffer
	 * properties <code>position</code> and <code>limit</code> determine the
	 * boundaries for the packet data.
	 * 
	 * @param buffer
	 *          user supplied buffer to hold the packet contents
		 * @param dlt
		 *          data-link-type, first protocol in the packet
	 * @return new empty packet
	 */
	public static Packet newPacket(Protocol dlt, ByteBuffer buffer) {
		return getLocal().newPacket(dlt, buffer);
	}

	/**
	 * Noop constructor, not supposed to be instantiated.
	 */
	private Files() {
		// Make sure no one can instantiate
	}
}
