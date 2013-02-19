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
package org.jnetstream.packet.format;

import java.io.IOException;

import org.jnetstream.packet.Field;
import org.jnetstream.packet.Header;
import org.jnetstream.packet.Packet;

/**
 * A set of constants that define the various standard strings that may appear
 * as part of fields formatted output. The constants provide their own builtin
 * defaults, which may be overriden in formatters.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum FieldFormatString implements FormatString<FieldGetter> {
	/**
	 * 
	 */
	FieldName("%s = ", new FieldGetter() {

		public Object get(Packet packet, Header header, Field<?> field) {
			return field.getName();
		}

	}),

	/**
	 * 
	 */
	MultiLineValue(" 0x%02X   [Dec:%d, Oct:%o] %s", new FieldGetter() {

		public Object get(Packet packet, Header header, Field<?> field)
		    throws IOException {
			return field.getValue();
		}

	}, new FieldGetter() {
		public Object get(Packet packet, Header header, Field<?> field)
		    throws IOException {
			return field.getValue();
		}

	}, new FieldGetter() {
		public Object get(Packet packet, Header header, Field<?> field)
		    throws IOException {
			return field.getValue();
		}

	}, new FieldGetter() {
		public Object get(Packet packet, Header header, Field<?> field) {
			if (field.getValueDescription().equals("")) {
				return "";
			}
			return field.getValueDescription();
		}

	}),

	/**
	 * 
	 */
	UniLineValue("%s %s %s", new FieldGetter() {

		public Object get(Packet packet, Header header, Field<?> field) {
			return field.format();
		}

	}, new FieldGetter() {
		public Object get(Packet packet, Header header, Field<?> field) throws IOException {
			if (field.getValueUnits() == null || field.getValueInUnits() == null) {
				return "";
			}
			return "(" + field.getValueInUnits().toString() + " "
			    + field.getValueUnits() + ")";
		}

	}, new FieldGetter() {
		public Object get(Packet packet, Header header, Field<?> field) {
			if (field.getValueDescription().equals("")) {
				return "";
			}
			return "\"" + field.getValueDescription() + "\"";
		}

	}),

	/**
	 * 
	 */
	SubFieldUniLineValue(" 0x%x :: %s", new FieldGetter() {

		public Object get(Packet packet, Header header, Field<?> field)
		    throws IOException {
			return (Integer) field.getValue();
		}

	}, new FieldGetter() {
		public Object get(Packet packet, Header header, Field<?> field) {
			String s = field.toString();

			return s.substring(0, s.length() - 1);
		}

	}),

	;
	private final String defaultString;

	private final FieldGetter[] getters;

	private FieldFormatString(String defaultString, FieldGetter... getters) {
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
	public final FieldGetter[] getGetters() {
		return this.getters;
	}
}