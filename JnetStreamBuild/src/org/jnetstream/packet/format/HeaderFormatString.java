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

import org.jnetstream.packet.Header;
import org.jnetstream.packet.Packet;

/**
 * A set of constants that define the various standard strings that may appear
 * as part of header's formatted output. The constants provide their own builtin
 * defaults, which may be overriden in formatters.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public enum HeaderFormatString implements FormatString<HeaderGetter> {
	/**
	 * 
	 */
	HeaderName("%s: ", new HeaderGetter() {

		public Object get(Packet packet, Header header) {
			return header.getName();
		}

	}),

	/**
	 * 
	 */
	LongSummary("--- %s ---", Header.StaticProperty.LongSummary),

	/**
	 * 
	 */
	ShortSummary("--- %s ---", Header.StaticProperty.ShortSummary),

	/**
	 * 
	 */
	ShortDescription("--- %s ---", Header.StaticProperty.Description),

	;
	private final String defaultString;

	private final HeaderGetter[] getters;

	private HeaderFormatString(String defaultString, HeaderGetter... getters) {
		this.defaultString = defaultString;
		this.getters = getters;

	}

	private HeaderFormatString(final String defaultString, final Header.StaticProperty property) {
		
		this(defaultString, new HeaderGetter() {

			public Object get(Packet packet, Header header) {
				return header.getProperty(property);
			}

		});
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
	public final HeaderGetter[] getGetters() {
		return this.getters;
	}
}