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
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.jnetstream.packet.EnumProperties;
import org.jnetstream.packet.Field;
import org.jnetstream.packet.HeaderElement;
import org.jnetstream.protocol.Protocol;
import org.jnetstream.protocol.codec.CodecException;

import com.slytechs.jnetstream.packet.AbstractField;
import com.slytechs.jnetstream.packet.AbstractHeader;
import com.slytechs.utils.crypto.Crc16;
import com.slytechs.utils.memory.BitBuffer;
import com.slytechs.utils.net.Ip4Address;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class Ip4Header
    extends AbstractHeader<Ip4.DynamicProperty, Ip4.StaticProperty> implements
    Ip4 {

	/**
	 * @param bits
	 * @param offset
	 * @param name
	 * @param type
	 * @param protocol
	 * @param properties
	 */
	public Ip4Header(BitBuffer bits, int offset,
	    EnumProperties<Ip4.DynamicProperty, Ip4.StaticProperty> properties) {
		super(bits, offset, "Ip4", Ip4.class, Tcpip.Ip4, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#crc()
	 */
	public Crc16 crc() {
		return readCrc16(Ip4.StaticField.CRC);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#crc(short)
	 */
	public void crc(short value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#destination()
	 */
	public Ip4Address destination() {
		return readIp4Address(Ip4.StaticField.DESTINATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#destination(com.slytechs.net.IP4)
	 */
	public void destination(Ip4Address value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#flags()
	 */
	public byte flags() {
		return readByte(Ip4.StaticField.FLAGS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#flags(byte)
	 */
	public void flags(byte value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#getDestinationByteArray()
	 */
	public byte[] getDestinationByteArray() {
		return readByteArray(Ip4.StaticField.DESTINATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#getSourceByteArray()
	 */
	public byte[] getSourceByteArray() {
		return readByteArray(Ip4.StaticField.SOURCE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#hlen()
	 */
	public byte hlen() {
		return readByte(Ip4.StaticField.HLEN);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#hlen(byte)
	 */
	public void hlen(byte value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#id()
	 */
	public short id() {
		return readShort(Ip4.StaticField.ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#id(short)
	 */
	public void id(short value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#length()
	 */
	public short length() {
		return readShort(Ip4.StaticField.LENGTH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#length(short)
	 */
	public void length(short value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#offset()
	 */
	public short offset() {
		return readShort(Ip4.StaticField.OFFSET);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#offset(short)
	 */
	public void offset(short value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#protocol()
	 */
	public byte protocol() {
		return readByte(Ip4.StaticField.PROTOCOL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#protocol(byte)
	 */
	public void protocol(byte value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#setDestinationByteArray(byte[])
	 */
	public void setDestinationByteArray(byte[] value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#setSourceByteArray(byte[])
	 */
	public void setSourceByteArray(byte[] value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#source()
	 */
	public Ip4Address source() {
		return readIp4Address(Ip4.StaticField.SOURCE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#source(com.slytechs.net.IP4)
	 */
	public void source(Ip4Address value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#tos()
	 */
	public byte tos() {
		return readByte(Ip4.StaticField.TOS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#tos(byte)
	 */
	public void tos(byte value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#ttl()
	 */
	public byte ttl() {
		return readByte(Ip4.StaticField.TTL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#ttl(byte)
	 */
	public void ttl(byte value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#ver()
	 */
	public byte ver() {
		return readByte(Ip4.StaticField.VER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.protocol.network.Ip4#ver(byte)
	 */
	public void ver(byte value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Header#getProtocol()
	 */
	public Protocol getProtocol() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
   * @see org.jnetstream.packet.Header#getField(java.lang.Class)
   */
  public <T extends Field<?>> T getField(Class<T> c) throws CodecException,
      IOException {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see org.jnetstream.packet.Header#getLength()
   */
  public int getLength() {
	  return readByte(Ip4.StaticField.HLEN) * 4 * 8;
  }
  
  public void fullScan() {
  	int p = offset;
		elements = new ArrayList<HeaderElement>();
  	
		elements.add(new AbstractField<Byte>(bits, StaticField.VER, p, false) {

			public Byte getValue() throws IOException {
	      return readByte(4);
      }});
		p += 4;
		
		elements.add(new AbstractField<Byte>(bits, StaticField.HLEN, p, false) {

			public Byte getValue() throws IOException {
	      return readByte(4);
      }});
		p += 4;
		
		elements.add(new AbstractField<Byte>(bits, StaticField.TOS, p, false) {

			public Byte getValue() throws IOException {
	      return readByte(8);
      }});
		p += 8;
		
		elements.add(new AbstractField<Short>(bits, StaticField.LENGTH, p, false) {

			public Short getValue() throws IOException {
	      return readShort(16);
      }});
		p += 16;

		
		elements.add(new AbstractField<Short>(bits, StaticField.ID, p, false) {

			public Short getValue() throws IOException {
	      return readShort(16);
      }});
		p += 16;
		
		elements.add(new AbstractField<Byte>(bits, StaticField.FLAGS, p, false) {

			public Byte getValue() throws IOException {
	      return readByte(3);
      }});
		p += 3;
		
		elements.add(new AbstractField<Short>(bits, StaticField.OFFSET, p, false) {

			public Short getValue() throws IOException {
	      return readShort(13);
      }});
		p += 13;
		
		elements.add(new AbstractField<Byte>(bits, StaticField.TTL, p, false) {

			public Byte getValue() throws IOException {
	      return readByte(8);
      }});
		p += 8;
		
		elements.add(new AbstractField<Byte>(bits, StaticField.PROTOCOL, p, false) {

			public Byte getValue() throws IOException {
	      return readByte(8);
      }});
		p += 8;
		
		elements.add(new AbstractField<Crc16>(bits, StaticField.CRC, p, false) {

			public Crc16 getValue() throws IOException {
	      Crc16 crc = readCrc16();
	      
	      computeCrc16(crc);
	      
	      return crc;
      }});
		p += 16;
		
		elements.add(new AbstractField<Ip4Address>(bits, StaticField.SOURCE, p, false) {

			public Ip4Address getValue() throws IOException {
	      return readIp4Address();
      }});
		p += 32;
		
		elements.add(new AbstractField<Ip4Address>(bits, StaticField.DESTINATION, p, false) {

			public Ip4Address getValue() throws IOException {
	      return readIp4Address();
      }});
		p += 32;
  }
  
  public void computeCrc16(Crc16 crc) {
  	final ByteBuffer b = bits.getBackingBuffer();
  	final int o = offset / 8;
  	final int length = getLength() / 8;
  	
  	crc.compute(b, o, length, 10);
  }

}
