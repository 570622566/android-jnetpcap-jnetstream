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
package com.slytechs.utils.options;

import java.nio.ByteOrder;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum ByteOrderOption implements Option {
	/**
	 * Big endian byte encoding. Most significant byte comes first in a
	 * stream.
	 */
	BIG_ENDIAN(ByteOrder.BIG_ENDIAN),

	/**
	 * Little endian byte encoding. Least significant byte comes first in a
	 * stream.
	 */
	LITTLE_ENDIAN(ByteOrder.LITTLE_ENDIAN)
	;
	private final ByteOrder order;

	/**
	 * 
	 */
	private ByteOrderOption(final ByteOrder order) {
		this.order = order;
	}

	public ByteOrder order() {
		return order;
	}

}
