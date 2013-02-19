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
package com.slytechs.capture.file.indexer;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jnetstream.capture.CapturePacket;
import org.jnetstream.capture.PacketIndexer;
import org.jnetstream.capture.PacketIterator;
import org.jnetstream.packet.Packet;
import org.jnetstream.protocol.Protocol;


/**
 * Implemenets the FileIndexer interface but translations all index values just
 * for packets only. No other types of records are possible to aquire from this
 * indexer. If the source file contains other types of records, (i.e. block/file
 * header is considered a record), then the size() and get() methods will
 * account for those extra records. The size() will only return the number
 * packets present within the capture file. If the capture file only contains
 * META records, then size() will return 0.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class PacketIndexerImpl<T extends CapturePacket>
    extends AbstractIndexer<T, Packet, T> implements PacketIndexer<T> {

	private PacketIterator<T> packets;

	/**
	 * Takes a normal record based position indexer that does not take into
	 * account any packet to non packet record translations
	 * 
	 * @param indexer
	 *          normal indexer that indexes all the records
	 * @param packets
	 *          packet iterator to do some of the back-end work
	 * @throws IOException
	 *           any IO errors
	 */
	public PacketIndexerImpl(final PositionIndexer indexer,
	    final PacketIterator<T> packets) throws IOException {

		this(new SimplePacketPositionIndexer(indexer), packets);

	}

	/**
	 * Takes a packet position indexer which already accounts for record to packet
	 * index translation.
	 * 
	 * @param indexer
	 *          packet indexer that already takes into account translations
	 * @param packets
	 *          packet iterator to do some of the back-end work
	 * @throws IOException
	 *           any IO errors
	 */
	public PacketIndexerImpl(final PacketPositionIndexer indexer,
	    final PacketIterator<T> packets) throws IOException {
		super(indexer, packets);

		this.packets = packets;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.PacketIndexer#add(long, java.nio.ByteBuffer)
	 */
	public void add(final long index, final ByteBuffer data) throws IOException {
		this.setPosition(index);

		this.packets.add(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.PacketIndexer#add(long, java.nio.ByteBuffer, int,
	 *      long, long, long)
	 */
	public void add(final long index, final ByteBuffer data, final int dlt,
	    final long original, final long seconds, final long nanos)
	    throws IOException {
		this.setPosition(index);

		this.packets.add(data, dlt, original, seconds, nanos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.PacketIndexer#add(long, java.nio.ByteBuffer,
	 *      org.jnetstream.protocol.ProtocolConst)
	 */
	public void add(final long index, final ByteBuffer data,
	    final Protocol dlt) throws IOException {
		this.setPosition(index);

		this.packets.add(data, dlt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.PacketIndexer#add(long, java.nio.ByteBuffer,
	 *      org.jnetstream.protocol.ProtocolConst, long)
	 */
	public void add(final long index, final ByteBuffer data,
	    final Protocol dlt, final long original) throws IOException {
		this.setPosition(index);

		this.packets.add(data, dlt, original);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.PacketIndexer#add(long, java.nio.ByteBuffer,
	 *      org.jnetstream.protocol.ProtocolConst, long, long, long)
	 */
	public void add(final long index, final ByteBuffer data,
	    final Protocol dlt, final long original, final long seconds,
	    final long nanos) throws IOException {
		this.setPosition(index);

		this.packets.add(data, dlt, original, seconds, nanos);
	}

}
