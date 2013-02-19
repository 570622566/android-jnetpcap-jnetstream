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
package com.slytechs.jnetstream.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jnetstream.packet.DataField;
import org.jnetstream.packet.EnumProperties;
import org.jnetstream.packet.Field;
import org.jnetstream.packet.Header;
import org.jnetstream.packet.HeaderElement;
import org.jnetstream.protocol.Protocol;
import org.jnetstream.protocol.codec.CodecException;
import org.jnetstream.protocol.codec.Scannable;

import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class AbstractHeader<D extends Enum<D>, S extends Enum<S>>
    extends AbstractData implements Header, Scannable {

	protected String name;

	protected EnumProperties<D, S> properties;

	private final Protocol protocol;

	protected final Class<? extends Header> type;
	
	protected List<HeaderElement> elements;

	/**
	 * @param bits
	 * @param offset
	 * @param name
	 *          TODO
	 * @param type
	 *          TODO
	 * @param protocol
	 *          TODO
	 * @param properties
	 *          TODO
	 * @param properties
	 */
	public AbstractHeader(BitBuffer bits, int offset, String name,
	    Class<? extends Header> type, Protocol protocol,
	    EnumProperties<D, S> properties) {
		super(bits, offset);
		this.name = name;
		this.type = type;
		this.protocol = protocol;
		this.properties = properties;
	}

	public Field<?>[] getAllFields() {
		return new Field<?>[0];
	}

	public Header[] getAllHeaders() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Header#getBuffer()
	 */
	public BitBuffer getBuffer() throws IOException {
		return this.bits;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Header#getName()
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Header#getProperty(org.jnetstream.packet.Header.CommonStaticProperty)
	 */
	@SuppressWarnings("unchecked")
	public <A> A getProperty(Header.DynamicProperty key) {
		return (A) properties.getDynamic(key.ordinal());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Header#getProperty(org.jnetstream.packet.Header.CommonStaticProperty)
	 */
	@SuppressWarnings("unchecked")
	public <A> A getProperty(Header.StaticProperty key) {
		return (A) properties.getStatic(key.ordinal());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Header#getProperty(org.jnetstream.packet.Header.CommonStaticProperty)
	 */
	@SuppressWarnings("unchecked")
	public <A> A getProperty(String key) {
		return (A) properties.getByName(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Header#getProtocol()
	 */
	public Protocol getProtocol() {
		return this.protocol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Header#getType()
	 */
	public Class<? extends Header> getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Header#isTruncated()
	 */
	public boolean isTruncated() throws IOException {
		return offset + getLength() > bits.limit();
	}

	public Iterator<HeaderElement> iterator() {
		if (elements == null) {
			fullScan();
		}
		
		return elements.iterator();
	}

	@SuppressWarnings("unchecked")
  public <T> Field<T> getField(DataField field) throws CodecException,
      IOException {
		
		if (elements == null) {
			fullScan();
		}

		
	  for (HeaderElement e: elements) {
	  	if (e.getFieldConstant() == field) {
	  		return (Field<T>) e;
	  	}
	  }
	  
	  return null;
  }

	/**
	 * 
	 */
	public DataField getFieldConstant() {
	  return null;
  }

}
