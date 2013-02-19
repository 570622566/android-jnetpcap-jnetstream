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
package com.slytechs.capture.file.editor;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jnetstream.capture.file.HeaderReader;



import com.slytechs.utils.memory.BitBuffer;
import com.slytechs.utils.memory.PartialBuffer;
import com.slytechs.utils.region.RegionHandle;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class EditorHandleImpl implements EditorHandle {

	private final RegionHandle<PartialLoader> handle;

	private final HeaderReader headerReader;

	private ByteBuffer buffer;

	private PartialBuffer bblock;

	private long length;

	private long regional;

	private boolean readonly = false;

	/**
	 * @param name
	 * @param headerReader
	 */
	public EditorHandleImpl(RegionHandle<PartialLoader> name,
	    HeaderReader lengthGetter) {
		this.handle = name;
		this.headerReader = lengthGetter;
	}

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.editor.EditorHandleInt#get()
   */
	public BitBuffer getBitBuffer() throws IOException {
		/*
		 * See if we have the buffer cached and more importantly if it hasn't change
		 * readonly state from false to true. If it did we need to get a new buffer
		 * that will be readonly.
		 */
		if (this.handle.isChanged() == false && buffer != null
		    && buffer.isReadOnly() == this.readonly) {

			this.bblock.reposition(regional, (int) length);

			return this.bblock.getBitBuffer();
		}

		final PartialLoader loader = handle.getData();
		this.regional = handle.getRegionalPosition();

		/*
		 * Read in enough buffer to get at the record length field.
		 */
		bblock = loader.fetchBlock(regional, headerReader.getMinLength());
		this.length = headerReader.readLength(bblock.getByteBuffer());

		/*
		 * Now check if the block is big enough for our entire record. If not,
		 * we need to do another fetch request with the desired length
		 */
		if (regional + length > bblock.getEndRegional()) {
			bblock = loader.fetchBlock(regional, (int) length);
		}
		
		buffer = bblock.getByteBuffer();
		this.readonly = buffer.isReadOnly();
		bblock.reposition(regional, (int) length);

		return bblock.getBitBuffer();
	}
	
	
	/* (non-Javadoc)
   * @see com.slytechs.capture.file.editor.EditorHandleInt#get()
   */
	public ByteBuffer getByteBuffer() throws IOException {
		/*
		 * See if we have the buffer cached and more importantly if it hasn't change
		 * readonly state from false to true. If it did we need to get a new buffer
		 * that will be readonly.
		 */
		if (this.handle.isChanged() == false && buffer != null
		    && buffer.isReadOnly() == this.readonly) {

			this.bblock.reposition(regional, (int) length);

			return buffer;
		}

		final PartialLoader loader = handle.getData();
		this.regional = handle.getRegionalPosition();

		/*
		 * Read in enough buffer to get at the record length field.
		 */
		bblock = loader.fetchBlock(regional, headerReader.getMinLength());
		this.length = headerReader.readLength(bblock.getByteBuffer());

		/*
		 * Now check if the block is big enough for our entire record. If not,
		 * we need to do another fetch request with the desired length
		 */
		if (regional + length > bblock.getEndRegional()) {
			bblock = loader.fetchBlock(regional, (int) length);
		}
		
		buffer = bblock.getByteBuffer();
		this.readonly = buffer.isReadOnly();
		bblock.reposition(regional, (int) length);

		return buffer;
	}

	

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.editor.EditorHandleInt#getPositionGlobal()
   */
	public long getPositionGlobal() {
		return handle.getPositionGlobal();
	}

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.editor.EditorHandleInt#getPositionRegional()
   */
	public long getPositionRegional() {
		return handle.getPositionRegional();
	}

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.editor.EditorHandleInt#getPositionLocal()
   */
	public long getPositionLocal() {
		return handle.getPositionLocal();
	}

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.editor.EditorHandleInt#isValid()
   */
	public boolean isValid() {
		return handle.isValid();
	}

}
