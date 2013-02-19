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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.jnetstream.capture.FileCapture;
import org.jnetstream.capture.FileFormatException;
import org.jnetstream.capture.file.BlockRecord;
import org.jnetstream.capture.file.DataRecord;
import org.jnetstream.capture.file.Record;
import org.jnetstream.capture.file.RecordType;
import org.jnetstream.capture.file.snoop.SnoopPacketRecord;
import org.jnetstream.capture.file.snoop.SnoopRecordType;


import com.slytechs.capture.file.AbstractDataRecord;
import com.slytechs.capture.file.editor.EditorHandle;
import com.slytechs.capture.file.editor.FileEditor;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public final class SnoopPacketRecordImpl
    extends AbstractDataRecord implements SnoopPacketRecord {

	public static ByteBuffer createBuffer(final ByteBuffer data) {

		final int included = data.limit() - data.position();
		final int original = included;
		final int record = included + SnoopPacketRecord.HEADER_LENGTH;
		final int drops = 0;
		final long millis = System.currentTimeMillis();
		final long seconds = millis / 1000;
		final int fraction = (int) (millis % 1000);
		final int micros = fraction * 1000;

		final ByteBuffer b = ByteBuffer.allocate(SnoopPacketRecord.HEADER_LENGTH
		    + included);

		SnoopPacketRecordImpl.initBuffer(b, included, original, record, drops,
		    seconds, micros);

		b.position(SnoopPacketRecord.HEADER_LENGTH);
		b.put(data);
		b.clear();

		return b;
	}

	public static ByteBuffer createBuffer(final int length) {

		final ByteBuffer data = ByteBuffer.allocate(SnoopPacketRecord.HEADER_LENGTH
		    + length);
		data.order(ByteOrder.BIG_ENDIAN);
		final int included = length;
		final int original = included;
		final int record = included + SnoopPacketRecord.HEADER_LENGTH;
		final int drops = 0;
		final long millis = System.currentTimeMillis();
		final long seconds = millis / 1000;
		final int fraction = (int) (millis % 1000);
		final int micros = fraction * 1000;

		SnoopPacketRecordImpl.initBuffer(data, included, original, record, drops,
		    seconds, micros);

		data.clear();

		return data;
	}

	public static void initBuffer(final ByteBuffer b, final int included,
	    final int original, final int record, final int drops, final long seconds,
	    final int micros) {
		final int p = b.position();

		SnoopPacketHeader.IncludedLength.write(b, p, included);
		SnoopPacketHeader.OriginalLength.write(b, p, original);
		SnoopPacketHeader.RecordLength.write(b, p, record);
		SnoopPacketHeader.Drops.write(b, p, drops);
		SnoopPacketHeader.Seconds.write(b, p, (int) seconds);
		SnoopPacketHeader.Microseconds.write(b, p, micros);
	}

	private final BlockRecord block;

	/**
	 * @param file
	 * @param block
	 * @param editor
	 * @param handle
	 */
	public SnoopPacketRecordImpl(final FileCapture file, final BlockRecord block,
	    final FileEditor editor, final EditorHandle handle) {
		super(file, editor, handle);

		this.block = block;
	}

	/**
   * @param buffer
   * @param position
   */
  public SnoopPacketRecordImpl(BlockRecord block, ByteBuffer buffer, long position) {
  	super(buffer, position);
		this.block = block;
  }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.DataRecord#asType(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <C extends DataRecord> C asType(final Class<C> c) throws FileFormatException {
		if (c == SnoopPacketRecord.class) {
			return (C) this;
		}

		throw new IllegalArgumentException("Invalid class for this record type");
	}

	/* (non-Javadoc)
   * @see org.jnetstream.capture.file.Record#asType(java.lang.Class)
   */
  @SuppressWarnings("unchecked")
  public <T extends Record> T asType(final Class<T> c) {
	  return (T) this;
  }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.DataRecord#getBlockRecord()
	 */
	public BlockRecord getBlockRecord() {
		return this.block;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.snoop.SnoopPacketRecord#getDrops()
	 */
	public int getDrops() throws IOException {
		return (Integer) SnoopPacketHeader.Drops.read(this.getRecordBuffer(), this.offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.snoop.SnoopPacketRecord#getIncludedLength()
	 */
	public long getIncludedLength() throws IOException {
		return (Long) SnoopPacketHeader.IncludedLength.read(this.getRecordBuffer(), this.offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.snoop.SnoopPacketRecord#getMicroseconds()
	 */
	public long getMicroseconds() throws IOException {
		return (Long) SnoopPacketHeader.Microseconds.read(this.getRecordBuffer(), this.offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.snoop.SnoopPacketRecord#getOriginalLength()
	 */
	public long getOriginalLength() throws IOException {
		return (Long) SnoopPacketHeader.OriginalLength.read(this.getRecordBuffer(), this.offset);
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
	 * @see org.jnetstream.capture.file.snoop.SnoopPacketRecord#getSeconds()
	 */
	public long getSeconds() throws IOException {
		return (Long) SnoopPacketHeader.RecordLength.read(this.getRecordBuffer(), this.offset);
	}

	/* (non-Javadoc)
   * @see org.jnetstream.capture.file.snoop.SnoopRecord#getSnoopRecordType()
   */
  public SnoopRecordType getSnoopRecordType() throws IOException {
	  return SnoopRecordType.PacketRecord;
  }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.snoop.SnoopPacketRecord#setDrops(int)
	 */
	public void setDrops(final int drops) throws IOException {
		SnoopPacketHeader.Drops.write(this.getEditBuffer(), (long) drops);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.snoop.SnoopPacketRecord#setIncludedLength(int)
	 */
	public void setIncludedLength(final int length) throws IOException {
		SnoopPacketHeader.IncludedLength.write(this.getEditBuffer(), (long) length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.snoop.SnoopPacketRecord#setMicroseconds(long)
	 */
	public void setMicroseconds(final long micros) throws IOException {
		SnoopPacketHeader.Microseconds.write(this.getEditBuffer(), micros);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.snoop.SnoopPacketRecord#setOriginalLength(int)
	 */
	public void setOriginalLength(final int length) throws IOException {
		SnoopPacketHeader.OriginalLength.write(this.getEditBuffer(), (long) length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.snoop.SnoopPacketRecord#setRecordLength(int)
	 */
	public void setRecordLength(final int length) throws IOException {
		SnoopPacketHeader.RecordLength.write(this.getEditBuffer(), (long) length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.snoop.SnoopPacketRecord#setSeconds(long)
	 */
	public void setSeconds(final long seconds) throws IOException {
		SnoopPacketHeader.Seconds.write(this.getEditBuffer(), seconds);
	}

}
