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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Iterator;

import org.jnetstream.capture.file.HeaderReader;
import org.jnetstream.capture.file.RawIterator;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.filter.Filter;


import com.slytechs.utils.io.AutoflushMonitor;
import com.slytechs.utils.io.Autoflushable;
import com.slytechs.utils.region.FlexRegion;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface FileEditor extends Iterable<ByteBuffer>, Autoflushable,
    AutoflushMonitor, Closeable {

	/**
	 * 
	 */
	public void abortChanges();

	public void add(ByteBuffer b, long global) throws IOException;

	public void autoflushChange(long delta) throws IOException;

	public void close() throws IOException;

	/**
	 * <p>
	 * Flushes all the changes made so far to the underlying file. If no changes
	 * have been made, nothing happens. Changes are flushed to the underlying file
	 * and the edits hierarchy is flattened. Any previously aquired change
	 * iterators should be discarded and new ones aquired. Any attempt to use them
	 * will throw InvalidRegionException since all changes have been invalidated.
	 * Forwarding to the new edits tree will forward any RegionHandles to new
	 * RegionOverlays which now contain the flattened changes.
	 * </p>
	 * <p>
	 * The algorithm in this flush method is optimized for changes that simply
	 * append record to the end. If anyother type of changes, besides the appended
	 * records, have been applied, the changes are flushed using a more generic
	 * algrorithm that ensure entegrity of the entire file.
	 * </p>
	 * 
	 * @see java.io.Flushable#flush()
	 */
	public void flush() throws IOException;

	/**
	 * Creates a handle that keeps track of position and buffer forwards after the
	 * editor is flushed.
	 * 
	 * @param global
	 *          position for which to generate the handle for.
	 * @return handle which will keep track of buffer at the specified position
	 */
	public EditorHandle generateHandle(long global);

	/**
	 * @param global
	 * @param headerReader
	 * @return
	 * @throws IOException
	 */
	public ByteBuffer get(long global, HeaderReader blockGetter)
	    throws IOException;

	public boolean getAutoflush();

	/**
	 * @return
	 */
	public abstract FileChannel getChannel();

	/**
	 * @return
	 */
	public abstract File getFile();

	/**
	 * @return
	 */
	public FlexRegion<PartialLoader> getFlexRegion();

	/**
	 * @return
	 */
	public long getLength();

	/**
	 * @return
	 */
	public HeaderReader getLengthGetter();

	public RawIterator getRawIterator() throws IOException;

	public abstract RawIterator getRawIterator(Filter<RecordFilterTarget> filter)
	    throws IOException;

	/**
	 * @return
	 */
	public boolean isMutable();

	/**
	 * @return
	 */
	public boolean isOpen();

	public Iterator<ByteBuffer> iterator();

	public ByteOrder order();

	public void order(ByteOrder order);

	public void replaceInPlace(final long global, boolean copy)
	    throws IOException;

	public void setAutoflush(boolean state) throws IOException;

}