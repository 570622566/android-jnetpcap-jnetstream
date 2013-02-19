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

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jnetstream.filter.Filter;

import com.slytechs.utils.collection.IOIterator;
import com.slytechs.utils.collection.SeekResult;

/**
 * <p>
 * An iterator that allows iteration over elements contained in a capture file.
 * Simple {@link IOIterator#next} and {@link IOIterator#hasNext} methods are
 * used to iterate over a long sequence of element which reside physically on a
 * some storage device. Elements which are typically records, are efficiently
 * accessed and returned as shared <tt>ByteBuffers</tt> who's
 * <tt>position</tt> and <tt>limit</tt> properties are set to enclose the
 * contents of the record within some shared buffer.
 * </p>
 * <p>
 * The data returned by <code>next</code>, is a shared buffer, not a view of
 * the buffer but shared instance as returned by the <code>next</code> call.
 * It is important to note that any consecutive call to <code>next</code>
 * overrides the <tt>position</tt> and <tt>limit</tt> properties of the
 * shared buffer that is returned in both instances. Therefore it is upto the
 * user to either save that information or create a view of the buffer using
 * <code>ByteBuffer.slice()</code> method. RawIterator purposely does not
 * return such views and leaves that upto the user to do. This way views are
 * created only when truely required where persistance is needed. This
 * optimization affords increadible performance when using a RawIterator. The
 * author has measured 6,000,000 packet per second iteration speeds over very
 * large capture files. Pefromance goes considerably down even when a single
 * buffer view or any other object instance is created within critical sections
 * of the iterator's code. The implementation goes to great lengths not to
 * initiate even a single object when working with the IOSkippableIterator
 * methods <code>next</code> and <code>skip</code>. These methods are
 * optimized for maximum speed possible. Lastly note that <code>skip</code> is
 * even more efficient then <code>next</code> at skipping over records. Skip's
 * implementation is able to omit certain amount of logic in order to skip
 * accross records even faster then <code>next</code> can.
 * </p>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface RawIterator extends
    FileIterator<ByteBuffer, ByteBuffer, Long>, Iterable<ByteBuffer> {

	/**
	 * Seeks to a record by its zero-based index as counted from the first record.
	 * Efficiency of this method is currently not strictly defined and is
	 * implementation dependent.
	 * 
	 * @param recordIndex
	 *          zero-based index of the record to seek to
	 * @return result of the seek
	 * @throws IOException
	 *           any IO errors
	 */
	public SeekResult seekToIndex(long recordIndex) throws IOException;

	/**
	 * Seek the first record from the current position that will match the
	 * supplied filter.
	 * 
	 * @param filter
	 *          a record filter to used in record matching
	 * @return the status of the seek
	 * @throws IOException
	 *           any IO errors
	 */
	public SeekResult seek(Filter<RecordFilterTarget> filter) throws IOException;

	public SeekResult seekSecond() throws IOException;

	/**
	 * Changes the length of the current record. All changes to the record size
	 * are done in-memory and may be cancelled using aborthChanges() call.
	 * 
	 * @param size
	 *          new size of the record
	 * @throws IOException
	 *           any IO errors
	 */
	public void resize(long size) throws IOException;

	/**
	 * Adds a new record using two buffers. This method is more efficient then
	 * using {@link #addAll(ByteBuffer[])} version as the two buffers are received
	 * as normal parameters. This version of the add method is used when record's
	 * header and content reside in two separate buffers. Addition is done by
	 * copy.
	 * 
	 * @param b1
	 *          first buffer containing the record's header
	 * @param b2
	 *          second buffer containing the record's content
	 * @throws IOException
	 *           any IO errors
	 */
	public void add(ByteBuffer b1, ByteBuffer b2) throws IOException;

	/**
	 * Adds a new record at the current cursor position. The record's header and
	 * content are to be found within the buffer bounded by ByteBuffer's
	 * properties <code>position</code> and <code>limit</code>. The
	 * additional boolean flag indicates if the buffer's content should be copied
	 * into private buffer or if the record's in-memory representation should be
	 * presented as the user supplied buffer. If copy is false, any changes made
	 * to the record will be reflected in the user buffer and visa versa, unless
	 * the supplied buffer is readonly. If the buffer is readonly, a copy of it
	 * will be made upon first change to the buffer automatically into a
	 * read-write buffer.
	 * 
	 * @param buffer
	 *          buffer containing the record
	 * @param copy
	 *          true means that record's content found in the buffer will be
	 *          copied into a private read-write buffer
	 * @throws IOException
	 *           any IO errors
	 */
	public void add(ByteBuffer buffer, boolean copy) throws IOException;

	/**
	 * Replaces the record at current cursor position with the new record found in
	 * the buffer. The record's header and content are to be found within the
	 * buffer bounded by ByteBuffer's properties <code>position</code> and
	 * <code>limit</code>. The additional boolean flag indicates if the
	 * buffer's content should be copied into private buffer or if the record's
	 * in-memory representation should be presented as the user supplied buffer.
	 * If copy is false, any changes made to the record will be reflected in the
	 * user buffer and visa versa, unless the supplied buffer is readonly. If the
	 * buffer is readonly, a copy of it will be made upon first change to the
	 * buffer automatically into a read-write buffer.
	 * 
	 * @param buffer
	 *          the buffer containing the new record
	 * @param copy
	 *          true means that record's content found in the buffer will be
	 *          copied into a private read-write buffer
	 * @throws IOException
	 *           any IO errors
	 */
	public void replace(ByteBuffer buffer, boolean copy) throws IOException;

	/**
	 * <p>
	 * Replaces the current record with its own contents and causes the original
	 * region to become invalid and replaced by new in-memory cache buffer which
	 * contains the same content.
	 * </p>
	 * <p>
	 * This method is primarily used by Packet and Record objects to setup the way
	 * their buffer modification are to take place. Either in-place or in the
	 * buffer they originally came from, or duplicated to in memory cache. The
	 * original content in physical storage stay unmodified while Packet and
	 * Record object make modifications to a duplicate copy of the buffer. Only a
	 * flush() forces the changes out to the physical storage. This is safer way
	 * to make changes as all the changes can be made in memory first, somewhat
	 * offline, and not be propaged to physical storage in case of crash or
	 * serious error. A flush() sends all the changes to physical storage in one
	 * step.
	 * </p>
	 * 
	 * @throws IOException
	 *           any IO errors
	 */
	public void replaceInPlace() throws IOException;

	/**
	 * Skips over a packet record and ignores errors if any. If a record contains
	 * an error in the header, the skip operations performs a positional packet
	 * seek to search out the next valid record header starting at the current
	 * position (which has the corrupted record.) Otherwise it behaves exactly the
	 * same as {@link com.slytechs.utils.collection.IOSkippable#skip}.
	 * 
	 * @return TODO
	 * @throws IOException
	 */
	public RecordError[] skipOverErrors() throws IOException;

	/**
	 * @return
	 */
	public Filter getFilter();

}
