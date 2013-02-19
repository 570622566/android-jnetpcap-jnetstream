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
package com.slytechs.utils.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * A counted input stream. The stream keeps count of number of bytes returned
 * which can be used as a position of the stream. The stream also keeps track of
 * <code>mark</code> and <code>reset</code> calls. If a mark was previously
 * set and a reset has been issued, the counter is rewound to the value it had
 * at the time the mark was first set.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class CountedInputStream
    extends InputStream {

	private final InputStream in;

	private long mark;

	private long counter;

	private int readlimit;

	/**
	 * Initialized the counted input stream.
	 * 
	 * @param in
	 *          source input stream for which we will count number of bytes. The
	 *          stream may or may not support the mark. If it does, this stream
	 *          will keep track of mark and reset calls. If it does not, calling
	 *          mark and reset has no impact on this stream and calls are simply
	 *          delagated to the source input stream.
	 */
	public CountedInputStream(InputStream in) {
		this.in = in;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		counter++;

		return in.read();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		in.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public synchronized void mark(int readlimit) {
		this.readlimit = readlimit;
		this.mark = (markSupported() ? counter : -1);

		in.mark(readlimit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		counter += len;

		return in.read(b, off, len);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] b) throws IOException {
		counter += b.length;

		return in.read(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {

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

		in.reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		counter += n;

		return in.skip(n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		return in.available();
	}

	/**
	 * Gets the current counter value which can be used as a position within the
	 * stream.
	 * 
	 * @return number of byte that have been consumed from the input stream, minus
	 *         any bytes that were reset back to the mark.
	 */
	public long getCounter() {
		return counter;
	}

}
