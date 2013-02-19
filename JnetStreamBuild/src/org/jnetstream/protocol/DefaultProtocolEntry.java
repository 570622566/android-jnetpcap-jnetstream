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
package org.jnetstream.protocol;

import java.util.Arrays;
import java.util.List;

import org.jnetstream.packet.Header;
import org.jnetstream.protocol.codec.HeaderCodec;


/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class DefaultProtocolEntry implements ProtocolEntry {
	private final int index;

	private final Protocol protocol;

	private ProtocolBinding[] bindings;

	private String suite;

	private String name;

	private Class<HeaderCodec<? extends Header>> codecClass;

	private HeaderCodec<? extends Header> codec;

	private Class<? extends Header> prot;

	private Class<? extends Header> protocolClass;
	
	/**
	 * 
	 * @param protocol
	 * @param index
	 */
	public DefaultProtocolEntry(Protocol protocol, int index) {
		this.protocol = protocol;
		this.index = index;
		
	}

	/**
	 * 
	 */
	public ProtocolBinding[] getBindings() {
		return bindings;
	}

	/**
	 * 
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * 
	 */
	public Protocol getProtocol() {
		return protocol;
	}

	/* (non-Javadoc)
   * @see org.jnetstream.protocol.ProtocolEntry#addBinding(org.jnetstream.protocol.ProtocolBinding)
   */
  public void addBinding(ProtocolBinding binding) {
	  
  	List<ProtocolBinding> l = Arrays.asList(bindings);
  	l.add(binding);
  	
  	bindings = l.toArray(new ProtocolBinding[l.size()]);
  }

	/**
   * @param suite
   */
  public void setSuite(String suite) {
		this.suite = suite;
	  
  }

	/**
   * @param name
   */
  public void setName(String name) {
	  this.name = name;
		
  }

	/**
   * @param forName
   */
  public void setCodec(Class<HeaderCodec<? extends Header>> c) {
	  this.codecClass = c;
  }
  
  public void setCodec(HeaderCodec<? extends Header> codec) {
		this.codec = codec;
  	
  }

	/**
   * @return
   */
  public Class<HeaderCodec<? extends Header>> getCodecClass() {
	  return this.codecClass;
  }

	/* (non-Javadoc)
   * @see org.jnetstream.protocol.ProtocolEntry#getCodec()
   */
  @SuppressWarnings("unchecked")
  public <T extends HeaderCodec<? extends Header>> T getCodec() {
	  return (T) codec;
  }

	/**
   * @return
   */
  public Class<? extends Header> getProtocolClass() {
	  return this.protocolClass;
  }

	/**
   * @param protocolClass the protocolClass to set
   */
  public final void setProtocolClass(Class<? extends Header> protocolClass) {
  	this.protocolClass = protocolClass;
  }
}
