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

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface RegionDataTranslator<T, S> {

	/**
	 * A generic way of creating new data objects for a new region. "translate" is
	 * called when a new region is created that is linked to another region. The
	 * translate method needs to take the source region's data and compute a
	 * return object that is suitable for the linked region.
	 * 
	 * @param source
	 *          source region's data
	 * @return target region's data
	 */
	public T translateData(S source);

}
