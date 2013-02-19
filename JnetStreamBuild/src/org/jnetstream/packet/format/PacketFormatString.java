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
package org.jnetstream.packet.format;

import org.jnetstream.packet.Packet;

/**
 * A set of constants that define the various standard strings that may appear
 * as part of PacketFormat output. The constants provide their own builtin
 * defaults, which may be overriden in formatters.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public enum PacketFormatString implements FormatString<PacketGetter> {
	/**
	 * Id column
	 */
	Id("%s", new PacketGetter() {

		public Object get(Packet packet) {
			return "####PACKET####";
		}

	}),

	/**
	 * Long summary of the packet contents
	 */
	LongSummary("%s", new PacketGetter() {

		public Object get(Packet packet) {
			return packet.getProperty(Packet.Property.LongSummary);
		}

	}),

	/**
	 * Short summary of the packet contents
	 */
	ShortSummary("%s", new PacketGetter() {

		public Object get(Packet packet) {
			return packet.getProperty(Packet.Property.ShortSummary);
		}

	}),

	;
	private final String defaultString;

	private final PacketGetter[] getters;

	private PacketFormatString(String defaultString, PacketGetter... getters) {
		this.defaultString = defaultString;
		this.getters = getters;

	}

	/**
	 * @return the defaultString
	 */
	public final String getDefaultString() {
		return this.defaultString;
	}

	/**
	 * @return the getters
	 */
	public final PacketGetter[] getGetters() {
		return this.getters;
	}
}
