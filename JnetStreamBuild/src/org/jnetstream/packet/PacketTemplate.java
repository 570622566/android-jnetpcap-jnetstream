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
package org.jnetstream.packet;

/**
 * <p>
 * Interface which allows a packet to be configured and initialized once and
 * then reused as a template to initialize efficiently other packets. The
 * requirements for a packet template are that the implementing class provide a
 * no-argument public constructor and that all the packet initialization code go
 * into the init method. In all respects the template is a normal packet with
 * the exception, that templates are singletons, instatiated only once,
 * initialized and then cached.
 * </p>
 * <p>
 * You can request new packets to be created based on the template using the
 * Packets.newPacket(Class<? extends PacketTemplate>) call or
 * Packets.newPacket(PacketTemplate). The first method will instantiate and
 * cache the template for you, the second call requires an already instantiated
 * packet to exist and no caching will be performed.
 * </p>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface PacketTemplate extends Packet {

	/**
	 * Initialization method for the template. You need to put all your packet
	 * initialization code in this method. Create any properties, add headers
	 * and set values.
	 */
	public void init();

}
