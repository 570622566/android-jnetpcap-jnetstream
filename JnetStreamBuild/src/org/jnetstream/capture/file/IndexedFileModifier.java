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
import java.util.Collection;
import java.util.List;

/**
 * Interface which allows modifications on the underlying storage using a
 * generic interface. You can manipulate storage contents using elements with
 * the following mutable actions.
 * <UL>
 * <LI> remove - remove at current position, remove a speficied segment, remove
 * a collection of segment, or retain a collection of segments while everything
 * else is removed.
 * <LI> insert - insert new content at current or specified position
 * <LI> replace and retain - replace a segment with new content
 * <LI> swap - swap two elements around so that first occupies the space of the
 * second and visa versa
 * <LI> add - adds new content at the end of storage
 * </UL>
 * 
 * @param <S>
 *          type for all setter operations
 * @param <R>
 *          type for all remove operations
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface IndexedFileModifier<S, R> {

	/**
	 * Aborts any in-memory changes that currently exist. Any changes that have
	 * already been flushed, can not be aborted as they have already been
	 * physically written out to storage. But changes that still remaining in
	 * memory can be safely aborted and discarded.
	 */
	public void abortChanges() throws IOException;

	/**
	 * Appends the content of the buffer to the end of the storage medium.
	 * 
	 * @param element
	 *          add new element at the end
	 * @throws IOException
	 *           any IO errors
	 */
	public void add(S element) throws IOException;

	/**
	 * Add new content from supplied buffer at the index position.
	 * 
	 * @param elements
	 *          adds a list of elements at the current location in order they
	 *          appear in the list
	 * @throws IOException
	 *           any IO errors
	 */
	public void addAll(long index, List<S> elements) throws IOException;

	/**
	 * Adds an array of list at the index location in the order they appear in the
	 * array.
	 * 
	 * @param elements
	 *          array of elements to add
	 * @throws IOException
	 *           any IO errors
	 */
	public void add(long index, S... elements) throws IOException;

	/**
	 * Removes an element at the index location.
	 * 
	 * @throws IOException
	 *           any IO errors
	 */
	public void remove(long index) throws IOException;

	/**
	 * All elements are removed. Care must be taken when using this method as this
	 * has the potential to remove all record or packets from a given file. The
	 * remove is done in memory only until a flush is invoked.
	 */
	public void removeAll() throws IOException;

	/**
	 * <p>
	 * Bulk remove, that removes several elements from the collection. The
	 * elements that are part of the collection, must have been created by the
	 * underlying capture session associated with this interface. For example
	 * FilePackets retain certain information about the position within the file,
	 * the packet came from and this information is needed to efficiently perform
	 * the operation.
	 * </p>
	 * <p>
	 * Note that it is typically a lot more efficient to use bulk methods instead
	 * of doing the same work one iteration at a time. The implementation can use
	 * certain optimazations to perform the task at hand as efficiently as
	 * possible.
	 * </p>
	 * 
	 * @param elements
	 *          collection of segments to be removed from the underlying storage
	 * @throws IOException
	 *           any IO errors
	 */
	public void removeAll(Collection<R> elements) throws IOException;

	/**
	 * Removes a consecutive sequence of elements starting with the element at the
	 * index position.
	 * 
	 * @param count
	 *          number of elements to remove from the current position on
	 * @throws IOException
	 *           any IO errors
	 */
	public void removeAll(long index, long count) throws IOException;

	/**
	 * Bulk remove of all the elements found in the array. All elements must be
	 * associated with this file capture session. All elements will be removed as
	 * efficiently as possible.
	 * 
	 * @param elements
	 *          offset of the region to be removed
	 * @throws IOException
	 *           any IO errors
	 */
	public void removeAll(R... elements) throws IOException;

	/**
	 * Replaces the index element with the new element specified. If the element
	 * being replaced is a Packet or a Record, that original record will no longer
	 * be valid and accessible.
	 * 
	 * @param element
	 *          element containing new data
	 * @throws IOException
	 *           any IO errors
	 */
	public void replace(long index, S element) throws IOException;

	/**
	 * <p>
	 * Bulk remove, that removes all records other then the ones found in the
	 * list. The records that are part of the list, must have been created by the
	 * underlying capture session associated with this interface. For example
	 * FilePackets retain certain information about the position within the file,
	 * the packet came from and this information is needed to efficiently perform
	 * the operation.
	 * <p>
	 * <p>
	 * The retained elements are reorder to match the order of as specified by the
	 * list's order. Therefore it is important order the records to be retained in
	 * a specificaly user defined order. Note you can always use Collections.sort
	 * method to sort the list easily if needed.
	 * </p>
	 * 
	 * @param elements
	 *          collection of elements to be retained in the underlying storage
	 * @throws IOException
	 *           any IO errors
	 */
	public void retainAll(List<R> elements) throws IOException;

	/**
	 * <p>
	 * Bulk remove, that removes all records other then the ones found in the
	 * array. The records that are part of the collection, must have been created
	 * by the underlying capture session associated with this interface. For
	 * example FilePackets retain certain information about the position within
	 * the file, the packet came from and this information is needed to
	 * efficiently perform the operation.
	 * <p>
	 * <p>
	 * The retained elements are reorder to match the order of as specified in the
	 * array.
	 * </p>
	 * 
	 * @param elements
	 *          array of elements to be retained in the underlying storage
	 * @throws IOException
	 *           any IO errors
	 */
	public void retainAll(R... elements) throws IOException;

	/**
	 * Sets the element at index position to the specified element. The index must
	 * address a valid position and not the end of the file or collection.
	 * 
	 * @param index
	 *          index of the element to affect
	 * @param element
	 *          element that will replace the element at the index position
	 * @throws IOException
	 *           any IO errors
	 */
	public void set(long index, S element) throws IOException;

	/**
	 * Swaps the two elements around, physically, within the storage.
	 * 
	 * @param dst
	 *          source record will be copied into this destination record's space
	 * @param src
	 *          destination record will be copied into this source record's space
	 */
	public void swap(R dst, R src) throws IOException;
}
