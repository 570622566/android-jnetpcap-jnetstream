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
package com.slytechs.utils.region;

import java.io.IOException;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface RegionTranslator<T, S> {

	/**
	 * A generic way of creating new data objects for a new region. "translate" is
	 * called when a new region is created that is linked to another region. The
	 * translate method needs to take the source region's data and compute a
	 * return object that is suitable for the linked region.
	 * 
	 * @param linkedFlexRegion
	 *          TODO
	 * @param data
	 *          source region's data
	 * @return target region's data
	 * @throws IOException
	 */
	public T data(FlexRegion<T> linkedFlexRegion, S data);

	/**
	 * @param tStart
	 *          TODO
	 * @param tData
	 *          TODO
	 * @param sRegion
	 *          TODO
	 * @param sStart
	 *          TODO
	 * @param sLength
	 * @param sData
	 *          TODO
	 * @return
	 * @throws IOException 
	 */
	public long oldLength(FlexRegion<T> tRegion, long tStart, T tData,
	    FlexRegion<S> sRegion, long sStart, long sLength, S sData) throws IOException;

	/**
	 * @param linkedFlexRegion
	 *          TODO
	 * @param length
	 * @param data
	 * @return
	 */
	public long newLength(FlexRegion<T> linkedFlexRegion, long length, T data);

	/**
	 * @param linkedFlexRegion
	 *          TODO
	 * @param start
	 * @return
	 */
	public long start(FlexRegion<T> linkedFlexRegion, long start);

	public long getTDataLength(T data);

	public long getLength(T data, long sStart, long sLength);
	
	public T flatten(FlexRegion<T> target, FlexRegion<S> source, S sData);

	/**
	 * @param data
	 *          TODO
	 * @param startRegional
	 * @return
	 */
	public long mapSRegionalToTRegional(T data, long startRegional);

}
