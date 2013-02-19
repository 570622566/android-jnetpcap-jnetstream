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
package com.slytechs.jnetstream.packet;

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
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Iterator;

import org.jnetstream.capture.CaptureDevice;
import org.jnetstream.capture.Captures;
import org.jnetstream.capture.DeserializedPacket;
import org.jnetstream.packet.Header;
import org.jnetstream.packet.Packet;
import org.jnetstream.packet.PacketInitializer;
import org.jnetstream.protocol.Protocol;
import org.jnetstream.protocol.ProtocolEntry;
import org.jnetstream.protocol.ProtocolInfo;
import org.jnetstream.protocol.ProtocolNotFoundException;
import org.jnetstream.protocol.codec.CodecCreateException;

import com.slytechs.utils.memory.BitBuffer;

/**
 * <P>
 * A class that is used to serialize CapturePacket data and deserialize into a
 * StreamPacket which implements the required fields after packet
 * deserialization.
 * </P>
 * <P>
 * Two new fields are serializedTimestamp and deserializedTimestamp which are
 * set by the serialization mechanism.
 * </P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
class DefaultDeserializedPacket implements DeserializedPacket, Serializable {

	private static final long serialVersionUID = 19823745234L;

	Timestamp serializedTimestamp;

	Timestamp deserializedTimestamp;

	CaptureDevice captureSystem;

	long includedPacketLength;

	long originalPacketLength;

	long timestampNanos;

	Timestamp timestamp;

	long timestampSeconds;

	/*
	 * ByteBuffer is not serializable, so we don't try and serial packetBuffer but
	 * copy its contents into a new byte array or extract the underlying array if
	 * buffer is backed by an array. The packetByteArray is what will be
	 * serialized and sent. On the receiving end just check if there is a buffer,
	 * which would not be deserialized so wrap the packetByteArray in buffer and
	 * return it.
	 */
	private transient BitBuffer packetBuffer;

	private byte[] packetByteArray;

	DefaultDeserializedPacket() {
		// Empty, needed for deserialization
	}

	DefaultDeserializedPacket(final Packet packet) throws IOException {
		// this.captureSystem = packet.getCaptureDevice();
		this.includedPacketLength = packet.getIncludedLength();
		this.originalPacketLength = packet.getOriginalLength();
		this.timestampSeconds = packet.getTimestampSeconds();
		this.timestampNanos = packet.getTimestampNanos();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.stream.StreamPacket#getSerializedTimestamp()
	 */
	public Timestamp getSerializedTimestamp() {
		return this.serializedTimestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.stream.StreamPacket#getDeserializedTimestamp()
	 */
	public Timestamp getDeserializedTimestamp() {
		return this.deserializedTimestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.CapturePacket#getCaptureSystem()
	 */
	public CaptureDevice getCaptureDevice() {
		return this.captureSystem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.CapturePacket#getPacketBuffer()
	 */
	public BitBuffer getBuffer() {
		if ((this.packetBuffer == null) && (this.packetByteArray == null)) {
			return null;
		}

		if (this.packetBuffer == null) {
			this.packetBuffer = BitBuffer.wrap(this.packetByteArray);
		}

		return this.packetBuffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.CapturePacket#getIncludedPacketLength()
	 */
	public long getIncludedLength() {
		return this.includedPacketLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.CapturePacket#getOriginalPacketLength()
	 */
	public long getOriginalLength() {
		return this.originalPacketLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.CapturePacket#getTimestampNanos()
	 */
	public long getTimestampNanos() {
		return this.timestampNanos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.CapturePacket#getTimestamp()
	 */
	public Timestamp getTimestamp() {
		if (this.timestamp == null) {
			this.timestamp = new Timestamp(this.getTimestampSeconds() * 1000);
			this.timestamp.setNanos((int) this.getTimestampNanos());
		}
		return this.timestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.CapturePacket#getTimestampSeconds()
	 */
	public long getTimestampSeconds() {
		return this.timestampSeconds;
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
	 * @see org.jnetstream.packet.Packet#getAllHeaders(java.lang.Class)
	 */
	public <T extends Header> T[] getAllHeaders(Class<T> c)
	    throws ProtocolNotFoundException {
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
	 * @see org.jnetstream.packet.Packet#getLastHeader(java.lang.Class)
	 */
	public <T extends Header> T getLastHeader(Class<T> c)
	    throws ProtocolNotFoundException, IllegalStateException {
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
	 * @see org.jnetstream.packet.Packet#hasCompleteLastHeader(java.lang.Class)
	 */
	public <T extends Header> boolean hasCompleteLastHeader(Class<T> c) {
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
	 * @see org.jnetstream.packet.Packet#hasLastHeader(java.lang.Class)
	 */
	public <T extends Header> boolean hasLastHeader(Class<T> c) {
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
	 * @see org.jnetstream.packet.Packet#remove(int, int)
	 */
	public void remove(int position, int length) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#remove(org.jnetstream.packet.Header)
	 */
	public void remove(Header header) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#update()
	 */
	public void update() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#format()
	 */
	public void format() throws IOException {
		Captures.defaultFormatter().format(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#isTruncated()
	 */
	public boolean isTruncated() {
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
		// Do nothing, allow the heap based packet to expire
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getOffset(org.jnetstream.protocol.Protocol)
	 */
	public int getOffset(ProtocolInfo<? extends Header> protocol) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
   * @see org.jnetstream.capture.DeserializedPacket#setDeserializedTimestamp(java.sql.Timestamp)
   */
  public void setDeserializedTimestamp(Timestamp timestamp) {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see org.jnetstream.capture.DeserializedPacket#setSerializedTimestamp(java.sql.Timestamp)
   */
  public void setSerializedTimestamp(Timestamp timestamp) {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see org.jnetstream.packet.Packet#getProperty(org.jnetstream.packet.Packet.Property)
   */
  public <A> A getProperty(Property property) {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }
}