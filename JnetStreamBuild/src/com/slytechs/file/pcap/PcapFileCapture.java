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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;

import org.jnetstream.capture.FileFormatException;
import org.jnetstream.capture.FileMode;
import org.jnetstream.capture.FormatType;
import org.jnetstream.capture.PacketIterator;
import org.jnetstream.capture.file.HeaderReader;
import org.jnetstream.capture.file.RawIterator;
import org.jnetstream.capture.file.Record;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.capture.file.RecordIterator;
import org.jnetstream.capture.file.pcap.PcapBlockRecord;
import org.jnetstream.capture.file.pcap.PcapDLT;
import org.jnetstream.capture.file.pcap.PcapFile;
import org.jnetstream.capture.file.pcap.PcapPacket;
import org.jnetstream.capture.file.pcap.PcapRecord;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;
import org.jnetstream.protocol.Protocol;

import com.slytechs.capture.DefaultCaptureDevice;
import com.slytechs.capture.file.AbstractFile;
import com.slytechs.capture.file.RawIteratorBuilder;
import com.slytechs.capture.file.editor.FileEditor;
import com.slytechs.capture.file.editor.FileEditorImpl;
import com.slytechs.utils.collection.IOSkippableIterator;
import com.slytechs.utils.number.Version;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class PcapFileCapture
    extends AbstractFile<PcapPacket, PcapRecord, PcapBlockRecord> implements
    PcapFile {

	private static HeaderReader headerReader = PcapFile.headerReader;

	/**
	 * Creates a new file by creating an empty file, then writting a block header
	 * into it and then closes the created file. The file has to be reopened as a
	 * normal file if you need access to its contents.
	 * 
	 * @param file
	 *          file to create
	 * @param mode
	 *          TODO
	 * @param order
	 *          byte ordering for all the headers within the file
	 * @param filter
	 *          TODO
	 * @throws FileNotFoundException
	 *           unable to find parent directory inorder to create a new file
	 * @throws IOException
	 *           any IO errors
	 * @throws FileFormatException
	 */
	public static PcapFile createFile(final File file, final FileMode mode,
	    final ByteOrder order, Filter<ProtocolFilterTarget> filter)
	    throws FileNotFoundException, IOException, FileFormatException {

		// Create empty file?
		if (file.createNewFile() == false) {
			throw new FileNotFoundException("Unable to create new file ["
			    + file.getName() + "]");
		}

		PcapFile capture = new PcapFileCapture(FileMode.ReadWrite, filter);
		final FileEditor editor = new FileEditorImpl(file, FileMode.ReadWrite,
		    headerReader, order, filter, (RawIteratorBuilder) capture);

		PcapBlockRecordImpl.createBlock(capture, editor);

		editor.close(); // Flush and close

		capture = new PcapFileCapture(file, mode, filter);

		if (logger.isDebugEnabled()) {
			logger.debug(file.getName() + ", mode=" + mode + ", order=" + order
			    + (filter == null ? "" : ", filter=" + filter));
		}

		return capture;
	}

	private final FileMode mode;

	public PcapFileCapture(final File f, final FileMode mode,
	    Filter<ProtocolFilterTarget> filter) throws FileFormatException,
	    IOException {
		super(logger, filter, headerReader);
		
		if (logger.isDebugEnabled()) {
			logger.debug(f.getName() + ", mode=" + mode
			    + (filter == null ? "" : filter));
		}

		this.mode = mode;

		this.openFile(f, filter);
	}

	/**
	 * @param filter
	 *          TODO
	 * @param read_only
	 */
	public PcapFileCapture(final FileMode mode,
	    Filter<ProtocolFilterTarget> filter) {
		super(logger, filter, headerReader);
		
		

		this.mode = mode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.FileCapture#getBlockIterator()
	 */
	public IOSkippableIterator<PcapBlockRecord> getBlockIterator()
	    throws IOException {
		final PcapBlockRecord parentsBlock = this.block;

		return new IOSkippableIterator<PcapBlockRecord>() {

			private PcapBlockRecord block = null;

			public boolean hasNext() throws IOException {
				return this.block == null;
			}

			public PcapBlockRecord next() throws IOException {
				this.block = parentsBlock;

				return this.block;
			}

			public void remove() throws IOException {
				throw new UnsupportedOperationException(
				    "Pcap file format does not allow block record to be removed");
			}

			public void skip() throws IOException {
				this.block = parentsBlock;
			}

		};
	}
	
	public Protocol getDlt() throws IOException {
		return PcapDLT.asConst(this.block.getLinktype());
  }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.FileCapture#getFileType()
	 */
	public FormatType getFormatType() {
		return FormatType.Pcap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapFile#getRecordIterator()
	 */
	public RecordIterator<PcapRecord> getRecordIterator() throws IOException {

		return new PcapRecordIterator<PcapRecord>(this, this.editor, this.block,
		    this.getRawIterator());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.FileCapture#getVersion()
	 */
	public Version getVersion() throws IOException {
		return this.block.getVersion();
	}

	private void openFile(final File file,
	    Filter<ProtocolFilterTarget> protocolFilter) throws IOException,
	    FileFormatException {

		if (file.canRead() == false) {
			throw new FileNotFoundException("File [" + file.getName()
			    + "] is not readable, can not open in [" + this.mode.toString()
			    + "]mode");
		}

		if (this.mode.isAppend() || this.mode.isContent()
		    || this.mode.isStructure()) {
			if (file.canWrite() == false) {
				throw new FileNotFoundException("File [" + file.getName()
				    + "] is readonly, can not open in read-write mode");
			}
		}

		this.editor = new FileEditorImpl(file, this.mode, headerReader,
		    ByteOrder.BIG_ENDIAN, protocolFilter, this);
		try {
			this.block = new PcapBlockRecordImpl(this, this.editor);
			editor.order(block.order());
		} finally {
			if (this.block == null) {
				/*
				 * Make sure to close the editor in case any errors occured, otherwise
				 * we could keep the file open until the VM terminates
				 */
				this.editor.close();
			}
		}

		final Protocol dlt = PcapDLT.asConst(block.getLinktype());
		setCaptureDevice(new DefaultCaptureDevice(dlt));

		if (logger.isDebugEnabled()) {
			logger.debug("editor=" + editor.getFlexRegion().toString());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.FileCapture#order()
	 */
	public ByteOrder order() {
		return this.block.order();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapFile#setBlockRecord(byte[], int, int,
	 *      org.jnetstream.capture.file.pcap.PcapDLT, int, int, int)
	 */
	public PcapBlockRecord setBlockRecord(final byte[] m, final int major,
	    final int minor, final PcapDLT dlt, final int a, final int tz, final int i)
	    throws IOException, IllegalStateException {

		this.block.setLinktype(dlt.intValue());
		this.block.setMajorVersion(major);
		this.block.setMinorVersion(minor);
		this.block.setAccuracy(a);
		this.block.setTimezone(tz);
		this.block.setSnaplen(i);

		return this.block;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.pcap.PcapFile#setBlockRecord(org.jnetstream.capture.file.pcap.PcapDLT,
	 *      int)
	 */
	public PcapBlockRecord setBlockRecord(final PcapDLT dlt, final int i)
	    throws IOException, IllegalStateException {

		this.block.setLinktype(dlt.intValue());
		this.block.setSnaplen(i);

		return this.block;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.FileCapture#getRecordIterator(org.jnetstream.filter.Filter)
	 */
	public RecordIterator<? extends Record> getRecordIterator(
	    Filter<RecordFilterTarget> filter) throws IOException {
		return new PcapRecordIterator<PcapRecord>(this, editor, block,
		    getRawIterator(filter));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.AbstractFile#createPacketIterator(org.jnetstream.capture.file.RawIterator)
	 */
	@Override
	protected PacketIterator<PcapPacket> createPacketIterator(RawIterator raw)
	    throws IOException {
		return new PcapPacketIterator(this.editor, this.block, raw,
		    getCaptureDevice());

	}

	public RawIterator createRawIterator(Filter<RecordFilterTarget> filter)
	    throws IOException {

		return new PcapRawIterator(editor.getFlexRegion(), editor, this, filter);
	}
}
