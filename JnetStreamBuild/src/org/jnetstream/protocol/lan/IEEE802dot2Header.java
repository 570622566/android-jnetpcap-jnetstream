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

import java.io.IOException;
import java.util.ArrayList;

import org.jnetstream.packet.EnumProperties;
import org.jnetstream.packet.Header;
import org.jnetstream.packet.HeaderElement;
import org.jnetstream.protocol.lan.Arp.StaticField;

import com.slytechs.jnetstream.packet.AbstractField;
import com.slytechs.jnetstream.packet.AbstractHeader;
import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class IEEE802dot2Header
    extends AbstractHeader<Header.DynamicProperty, Header.StaticProperty>
    implements IEEE802dot2 {

	/**
	 * @param bits
	 * @param offset
	 * @param name
	 * @param type
	 * @param protocol
	 * @param properties
	 */
	public IEEE802dot2Header(BitBuffer bits, int offset,
	    EnumProperties<DynamicProperty, StaticProperty> properties) {
		super(bits, offset, "LLC2", IEEE802dot2.class, Lan.IEEE802dot2, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.lan.IEEE802dot2#control()
	 */
	public short control() {
		return readShort(StaticField.CONTROL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.lan.IEEE802dot2#dsap()
	 */
	public byte dsap() {
		return readByte(StaticField.DSAP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.lan.IEEE802dot2#ssap()
	 */
	public byte ssap() {
		return readByte(StaticField.SSAP);
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

		elements.add(new AbstractField<Byte>(bits, StaticField.DSAP, p, false) {

			public Byte getValue() throws IOException {
				return readByte(8);
			}
		});
		p += 8;

		elements.add(new AbstractField<Byte>(bits, StaticField.SSAP, p, false) {

			public Byte getValue() throws IOException {
				return readByte(8);
			}
		});
		p += 8;

		elements.add(new AbstractField<Short>(bits, StaticField.CONTROL, p, false) {

			public Short getValue() throws IOException {
				return readShort(8);
			}
		});
		p += 8;
	}

}
