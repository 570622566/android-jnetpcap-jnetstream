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
package com.slytechs.jnetstream.packet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

import org.jnetstream.capture.CaptureDevice;
import org.jnetstream.capture.Captures;
import org.jnetstream.capture.FilePacket;
import org.jnetstream.protocol.ProtocolEntry;

import com.slytechs.capture.file.editor.EditorHandle;
import com.slytechs.capture.file.editor.FileEditor;
import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class AFilePacket
    extends APacket implements FilePacket {

	private boolean autoedit = true;

	private BitBuffer bits;

	private ByteBuffer bytes;

	protected final FileEditor editor;

	protected EditorHandle handle;

	/*
	 * Offset into the currently fetched buffer, as a local offset
	 */
	protected int offset = 0;

	private final long staticPosition;

	/**
	 * Uses static buffers
	 * 
	 * @param protocol
	 * @param bytes
	 *          TODO
	 * @param bits
	 * @param position
	 */
	public AFilePacket(ProtocolEntry protocol, ByteBuffer bytes,
	    BitBuffer bits, long position) {
		super(protocol);

		this.editor = null;
		this.handle = null;
		this.bits = bits;
		this.bytes = bytes;
		this.staticPosition = position;
	}

	/**
	 * Where the buffer is dynamically accessed and can be replaced.
	 * 
	 * @param protocol
	 * @param editor
	 * @param handle
	 */
	public AFilePacket(ProtocolEntry protocol,
	    final FileEditor editor, final EditorHandle handle) {
		super(protocol);

		this.editor = editor;
		this.handle = handle;
		this.bits = null;
		this.bytes = null;
		this.staticPosition = -1;
	}

	/**
	 * @throws IOException
	 */
	public void autoedit() throws IOException {
		final ByteBuffer b = getRecordByteBuffer();

		if (bytes != null && autoedit) {
			throw new IllegalStateException(
			    "Autoedit must be disabled for static buffer based records");
		}

		if (autoedit && b.isReadOnly()) {
			editor.replaceInPlace(getPositionGlobal(), true);
		}
	}

	/**
	 * @throws IOException
	 */
	public void edit() throws IOException {
		final ByteBuffer b = getRecordByteBuffer();

		/*
		 * If static buffer is RO, we can not convert it to RW. Only dynamic buffers
		 * can be converted.
		 */
		if (b.isReadOnly() && bits != null) {
			throw new ReadOnlyBufferException();
		}

		if (b.isReadOnly()) {

			final long global = handle.getPositionGlobal();
			editor.replaceInPlace(getPositionGlobal(), true);

			handle = editor.generateHandle(global);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.Packet#format()
	 */
	public void format() throws IOException {
		Captures.defaultFormatter().format(this);
	}

	/**
	 * The method returns a byte buffer with a view exactly bound around the
	 * packet data within a record. That is the original byte buffer is offset to
	 * point at the packet data portion of the buffer referenced as a view.
	 * 
	 * @see com.slytechs.packet.AbstractPacket#getBuffer()
	 */
	public BitBuffer getBuffer() throws IOException {
		final BitBuffer b = handle.getBitBuffer();

		if (bits != b) {
			b.position(b.position() + getRecordHeaderLength());
			bits = BitBuffer.wrap(b.toByteBuffer());
		}

		return bits;
	}

	public CaptureDevice getCaptureDevice() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/**
	 * Combines getRecordBuffer and autoedit calls more efficiently.
	 * 
	 * @return editable buffer if it was
	 * @throws IOException
	 */
	protected ByteBuffer getEditBuffer() throws IOException {
		if (bytes != null) {
			return bytes;
		}

		final ByteBuffer b = this.handle.getByteBuffer();
		this.offset = b.position();

		if (autoedit && b.isReadOnly()) {
			final long global = handle.getPositionGlobal();

			editor.replaceInPlace(getPositionGlobal(), true);

			handle = editor.generateHandle(global);

			return getRecordByteBuffer();
		}

		return b;
	}

	/**
	 * @return
	 */
	public ByteBuffer getLocalBuffer() {
		throw new UnsupportedOperationException(
		    "File based packets, do not support local buffers.");
	}

	/**
	 * 
	 */
	public long getPositionGlobal() throws IOException {
		return (handle == null ? staticPosition : handle.getPositionGlobal());
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public BitBuffer getRecordBitBuffer() throws IOException {
		if (bits != null) {
			return bits;
		}

		final BitBuffer b = this.handle.getBitBuffer();
		this.offset = b.position();

		return b;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public ByteBuffer getRecordByteBuffer() throws IOException {
		if (bytes != null) {
			return bytes;
		}

		final ByteBuffer b = this.handle.getByteBuffer();
		this.offset = b.position();

		return b;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public ByteBuffer getRecordDataBuffer() throws IOException {

		final ByteBuffer b = this.getRecordByteBuffer();
		final int start = offset + getRecordHeaderLength();
		final int length = b.limit() - start;

		b.limit(start + length);
		b.position(start);

		return b;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public final int getRecordDataLength() throws IOException {

		return (int) getRecordLength() - getRecordHeaderLength();
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public final ByteBuffer getRecordHeaderBuffer() throws IOException {
		final ByteBuffer b = this.getRecordByteBuffer();
		final int start = offset;
		final int length = this.getRecordHeaderLength();

		b.limit(start + length);
		b.position(start);

		return b;
	}

	/**
	 * @return
	 */
	public abstract int getRecordHeaderLength();

	/**
	 * @return
	 * @throws IOException
	 */
	public long getRecordLength() throws IOException {
		final ByteBuffer b = getRecordByteBuffer();
		final int length = b.limit() - b.position();

		return length;
	}

	/**
	 * @return
	 */
	public final long getRecordStart() {
		return this.handle.getPositionGlobal();
	}

	public boolean isTruncated() throws IOException {
		return getIncludedLength() != getOriginalLength();
	}

	/**
	 * @return
	 */
	public boolean isValid() {
		return handle.isValid();
	}

	/**
	 * @param size
	 */
	public void resize(int size) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

}
