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

/**
 * <P>
 * Provides indexing services to file based captures. Any file, or more
 * generally finate and immediately determinate capture session can be indexed,
 * in some cases very efficiently and in other not so efficiently. Indexed
 * capture sessions allow the user to access packets as if they were simple
 * lists, hiding all the complexity of actually achieving this kind of a view on
 * a packet dataset behind the capture session.
 * </P>
 * <P>
 * The indexer is a class that turns an ordinary file capture into a similar
 * entity that a Collections list is. All the standard List methods are
 * implemented by the counter part interface IOList that mimics the behaviour
 * and functionality of the regular list with the addtional restriction that
 * each method can throw an IOException at anytime since the actuall capture
 * session data set is backed by IO operations. Due to the fact that capture
 * files can be of increadible size, the implementation algorithm only caches
 * and keeps in memory only portions of the overall packet dataset. Previously
 * cached information may be discarded at any time and when accessed again at a
 * later time may have to be reaquired using various IO operations, at which
 * time there is a risk that an IOException may occur once more.
 * </P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface FileIndexer<G, S, R> extends IndexedFileModifier<S, R>,
    Closeable, Flushable, Iterable<G> {

	/**
	 * Enables or disables the autoflush (enabled by default). Autoflush allows
	 * modifications to a capture file to be flushed to physical storage. With
	 * autoflush disabled, all changes made are kept in memory and the user is
	 * responsible for invoking flush() method manually. A close() operation
	 * always does a flush before closing the connection to the underlying
	 * physical storage.
	 * 
	 * @param state
	 *          true sets autoflush to enabled and false disables it
	 * @throws IOException
	 *           any IO errors as a result of the state change
	 */
	public void setAutoflush(boolean state) throws IOException;

	/**
	 * Main getter method for accessing elements from this indexer. The element
	 * type returned is specific to the indexer implementing this interface.
	 * 
	 * @param index
	 *          index position of the element to get
	 * @return the element at the specified index position
	 * @throws IOException
	 *           any IO errors
	 * @throws IndexOutOfBoundsException
	 *           if index is greater then the amount of records and indexes
	 */
	public G get(long index) throws IOException;

	/**
	 * Number of elements currently present. This is not a static value as it will
	 * change depending on various properties. If the capture file is not in
	 * FileMode.ReadOnly and it is being actively modified, the size returned will
	 * reflect the latest state of the capture sesssion. Also if a file has been
	 * applied or changed this number will change accordingly as well.
	 * 
	 * @return number of elements currently in the indexer
	 * @throws IOException
	 *           any IO errors
	 */
	public long size() throws IOException;

	/**
	 * Maps an index to a global position within the capture session.
	 * 
	 * @param index
	 *          index of the record to map to
	 * @return global position of the record at the specified index
	 * @throws IOException
	 *           any IO errors
	 */
	public long mapIndexToPosition(long index) throws IOException;

	/**
	 * With this method, you can ensure that any portion of the index table are
	 * always kept in memory. The opaque <code>Object</code> returned is a
	 * handle that ensures that the indexes are kept in memory and not reclaimed
	 * by java's garbage collector when memory runs low. As long as the user holds
	 * a hard reference to the returned object, the indexes will be kept in
	 * memory. To release the index references to be possibly garbage collected,
	 * the user has to release any hard references to the object.
	 * 
	 * @param start
	 *          starting index number of of the block of indexes to be retained in
	 *          memory
	 * @param length
	 *          number of consecutive indexes to retain in memory, starting with
	 *          the "start" index
	 * @return an opaque handle which allows prevents requested indexes from being
	 *         reclaimed by java's garbage collector
	 * @throws IOException
	 *           any IO errors if indexes had to be reaquired by rescanning the
	 *           file
	 */
	public Object keepInMemory(long start, long length) throws IOException;
}
