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
import org.jnetstream.packet.Field;
import org.jnetstream.packet.HeaderElement;
import org.jnetstream.protocol.codec.CodecException;

import com.slytechs.jnetstream.packet.AbstractField;
import com.slytechs.jnetstream.packet.AbstractHeader;
import com.slytechs.utils.memory.BitBuffer;
import com.slytechs.utils.net.EUI48;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class IEEE802dot3Header
    extends
    AbstractHeader<IEEE802dot3.DynamicProperty, IEEE802dot3.StaticProperty>
    implements IEEE802dot3 {

	/**
	 * @param buffer
	 * @param offset
	 * @param properties
	 *          TODO
	 */
	public IEEE802dot3Header(
	    BitBuffer buffer,
	    int offset,
	    EnumProperties<IEEE802dot3.DynamicProperty, IEEE802dot3.StaticProperty> properties) {
		super(buffer, offset, "IEEE802dot3", IEEE802dot3.class, Lan.IEEE802dot3,
		    properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocols.lan.IEEE802dot3#destination()
	 */
	public EUI48 destination() throws IOException {
		return readEUI48(StaticField.DESTINATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocols.lan.IEEE802dot3#destination(com.slytechs.net.EUI48)
	 */
	public void destination(EUI48 value) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocols.lan.IEEE802dot3#getDestinationRaw()
	 */
	public byte[] getDestinationRaw() throws IOException {
		return readByteArray(StaticField.DESTINATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocols.lan.IEEE802dot3#getDestinationRaw()
	 */
	public byte[] getDestinationRaw(byte[] a) throws IOException {
		if (a.length != 6) {
			throw new IllegalArgumentException();
		}
		return readByteArray(StaticField.DESTINATION, a);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Header#getField(java.lang.Class)
	 */
	public <T extends Field<?>> T getField(Class<T> c) throws CodecException,
	    IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Header#getLength()
	 */
	public int getLength() {
		return IEEE802dot3.LENGTH;
	}

	@SuppressWarnings("unchecked")
	public <A> A getProperty(IEEE802dot3.DynamicProperty key) {
		return (A) properties.getDynamic(key.ordinal());
	}

	@SuppressWarnings("unchecked")
	public <A> A getProperty(IEEE802dot3.StaticProperty key) {
		return (A) properties.getDynamic(key.ordinal());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocols.lan.IEEE802dot3#sourceByteArray()
	 */
	public byte[] getSourceRaw() throws IOException {
		return readByteArray(StaticField.SOURCE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocols.lan.IEEE802dot3#sourceByteArray()
	 */
	public byte[] getSourceRaw(byte[] a) throws IOException {
		return readByteArray(StaticField.SOURCE, a);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocols.lan.IEEE802dot3#setDestinationRaw(byte[])
	 */
	public void setDestinationRaw(byte[] value) throws IOException {
		if (value.length != 6) {
			throw new IllegalArgumentException();
		}

		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocols.lan.IEEE802dot3#sourceByteArray(byte[])
	 */
	public void setSourceRaw(byte[] value) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocols.lan.IEEE802dot3#source()
	 */
	public EUI48 source() throws IOException {
		return readEUI48(StaticField.SOURCE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocols.lan.IEEE802dot3#source(com.slytechs.net.EUI48)
	 */
	public void source(EUI48 value) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocols.lan.IEEE802dot3#type()
	 */
	public short length() throws IOException {
		return readShort(StaticField.LENGTH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocols.lan.IEEE802dot3#type(short)
	 */
	public void length(short value) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocols.lan.IEEE802dot3#typeEnum()
	 */
	public EtherType typeEnum() throws IOException {
		return EtherType.valueOf(readShort(StaticField.LENGTH));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocols.lan.IEEE802dot3#typeEnum(protocols.lan.EtherType)
	 */
	public void typeEnum(EtherType value) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
   * @see org.jnetstream.protocol.codec.Scannable#fullScan()
   */
  public void fullScan() {
		int p = offset;
		elements = new ArrayList<HeaderElement>();
		
		elements.add(new AbstractField<EUI48>(bits, StaticField.DESTINATION, p, false) {

			public EUI48 getValue() throws IOException {
	      return readEUI48();
      }});
		p += 48;
		
		elements.add(new AbstractField<EUI48>(bits, StaticField.SOURCE, p, false) {

			public EUI48 getValue() throws IOException {
	      return readEUI48();
      }});
		
		p += 48;
		
		elements.add(new AbstractField<Short>(bits, StaticField.LENGTH, p, false) {

			public Short getValue() throws IOException {
	      return readShort(16);
      }});
		
		p += 16;  }

}
