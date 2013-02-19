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


import com.slytechs.utils.collection.IOIterator;

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
public interface RawIndexer extends FileIndexer<ByteBuffer, ByteBuffer, Long> {

	/**
	 * Changes the length of the indexed record. All changes to the record size
	 * are done in-memory and may be cancelled using aborthChanges() call.
	 * 
	 * @param size
	 *          new size of the record
	 * @throws IOException
	 *           any IO errors
	 */
	public void resize(long index, long size) throws IOException;

	/**
	 * Adds a new record using two buffers. This method is more efficient then
	 * using {@link #add(ByteBuffer[])} version as the two buffers are received as
	 * normal parameters. This version of the add method is used when record's
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
	public void add(long index, ByteBuffer b1, ByteBuffer b2) throws IOException;

	/**
	 * Adds a new record at the current indexed position. The record's header and
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
	public void add(long index, ByteBuffer buffer, boolean copy)
	    throws IOException;

	/**
	 * Replaces the record at indexed position with the new record found in the
	 * buffer. The record's header and content are to be found within the buffer
	 * bounded by ByteBuffer's properties <code>position</code> and
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
	public void replace(long index, ByteBuffer buffer, boolean copy)
	    throws IOException;

	/**
	 * <p>
	 * Replaces the indexed record with its own contents and causes the original
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
	public void replaceInPlace(long index) throws IOException;

}
