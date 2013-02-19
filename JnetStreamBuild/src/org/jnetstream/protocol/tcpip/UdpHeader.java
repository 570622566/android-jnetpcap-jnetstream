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

import org.jnetstream.packet.EnumProperties;
import org.jnetstream.packet.Header;
import org.jnetstream.packet.HeaderElement;

import com.slytechs.jnetstream.packet.AbstractField;
import com.slytechs.jnetstream.packet.AbstractHeader;
import com.slytechs.utils.crypto.Crc16;
import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class UdpHeader
    extends AbstractHeader<Header.DynamicProperty, Header.StaticProperty>
    implements Udp {

	/**
	 * @param bits
	 * @param offset
	 * @param name
	 * @param type
	 * @param protocol
	 * @param properties
	 */
	public UdpHeader(BitBuffer bits, int offset,
	    EnumProperties<DynamicProperty, StaticProperty> properties) {
		super(bits, offset, "Udp", Udp.class, Tcpip.Udp, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.tcpip.Udp#crc()
	 */
	public Crc16 crc() {
		return readCrc16(StaticField.CRC);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.tcpip.Udp#destination()
	 */
	public short destination() {
		return readShort(StaticField.DESTINATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.tcpip.Udp#length()
	 */
	public short length() {
		return readShort(StaticField.LENGTH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.tcpip.Udp#source()
	 */
	public short source() {
		return readShort(StaticField.SOURCE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Header#getLength()
	 */
	public int getLength() {
		return 64;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.codec.Scannable#fullScan()
	 */
	public void fullScan() {
		int p = offset;
		elements = new ArrayList<HeaderElement>();

		elements.add(new AbstractField<Short>(bits, StaticField.SOURCE, p, false) {

			public Short getValue() throws IOException {
				return readShort(16);
			}
		});
		p += 16;

		elements.add(new AbstractField<Short>(bits, StaticField.DESTINATION, p,
		    false) {

			public Short getValue() throws IOException {
				return readShort(16);
			}
		});
		p += 16;

		elements.add(new AbstractField<Short>(bits, StaticField.LENGTH, p, false) {

			public Short getValue() throws IOException {
				return readShort(16);
			}
		});
		p += 16;

		elements.add(new AbstractField<Crc16>(bits, StaticField.CRC, p, false) {

			public Crc16 getValue() throws IOException {
				return readCrc16();
			}
		});
		p += 16;

	}

}
