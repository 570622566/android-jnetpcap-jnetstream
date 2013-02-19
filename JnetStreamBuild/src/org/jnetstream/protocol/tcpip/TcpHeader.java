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
import java.net.NetworkInterface;
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
public class TcpHeader
    extends AbstractHeader<Header.DynamicProperty, Header.StaticProperty>
    implements Tcp {

	/**
	 * @param bits
	 * @param offset
	 * @param name
	 * @param type
	 * @param protocol
	 * @param properties
	 */
	public TcpHeader(BitBuffer bits, int offset,
	    EnumProperties<DynamicProperty, StaticProperty> properties) {
		super(bits, offset, "Tcp", Tcp.class, Tcpip.Tcp, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.tcpip.Tcp#ack()
	 */
	public int ack() {
		return readInt(StaticField.ACK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.tcpip.Tcp#crc()
	 */
	public Crc16 crc() {
		return readCrc16(StaticField.CRC);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.tcpip.Tcp#destination()
	 */
	public short destination() {
		return readShort(StaticField.DESTINATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.tcpip.Tcp#flags()
	 */
	public byte flags() {
		return readByte(StaticField.FLAGS);
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
      }});
		p += 16;
		
		elements.add(new AbstractField<Short>(bits, StaticField.DESTINATION, p, false) {

			public Short getValue() throws IOException {
	      return readShort(16);
      }});
		p += 16;
		
		elements.add(new AbstractField<Integer>(bits, StaticField.SEQUENCE, p, false) {

			public Integer getValue() throws IOException {
	      return readInt(32);
      }});
		p += 32;
		
		elements.add(new AbstractField<Integer>(bits, StaticField.ACK, p, false) {

			public Integer getValue() throws IOException {
	      return readInt(32);
      }});
		p += 32;
		
		elements.add(new AbstractField<Byte>(bits, StaticField.OFFSET, p, false) {

			public Byte getValue() throws IOException {
	      return readByte(4);
      }});
		p += 4;

		elements.add(new AbstractField<Byte>(bits, StaticField.RESERVED, p, false) {

			public Byte getValue() throws IOException {
	      return readByte(6);
      }});
		p += 6;
		
		elements.add(new AbstractField<Byte>(bits, StaticField.FLAGS, p, false) {

			public Byte getValue() throws IOException {
	      return readByte(6);
      }});
		p += 6;

		elements.add(new AbstractField<Short>(bits, StaticField.WINDOW, p, false) {

			public Short getValue() throws IOException {
	      return readShort(16);
      }});
		p += 16;

		elements.add(new AbstractField<Crc16>(bits, StaticField.CRC, p, false) {

			public Crc16 getValue() throws IOException {
	      return readCrc16();
      }});
		p += 16;

		elements.add(new AbstractField<Short>(bits, StaticField.URGENT, p, false) {

			public Short getValue() throws IOException {
	      return readShort(16);
      }});
		p += 16;



	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Header#getLength()
	 */
	public int getLength() {
		return readByte(StaticField.OFFSET) * 4 * 8;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.tcpip.Tcp#offset()
	 */
	public byte offset() {
		return readByte(StaticField.OFFSET);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.tcpip.Tcp#reserved()
	 */
	public byte reserved() {
		return readByte(StaticField.RESERVED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.tcpip.Tcp#sequence()
	 */
	public int sequence() {
		return readInt(StaticField.SEQUENCE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.tcpip.Tcp#source()
	 */
	public short source() {
		return readShort(StaticField.SOURCE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.tcpip.Tcp#urgent()
	 */
	public short urgent() {
		return readShort(StaticField.URGENT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.tcpip.Tcp#window()
	 */
	public short window() {
		return readShort(StaticField.WINDOW);
	}

}
