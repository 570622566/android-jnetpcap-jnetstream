/**
 * $Id$
 *
 * Copyright (C) 2006 Mark Bednarczyk, Sly Technologies, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place,
 * Suite 330, Boston,
 * MA 02111-1307 USA
 * 
 */
package com.slytechs.utils.info;

/**
 * Industry software and other standard constants.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum IndustryStandards {
	IEEE("Insitute of Electrical and Electronics Engineers, Inc."),
	ITU("International Telecommunications Union"),
	IAB("Internet Architecture Board"),
	IETF("Internet Engineering Task Force"),
	NANOG("North American Network Operator's Group"),
	ISCO("The Internet Society"),
	;
	
	private final String description;

	private IndustryStandards(String description) {
		this.description = description;
		
	}

	public String getDescription() {
		return description;
	}
}
