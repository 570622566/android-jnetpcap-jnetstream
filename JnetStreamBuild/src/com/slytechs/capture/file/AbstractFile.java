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
package com.slytechs.capture.file;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Iterator;


import org.apache.commons.logging.Log;
import org.jnetstream.capture.CapturePacket;
import org.jnetstream.capture.CaptureType;
import org.jnetstream.capture.FileCapture;
import org.jnetstream.capture.FilePacket;
import org.jnetstream.capture.PacketIndexer;
import org.jnetstream.capture.PacketIterator;
import org.jnetstream.capture.file.BlockRecord;
import org.jnetstream.capture.file.HeaderReader;
import org.jnetstream.capture.file.PacketCounterModel;
import org.jnetstream.capture.file.RawIndexer;
import org.jnetstream.capture.file.RawIterator;
import org.jnetstream.capture.file.Record;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.capture.file.RecordIndexer;
import org.jnetstream.capture.file.RecordIterator;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;

import com.slytechs.capture.DefaultCaptureDevice;
import com.slytechs.capture.file.editor.FileEditor;
import com.slytechs.capture.file.indexer.PacketIndexerImpl;
import com.slytechs.capture.file.indexer.PositionIndexer;
import com.slytechs.capture.file.indexer.RawIndexerImpl;
import com.slytechs.capture.file.indexer.RecordIndexerImpl;
import com.slytechs.capture.file.indexer.RecordPositionIndexer;
import com.slytechs.utils.collection.IOIterator;
import com.slytechs.utils.collection.IOIterator.IteratorAdapter;
import com.slytechs.utils.io.IORuntimeException;
import com.slytechs.utils.number.Version;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class AbstractFile<T extends FilePacket, R extends Record, B extends BlockRecord>
    implements FileCapture<T>, RawIteratorBuilder {
	
	protected B block;

	private DefaultCaptureDevice captureDevice;

	public FileEditor editor;

	protected final Filter<ProtocolFilterTarget> filter;

	private final HeaderReader headerReader;

	private SoftReference<PositionIndexer> indexer;

	@SuppressWarnings("unused")
  private final Log logger;

	public long packetCount = -1;

	/**
	 * @param logger TODO
	 * @param filter
	 */
	public AbstractFile(Log logger,
	    Filter<ProtocolFilterTarget> filter, HeaderReader headerReader) {
		this.logger = logger;
		this.filter = filter;
		this.headerReader = headerReader;
	}

	public void abortChanges() throws IOException {
		editor.abortChanges();
	}

	public void close() throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug(editor.getFlexRegion().toString());
		}

		editor.close();
	}

	/**
	 * Counts all packets very fast using RecordChannelReader parser.
	 * 
	 * @return number of packet records within the channel
	 * @throws IOException
	 *           any IO errors
	 */
	public long countPackets() throws IOException {

		PacketIterator<? extends CapturePacket> i = getPacketIterator();
		long count = 0;

		long old = 0;
		while (i.hasNext()) {
			old = i.getPosition();
			i.skip();

			if (old == i.getPosition()) {
				throw new IOException("0 length record at [" + old
				    + "]. Unable to advance to the next record.");

			}

			count++;
		}

		return count;
	}
	
	protected abstract PacketIterator<T> createPacketIterator(
	    final RawIterator raw) throws IOException;

	public abstract RawIterator createRawIterator(
	    Filter<RecordFilterTarget> filter) throws IOException;

	public void flush() throws IOException {
		editor.flush();
	}

	public B getBlockRecord() {
		return this.block;
	}

	public final DefaultCaptureDevice getCaptureDevice() {
  	return this.captureDevice;
  }

	public File getFile() {
		return editor.getFile();
	}

	/**
	 * @return
	 */
	public FileEditor getFileEditor() {
		return this.editor;
	}

	public Filter<ProtocolFilterTarget> getFilter() {
		return filter;
	}

	public long getLength() {
		return this.editor.getLength();
	}

	public long getPacketCount() throws IOException {
		if (packetCount == -1) {
			packetCount = countPackets();
		}

		return packetCount;
	}

	public long getPacketCount(PacketCounterModel model) throws IOException {

		switch (model) {
			case RealCount:
				return getPacketCount();

			default:
				throw new UnsupportedOperationException("Model not implemented yet");

		}
	}

	public PacketIndexer<T> getPacketIndexer() throws IOException {

		final PositionIndexer indexer = this.getPositionIndexer();
		final PacketIterator<T> packets = this.getPacketIterator();

		return new PacketIndexerImpl<T>(indexer, packets);
	}

	public PacketIterator<T> getPacketIterator() throws IOException {

		final RawIterator raw;
		if (filter != null) {
			final Filter<RecordFilterTarget> filter = headerReader.asRecordFilter(
			    this.filter, getCaptureDevice().getLinkType());

			raw = getRawIterator(filter);

		} else {
			raw = getRawIterator();
		}

		return createPacketIterator(raw);
	}

	/**
	 * Caches an indexer and creates a new instance if one is not cached.
	 * 
	 * @return position indexer for all records
	 * @throws IOException
	 *           any IO errors
	 */
	public PositionIndexer getPositionIndexer() throws IOException {
		if (this.indexer == null || this.indexer.get() == null) {
			this.indexer = new SoftReference<PositionIndexer>(
			    new RecordPositionIndexer(this.editor.getFlexRegion()));
		}

		return this.indexer.get();
	}

	public RawIndexer getRawIndexer() throws IOException {
		final PositionIndexer indexer = this.getPositionIndexer();
		final RawIterator raw = this.getRawIterator();

		return new RawIndexerImpl(raw, indexer);
	}

	public RawIterator getRawIterator() throws IOException {
		return getRawIterator(null);
	}

	public RawIterator getRawIterator(Filter<RecordFilterTarget> filter)
	    throws IOException {

		return createRawIterator(filter);
	}

	@SuppressWarnings("unchecked")
	public RecordIndexer<R> getRecordIndexer() throws IOException {

		final PositionIndexer indexer = this.getPositionIndexer();
		RecordIterator<R> records = (RecordIterator<R>) this.getRecordIterator();

		return new RecordIndexerImpl<R>(indexer, records);
	}

	public CaptureType getType() {
		return CaptureType.FileCapture;
	}

	public Version getVersion() throws IOException {
		return this.block.getVersion();
	}

	public boolean isEmpty() throws IOException {
		return editor.getLength() <= 24;
	}

	public boolean isMutable() {
		return editor.isMutable();
	}

	public boolean isOpen() {
		return editor.isOpen();
	}

	public Iterator<T> iterator() {
		try {
			final IOIterator<T> i = this.getPacketIterator();
			return new IteratorAdapter<T>(i);

		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
   * @param device
   */
  protected void setCaptureDevice(DefaultCaptureDevice device) {
    this.captureDevice = device;
  }
  
  public String toString() {
  	return editor.getFlexRegion().toString();
  }

}
