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
package com.slytechs.file.pcap;

import java.io.IOException;
import java.sql.Timestamp;

import org.jnetstream.capture.CaptureDevice;
import org.jnetstream.capture.file.pcap.PcapPacket;
import org.jnetstream.capture.file.pcap.PcapPacketRecord;
import org.jnetstream.packet.HeaderCache;
import org.jnetstream.packet.Packet;
import org.jnetstream.protocol.ProtocolEntry;

import com.slytechs.capture.file.editor.EditorHandle;
import com.slytechs.capture.file.editor.FileEditor;
import com.slytechs.jnetstream.packet.AFilePacket;
import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class PcapPacketImpl
    extends AFilePacket implements PcapPacket {

	// private final FileCapture file;

	/**
	 * @param handle
	 * @param protocol
	 *          TODO
	 * @throws IOException
	 */
	public PcapPacketImpl(final FileEditor editor, final EditorHandle handle,
	    ProtocolEntry protocol) throws IOException {
		super(protocol, editor, handle);

	}

	/**
	 * @param buffer
	 * @param position
	 * @param protocol
	 *          TODO
	 * @param lengthGetter
	 * @throws IOException
	 */
	public PcapPacketImpl(BitBuffer buffer, long position,
	    ProtocolEntry protocol) throws IOException {
		super(protocol, null, buffer, position);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getBuffer()
	 */
	public BitBuffer getBuffer() throws IOException {
		final BitBuffer buffer = getRecordBitBuffer();
		buffer.position(buffer.position() + PcapPacketRecord.HEADER_LENGTH * 8);

		return buffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CapturePacket#getIncludedLength()
	 */
	public long getIncludedLength() throws IOException {
		return (Long) PcapPacketRecord.PcapPacketHeader.IncludedLength.read(
		    getRecordByteBuffer(), offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CapturePacket#getOriginalLength()
	 */
	public long getOriginalLength() throws IOException {
		return (Long) PcapPacketRecord.PcapPacketHeader.OriginalLength.read(
		    getRecordByteBuffer(), offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.FilePacket#getRecordFilePosition()
	 */
	public long getPositionGlobal() throws IOException {
		return handle.getPositionGlobal();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.FilePacket#getLocalPosition()
	 */
	public int getPositionLocal() throws IOException {
		return (int) handle.getPositionLocal();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.FilePacket#getRegionalPosition()
	 */
	public long getPositionRegional() throws IOException {
		return handle.getPositionRegional();
	}

	public final int getRecordHeaderLength() {
		return PcapPacketRecord.HEADER_LENGTH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getTimestamp()
	 */
	public Timestamp getTimestamp() throws IOException {
		final long seconds = getTimestampSeconds();
		final long nanos = getTimestampNanos();
		final long millis = seconds * 1000 + nanos / 1000000;
		final Timestamp ts = new Timestamp(millis);

		// ts.setNanos((int) nanos);

		return ts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getTimestampNanos()
	 */
	public long getTimestampNanos() throws IOException {
		final long micros =
		    (Long) PcapPacketRecord.PcapPacketHeader.Microseconds.read(
		        getRecordByteBuffer(), offset);

		return micros * 1000; // convert to nanos
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getTimestampSeconds()
	 */
	public long getTimestampSeconds() throws IOException {
		return (Long) PcapPacketRecord.PcapPacketHeader.Seconds.read(
		    getRecordByteBuffer(), offset);
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
	 * @see org.jnetstream.capture.CapturePacket#setCaptureDevice(org.jnetstream.capture.CaptureDevice)
	 */
	public void setCaptureDevice(CaptureDevice device) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CapturePacket#setIncludedLength(int)
	 */
	public void setIncludedLength(int length) throws IOException {
		PcapPacketRecord.PcapPacketHeader.IncludedLength.write(getEditBuffer(),
		    offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CapturePacket#setOriginalLength(int)
	 */
	public void setOriginalLength(int length) throws IOException {
		PcapPacketRecord.PcapPacketHeader.OriginalLength.write(getEditBuffer(),
		    offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#setTimestamp(long, int)
	 */
	public void setTimestamp(long seconds, int nanos)
	    throws IllegalArgumentException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#setTimestamp(java.sql.Timestamp)
	 */
	public void setTimestamp(Timestamp time) throws IOException {
		final long millis = time.getTime();
		final long seconds = millis / 1000;
		final int micros = (int) (millis - seconds * 1000) + time.getNanos() / 1000;
		setTimestampSeconds(seconds);
		setTimestampMicros(micros);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapPacket#setTimestampMicros(int)
	 */
	public void setTimestampMicros(int micros) throws IOException {
		if (micros < 0 || micros > 999999) {
			throw new IllegalArgumentException(
			    "Microsecond fraction value out of range. "
			        + "Valid range is 0 to 999,999");
		}
		PcapPacketRecord.PcapPacketHeader.Microseconds.write(getEditBuffer(),
		    (long) micros);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapPacket#setTimestampSeconds(long)
	 */
	public void setTimestampSeconds(long seconds) throws IOException {
		if (seconds < 0 || seconds > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
			    "Timestamp seconds value out of valid range. "
			        + "Negative or greater than 2^31 are not allowed.");
		}
		PcapPacketRecord.PcapPacketHeader.Seconds.write(getEditBuffer(),
		    (long) seconds);
	}


	/* (non-Javadoc)
   * @see org.jnetstream.protocol.codec.PacketRuntime#getHeaderCache()
   */
  public HeaderCache getHeaderCache() {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see org.jnetstream.protocol.codec.PacketRuntime#getLastDecodedOffset()
   */
  public int getLastDecodedOffset() {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see org.jnetstream.protocol.codec.PacketRuntime#getSource()
   */
  public Packet getSource() {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see org.jnetstream.protocol.codec.PacketRuntime#isDecoded()
   */
  public boolean isDecoded() {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see org.jnetstream.protocol.codec.PacketRuntime#setLastDecodedOffset(int)
   */
  public void setLastDecodedOffset(int offset) {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see org.jnetstream.protocol.codec.CodecRuntime#getBitBuffer()
   */
  public BitBuffer getBitBuffer() throws IOException {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see org.jnetstream.protocol.codec.CodecRuntime#getOffset()
   */
  public int getOffset() {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }
}
