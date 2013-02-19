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

import java.io.Flushable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.TimeZone;

import org.jnetstream.capture.FileCapture;
import org.jnetstream.capture.FileFormatException;
import org.jnetstream.capture.file.Record;
import org.jnetstream.capture.file.RecordType;
import org.jnetstream.capture.file.pcap.PCAPRecordType;
import org.jnetstream.capture.file.pcap.PcapBlockRecord;
import org.jnetstream.capture.file.pcap.PcapDLT;
import org.jnetstream.capture.file.pcap.PcapFile;
import org.jnetstream.capture.file.pcap.PcapFormat;


import com.slytechs.capture.file.AbstractBlockRecord;
import com.slytechs.capture.file.editor.EditorHandle;
import com.slytechs.capture.file.editor.FileEditor;
import com.slytechs.utils.number.Version;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class PcapBlockRecordImpl
    extends AbstractBlockRecord implements PcapBlockRecord, Flushable {

	/**
	 * Adds a newly constructed block record to an empty file. The file must be
	 * completely empty with length of 0 bytes. The method initializes a header
	 * buffer for a Pcap block record and adds it to the file editor. The editor
	 * is not flushed so changes remain only in memory. If the editor changes are
	 * aborted before they are flushed, the newly created block record will be
	 * invalid if you call on its isValid method.
	 * 
	 * @param parent
	 *          main file capture
	 * @param editor
	 *          empty file editor
	 * @return new block record bound to the file capture
	 * @throws IOException
	 *           any IO errors
	 * @throws FileFormatException
	 */
	public static PcapBlockRecordImpl createBlock(final FileCapture parent,
	    final FileEditor editor) throws IOException, FileFormatException {
		if (editor.getLength() != 0) {
			throw new IllegalArgumentException("Editor is no empty."
			    + " Can only add new block record to completely empty file.");
		}

		final ByteBuffer b = ByteBuffer.allocate(PcapBlockRecord.HEADER_LENGTH);
		b.order(editor.order());

		PcapBlockRecordImpl.initBuffer(b);

		/*
		 * Add our new block record at byte offset 0 to the editor.
		 */
		editor.add(b, 0);

		final PcapBlockRecordImpl block = new PcapBlockRecordImpl(parent, editor);

		return block;
	}

	/**
	 * Initializes a buffer statically to default values for a Pcap block header
	 * 
	 * @param header
	 *          buffer to initialize to default values
	 */
	public static void initBuffer(final ByteBuffer header) {
		final int offset = header.position();

		if (header.order() == ByteOrder.BIG_ENDIAN) {
			PcapBlockHeader.Magicnumber.write(header, offset,
			    PcapFile.MAGIC_PATTERN_BE);
		} else {
			PcapBlockHeader.Magicnumber.write(header, offset,
			    PcapFile.MAGIC_PATTERN_LE);
		}

		PcapBlockHeader.MajorVersion.write(header, offset, PcapFile.MAJOR_VERSION);
		PcapBlockHeader.MinorVersion.write(header, offset, PcapFile.MINOR_VERSION);
		PcapBlockHeader.Snaplen.write(header, offset, PcapFormat.DEFAULT_SNAPLEN);
		PcapBlockHeader.Accuracy.write(header, offset, PcapFormat.DEFAULT_ACCURACY);
		PcapBlockHeader.Timezone.write(header, offset, (long) TimeZone.getDefault()
		    .getRawOffset());
		PcapBlockHeader.Linktype.write(header, offset, PcapDLT.EN10.intValue());
	}

	public PcapBlockRecordImpl(final FileCapture file, final FileEditor editor)
	    throws IOException, FileFormatException {
		this(file, editor, editor.generateHandle(0));

	}

	public PcapBlockRecordImpl(final FileCapture file, final FileEditor editor,
	    final EditorHandle handle) throws IOException, FileFormatException {
		super(file, editor, handle, PcapFile.headerReader);

		final ByteBuffer b = this.getRecordBuffer();
		final ByteOrder order = this.determineByteOrder(b);
		editor.order(order);

	}
	
  public PcapBlockRecordImpl(ByteBuffer buffer, long position) {
	  super(buffer, position);
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

	/**
	 * @throws IOException
	 * @throws FileFormatException
	 */
	private ByteOrder determineByteOrder(final ByteBuffer header)
	    throws FileFormatException {

		if (header.get(0) == PcapFile.MAGIC_PATTERN_BE[0]) {
			return ByteOrder.BIG_ENDIAN;
			
		} else if (header.get(0) == PcapFile.MAGIC_PATTERN_LE[0]) {
			return ByteOrder.LITTLE_ENDIAN;
			
		} else {
			throw new FileFormatException("Invalid PCAP block header magic number");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Flushable#flush()
	 */
	public void flush() throws IOException {
		this.editor.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#getAccuracy()
	 */
	public long getAccuracy() throws IOException {
		return (Long) PcapBlockRecord.PcapBlockHeader.Accuracy.read(
		    this.getRecordBuffer(), this.offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.BlockRecord#getDataRecordCount()
	 */
	public long getDataRecordCount() throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.Record#getRecordLength()
	 */
	public long getLength() {
		return this.editor.getLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#getLinktype()
	 */
	public long getLinktype() throws IOException {
		return (Long) PcapBlockRecord.PcapBlockHeader.Linktype.read(
		    this.getRecordBuffer(), this.offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#getMagicPattern()
	 */
	public byte[] getMagicPattern() throws IOException {
		return (byte[]) PcapBlockRecord.PcapBlockHeader.Magicnumber.read(this
		    .getRecordBuffer(), this.offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#getMajorVersion()
	 */
	public long getMajorVersion() throws IOException {
		return (Long) PcapBlockRecord.PcapBlockHeader.MajorVersion.read(this
		    .getRecordBuffer(), this.offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#getMinorVersion()
	 */
	public int getMinorVersion() throws IOException {
		return (Integer) PcapBlockRecord.PcapBlockHeader.MinorVersion.read(this
		    .getRecordBuffer(), this.offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapRecord#getPcapRecordType()
	 */
	public PCAPRecordType getPcapRecordType() throws IOException {
		return PCAPRecordType.BlockRecord;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.Record#getRecordHeaderLength()
	 */
	@Override
	public int getRecordHeaderLength() {
		return PcapBlockRecord.HEADER_LENGTH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.Record#getRecordLength()
	 */
	@Override
	public long getRecordLength() throws IOException {
		return this.getRecordHeaderLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.Record#getRecordType()
	 */
	public RecordType getRecordType() {
		return RecordType.BlockRecord;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#getSnaplen()
	 */
	public long getSnaplen() throws IOException {
		return (Long) PcapBlockRecord.PcapBlockHeader.Snaplen.read(this.getRecordBuffer(),
		    this.offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#getTimeZone()
	 */
	public long getTimeZone() throws IOException {
		return (Long) PcapBlockRecord.PcapBlockHeader.Timezone.read(
		    this.getRecordBuffer(), this.offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.BlockRecord#getVersion()
	 */
	public Version getVersion() throws IOException {
		return new Version((int) this.getMajorVersion(), this.getMinorVersion());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.BlockRecord#getByteOrder()
	 */
	public ByteOrder order() {
		return this.editor.order();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#setAccuracy(int)
	 */
	public void setAccuracy(final long accuracy) throws IOException {
		PcapBlockRecord.PcapBlockHeader.Accuracy.write(this.getEditBuffer(),
		    this.offset, accuracy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#setLinktype(int)
	 */
	public void setLinktype(final long linktype) throws IOException {
		PcapBlockRecord.PcapBlockHeader.Linktype.write(this.getEditBuffer(),
		    this.offset, linktype);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#setMagicPattern(byte[])
	 */
	public void setMagicPattern(final byte[] magicNumber) throws IOException {
		PcapBlockRecord.PcapBlockHeader.Magicnumber.write(this.getEditBuffer(),
		    this.offset, magicNumber);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#setMajorVersion(int)
	 */
	public void setMajorVersion(final long majorVersion) throws IOException {
		PcapBlockRecord.PcapBlockHeader.MajorVersion.write(this.getEditBuffer(),
		    this.offset, majorVersion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#setMinorVersion(int)
	 */
	public void setMinorVersion(final int minorVersion) throws IOException {
		PcapBlockRecord.PcapBlockHeader.MinorVersion.write(this.getEditBuffer(),
		    this.offset, minorVersion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#setSnaplen(int)
	 */
	public void setSnaplen(final long snaplen) throws IOException {
		PcapBlockRecord.PcapBlockHeader.Snaplen.write(this.getEditBuffer(),
		    this.offset, snaplen);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#setTimezone(int)
	 */
	public void setTimezone(final long timezone) throws IOException {
		PcapBlockRecord.PcapBlockHeader.Timezone.write(this.getEditBuffer(),
		    this.offset, timezone);
	}

	@Override
	public String toString() {
		final StringBuilder b = new StringBuilder();

		b.append('[');
		try {
			b.append(this.getVersion()).append(',');

			b.append(this.getAccuracy()).append(',');
			b.append(this.getSnaplen()).append(',');
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		b.append(']');

		return b.toString();
	}

}
