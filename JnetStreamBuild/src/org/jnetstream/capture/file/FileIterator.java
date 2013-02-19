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
package org.jnetstream.capture.file;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

import com.slytechs.utils.collection.IOIterator;
import com.slytechs.utils.collection.IOPositional;
import com.slytechs.utils.collection.IOSeekable;
import com.slytechs.utils.collection.IOSeekableFirstLast;
import com.slytechs.utils.collection.IOSkippableIterator;
import com.slytechs.utils.collection.SeekResult;

/**
 * An iterator that allows iteration over elements contained in a capture file.
 * Simple {@link IOIterator#next} and {@link IOIterator#hasNext} methods are
 * used to iterator over a long sequence of element which reside physically on a
 * some storage device. The element can be a packet, a record or in raw mode
 * specifically configured ByteBuffer objects. *
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * @param <G>
 *          Type for all getter methods. Any method that returns an object will
 *          use this type.
 * @param <S>
 *          Type for all setter methods. Any method that uses setter methods
 *          such as add or swap will use this type.
 * @param <R>
 *          Type for all remove methods. Any method that uses any of the remove
 *          or retain methods, will use this type.
 */
public interface FileIterator<G, S, R> extends IOSeekableFirstLast,
    IOSkippableIterator<G>, FileModifier<S, R>, IOSeekable<G>, IOPositional,
    Flushable, Closeable {

	/**
	 * Seeks or searches for the first record within the iterator starting at the
	 * beggining, that has its capture timestamp equal or older then the specified
	 * timestamp. Another words, the iterator will be positioned at the packet
	 * with the timestamp closest to this user supplied earliestTimestamp but not
	 * before this timestamp.
	 * 
	 * @param seconds
	 *          earliest timestamp to search for
	 * @param nanos
	 *          nanos second fraction of the timestamp with valid range of 0 to
	 *          999,999,999
	 * @return the status of the seek
	 * @throws IOException
	 *           any IO errors
	 */
	public SeekResult seek(long seconds, long nanos) throws IOException;

	public void setAutoflush(boolean state) throws IOException;

}
