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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;


import org.apache.commons.logging.Log;
import org.jnetstream.capture.FileFormatException;
import org.jnetstream.capture.FileMode;
import org.jnetstream.capture.FormatType;
import org.jnetstream.capture.PacketIterator;
import org.jnetstream.capture.file.HeaderReader;
import org.jnetstream.capture.file.RawIterator;
import org.jnetstream.capture.file.Record;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.capture.file.RecordIterator;
import org.jnetstream.capture.file.snoop.SnoopBlockRecord;
import org.jnetstream.capture.file.snoop.SnoopDLT;
import org.jnetstream.capture.file.snoop.SnoopFile;
import org.jnetstream.capture.file.snoop.SnoopPacket;
import org.jnetstream.capture.file.snoop.SnoopRecord;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;
import org.jnetstream.protocol.Protocol;

import com.slytechs.capture.DefaultCaptureDevice;
import com.slytechs.capture.file.AbstractFile;
import com.slytechs.capture.file.RawIteratorBuilder;
import com.slytechs.capture.file.editor.FileEditor;
import com.slytechs.capture.file.editor.FileEditorImpl;
import com.slytechs.utils.collection.IOSkippableIterator;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class SnoopFileCapture
    extends AbstractFile<SnoopPacket, SnoopRecord, SnoopBlockRecord> implements
    SnoopFile {

	private static final Log logger = SnoopFile.logger;

	/**
	 * 
	 */
	private static HeaderReader headerReader = SnoopFile.headerReader;

	/**
	 * Creates a new file by creating an empty file, then writting a block header
	 * into it and then closes the created file. The file has to be reopened as a
	 * normal file if you need access to its contents.
	 * 
	 * @param file
	 *          file to create
	 * @param mode
	 *          TODO
	 * @param filter
	 *          TODO
	 * @param order
	 *          byte ordering for all the headers within the file
	 * @throws FileNotFoundException
	 *           unable to find parent directory inorder to create a new file
	 * @throws IOException
	 *           any IO errors
	 * @throws FileFormatException
	 */
	public static SnoopFile createFile(final File file, final FileMode mode,
	    Filter<ProtocolFilterTarget> filter)
	    throws FileNotFoundException, IOException, FileFormatException {
		
		if (logger.isDebugEnabled()) {
			logger.debug(file.getName() + ", mode=" + mode
			    + (filter == null ? "" : filter));
		}


		// Create empty file?
		if (file.createNewFile() == false) {
			throw new FileNotFoundException("Unable to create new file ["
			    + file.getName() + "]");
		}

		SnoopFile capture = new SnoopFileCapture(FileMode.ReadWrite);

		/*
		 * Open up in READONLY MODE since file is empty anyway, nothing to override,
		 * we will append memory cache based segments and flush them out.
		 */
		final FileEditor editor = new FileEditorImpl(file, FileMode.ReadWrite,
		    headerReader, ByteOrder.BIG_ENDIAN, filter,
		    (RawIteratorBuilder) capture);

		SnoopBlockRecordImpl.createBlock(capture, editor);

		editor.close(); // Flush and close

		capture = new SnoopFileCapture(file, mode, null);

		return capture;
	}

	/**
	 * 
	 */
	private FileMode mode;

	/**
	 * @param f
	 * @param mode
	 * @param filter
	 * @throws FileFormatException
	 * @throws IOException
	 */
	public SnoopFileCapture(final File f, final FileMode mode,
	    Filter<ProtocolFilterTarget> filter) throws FileFormatException,
	    IOException {
		super(logger, filter, headerReader);

		this.mode = mode;

		if (logger.isDebugEnabled()) {
			logger.debug(f.getName() + ", mode=" + mode
			    + (filter == null ? "" : filter));
		}

		this.openFile(f, this.filter);
	}

	private SnoopFileCapture(final FileMode mode) {
		super(logger, null, headerReader);
		this.mode = mode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.FileCapture#getBlockIterator()
	 */
	public IOSkippableIterator<SnoopBlockRecord> getBlockIterator()
	    throws IOException {

		final SnoopBlockRecord parentsBlock = this.block;

		return new IOSkippableIterator<SnoopBlockRecord>() {

			private SnoopBlockRecord block = null;

			public boolean hasNext() throws IOException {
				return this.block == null;
			}

			public SnoopBlockRecord next() throws IOException {
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

	protected PacketIterator<SnoopPacket> createPacketIterator(
	    final RawIterator raw) throws IOException {
		return new SnoopPacketIterator(this.editor, this.block, raw,
		    getCaptureDevice());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.snoop.SnoopFile#getRecordIterator()
	 */
	public RecordIterator<SnoopRecord> getRecordIterator() throws IOException {
		return new SnoopRecordIterator(this, this.editor, this.block,
		    getRawIterator());
	}

	private void openFile(final File file, Filter<ProtocolFilterTarget> filter)
	    throws IOException, FileFormatException {

		if (file.canRead() == false) {
			throw new FileNotFoundException("File [" + file.getName()
			    + "] is not readable, can not open in [" + mode.toString() + "]mode");
		}

		if (mode.isAppend() || mode.isContent() || mode.isStructure()) {
			if (file.canWrite() == false) {
				throw new FileNotFoundException("File [" + file.getName()
				    + "] is readonly, can not open in read-write mode");
			}
		}

		this.editor = new FileEditorImpl(file, this.mode, headerReader,
		    ByteOrder.BIG_ENDIAN, filter, this);
		try {
			this.block = new SnoopBlockRecordImpl(this, this.editor);
		} finally {
			if (this.block == null) {
				/*
				 * Make sure to close the editor in case any errors occured, otherwise
				 * we could keep the file open until the VM terminates
				 */
				this.editor.close();
			}
		}

		final Protocol dlt = SnoopDLT.asConst(block.getLinktype());
		setCaptureDevice(new DefaultCaptureDevice(dlt));

		if (logger.isDebugEnabled()) {
			logger.debug("edito=" + editor.getFlexRegion().toString());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.FileCapture#order()
	 */
	public ByteOrder order() {
		return ByteOrder.BIG_ENDIAN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.FileCapture#getFileType()
	 */
	public FormatType getFormatType() {
		return FormatType.Snoop;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.FileCapture#getRecordIterator(org.jnetstream.filter.Filter)
	 */
	public RecordIterator<? extends Record> getRecordIterator(
	    Filter<RecordFilterTarget> filter) throws IOException {
		return new SnoopRecordIterator(this, editor, block, getRawIterator(filter));
	}

	public RawIterator createRawIterator(Filter<RecordFilterTarget> filter)
	    throws IOException {

		return new SnoopRawIterator(editor.getFlexRegion(), editor, this, filter);
	}

}
