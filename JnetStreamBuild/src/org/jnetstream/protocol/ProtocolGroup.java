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
package org.jnetstream.protocol;

import org.jnetstream.packet.Header;

/**
 * <p>
 * Grouping of protocols as a family. For example 802.3 and Ethernet are related
 * and the NIC card can deliver both types of link layer frames. Protocol group
 * contain no CODECs only bindings which allow it to bind to other protocols and
 * other protocols to be grouped under it.
 * </p>
 * <p>
 * Protocol group produces no headers and exports nothing into the packet. It is
 * only a binding container for other normal (non-group) protocols. However the
 * protocol can be referenced normally from anywhere a protocol is required
 * allowing CaptureDevices to be bound to protocol groups.
 * </p>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface ProtocolGroup extends ProtocolInfo<Header> {

}
