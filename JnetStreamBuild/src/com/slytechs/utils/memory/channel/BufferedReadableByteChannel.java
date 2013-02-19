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
public class BufferedReadableByteChannel implements MarkableReadableByteChannel {

	private final ReadableByteChannel in;

	private ByteBuffer buffer = null;

	/**
	 * @param in
	 */
	public BufferedReadableByteChannel(final ReadableByteChannel in) {
		this.in = in;
	}

	public boolean markSupported() {
		return true;
	}

	public void mark(int max) {

		buffer = ByteBuffer.allocate(max);
		buffer.limit(0);
	}

	public void reset() {
		if (buffer == null) {
			return;
		}

		buffer.position(0);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.nio.channels.ReadableByteChannel#read(java.nio.ByteBuffer)
	 */
	public final int read(final ByteBuffer dst) throws IOException {
		/*
		 * This should very efficiently delagate the read call once the mark has
		 * been cleared.
		 */
		if (buffer == null) {
			return in.read(dst);
		}

		int count = dst.remaining();

		if (buffer.hasRemaining() && buffer.remaining() >= dst.remaining()) {
			int l = buffer.limit();
			buffer.limit(buffer.position() + dst.remaining());
			dst.put(buffer);
			buffer.limit(l);
		} else if (buffer.hasRemaining()) {
			dst.put(buffer);
		}

		if (dst.hasRemaining() == false) {
			return count;
		}

		int l = buffer.limit() + dst.remaining();

		if (l > buffer.capacity()) {

			if (buffer.hasRemaining()) {
				dst.put(buffer);
			}

			buffer = null;

			return (read(dst) == -1 ? -1 : count);
		}

		int p = buffer.position();

		buffer.limit(l);

		if (in.read(buffer) == -1) {
			return -1;
		}

		buffer.position(p);

		dst.put(buffer);

		return count;

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

}
