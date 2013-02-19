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
package com.slytechs.file.snoop;

import java.io.IOException;
import java.sql.Timestamp;

import org.jnetstream.capture.CaptureDevice;
import org.jnetstream.capture.file.RecordType;
import org.jnetstream.capture.file.snoop.SnoopPacket;
import org.jnetstream.capture.file.snoop.SnoopPacketRecord;
import org.jnetstream.capture.file.snoop.SnoopPacketRecord.SnoopPacketHeader;
import org.jnetstream.packet.Header;
import org.jnetstream.protocol.ProtocolEntry;
import org.jnetstream.protocol.ProtocolNotFoundException;

import com.slytechs.capture.file.editor.EditorHandle;
import com.slytechs.capture.file.editor.FileEditor;
import com.slytechs.jnetstream.packet.AFilePacket;
import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class SnoopPacketImpl
    extends AFilePacket implements SnoopPacket {

	/**
	 * @param editor
	 * @param handle
	 * @param dlt
	 *          TODO
	 * @throws IOException
	 */
	public SnoopPacketImpl(FileEditor editor, EditorHandle handle,
	    ProtocolEntry dlt) throws IOException {
		super(dlt, editor, handle);
	}

	public SnoopPacketImpl(BitBuffer buffer, long position, ProtocolEntry dlt)
	    throws IOException {
		super(dlt, null, buffer, position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getBuffer()
	 */
	public BitBuffer getBuffer() throws IOException {
		final BitBuffer b = getRecordBitBuffer();
		b.position(b.position() + SnoopPacketRecord.HEADER_LENGTH * 8);

		return b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CapturePacket#getIncludedLength()
	 */
	public long getIncludedLength() throws IOException {
		return (Long) SnoopPacketHeader.IncludedLength.read(getRecordByteBuffer(), offset);
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
	 * @see org.jnetstream.capture.CapturePacket#getOriginalLength()
	 */
	public long getOriginalLength() throws IOException {
		return (Long) SnoopPacketHeader.OriginalLength.read(getRecordByteBuffer(), offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.FilePacket#getPositionLocal()
	 */
	public int getPositionLocal() throws IOException {
		return (int) handle.getPositionLocal();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.FilePacket#getPositionRegional()
	 */
	public long getPositionRegional() throws IOException {
		return handle.getPositionRegional();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.AbstractRecord#getRecordHeaderLength()
	 */
	@Override
	public int getRecordHeaderLength() {
		return SnoopPacketRecord.HEADER_LENGTH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.Record#getRecordType()
	 */
	public RecordType getRecordType() {
		return RecordType.PacketRecord;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getTimestamp()
	 */
	public Timestamp getTimestamp() throws IOException {
		final long seconds = getTimestampSeconds();
		final long nanos = getTimestampNanos();
		final long millis = seconds * 1000 + nanos / 1000;

		return new Timestamp(millis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getTimestampNanos()
	 */
	public long getTimestampNanos() throws IOException {
		return 1000L * (Long) SnoopPacketHeader.Microseconds.read(
		    getRecordByteBuffer(), offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#getTimestampSeconds()
	 */
	public long getTimestampSeconds() throws IOException {
		return (Long) SnoopPacketHeader.Seconds.read(getRecordByteBuffer(), offset);
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
		SnoopPacketHeader.IncludedLength.write(getEditBuffer(), offset,
		    (long) length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.CapturePacket#setOriginalLength(int)
	 */
	public void setOriginalLength(int length) throws IOException {
		SnoopPacketHeader.OriginalLength.write(getEditBuffer(), offset,
		    (long) length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#setTimestamp(long, int)
	 */
	public void setTimestamp(long seconds, int nanos)
	    throws IllegalArgumentException, IOException {

		if (nanos < 0 || nanos > 999999999) {
			throw new IllegalArgumentException(
			    "Nanos value out of range for timestamp fraction. "
			        + "Valid values are 0 to 999,999,999");
		}

		final long micros = nanos / 1000;

		SnoopPacketHeader.Seconds.write(getEditBuffer(), offset, micros);
		SnoopPacketHeader.Microseconds.write(getEditBuffer(), offset, micros);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#setTimestamp(java.sql.Timestamp)
	 */
	public void setTimestamp(Timestamp time) throws IOException {
		final long millis = time.getTime();
		final int nanos = time.getNanos();

		final long seconds = millis / 1000;

		setTimestamp(seconds, nanos);
	}

}
