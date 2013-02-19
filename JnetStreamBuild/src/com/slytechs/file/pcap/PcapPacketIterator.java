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
import java.util.Collection;
import java.util.List;

import org.jnetstream.capture.CaptureDevice;
import org.jnetstream.capture.PacketIterator;
import org.jnetstream.capture.file.RawIterator;
import org.jnetstream.capture.file.pcap.PcapBlockRecord;
import org.jnetstream.capture.file.pcap.PcapPacket;
import org.jnetstream.capture.file.pcap.PcapPacketRecord;
import org.jnetstream.packet.Packet;
import org.jnetstream.protocol.Protocol;
import org.jnetstream.protocol.ProtocolRegistry;

import com.slytechs.capture.file.AbstractPacketIterator;
import com.slytechs.capture.file.editor.FileEditor;
import com.slytechs.utils.memory.BufferUtils;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public final class PcapPacketIterator
    extends AbstractPacketIterator implements PacketIterator<PcapPacket> {

	private final FileEditor editor;

	private final PcapPacketFactory factory;

	/**
	 * @param raw
	 * @param captureDevice
	 *          TODO
	 * @throws IOException
	 */
	public PcapPacketIterator(final FileEditor editor,
	    final PcapBlockRecord block, final RawIterator raw,
	    CaptureDevice captureDevice) throws IOException {
		super(raw, captureDevice);
		this.editor = editor;

		this.factory =
		    ProtocolRegistry.getPacketFactory(PcapPacketFactory.class,
		        "com.slytechs.file.pcap.DefaultPcapPacketFactory");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.PacketIterator#add(java.nio.ByteBuffer)
	 */
	public void add(final ByteBuffer data) throws IOException {
		/*
		 * DLT is ignored in PCAP packet records. The DLT is recorded globally in
		 * the block record. We do not check if the supplied DLT matches the block
		 * DLT, its upto the user to do the check.
		 */
		this.add(data, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.PacketIterator#add(java.nio.ByteBuffer, int,
	 *      long, long, long)
	 */
	public void add(final ByteBuffer data, final int dlt, final long original,
	    final long seconds, final long nanos) throws IOException {

		/*
		 * DLT is ignored in PCAP packet records. The DLT is recorded globally in
		 * the block record. We do not check if the supplied DLT matches the block
		 * DLT, its upto the user to do the check.
		 */
		this.add(data, null, original, seconds, nanos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.PacketIterator#add(java.nio.ByteBuffer,
	 *      org.jnetstream.protocol.ProtocolConst)
	 */
	public void add(final ByteBuffer data, final Protocol dlt) throws IOException {
		final long original = data.limit() - data.position();

		this.add(data, dlt, original);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.PacketIterator#add(java.nio.ByteBuffer,
	 *      org.jnetstream.protocol.ProtocolConst, long)
	 */
	public void add(final ByteBuffer data, final Protocol dlt, final long original)
	    throws IOException {
		final long millis = System.currentTimeMillis();
		final long seconds = millis / 1000;

		final long nanos = System.nanoTime();

		this.add(data, dlt, original, seconds, nanos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.PacketIterator#add(java.nio.ByteBuffer,
	 *      org.jnetstream.protocol.ProtocolConst, long, long, long)
	 */
	public void add(final ByteBuffer data, final Protocol dlt,
	    final long original, final long seconds, final long nanos)
	    throws IOException {
		final int included = data.limit() - data.position();
		final int length = included + PcapPacketRecord.HEADER_LENGTH;
		final ByteBuffer buffer = ByteBuffer.allocate(length);
		buffer.order(this.editor.order());

		PcapPacketRecordImpl.initBuffer(buffer, included, (int) original, seconds,
		    (int) nanos / 1000);

		/*
		 * Now copy the data into our new record buffer
		 */
		buffer.position(PcapPacketRecord.HEADER_LENGTH);
		buffer.put(data);
		buffer.clear();

		this.raw.add(buffer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#add(java.util.List)
	 */
	public void addAll(final List<Packet> elements) throws IOException {
		final Packet[] array = elements.toArray(new Packet[elements.size()]);

		this.addAll(array);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#add(java.lang.Object)
	 */
	public void add(final Packet element) throws IOException {

		final ByteBuffer buffer = this.convertToBuffer(element);

		/*
		 * Check the byte order of the old buffer with the byte order we will be
		 * converting it to. If the byte order is different that means that our
		 * conversion utility has copied the contents to another buffer inorder to
		 * flip byte order of the header. Otherwise the we are still referencing a
		 * slice of the original buffer. Therefore if orders are equal, we add with
		 * a copy, since we didn't do any copies our selves. AND if the orders are
		 * different we have already made a copy once into a readwrite buffer,
		 * therefore we add with copy flag "false"
		 */
		if (element.getBuffer().order() == this.editor.order()) {
			this.raw.add(buffer); // Add with copy buffer
		} else {
			this.raw.add(buffer, false); // Add with no copy of the buffer
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#add(T[])
	 */
	public void addAll(final Packet... elements) throws IOException {

		/*
		 * Build up an array and add all the packets all at once. It is much more
		 * efficient in the editor to do it this way as entire large region will be
		 * inserted instead of small regions, one per packet.
		 */
		final ByteBuffer[] array = new ByteBuffer[elements.length];

		for (int i = 0; i < elements.length; i++) {
			final Packet packet = elements[i];
			final ByteBuffer b = this.convertToBuffer(packet);
			array[i] = b;
		}

		this.raw.addAll(array);
	}

	private ByteBuffer convertToBuffer(final Packet element) throws IOException {
		final ByteBuffer buffer;

		if (element instanceof PcapPacketImpl) {
			final PcapPacketImpl p = (PcapPacketImpl) element;

			final ByteBuffer old = p.getRecordByteBuffer();
			if (old.order() == this.editor.order()) {
				buffer = old.slice();
			} else {
				buffer =
				    PcapRecordIterator.convertByteOrder(BufferUtils.copy(old),
				        this.editor.order());
			}

		} else {

			final int included = (int) element.getIncludedLength();
			final int original = (int) element.getOriginalLength();
			final int seconds = (int) element.getTimestampSeconds();
			final int micros = (int) element.getTimestampNanos() / 1000;
			final int length = included + PcapPacketRecord.HEADER_LENGTH;

			buffer = ByteBuffer.allocate(length);
			PcapPacketRecordImpl.initBuffer(buffer, included, original, seconds,
			    micros);

			buffer.position(PcapPacketRecord.HEADER_LENGTH);
			buffer.put(element.getBuffer().toByteBuffer());
			buffer.clear();
		}

		return buffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOIterator#next()
	 */
	public PcapPacket next() throws IOException {
		final long position = this.raw.getPosition();
		this.raw.skip();

		return factory.newPacket(this.editor, this.editor.generateHandle(position),
        dlt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#remove(java.util.Collection)
	 */
	public void removeAll(final Collection<PcapPacket> elements)
	    throws IOException {
		final PcapPacket[] array =
		    elements.toArray(new PcapPacket[elements.size()]);

		this.removeAll(array);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#remove(D[])
	 */
	public void removeAll(final PcapPacket... elements) throws IOException {

		for (final PcapPacket packet : elements) {
			final long global = packet.getPositionGlobal();

			this.setPosition(global);

			this.remove();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#replace(java.lang.Object)
	 */
	public void replace(final Packet element) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#retain(java.util.List)
	 */
	public void retainAll(final List<PcapPacket> elements) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#retain(D[])
	 */
	public void retainAll(final PcapPacket... elements) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#swap(java.lang.Object,
	 *      java.lang.Object)
	 */
	public void swap(final PcapPacket dst, final PcapPacket src)
	    throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
