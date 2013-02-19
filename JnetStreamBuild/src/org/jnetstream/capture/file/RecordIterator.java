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

import com.slytechs.utils.collection.IOIterator;
import com.slytechs.utils.collection.SeekResult;

/**
 * An iterator that allows iteration over records contained in a capture file.
 * Simple {@link IOIterator#next} and {@link IOIterator#hasNext} methods are
 * used to iterate over long sequences of records which physically reside on a
 * some storage device.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface RecordIterator<T extends Record> extends FileIterator<T, T, T> {

	/**
	 * Skips over the block header, which is always the first record and aligns on
	 * the second record which is usually the data record. The first record past
	 * the main block record.
	 * 
	 * @throws IOException
	 */
	public SeekResult seekSecond() throws IOException;

}
