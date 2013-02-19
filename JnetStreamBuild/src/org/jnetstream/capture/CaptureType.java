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
package org.jnetstream.capture;

import com.slytechs.utils.namespace.Named;

/**
 * Defines the type of capture this is. The type can be very specified even for
 * individual file capture types. There are several differnt types of capture
 * that can take place, and this interface provides generic way of accessing
 * information about the type.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum CaptureType implements Named {
	/**
	 * 
	 */
	LiveCapture,
	
	/**
	 * 
	 */
	FileCapture,
	
	/**
	 * 
	 */
	StreamCapture,
	;

	/* (non-Javadoc)
   * @see com.slytechs.utils.namespace.Named#getName()
   */
  public String getName() {
	  return toString();
  }

}
