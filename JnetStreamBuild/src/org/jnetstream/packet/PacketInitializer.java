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
 * Interface which allows packets to be initialized from other objects. A custom
 * packet initializer is built and some are supplied with jNetStream, to
 * initialize certain aspects of packets. For example a
 * SourceCaptureDeviceInitializer will use a capture device (ie. network
 * interface) to initialize various source related fields within a packet. An
 * Ethernet.source field may lookup the MAC address of the CaptureDevice to
 * initialize that field, while IPv4.source field will most likely be interested
 * in the IP address assigned to the NIC instead of the MAC address. The
 * supplied jNetStream package org.jnetstream.packet.templates contains various
 * predefined initializers which can be used to easily initialize various
 * components of a new packet.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface PacketInitializer {

}
