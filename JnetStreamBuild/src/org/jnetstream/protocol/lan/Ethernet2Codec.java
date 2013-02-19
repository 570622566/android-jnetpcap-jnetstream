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
package org.jnetstream.protocol.lan;

import org.jnetstream.packet.EnumProperties;
import org.jnetstream.packet.Header;
import org.jnetstream.protocol.codec.HeaderCodec;

import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class Ethernet2Codec implements HeaderCodec<Ethernet2> {

	private final static Object[] STATICS =
	    new Object[Header.StaticProperty.values().length];

	public Ethernet2 newHeader(BitBuffer buffer, int offset) {
		return new Ethernet2Header(
		    buffer,
		    offset,
		    new EnumProperties<Ethernet2.DynamicProperty, Ethernet2.StaticProperty>(
		        STATICS, Ethernet2.DynamicProperty.values(),
		        Ethernet2.StaticProperty.values()));
	}

	public Class<Ethernet2> getType() {
		return Ethernet2.class;
	}
}
