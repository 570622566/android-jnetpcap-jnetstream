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
package com.slytechs.packet.format;

import java.io.IOException;

import org.jnetstream.packet.Field;
import org.jnetstream.packet.Header;
import org.jnetstream.packet.Packet;
import org.jnetstream.packet.format.FieldFormatString;
import org.jnetstream.packet.format.FieldGetter;
import org.jnetstream.packet.format.HeaderFormatString;
import org.jnetstream.packet.format.HeaderGetter;
import org.jnetstream.packet.format.PacketFormatString;
import org.jnetstream.packet.format.PacketGetter;
import org.jnetstream.packet.format.SubFieldFormatString;
import org.jnetstream.packet.format.SubFieldGetter;

import com.slytechs.utils.format.TableOutput;

public class PacketFormattableOutput
    extends TableOutput {
	
	private Object[] results = new Object[100];

	/**
	 * @param count
	 */
	public PacketFormattableOutput(Appendable out) {
		super(out);
	}

	/**
	 * @param out
	 * @param count
	 */
	public PacketFormattableOutput(Appendable out, int count) {
		super(out, count);
	}

	public PacketFormattableOutput append(PacketFormatString format,
	    Packet packet) throws IOException {
		
		append(format(format, packet));
		
		return this;
	}
	
	public PacketFormattableOutput append(HeaderFormatString format,
	    Packet packet, Header header) throws IOException {
		
		append(format(format, packet, header));
		
		return this;
	}

	public PacketFormattableOutput append(FieldFormatString format,
	    Packet packet, Header header, Field<?> field) throws IOException {

		append(format(format, packet, header, field));
		
		return this;
	}
	
	public PacketFormattableOutput append(SubFieldFormatString format,
	    Packet packet, Header header, Field<?> field, Field<?> subField) throws IOException {

		append(format(format, packet, header, field, subField));
		
		return this;
	}

	public String format(PacketFormatString format,
	    Packet packet) throws IOException {

		int i = 0;
		for (PacketGetter g: format.getGetters()) {
			results[i++] = g.get(packet);
		}
		
		return String.format(format.getDefaultString(), results);
	}
	
	public String format(HeaderFormatString format,
	    Packet packet, Header header) throws IOException {

		int i = 0;
		for (HeaderGetter g: format.getGetters()) {
			results[i++] = g.get(packet, header);
		}
		
		return String.format(format.getDefaultString(), results);
	}

	public String format(FieldFormatString format,
	    Packet packet, Header header, Field<?> field) throws IOException {

		int i = 0;
		for (FieldGetter g: format.getGetters()) {
			results[i++] = g.get(packet, header, field);
		}
		
		return String.format(format.getDefaultString(), results);
	}
	
	public String format(SubFieldFormatString format,
	    Packet packet, Header header, Field<?> field, Field<?> subField) throws IOException {

		int i = 0;
		for (SubFieldGetter g: format.getGetters()) {
			results[i++] = g.get(packet, header, field, subField);
		}
		
		return String.format(format.getDefaultString(), results);
	}
}