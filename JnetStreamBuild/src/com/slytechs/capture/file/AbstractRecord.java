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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

import org.jnetstream.capture.FileCapture;
import org.jnetstream.capture.file.HeaderReader;


import com.slytechs.capture.file.editor.EditorHandle;
import com.slytechs.capture.file.editor.FileEditor;

/**
 * Provides low level record methods.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class AbstractRecord {

	protected EditorHandle handle;

	protected final FileEditor editor;

	protected int offset = 0;

	protected final FileCapture file;

	protected final HeaderReader lengthGetter;

	/**
	 * If this buffer is set, then the record is based on a static buffer and
	 * therefore will not use a dynamic handle to aquire buffer and edit buffer.
	 * If the static buffer is readonly there is no way to make it editable.
	 */
	protected final ByteBuffer staticBuffer;

	protected final long staticPosition;

	private boolean autoedit = true;

	/**
	 * 
	 * @param file
	 * @param editor
	 * @param handle
	 */
	public AbstractRecord(FileCapture file, final FileEditor editor,
	    final EditorHandle handle) {
		this.file = file;
		this.editor = editor;
		this.handle = handle;
		this.lengthGetter = null;

		this.staticBuffer = null;
		this.staticPosition = -1;
	}

	/**
	 * 
	 * @param buffer
	 * @param position
	 */
	public AbstractRecord(ByteBuffer buffer, long position) {
		this.file = null;
		this.editor = null;
		this.handle = null;
		this.lengthGetter = null;

		this.staticBuffer = buffer;
		this.staticPosition = position;
	}

	public ByteBuffer getRecordBuffer() throws IOException {
		if (staticBuffer != null) {
			return staticBuffer;
		}

		final ByteBuffer b = this.handle.getByteBuffer();
		this.offset = b.position();

		return b;
	}

	/**
	 * Combines getRecordBuffer and autoedit calls more efficiently.
	 * 
	 * @return editable buffer if it was
	 * @throws IOException
	 */
	protected ByteBuffer getEditBuffer() throws IOException {
		if (staticBuffer != null) {
			return staticBuffer;
		}

		final ByteBuffer b = this.handle.getByteBuffer();
		this.offset = b.position();

		if (autoedit && b.isReadOnly()) {
			final long global = handle.getPositionGlobal();

			editor.replaceInPlace(getPositionGlobal(), true);

			handle = editor.generateHandle(global);

			return getRecordBuffer();
		}

		return b;
	}

	public boolean isValid() {
		return handle.isValid();
	}

	public long getPositionGlobal() throws IOException {
		return (handle == null ? staticPosition : handle.getPositionGlobal());
	}

	public final FileCapture getFileCapture() {
		return this.file;
	}

	public ByteBuffer getRecordDataBuffer() throws IOException {

		final ByteBuffer b = this.getRecordBuffer();
		final int start = offset + getRecordHeaderLength();
		final int length = (int) b.limit() - start;

		b.limit(start + length);
		b.position(start);

		return b;
	}

	public final int getRecordDataLength() throws IOException {

		return (int) getRecordLength() - getRecordHeaderLength();
	}

	public final ByteBuffer getRecordHeaderBuffer() throws IOException {
		final ByteBuffer b = this.getRecordBuffer();
		final int start = offset;
		final int length = this.getRecordHeaderLength();

		b.limit(start + length);
		b.position(start);

		return b;
	}

	public abstract int getRecordHeaderLength();

	public long getRecordLength() throws IOException {
		final ByteBuffer b = getRecordBuffer();
		final int length = (int) b.limit() - b.position();

		return length;
	}

	public final long getRecordStart() {
		return this.handle.getPositionGlobal();
	}

	public void edit() throws IOException {
		final ByteBuffer b = getRecordBuffer();

		/*
		 * If static buffer is RO, we can not convert it to RW. Only dynamic buffers
		 * can be converted.
		 */
		if (b.isReadOnly() && staticBuffer != null) {
			throw new ReadOnlyBufferException();
		}

		if (b.isReadOnly()) {

			final long global = handle.getPositionGlobal();
			editor.replaceInPlace(getPositionGlobal(), true);

			handle = editor.generateHandle(global);
		}
	}

	public void autoedit() throws IOException {
		final ByteBuffer b = getRecordBuffer();

		if (staticBuffer != null && autoedit) {
			throw new IllegalStateException(
			    "Autoedit must be disabled for static buffer based records");
		}

		if (autoedit && b.isReadOnly()) {
			editor.replaceInPlace(getPositionGlobal(), true);
		}
	}
}
