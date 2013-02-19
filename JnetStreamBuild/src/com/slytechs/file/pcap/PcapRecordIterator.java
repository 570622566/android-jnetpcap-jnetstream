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
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jnetstream.capture.FileCapture;
import org.jnetstream.capture.file.BlockRecord;
import org.jnetstream.capture.file.RawIterator;
import org.jnetstream.capture.file.RecordIterator;
import org.jnetstream.capture.file.pcap.PcapBlockRecord;
import org.jnetstream.capture.file.pcap.PcapPacket;
import org.jnetstream.capture.file.pcap.PcapRecord;


import com.slytechs.capture.file.AbstractFile;
import com.slytechs.capture.file.editor.AbstractIterator;
import com.slytechs.capture.file.editor.FileEditor;
import com.slytechs.utils.memory.BufferUtils;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class PcapRecordIterator<T extends PcapRecord>
    extends AbstractIterator implements RecordIterator<T> {

	private final BlockRecord block;

	private final FileEditor editor;

	private final FileCapture file;

	/**
	 * @param raw
	 * @throws IOException 
	 */
	public PcapRecordIterator(final FileCapture file, final FileEditor editor,
	    final PcapBlockRecord block, final RawIterator raw) throws IOException {
		super(raw);
		this.file = file;
		this.editor = editor;
		this.block = block;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#add(java.util.List)
	 */
	public void addAll(final List<T> elements) throws IOException {

		final List<ByteBuffer> buffers = this.convertToBufferList(elements);

		/*
		 * Now add all at once
		 */
		this.raw.addAll(buffers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#add(java.lang.Object)
	 */
	public void add(final T element) throws IOException {
		ByteBuffer b = element.getRecordBuffer();
		b = convertByteOrder(b, this.block.order());

		this.raw.add(this.convertToReadonly(b));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#add(T[])
	 */
	public void addAll(final T... elements) throws IOException {
		final ByteBuffer[] array = this.convertToBufferArray(elements);

		/*
		 * Now add all at once
		 */
		this.raw.addAll(array);
	}

	/**
	 * If the byte order of the source buffer is different from this iterator's
	 * then the source buffer is copied into another buffer and its header
	 * rewritten to reflect the new byte order matching this iterators. If the
	 * source byte buffer uses the same byte order, then the same source buffer
	 * reference is returned.
	 * 
	 * @param b
	 *          source buffer
	 * @return either the source buffer if the byte order matches, or a new
	 *         duplicate buffer with header rewritten
	 */
	static ByteBuffer convertByteOrder(final ByteBuffer b, final ByteOrder order) {
		if (b.order() == order) {
			return b;
		}

		final int length = b.limit() - b.position();
		final ByteBuffer r = ByteBuffer.allocate(length);
		r.order(order);

		r.putInt(b.getInt());
		r.putInt(b.getInt());
		r.putInt(b.getInt());
		r.putInt(b.getInt());
		r.put(b);

		/*
		 * Now reset the buffer
		 */
		r.clear();

		return r;
	}

	private ByteBuffer[] convertToBufferArray(final PcapRecord[] elements)
	    throws IOException {
		final ByteBuffer[] array = new ByteBuffer[elements.length];

		int i = 0;
		for (final PcapRecord record : elements) {
			ByteBuffer b = record.getRecordBuffer();
			b = convertByteOrder(b, this.block.order());
			b = this.convertToView(b);

			final ByteBuffer readonly = this.convertToReadonly(b);

			array[i++] = readonly;
		}

		return array;
	}

	/**
	 * @param b
	 * @return
	 */
	private ByteBuffer convertToView(ByteBuffer b) {
		return (b.position() != 0 || b.limit() != b.capacity()) ? b.slice() : b;
	}

	private Long[] convertToPositionArray(final PcapRecord[] elements)
	    throws IOException {
		final Long[] array = new Long[elements.length];

		int i = 0;
		for (final PcapRecord record : elements) {

			array[i++] = record.getPositionGlobal();
		}

		return array;
	}

	private List<ByteBuffer> convertToBufferList(
	    final List<T> elements) throws IOException {

		final List<ByteBuffer> list = new ArrayList<ByteBuffer>(elements.size());

		/*
		 * Get total length of all the data buffers
		 */
		for (final PcapRecord record : elements) {
			ByteBuffer b = record.getRecordBuffer();
			b = convertByteOrder(b, this.block.order());
			b = this.convertToView(b);

			final ByteBuffer readonly = this.convertToReadonly(b);

			list.add(readonly);
		}

		return list;
	}

	private List<Long> convertToPositionList(
	    final Collection<T> elements) throws IOException {

		final List<Long> list = new ArrayList<Long>(elements.size());

		/*
		 * Get total length of all the data buffers
		 */
		for (final PcapRecord record : elements) {

			list.add(record.getPositionGlobal());
		}

		return list;
	}

	private ByteBuffer convertToReadonly(final ByteBuffer b) {
		return (b.isReadOnly()) ? b : BufferUtils.asReadonly(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.collection.IOIterator#next()
	 */
	@SuppressWarnings("unchecked")
  public T next() throws IOException {

		final long position = this.raw.getPosition();
		this.raw.skip();

		/*
		 * We could read the record type directly from the header by calling
		 * headerReader.readRecordType(), but for Pcap files unless the position is
		 * 0, its always a Pcap Packet
		 */
		final PcapRecord record;

		if (position == 0) {
			record = ((AbstractFile<PcapPacket, PcapRecord, PcapBlockRecord>) file).getBlockRecord();
		} else {
			record = new PcapPacketRecordImpl(this.file, this.editor, this.block,
			    position);
		}

		return (T) record;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#remove(java.util.Collection)
	 */
	public void removeAll(final Collection<T> elements)
	    throws IOException {
		final List<Long> list = this.convertToPositionList(elements);

		this.raw.removeAll(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#remove(D[])
	 */
	public void removeAll(final T... elements) throws IOException {
		final Long[] list = this.convertToPositionArray(elements);

		this.raw.removeAll(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#replace(java.lang.Object)
	 */
	public void replace(final T element) throws IOException {
		ByteBuffer b = element.getRecordBuffer();
		b = convertByteOrder(b, this.block.order());

		final ByteBuffer readonly = this.convertToReadonly(b);

		this.raw.replace(readonly);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#retain(java.util.List)
	 */
	public void retainAll(final List<T> elements) throws IOException {
		final List<Long> list = this.convertToPositionList(elements);

		this.raw.retainAll(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#retain(D[])
	 */
	public void retainAll(final T... elements) throws IOException {
		final Long[] array = this.convertToPositionArray(elements);

		this.raw.retainAll(array);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileModifier#swap(java.lang.Object,
	 *      java.lang.Object)
	 */
	public void swap(final T dst, final T src)
	    throws IOException {

		this.raw.swap(dst.getPositionGlobal(), src.getPositionGlobal());
	}
}
