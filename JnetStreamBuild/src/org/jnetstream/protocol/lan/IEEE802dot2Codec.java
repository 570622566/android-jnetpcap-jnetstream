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
import org.jnetstream.protocol.codec.HeaderCodec;

import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class IEEE802dot2Codec implements HeaderCodec<IEEE802dot2> {

	private final static Object[] STATICS =
	    new Object[IEEE802dot2.StaticProperty.values().length];

	public IEEE802dot2 newHeader(BitBuffer buffer, int offset) {
		return new IEEE802dot2Header(
		    buffer,
		    offset,
		    new EnumProperties<IEEE802dot2.DynamicProperty, IEEE802dot2.StaticProperty>(
		        STATICS, IEEE802dot2.DynamicProperty.values(),
		        IEEE802dot2.StaticProperty.values()));
	}

	public Class<IEEE802dot2> getType() {
		return IEEE802dot2.class;
	}
}
