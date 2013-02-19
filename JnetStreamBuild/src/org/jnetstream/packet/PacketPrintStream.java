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
package org.jnetstream.packet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.jnetstream.packet.format.FormatterType;
import org.jnetstream.packet.format.PacketFormatter;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class PacketPrintStream
    extends PrintStream {

	private final PacketFormatter printFormatter = new PacketFormatter(
	    FormatterType.Plain, this);

	/**
	 * @param out
	 */
	public PacketPrintStream(OutputStream out) {
		super(out);

	}

	/**
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public PacketPrintStream(String fileName) throws FileNotFoundException {
		super(fileName);
	}

	/**
	 * @param file
	 * @throws FileNotFoundException
	 */
	public PacketPrintStream(File file) throws FileNotFoundException {
		super(file);
	}

	/**
	 * @param out
	 * @param autoFlush
	 */
	public PacketPrintStream(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}

	/**
	 * @param fileName
	 * @param csn
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public PacketPrintStream(String fileName, String csn)
	    throws FileNotFoundException, UnsupportedEncodingException {
		super(fileName, csn);
	}

	/**
	 * @param file
	 * @param csn
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public PacketPrintStream(File file, String csn) throws FileNotFoundException,
	    UnsupportedEncodingException {
		super(file, csn);
	}

	/**
	 * @param out
	 * @param autoFlush
	 * @param encoding
	 * @throws UnsupportedEncodingException
	 */
	public PacketPrintStream(OutputStream out, boolean autoFlush, String encoding)
	    throws UnsupportedEncodingException {
		super(out, autoFlush, encoding);
	}

	public void print(Packet packet) {
		printFormatter.format(packet);
	}

	public PacketFormatter getFormatter() {
		return printFormatter;
	}

}
