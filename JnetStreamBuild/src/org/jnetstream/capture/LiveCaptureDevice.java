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
package org.jnetstream.capture;

import org.jnetstream.filter.Filter;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public interface LiveCaptureDevice extends CaptureDevice {

	/**
	 * Gets the assigned filter to this capture device. Null if none exists. The
	 * currently assigned filter may have been assigned either by
	 * {@link #setFilter} method or at the time the capture device was opened
	 * using one of {@link Captures#openLive}, {@link Captures#openLive} or
	 * {@link Captures.Factory#openLive} methods.
	 * 
	 * @return currently assigned filter or null if none assigned
	 */
	public Filter<?> getFilter();
}
