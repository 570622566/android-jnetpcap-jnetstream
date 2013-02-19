/**
 * Copyright (C) 2007 Sly Technologies, Inc. This library is free software; you
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
package org.jnetstream.packet.templates;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import org.jnetstream.packet.Header;
import org.jnetstream.packet.PacketInitializer;
import org.jnetstream.packet.PacketTemplate;
import org.jnetstream.packet.UpdateException;
import org.jnetstream.protocol.Protocol;
import org.jnetstream.protocol.ProtocolEntry;
import org.jnetstream.protocol.ProtocolInfo;
import org.jnetstream.protocol.codec.CodecCreateException;

import com.slytechs.utils.memory.BitBuffer;

/**
 * A packet template which initializes the packet with Ethernet, IPv4 and TCP
 * headers. Each header is then further initialized to hold the source addresses
 * of network interface #0 found on this system.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class TcpNic0Template implements PacketTemplate {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#format()
	 */
	public void format() throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getAllHeaders()
	 */
	public Header[] getAllHeaders() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getBuffer()
	 */
	public BitBuffer getBuffer() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getHeader(java.lang.Class)
	 */
	public <T extends Header> T getHeader(Class<T> c)
	    throws IllegalStateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getHeaderCount()
	 */
	public int getHeaderCount() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getIncludedLength()
	 */
	public long getIncludedLength() throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getOriginalLength()
	 */
	public long getOriginalLength() throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getTimestampNanos()
	 */
	public long getTimestampNanos() throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getTimestampSeconds()
	 */
	public long getTimestampSeconds() throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getUndecodedBuffer()
	 */
	public BitBuffer getData() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#hasCompleteHeader(java.lang.Class)
	 */
	public <T extends Header> boolean hasCompleteHeader(Class<T> c) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#hasHeader(java.lang.Class)
	 */
	public <T extends Header> boolean hasHeader(Class<T> c) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#isTruncated()
	 */
	public boolean isTruncated() throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#iterator()
	 */
	public Iterator<Header> iterator() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.PacketModifier#add(java.nio.ByteBuffer)
	 */
	public ByteBuffer add(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.PacketModifier#add(byte[])
	 */
	public ByteBuffer add(byte[] buffer) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.PacketModifier#add(java.nio.ByteBuffer,
	 *      java.lang.Class)
	 */
	public ByteBuffer add(ByteBuffer buffer, Class<? extends Header> c) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.PacketModifier#add(byte[], java.lang.Class)
	 */
	public ByteBuffer add(byte[] buffer, Class<? extends Header> c) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.PacketModifier#resize(int)
	 */
	public void resize(int size) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.PacketModifier#setTimestamp(long, int)
	 */
	public void setTimestamp(long seconds, int nanos)
	    throws IllegalArgumentException, IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.HeaderModifier#add(org.jnetstream.packet.Header)
	 */
	public <T extends Header> T add(T t) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.HeaderModifier#add(java.lang.Class)
	 */
	public <T extends Header> T add(Class<T> c) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.HeaderModifier#addAll(java.lang.Class,
	 *      java.lang.Class<?>[])
	 */
	public void addAll(Class<? extends Header> c1, Class<?>... c3) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.HeaderModifier#addAll(java.util.List)
	 */
	public void addAll(List<Class<? extends Header>> c) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.HeaderModifier#clearAll()
	 */
	public void clearAll() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.HeaderModifier#compact()
	 */
	public ByteBuffer compact() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.HeaderModifier#copy(java.nio.ByteBuffer)
	 */
	public ByteBuffer copy(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.HeaderModifier#copy(byte[])
	 */
	public ByteBuffer copy(byte[] buffer) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.HeaderModifier#insert(java.lang.Class,
	 *      java.lang.Class)
	 */
	public <T extends Header> T insert(Class<T> c,
	    Class<? extends Header> afterHeader) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.HeaderModifier#insert(java.lang.Class, int)
	 */
	public <T extends Header> T insert(Class<T> c, int afterIndex) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.HeaderModifier#remove(java.lang.Class)
	 */
	public <T extends Header> T remove(Class<T> c) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.HeaderModifier#remove(int)
	 */
	public Header remove(int index) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.HeaderModifier#removeAll()
	 */
	public Header[] removeAll() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.HeaderModifier#replace(java.nio.ByteBuffer)
	 */
	public ByteBuffer replace(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.HeaderModifier#replace(byte[])
	 */
	public ByteBuffer replace(byte[] buffer) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Updatable#update()
	 */
	public void update() throws IOException, UpdateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.PacketTemplate#init()
	 */
	public void init() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.HeaderModifier#init(org.jnetstream.packet.PacketInitializer)
	 */
	public void init(PacketInitializer initializer) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#free()
	 */
	public void free() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#add(java.lang.Class)
	 */
	public void add(Class<? extends Header> c1) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getHeader(org.jnetstream.protocol.Protocol)
	 */
	public <T extends Header> T getHeader(Protocol p)
	    throws IllegalStateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#hasCompleteHeader(org.jnetstream.protocol.Protocol)
	 */
	public <T extends Header> boolean hasCompleteHeader(ProtocolInfo<T> p)
	    throws CodecCreateException, IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#hasHeader(org.jnetstream.protocol.Protocol)
	 */
	public <T extends Header> boolean hasHeader(ProtocolEntry p)
	    throws CodecCreateException, IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
   * @see org.jnetstream.packet.Packet#getOffset(org.jnetstream.protocol.Protocol)
   */
  public int getOffset(ProtocolInfo<? extends Header> protocol) {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see org.jnetstream.packet.Packet#getProperty(org.jnetstream.packet.Packet.Property)
   */
  public Object getProperty(Property property) {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

}
