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
package com.slytechs.utils.memory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class BufferUtils {

	/**
	 * <p>
	 * Creates a new byte buffer whose content is a shared subsequence of this
	 * buffer's content.
	 * </p>
	 * <p>
	 * The content of the new buffer will start at this buffer's current position.
	 * Changes to this buffer's content will be visible in the new buffer, and
	 * vice versa; the two buffers' position, limit, and mark values will be
	 * independent. The utility method also preserves the byte order of the source
	 * buffer into the new buffer.
	 * </p>
	 * <p>
	 * The new buffer's position will be zero, its capacity and its limit will be
	 * the number of bytes remaining in this buffer, and its mark will be
	 * undefined. The new buffer will be direct if, and only if, this buffer is
	 * direct, and it will be read-only if, and only if, this buffer is read-only.
	 * </p>
	 * 
	 * @param buffer
	 *          source buffer
	 * @return new buffer
	 */
	public static ByteBuffer slice(ByteBuffer buffer) {

		final ByteOrder o = buffer.order();
		final ByteBuffer r = buffer.slice();
		r.order(o);

		return r;
	}

	/**
	 * <p>
	 * Creates a new, read-only byte buffer that shares this buffer's content.
	 * </p>
	 * <p>
	 * The content of the new buffer will be that of this buffer. Changes to this
	 * buffer's content will be visible in the new buffer; the new buffer itself,
	 * however, will be read-only and will not allow the shared content to be
	 * modified. The two buffers' position, limit, and mark values will be
	 * independent. Its byte order will be preserved.
	 * </p>
	 * <p>
	 * The new buffer's capacity, limit, position, and mark values will be
	 * identical to those of this buffer.
	 * </p>
	 * <p>
	 * If this buffer is itself read-only then this method behaves in exactly the
	 * same way as the duplicate method.
	 * </p>
	 * 
	 * @param buffer
	 *          source buffer
	 * @return new buffer
	 */
	public static ByteBuffer asReadonly(ByteBuffer buffer) {

		final ByteOrder o = buffer.order();
		final ByteBuffer r = buffer.asReadOnlyBuffer();
		r.order(o);

		return r;
	}

	public static ByteBuffer copy(ByteBuffer buffer) {

		final int length = buffer.limit() - buffer.position();
		final ByteOrder o = buffer.order();
		final ByteBuffer r = ByteBuffer.allocate(length);
		r.order(o);

		r.put(buffer);
		r.clear(); // Reset position and limit after the put()

		return r;

	}

	/**
	 * @return
	 */
	public static WritableByteChannel asWritableByteChannel(
	    final ByteBuffer buffer) {
		return new WritableByteChannel() {

			private boolean open = true;

			public int write(ByteBuffer src) throws IOException {
				if (open == false) {
					throw new ClosedChannelException();
				}

				final int p = buffer.position();
				final int l = buffer.limit();
				final int r = src.remaining();

				buffer.limit(l + r);
				buffer.position(l);

				buffer.put(src);

				buffer.position(p);

				return r;
			}

			public void close() throws IOException {
				open = false;
			}

			public boolean isOpen() {
				return open;
			}

		};
	}

	public static ReadableByteChannel asReadableByteChannel(
	    final ByteBuffer buffer) {

		return new ReadableByteChannel() {

			private boolean open = true;

			public int read(ByteBuffer dst) throws IOException {
				if (open == false) {
					throw new ClosedChannelException();
				}

				final int p = buffer.position();
				final int l = buffer.limit();
				final int r = dst.remaining();

				buffer.limit(p + r);

				dst.put(buffer);

				buffer.limit(l);

				return r;
			}

			public void close() throws IOException {
				open = false;
			}

			public boolean isOpen() {
				return open;
			}

		};
	}

	/**
	 * @param buffer
	 * @return
	 */
	public static ByteBuffer duplicate(ByteBuffer buffer) {
		final ByteBuffer r = buffer.duplicate();
		r.order(buffer.order());

		return r;
	}
}
