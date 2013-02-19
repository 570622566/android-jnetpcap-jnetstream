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
package com.slytechs.capture;

import org.jnetstream.capture.FormatType;
import org.jnetstream.capture.FormatType.Detail;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public class DefaultFormatTypeDetail implements Detail {
	
	private final FormatType type;
	
	private final String detail;
	
	/**
   * @param type
   * @param detail
   */
  public DefaultFormatTypeDetail(final FormatType type) {
	  this.type = type;
	  this.detail = type.getDetailedName();
  }

	/**
   * @param type
   * @param detail
   */
  public DefaultFormatTypeDetail(final FormatType type, final String detail) {
	  this.type = type;
	  this.detail = detail;
  }

	/* (non-Javadoc)
	 * @see org.jnetstream.capture.FormatType.Detail#getDetailedName()
	 */
	public String getDetailedName() {
		return detail;
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.capture.FormatType.Detail#getType()
	 */
	public FormatType getType() {
		return type;
	}
	
	public String toString() {
		return getDetailedName();
	}

}
