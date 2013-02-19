/**
 * Copyright (C) 2007 Sly Technologies, Inc.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.slytechs.capture.file.indexer;

import java.io.IOException;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public interface PositionIndexer {

	/**
	 * Translates an index into a position. The method translates index (from
	 * address domain for indexes) into a position (from a second address domain
	 * of positions).
	 * 
	 * @param globalIndex
	 *          global index to translate
	 * @return global position after translation
	 * @throws IOException
	 *           any IO errors
	 */
	@SuppressWarnings("unchecked")
	public abstract Long get(long globalIndex) throws IOException;

	/**
	 * @return
	 */
	public abstract long size();

	/**
   * @return
   */
  public abstract int getSegmentCount();

	/**
   * @param start
   * @param length
   * @return
	 * @throws IOException 
   */
  public abstract Object keepInMemory(long start, long length)
	    throws IOException;

}