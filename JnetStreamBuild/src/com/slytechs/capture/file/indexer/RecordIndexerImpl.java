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
package com.slytechs.capture.file.indexer;

import java.io.IOException;

import org.jnetstream.capture.file.Record;
import org.jnetstream.capture.file.RecordIndexer;
import org.jnetstream.capture.file.RecordIterator;


/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class RecordIndexerImpl<T extends Record>
    extends AbstractIndexer<T, T, T> implements RecordIndexer<T> {

	/**
	 * @param raw
	 * @throws IOException 
	 */
	public RecordIndexerImpl(final PositionIndexer indexer,
	    final RecordIterator<T> raw) throws IOException {
		super(indexer, raw);
	}
}
