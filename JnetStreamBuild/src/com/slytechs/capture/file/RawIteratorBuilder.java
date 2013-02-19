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
package com.slytechs.capture.file;

import java.io.IOException;

import org.jnetstream.capture.file.RawIterator;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.filter.Filter;


/**
 * Utility interface for creating raw iterators generically.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface RawIteratorBuilder {

	/**
	 * Creates a new instance of RawIterator and returns it.
	 * 
	 * @param filter
	 *          filter to apply to the iterator which can be null if non filter is
	 *          required
	 * @return appropriate instance of raw iterator
	 * @throws IOException 
	 */
	public abstract RawIterator createRawIterator(
	    Filter<RecordFilterTarget> filter) throws IOException;

}
