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
package org.jnetstream.capture.file.pcap;

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
import org.jnetstream.capture.file.pcap.PcapPacketRecord.PcapPacketHeader;
import org.jnetstream.filter.Filter;
import org.jnetstream.filter.FilterException;
import org.jnetstream.packet.ProtocolFilterTarget;
import org.jnetstream.protocol.Protocol;

import com.slytechs.utils.collection.IOSkippableIterator;

/**
 * PCAP file format from tcpdump.org folks.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface PcapFile extends FileCapture<PcapPacket>, PcapFormat {

	public static final HeaderReader headerReader = new HeaderReader() {

		public String toString() {
			return "[PcapHeader, min=16]";
		}

		public int getHeaderLength(ByteBuffer buffer) {
			final RecordType type = this.readType(buffer);

			switch (type) {
				case BlockRecord:
					return 24;

				case PacketRecord:
					return 16;

				default:
					throw new UnsupportedOperationException("Unsupported record type ["
					    + type + "]");
			}
		}

		/**
		 * Our minimum is the offset of the included length in the packet record
		 * header as that is the absolute minimum to determine the record type and
		 * the length of the record. For block records, we only need access to MAGIC
		 * pattern which is first 4 bytes. For packets its 8 through 12.
		 */
		public int getMinLength() {
			return 12;
		}

		public int getOffset() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		public long readLength(ByteBuffer buffer) {

			final RecordType type = this.readType(buffer);

			switch (type) {
				case BlockRecord:
					return 24;

				case PacketRecord:
					final int offset = buffer.position();
					final long length = buffer.getInt(offset
					    + PcapPacketHeader.IncludedLength.getOffset())
					    + PcapPacketRecord.HEADER_LENGTH;

					return length;

				default:
					throw new UnsupportedOperationException("Unsupported record type ["
					    + type + "]");
			}
		}

		/**
		 * Checks for PCAP block record pattern and if not found, then it assumes
		 * its a packet record since there are only these 2 types possible in a PCAP
		 * file.
		 */
		public RecordType readType(ByteBuffer buffer) {

			final int offset = buffer.position();

			if ((buffer.get(offset + 0) == PcapFile.MAGIC_PATTERN_BE[0])
			    && (buffer.get(offset + 1) == PcapFile.MAGIC_PATTERN_BE[1])
			    && (buffer.get(offset + 2) == PcapFile.MAGIC_PATTERN_BE[2])
			    && (buffer.get(offset + 3) == PcapFile.MAGIC_PATTERN_BE[3])) {

				return RecordType.BlockRecord;

			} else if ((buffer.get(offset + 0) == PcapFile.MAGIC_PATTERN_LE[0])
			    && (buffer.get(offset + 1) == PcapFile.MAGIC_PATTERN_LE[1])
			    && (buffer.get(offset + 2) == PcapFile.MAGIC_PATTERN_LE[2])
			    && (buffer.get(offset + 3) == PcapFile.MAGIC_PATTERN_LE[3])) {

				return RecordType.BlockRecord;

			} else {
				return RecordType.PacketRecord;
			}
		}

		public RecordFilterTarget readRecordFilterTarget(ByteBuffer buffer) {
			final RecordType type = readType(buffer);

			switch (type) {
				case PacketRecord:
					return PCAPRecordType.PacketRecord;

				case BlockRecord:
					return PCAPRecordType.BlockRecord;

				default:
					throw new UnsupportedOperationException("Unsupported record type ["
					    + type + "]");
			}
		}

		public Filter<RecordFilterTarget> asRecordFilter(
		    final Filter<ProtocolFilterTarget> filter,
		    final ProtocolFilterTarget protocol) {
			
			if (filter == null) {
				return null;
			}
			
			return new Filter<RecordFilterTarget>() {

				public boolean accept(ByteBuffer buffer, RecordFilterTarget target)
				    throws FilterException {
					if (target != PCAPRecordType.PacketRecord) {
						return false;
					}

					/*
					 * Shift buffer position past the record's header to the beginning of
					 * packet data. This is the main part of the adapting process.
					 */
					final int p = buffer.position();
					buffer.position(p + PcapPacketRecord.HEADER_LENGTH);

					final boolean r = filter.accept(buffer, protocol);

					buffer.position(p);

					return r;
				}

				public long execute(ByteBuffer buffer, RecordFilterTarget target)
				    throws FilterException {
					if (target != PCAPRecordType.PacketRecord) {
						return 0;
					}

					/*
					 * Shift buffer position past the record's header to the beginning of
					 * packet data. This is the main part of the adapting process.
					 */
					final int p = buffer.position();
					buffer.position(p + PcapPacketRecord.HEADER_LENGTH);

					final long r = filter.execute(buffer, protocol);

					buffer.position(p);

					return r;

				}

			};
		}

	};

	public final static Log logger = LogFactory.getLog(PcapFile.class);

	/**
	 * Default MAGIC byte pattern for Pcap file in big-endian byte encoding
	 * 
	 * <pre>
	 *   A1 B2 C3 D4
	 * </pre>
	 */
	public static final byte[] MAGIC_PATTERN_BE = { (byte) 0xA1, (byte) 0xB2,
	    (byte) 0xC3, (byte) 0xD4 };

	/**
	 * Default MAGIC byte pattern for Pcap file in little-endian byte encoding
	 * 
	 * <pre>
	 *   D4 C3 B2 A1
	 * </pre>
	 */
	public static final byte[] MAGIC_PATTERN_LE = { (byte) 0xD4, (byte) 0xC3,
	    (byte) 0xB2, (byte) 0xA1 };

	/**
	 * Default major version of the file format
	 */
	public static final long MAJOR_VERSION = 2;

	/**
	 * Default minor version of the file format
	 */
	public static final int MINOR_VERSION = 4;

	public IOSkippableIterator<PcapBlockRecord> getBlockIterator()
	    throws IOException;

	public PcapBlockRecord getBlockRecord() throws IOException;
	
	public Protocol getDlt() throws IOException;

	public PacketIndexer<PcapPacket> getPacketIndexer() throws IOException;

	public PacketIterator<PcapPacket> getPacketIterator() throws IOException;

	public RawIndexer getRawIndexer() throws IOException;

	public RawIterator getRawIterator() throws IOException;

	public RecordIndexer<PcapRecord> getRecordIndexer() throws IOException;

	public RecordIterator<PcapRecord> getRecordIterator() throws IOException;

	/**
	 * Gets the time accuracy as found in the Pcap block record header. TODO:
	 * Figure out what accuracy units are
	 * 
	 * @return accuracy in ?????
	 */
	// public int getAccuracy();
	/**
	 * Timezone in seconds since timezone 0. This is the raw timezone number that
	 * does not take into account DST.
	 * 
	 * @return timezone offset in seconds
	 */
	// public int getTimeZone();
	/**
	 * Gets the Data Link Type (DLT) as found in the Pcap block record header.
	 * 
	 * @return integer value representing the DLT as defined by Pcap format
	 */
	// public int getDLT();
	/**
	 * @return
	 */
	// public byte[] getMagicPattern();
	/**
	 * Adds a new block record at the beginning of the Pcap file. The file must be
	 * completely empty inorder for this operation to succeed, otherwise
	 * InvalidStateException will be thrown.
	 * 
	 * @param m
	 *          magic number to include in the block header, the magic number must
	 *          exactly 4 octets in length and its position and limit properties
	 *          point to the beginning of pattern
	 * @param major
	 *          major version number
	 * @param minor
	 *          minor version number
	 * @param dlt
	 *          PCAP data link type
	 * @param a
	 *          accuracy of the timestamp
	 * @param tz
	 *          timezone of the timestamp
	 * @param i
	 *          included length or sometimes refered to as snaplen value
	 * @throws IOException
	 *           any IO errors
	 * @throws IllegalStateException
	 *           if the file is not completely empty and its size not equal to
	 *           length of 0 bytes
	 */
	public PcapBlockRecord setBlockRecord(byte[] m, int major, int minor,
	    PcapDLT dlt, int a, int tz, int i) throws IOException,
	    IllegalStateException;

	/**
	 * Adds a new block record at the beginning of the Pcap file. The file must be
	 * completely empty inorder for this operation to succeed, otherwise
	 * InvalidStateException will be thrown. This method uses defaults for many of
	 * the required parameters, such as the timezone is taken from the current
	 * runtime environment settings.
	 * 
	 * @param dlt
	 *          PCAP data link type
	 * @param i
	 *          included length or sometimes refered to as snaplen value
	 * @throws IOException
	 *           any IO errors
	 * @throws IllegalStateException
	 *           if the file is not completely empty and its size not equal to
	 *           length of 0 bytes
	 */
	public PcapBlockRecord setBlockRecord(PcapDLT dlt, int i) throws IOException,
	    IllegalStateException;
}
