/**
 * Copyright (C) 2008 Sly Technologies, Inc. This library is free software; you
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
package org.jnetstream.protocol.tcpip;

import org.jnetstream.packet.EnumProperties;
import org.jnetstream.packet.Header;
import org.jnetstream.protocol.codec.HeaderCodec;

import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class IcmpCodec implements HeaderCodec<Icmp> {

	private final static Object[] STATICS =
	    new Object[Header.StaticProperty.values().length];

	public Icmp newHeader(BitBuffer buffer, int offset) {
		return new IcmpHeader(
		    buffer,
		    offset,
		    new EnumProperties<Icmp.DynamicProperty, Icmp.StaticProperty>(
		        STATICS, Icmp.DynamicProperty.values(),
		        Icmp.StaticProperty.values()));
	}

	public Class<Icmp> getType() {
		return Icmp.class;
	}
}
