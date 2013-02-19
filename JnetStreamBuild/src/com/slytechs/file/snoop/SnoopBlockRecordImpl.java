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

import java.io.Flushable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.jnetstream.capture.FileCapture;
import org.jnetstream.capture.FileFormatException;
import org.jnetstream.capture.file.Record;
import org.jnetstream.capture.file.RecordType;
import org.jnetstream.capture.file.snoop.SnoopBlockRecord;
import org.jnetstream.capture.file.snoop.SnoopDLT;
import org.jnetstream.capture.file.snoop.SnoopFile;
import org.jnetstream.capture.file.snoop.SnoopRecordType;


import com.slytechs.capture.file.AbstractBlockRecord;
import com.slytechs.capture.file.editor.EditorHandle;
import com.slytechs.capture.file.editor.FileEditor;
import com.slytechs.utils.number.Version;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class SnoopBlockRecordImpl
    extends AbstractBlockRecord implements SnoopBlockRecord, Flushable {

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
	public static SnoopBlockRecordImpl createBlock(final FileCapture parent,
	    final FileEditor editor) throws IOException, FileFormatException {
		if (editor.getLength() != 0) {
			throw new IllegalArgumentException("Editor is no empty."
			    + " Can only add new block record to completely empty file.");
		}

		final ByteBuffer b = ByteBuffer.allocate(SnoopBlockRecord.HEADER_LENGTH);
		b.order(editor.order());

		SnoopBlockRecordImpl.initBuffer(b);

		/*
		 * Add our new block record at byte offset 0 to the editor.
		 */
		editor.add(b, 0);

		final SnoopBlockRecordImpl block = new SnoopBlockRecordImpl(parent, editor);

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

		if (header.order() != ByteOrder.BIG_ENDIAN) {
			throw new IllegalArgumentException(
			    "SNOOP header's only support BIG_ENDIAN encoding. "
			        + "Buffer uses LITTLE_ENDIAN and is incompatible.");
		}

		SnoopBlockHeader.Magicnumber.write(header, offset,
		    SnoopBlockRecord.MAGIC_PATTERN);

		SnoopBlockHeader.MajorVersion.write(header, offset,
		    SnoopBlockRecord.MAJOR_VERSION);
		SnoopBlockHeader.Linktype.write(header, offset, SnoopDLT.Ethernet
		    .intValue());
	}

	public SnoopBlockRecordImpl(final FileCapture file, final FileEditor editor)
	    throws IOException, FileFormatException {
		this(file, editor, editor.generateHandle(0));

	}

	public SnoopBlockRecordImpl(final FileCapture file, final FileEditor editor,
	    final EditorHandle handle) throws IOException, FileFormatException {
		super(file, editor, handle, SnoopFile.headerReader);

		final ByteBuffer b = this.getRecordBuffer();
		final ByteOrder order = this.determineByteOrder(b);
		editor.order(order);

	}

	/**
   * @param buffer
   * @param position
   */
  public SnoopBlockRecordImpl(ByteBuffer buffer, long position) {
  	super(buffer, position);
  }

	/* (non-Javadoc)
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

		if (header.get(0) == SnoopBlockRecord.MAGIC_PATTERN[0]) {
			return ByteOrder.BIG_ENDIAN;
		} else {
			throw new FileFormatException("Invalid SNOOP block header magic number");
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
	 * @see org.jnetstream.capture.file.snoop.SnoopBlockRecord#getLinktype()
	 */
	public long getLinktype() throws IOException {
		return (Long) SnoopBlockHeader.Linktype.read(this.getRecordBuffer(),
		    this.offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.snoop.SnoopBlockRecord#getMagicNumber()
	 */
	public byte[] getMagicNumber() throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#getMagicPattern()
	 */
	public byte[] getMagicPattern() throws IOException {
		return SnoopBlockHeader.Magicnumber.read(this.getRecordBuffer(),
		    this.offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#getMajorVersion()
	 */
	public long getMajorVersion() throws IOException {
		return (Long) SnoopBlockHeader.MajorVersion.read(this.getRecordBuffer(),
		    this.offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.Record#getRecordHeaderLength()
	 */
	@Override
  public int getRecordHeaderLength() {
		return SnoopBlockRecord.HEADER_LENGTH;
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

	/* (non-Javadoc)
   * @see org.jnetstream.capture.file.snoop.SnoopRecord#getSnoopRecordType()
   */
  public SnoopRecordType getSnoopRecordType() throws IOException {
	  return SnoopRecordType.BlockRecord;
  }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.BlockRecord#getVersion()
	 */
	public Version getVersion() throws IOException {
		return new Version((int) this.getMajorVersion());
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
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#setLinktype(int)
	 */
	public void setLinktype(final long linktype) throws IOException {
		SnoopBlockHeader.Linktype.write(this.getEditBuffer(), this.offset,
		    linktype);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.snoop.SnoopBlockRecord#setMagicNumber(byte[])
	 */
	public void setMagicNumber(final byte[] magic) throws IOException {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapBlockRecord#setMagicPattern(byte[])
	 */
	public void setMagicPattern(final byte[] magicNumber) throws IOException {
		SnoopBlockHeader.Magicnumber.write(this.getEditBuffer(), this.offset,
		    magicNumber);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.snoop.SnoopBlockRecord#setMajorVersion(long)
	 */
	public void setMajorVersion(final long version) throws IOException {
		SnoopBlockHeader.MajorVersion.write(this.getEditBuffer(), this.offset,
		    version);
	}

	@Override
	public String toString() {
		final StringBuilder b = new StringBuilder();

		b.append('[');
		try {
			b.append(this.getVersion()).append(',');
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		b.append(']');

		return b.toString();
	}

}
