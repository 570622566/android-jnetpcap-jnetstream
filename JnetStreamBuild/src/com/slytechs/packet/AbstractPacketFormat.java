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
package com.slytechs.packet;

import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

import org.jnetstream.packet.format.FieldOption;
import org.jnetstream.packet.format.FormatterType;
import org.jnetstream.packet.format.HeaderOption;
import org.jnetstream.packet.format.PacketFormatter;
import org.jnetstream.packet.format.PacketOption;

import com.slytechs.packet.format.FormatConfig;
import com.slytechs.utils.namespace.Named;
import com.slytechs.utils.namespace.Path;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * 
 */
public abstract class AbstractPacketFormat
    extends PacketFormatter {

	private final Path.Element[] elements = new Path.Element[100];

	private FormatConfig<FieldOption> field = new FormatConfig<FieldOption>();

	private FormatConfig<FieldOption> subField = new FormatConfig<FieldOption>();

	private FormatConfig<HeaderOption> header = new FormatConfig<HeaderOption>();

	private FormatConfig<PacketOption> packet = new FormatConfig<PacketOption>();

//	private FormatConfig<PacketOption> property = new FormatConfig<PacketOption>();

	private Path path = null;

	private int pointer = -1;

	protected IOException ioException = null;

	public AbstractPacketFormat() {
		super(true);
	}

	public FormatConfig<FieldOption> getFieldConfig() {
		return getFieldConfig(Path.empty);
	}

	public FormatConfig<FieldOption> getFieldConfig(final Path path) {
		return this.field.findMost(path);
	}

	public FormatConfig<FieldOption> getSubFieldConfig() {
		return getSubFieldConfig(Path.empty);
	}

	public FormatConfig<FieldOption> getSubFieldConfig(final Path path) {
		return this.subField.findMost(path);
	}

	public FormatConfig<HeaderOption> getHeaderConfig() {
		return getHeaderConfig(Path.empty);
	}

	public FormatConfig<HeaderOption> getHeaderConfig(final Path path) {
		return this.header.findMost(path);
	}

	public FormatConfig<PacketOption> getPacketConfig() {
		return getPacketConfig(Path.empty);
	}

	public FormatConfig<PacketOption> getPacketConfig(final Path path) {
		return this.packet.findMost(path);
	}

	public FormatConfig<PacketOption> getPropertyConfig() {
		return getPropertyConfig(Path.empty);
	}

	public FormatConfig<PacketOption> getPropertyConfig(final Path path) {
		return this.packet.findMost(path);
	}

	protected Path getPath() {
		if (this.path == null) {
			this.path = new Path(0, this.pointer, this.elements);
		}

		return this.path;
	}

	protected String pop() {
		final Path.Element e = this.elements[this.pointer--];
		this.path = null;

		return e.name;
	}

	protected void push(final Named named) {
		this.elements[++this.pointer] = new Path.Element(named.getName());
		this.path = null;
	}

	public IOException ioException() {
		return ioException;
	}
	

	/**
	 * 
	 */
	public static void init() {
		
		if (PacketFormatter.hasRegisteredBuilder(FormatterType.Plain)) {
			return;
		}
	
		PacketFormatter.registerBuilder(FormatterType.Plain,
		    new PacketFormatter.Builder() {

			    public PacketFormatter newInstance(Formatter f, Appendable a, Locale l) {
			    	// TODO: reinstate plain format
				    return new PlainFormat(f, a, l);
//			    	return null;
			    }

		    });
	}

}
