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
package org.jnetstream.capture.file.snoop;

import java.io.IOException;
import java.nio.ByteBuffer;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jnetstream.capture.FileCapture;
import org.jnetstream.capture.PacketIndexer;
import org.jnetstream.capture.PacketIterator;
import org.jnetstream.capture.file.HeaderReader;
import org.jnetstream.capture.file.RawIndexer;
import org.jnetstream.capture.file.RawIterator;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.capture.file.RecordIndexer;
import org.jnetstream.capture.file.RecordIterator;
import org.jnetstream.capture.file.RecordType;
import org.jnetstream.capture.file.snoop.SnoopPacketRecord.SnoopPacketHeader;
import org.jnetstream.filter.Filter;
import org.jnetstream.filter.FilterException;
import org.jnetstream.packet.ProtocolFilterTarget;

import com.slytechs.utils.collection.IOSkippableIterator;

/**
 * SNOOP capture file format from SUN Microsystem's folks.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface SnoopFile extends FileCapture<SnoopPacket> {
	public static final int HEADER_LENGTH = 16;

	/**
	 * Magic pattern made up of octets that uniquely identifies the "snoop" block
	 * record and the entire file as snoop file.
	 * <pre>
	 * 73 6e 6f 6f 70 00 00 00 
	 *  S  N  O  O  P '' '' ''
	 * </pre>
	 */
	public static final byte[] MAGIC_PATTERN = { (byte) 0x73, (byte) 0x6e,
	    (byte) 0x6f, (byte) 0x6f, (byte) 0x70, 0, 0, 0 };

	public static final HeaderReader headerReader = new HeaderReader() {
		
		public String toString() {
			return "[SnoopHeader, min=16]";
		}

		public int getMinLength() {
			return SnoopBlockRecord.HEADER_LENGTH;
		}

		public long readLength(ByteBuffer buffer) {
			final RecordType type = this.readType(buffer);

			switch (type) {
				case BlockRecord:
					return 16;

				case PacketRecord:
					final int offset = buffer.position();
					final long length = buffer.getInt(offset
					    + SnoopPacketHeader.RecordLength.getOffset());

					return length;

				default:
					throw new UnsupportedOperationException("Unsupported record type ["
					    + type + "]");
			}
		}

		public int getOffset() {
			return SnoopBlockRecord.HEADER_LENGTH;
		}

		public RecordType readType(ByteBuffer buffer) {
			final int offset = buffer.position();

			if ((buffer.get(offset + 0) == SnoopFile.MAGIC_PATTERN[0])
			    && (buffer.get(offset + 1) == SnoopFile.MAGIC_PATTERN[1])
			    && (buffer.get(offset + 2) == SnoopFile.MAGIC_PATTERN[2])
			    && (buffer.get(offset + 3) == SnoopFile.MAGIC_PATTERN[3])
			    && (buffer.get(offset + 4) == SnoopFile.MAGIC_PATTERN[4])
			    && (buffer.get(offset + 5) == SnoopFile.MAGIC_PATTERN[5])
			    && (buffer.get(offset + 6) == SnoopFile.MAGIC_PATTERN[6])
			    && (buffer.get(offset + 7) == SnoopFile.MAGIC_PATTERN[7])) {

				return RecordType.BlockRecord;

			} else {
				return RecordType.PacketRecord;
			}
		}

		public int getHeaderLength(ByteBuffer buffer) {
			final RecordType type = this.readType(buffer);

			switch (type) {
				case BlockRecord:
					return 16;

				case PacketRecord:
					return 24;

				default:
					throw new UnsupportedOperationException("Unsupported record type ["
					    + type + "]");
			}
		}
		
		public RecordFilterTarget readRecordFilterTarget(ByteBuffer buffer) {
	    final RecordType type = readType(buffer);
	    
	    switch (type) {
	    	case PacketRecord:
	    		return SnoopRecordType.PacketRecord;
	    		
	    	case BlockRecord:
	    		return SnoopRecordType.BlockRecord;
	    		
				default:
					throw new UnsupportedOperationException("Unsupported record type ["
					    + type + "]");
	    }
    }

		public Filter<RecordFilterTarget> asRecordFilter(
		    final Filter<ProtocolFilterTarget> filter, final ProtocolFilterTarget protocol) {
			return new Filter<RecordFilterTarget>() {

				public boolean accept(ByteBuffer buffer, RecordFilterTarget target)
				    throws FilterException {
					if (target != SnoopRecordType.PacketRecord) {
						return false;
					}
					
					/*
					 * Shift buffer position past the record's header to the beginning
					 * of packet data. This is the main part of the adapting process.
					 */
					final int p = buffer.position();
					buffer.position(p + SnoopPacketRecord.HEADER_LENGTH);
					
					final boolean r = filter.accept(buffer, protocol);
					
					buffer.position(p);
					
					return r;
				}

				public long execute(ByteBuffer buffer, RecordFilterTarget target)
				    throws FilterException {
					if (target != SnoopRecordType.PacketRecord) {
						return 0;
					}
					
					/*
					 * Shift buffer position past the record's header to the beginning
					 * of packet data. This is the main part of the adapting process.
					 */
					final int p = buffer.position();
					buffer.position(p + SnoopPacketRecord.HEADER_LENGTH);
					
					final long r = filter.execute(buffer, protocol);
					
					buffer.position(p);
					
					return r;

				}

			};
		}


	};

	public static final Log logger = LogFactory.getLog(SnoopFile.class);

	public IOSkippableIterator<SnoopBlockRecord> getBlockIterator()
	    throws IOException;

	public SnoopBlockRecord getBlockRecord();

	public PacketIndexer<SnoopPacket> getPacketIndexer() throws IOException;

	public PacketIterator<SnoopPacket> getPacketIterator() throws IOException;

	public RawIndexer getRawIndexer() throws IOException;

	public RawIterator getRawIterator() throws IOException;

	public RecordIndexer<SnoopRecord> getRecordIndexer() throws IOException;

	public RecordIterator<SnoopRecord> getRecordIterator() throws IOException;
}
