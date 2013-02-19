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
package com.slytechs.utils.memory.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class CountedReadableByteChannel implements MarkableReadableByteChannel {

	private final ReadableByteChannel in;

	private long counter = 0;

	private MarkableReadableByteChannel marked;

	private long mark = -1;

	private int readlimit;

	/**
	 * @param in
	 */
	public CountedReadableByteChannel(final ReadableByteChannel in) {
		this.in = in;
	}

	public CountedReadableByteChannel(final MarkableReadableByteChannel in) {
		this.in = in;
		this.marked = in;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.nio.channels.ReadableByteChannel#read(java.nio.ByteBuffer)
	 */
	public int read(ByteBuffer dst) throws IOException {
		final int c = in.read(dst);

		if (c != -1) {
			counter += c;
		}
		
		if (mark != -1 && counter > mark + readlimit) {
			mark = -1;
		}

		return c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.nio.channels.Channel#close()
	 */
	public void close() throws IOException {
		in.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.nio.channels.Channel#isOpen()
	 */
	public boolean isOpen() {
		return in.isOpen();
	}

	public long getCounter() {
		return counter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.channel.MarkableReadableByteChannel#mark(int)
	 */
	public void mark(int readlimit) {
		this.readlimit = readlimit;
		this.mark = (markSupported() ? counter : -1);
		
		marked.mark(readlimit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.channel.MarkableReadableByteChannel#reset()
	 */
	public void reset() {
		/*
		 * Check if we had already read past the readlimit of this mark, if we have
		 * the mark is supposed to be invalidated by our source inputstream so we
		 * need to do the same.
		 */
		if (counter > mark + readlimit) {
			mark = -1;
		}

		if (mark != -1) {
			counter = mark;
		}

		marked.reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.memory.channel.MarkableReadableByteChannel#supportsMark()
	 */
	public boolean markSupported() {
		return marked != null && marked.markSupported();
	}

}
