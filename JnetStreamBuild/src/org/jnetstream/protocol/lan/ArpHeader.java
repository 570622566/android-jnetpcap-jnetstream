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
import org.jnetstream.protocol.tcpip.Ip4.StaticField;

import com.slytechs.jnetstream.packet.AbstractField;
import com.slytechs.jnetstream.packet.AbstractHeader;
import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class ArpHeader
    extends AbstractHeader<Header.DynamicProperty, Header.StaticProperty>
    implements Arp {

	/**
	 * @param bits
	 * @param offset
	 * @param name
	 * @param type
	 * @param protocol
	 * @param properties
	 */
	public ArpHeader(BitBuffer bits, int offset,
	    EnumProperties<DynamicProperty, StaticProperty> properties) {
		super(bits, offset, "Arp", Arp.class, Lan.Arp, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.lan.Arp#hardwareLength()
	 */
	public byte hardwareLength() {
		return readByte(StaticField.HARDWARE_LENGTH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.lan.Arp#hardwareType()
	 */
	public short hardwareType() {
		return readShort(StaticField.HARDWARE_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.lan.Arp#opcode()
	 */
	public short opcode() {
		return readShort(StaticField.OPCODE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.lan.Arp#protocolLength()
	 */
	public byte protocolLength() {
		return readByte(StaticField.PROTOCOL_LENGTH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.lan.Arp#protocolType()
	 */
	public short protocolType() {
		return readByte(StaticField.PROTOCOL_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Header#getLength()
	 */
	public int getLength() {
		return (hardwareLength() * 2 + protocolLength() * 2 + 8) * 8;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.codec.Scannable#fullScan()
	 */
	public void fullScan() {
		int p = offset;
		elements = new ArrayList<HeaderElement>();

		elements.add(new AbstractField<Short>(bits, StaticField.HARDWARE_TYPE, p,
		    false) {

			public Short getValue() throws IOException {
				return readShort(16);
			}
		});
		p += 16;

		elements.add(new AbstractField<Short>(bits, StaticField.PROTOCOL_TYPE, p,
		    false) {

			public Short getValue() throws IOException {
				return readShort(16);
			}
		});
		p += 16;
		
		elements.add(new AbstractField<Byte>(bits, StaticField.HARDWARE_LENGTH, p, false) {

			public Byte getValue() throws IOException {
	      return readByte(8);
      }});
		p += 8;
		
		elements.add(new AbstractField<Byte>(bits, StaticField.PROTOCOL_LENGTH, p, false) {

			public Byte getValue() throws IOException {
	      return readByte(8);
      }});
		p += 8;
		
		elements.add(new AbstractField<Short>(bits, StaticField.OPCODE, p, false) {

			public Short getValue() throws IOException {
	      return readShort(16);
      }});
		p += 16;
	}

}
