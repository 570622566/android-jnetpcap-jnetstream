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

import java.io.IOException;
import java.util.ArrayList;

import org.jnetstream.packet.DataField;
import org.jnetstream.packet.EnumProperties;
import org.jnetstream.packet.Field;
import org.jnetstream.packet.Header;
import org.jnetstream.packet.HeaderElement;
import org.jnetstream.packet.Header.DynamicProperty;
import org.jnetstream.packet.Header.StaticProperty;
import org.jnetstream.protocol.Protocol;
import org.jnetstream.protocol.codec.CodecException;
import org.jnetstream.protocol.tcpip.Ip4.StaticField;

import com.slytechs.jnetstream.packet.AbstractField;
import com.slytechs.jnetstream.packet.AbstractHeader;
import com.slytechs.utils.crypto.Crc16;
import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class IcmpHeader
    extends AbstractHeader<Header.DynamicProperty, Header.StaticProperty>
    implements Icmp {

	/**
	 * @param bits
	 * @param offset
	 * @param name
	 * @param type
	 * @param protocol
	 * @param properties
	 */
	public IcmpHeader(BitBuffer bits, int offset,
	    EnumProperties<DynamicProperty, StaticProperty> properties) {
		super(bits, offset, "Icmp", Icmp.class, Tcpip.Icmp, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Icmp#code()
	 */
	public byte code() {
		return readByte(StaticField.CODE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Icmp#crc()
	 */
	public Crc16 crc() {
		return readCrc16(StaticField.CRC);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Icmp#type()
	 */
	public byte type() {
		return readByte(StaticField.TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Header#getLength()
	 */
	public int getLength() {
		return 32;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.codec.Scannable#fullScan()
	 */
	public void fullScan() {
		int p = offset;
		elements = new ArrayList<HeaderElement>();

		elements.add(new AbstractField<Byte>(bits, StaticField.TYPE, p, false) {

			public Byte getValue() throws IOException {
				return readByte(8);
			}
		});
		p += 8;

		elements.add(new AbstractField<Byte>(bits, StaticField.CODE, p, false) {

			public Byte getValue() throws IOException {
				return readByte(8);
			}
		});
		p += 8;

		elements.add(new AbstractField<Crc16>(bits, StaticField.CRC, p, false) {

			public Crc16 getValue() throws IOException {
				return readCrc16();
			}
		});
		p += 16;
	}

}
