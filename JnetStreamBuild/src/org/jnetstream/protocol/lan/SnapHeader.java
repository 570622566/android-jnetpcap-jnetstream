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

import com.slytechs.jnetstream.packet.AbstractField;
import com.slytechs.jnetstream.packet.AbstractHeader;
import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class SnapHeader
    extends AbstractHeader<Header.DynamicProperty, Header.StaticProperty>
    implements Snap {

	/**
	 * @param bits
	 * @param offset
	 * @param name
	 * @param type
	 * @param protocol
	 * @param properties
	 */
	public SnapHeader(BitBuffer bits, int offset,
	    EnumProperties<DynamicProperty, StaticProperty> properties) {
		super(bits, offset, "Snap", Snap.class, Lan.Snap, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.lan.Snap#oui()
	 */
	public int oui() {
		return readInt(StaticField.OUI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.lan.Snap#type()
	 */
	public short type() {
		return readShort(StaticField.TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Header#getLength()
	 */
	public int getLength() {
		return 40;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.codec.Scannable#fullScan()
	 */
	public void fullScan() {
		int p = offset;
		elements = new ArrayList<HeaderElement>();

		elements.add(new AbstractField<Integer>(bits, StaticField.OUI, p, false) {

			public Integer getValue() throws IOException {
				return readInt(24);
			}
		});
		p += 24;

		elements.add(new AbstractField<Short>(bits, StaticField.TYPE, p, false) {

			public Short getValue() throws IOException {
				return readShort(16);
			}
		});
		p += 16;
	}

}
