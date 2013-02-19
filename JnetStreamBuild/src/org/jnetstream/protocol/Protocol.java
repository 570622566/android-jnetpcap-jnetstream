/**
 * Copyright (C) 2008 Sly Technologies, Inc. This library is free software; you
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
package org.jnetstream.protocol;

import org.jnetstream.packet.ProtocolFilterTarget;

/**
 * Contains basic information about a protocol such as a unique ID that is only
 * valid in single VM and single instance, name of the protocol. This is the
 * base interface for {@link ProtocolInfo} which provides more information about
 * the protocol and also acts as a {@link ProtocolRegistry} entry.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Protocol extends ProtocolFilterTarget {

	/**
	 * A list of builtin protocols, used for testing and other purposes
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public enum Builtin implements Protocol {
		Null,
		Test,
	}

	/**
	 * Name of the protocol
	 */
	public String toString();

}
