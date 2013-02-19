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
public class Ip4Codec implements HeaderCodec<Ip4> {

	private final static Object[] STATICS =
	    new Object[Header.StaticProperty.values().length];

	public Ip4 newHeader(BitBuffer buffer, int offset) {
		return new Ip4Header(
		    buffer,
		    offset,
		    new EnumProperties<Ip4.DynamicProperty, Ip4.StaticProperty>(
		        STATICS, Ip4.DynamicProperty.values(),
		        Ip4.StaticProperty.values()));
	}

	public Class<Ip4> getType() {
		return Ip4.class;
	}
}
