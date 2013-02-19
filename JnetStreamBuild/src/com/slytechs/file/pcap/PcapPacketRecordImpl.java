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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.jnetstream.capture.FileCapture;
import org.jnetstream.capture.FileFormatException;
import org.jnetstream.capture.file.BlockRecord;
import org.jnetstream.capture.file.DataRecord;
import org.jnetstream.capture.file.Record;
import org.jnetstream.capture.file.RecordType;
import org.jnetstream.capture.file.pcap.PCAPRecordType;
import org.jnetstream.capture.file.pcap.PcapPacketRecord;


import com.slytechs.capture.file.AbstractDataRecord;
import com.slytechs.capture.file.AbstractRecord;
import com.slytechs.capture.file.editor.FileEditor;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class PcapPacketRecordImpl
    extends AbstractDataRecord implements PcapPacketRecord {

	/**
	 * @param file
	 * @param editor
	 * @param block
	 * @param position
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static AbstractRecord addPacket(final FileCapture file,
	    final FileEditor editor, final BlockRecord block, final long position,
	    final ByteBuffer data) throws IOException {

		final int included = data.limit() - data.position();
		final int original = included;
		final long millis = System.currentTimeMillis();
		final long seconds = millis / 1000;
		final int fraction = (int) (millis % 1000);
		final int micros = fraction * 1000;

		final ByteBuffer b = ByteBuffer.allocate(PcapPacketRecord.HEADER_LENGTH
		    + included);
		b.order(editor.order());

		PcapPacketRecordImpl.initBuffer(b, included, original, seconds, micros);

		b.position(PcapPacketRecord.HEADER_LENGTH);
		b.put(data);
		b.clear();

		editor.add(b, position);

		final AbstractRecord packet = new PcapPacketRecordImpl(file, editor, block,
		    position);

		return packet;
	}

	/**
	 * @return
	 */
	public static ByteBuffer createBuffer(final ByteBuffer data) {

		final int included = data.limit() - data.position();
		final int original = included;
		final long millis = System.currentTimeMillis();
		final long seconds = millis / 1000;
		final int fraction = (int) (millis % 1000);
		final int micros = fraction * 1000;

		final ByteBuffer b = ByteBuffer.allocate(PcapPacketRecord.HEADER_LENGTH
		    + included);

		PcapPacketRecordImpl.initBuffer(b, included, original, seconds, micros);

		b.position(PcapPacketRecord.HEADER_LENGTH);
		b.put(data);
		b.clear();

		return b;
	}

	/**
	 * @param i
	 * @return
	 */
	public static ByteBuffer createBuffer(final int length, final ByteOrder order) {

		final ByteBuffer data = ByteBuffer.allocate(PcapPacketRecord.HEADER_LENGTH
		    + length);
		data.order(order);
		final int included = length;
		final int original = included;
		final long millis = System.currentTimeMillis();
		final long seconds = millis / 1000;
		final int fraction = (int) (millis % 1000);
		final int micros = fraction * 1000;

		PcapPacketRecordImpl.initBuffer(data, included, original, seconds, micros);

		data.clear();

		return data;
	}

	/**
	 * @param b
	 * @param included
	 * @param original
	 * @param seconds
	 * @param micros
	 */
	public static void initBuffer(final ByteBuffer b, final int included,
	    final int original, final long seconds, final int micros) {
		final int p = b.position();

		PcapPacketRecord.PcapPacketHeader.IncludedLength.write(b, p, included);
		PcapPacketRecord.PcapPacketHeader.OriginalLength.write(b, p, original);
		PcapPacketRecord.PcapPacketHeader.Seconds.write(b, p, (int) seconds);
		PcapPacketRecord.PcapPacketHeader.Microseconds.write(b, p, micros);
	}

	protected final BlockRecord block;

	/**
	 * @param file
	 * @param editor
	 * @param block
	 * @param position
	 */
	public PcapPacketRecordImpl(final FileCapture file, final FileEditor editor,
	    final BlockRecord block, final long position) {
		super(file, editor, editor.generateHandle(position));
		this.block = block;
	}

	public PcapPacketRecordImpl(ByteBuffer buffer, long position,
	    final BlockRecord block) {
		super(buffer, position);

		this.block = block;
	}

	/**
	 * @param block
	 * @param buffer
	 * @param position
	 */
	public PcapPacketRecordImpl(PcapBlockRecordImpl block, ByteBuffer buffer,
	    long position) {
		super(buffer, position);

		this.block = block;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.DataRecord#asType(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <C extends DataRecord> C asType(final Class<C> c)
	    throws FileFormatException {
		if (c == PcapPacketRecord.class) {
			return (C) this;
		}

		throw new IllegalArgumentException(
		    "Invalid record class. Record type must be PcapPacketRecord.class");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.Record#asType(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <T extends Record> T asType(final Class<T> c) {
		return (T) this;
	}

	public BlockRecord getBlockRecord() {
		return this.block;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapPacketRecord#getIncludedLength()
	 */
	public long getIncludedLength() throws IOException {
		return (Long) PcapPacketRecord.PcapPacketHeader.IncludedLength.read(this
		    .getRecordBuffer(), this.offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapPacketRecord#getMicroseconds()
	 */
	public long getMicroseconds() throws IOException {
		return (Long) PcapPacketRecord.PcapPacketHeader.Microseconds.read(this
		    .getRecordBuffer(), this.offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapPacketRecord#getOriginalLength()
	 */
	public long getOriginalLength() throws IOException {
		return (Long) PcapPacketRecord.PcapPacketHeader.OriginalLength.read(this
		    .getRecordBuffer(), this.offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapRecord#getPcapRecordType()
	 */
	public PCAPRecordType getPcapRecordType() throws IOException {
		return PCAPRecordType.PacketRecord;
	}

	@Override
	public final int getRecordHeaderLength() {
		return PcapPacketRecord.HEADER_LENGTH;
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
	 * @see org.jnetstream.capture.file.pcap.PcapPacketRecord#getSeconds()
	 */
	public long getSeconds() throws IOException {
		return (Long) PcapPacketRecord.PcapPacketHeader.Seconds.read(this
		    .getRecordBuffer(), this.offset);
	}

	/**
	 * @return
	 */
	public final int getStartLocal() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapPacketRecord#setIncludedLength(int)
	 */
	public void setIncludedLength(final int snaplen) throws IOException {
		PcapPacketRecord.PcapPacketHeader.IncludedLength.write(
		    this.getEditBuffer(), this.offset, snaplen);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapPacketRecord#setMicroseconds(long)
	 */
	public void setMicroseconds(final long micros) throws IOException {
		PcapPacketRecord.PcapPacketHeader.Microseconds.write(this.getEditBuffer(),
		    this.offset, micros);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapPacketRecord#setOrignalLength(int)
	 */
	public void setOrignalLength(final int length) throws IOException {
		PcapPacketRecord.PcapPacketHeader.OriginalLength.write(
		    this.getEditBuffer(), this.offset, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapPacketRecord#setSeconds(long)
	 */
	public void setSeconds(final long seconds) throws IOException {
		PcapPacketRecord.PcapPacketHeader.Seconds.write(this.getEditBuffer(),
		    this.offset, seconds);
	}

}
