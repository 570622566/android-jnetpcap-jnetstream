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

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jnetstream.JNetStreamInitializer;
import org.jnetstream.capture.FormatType;
import org.jnetstream.packet.Header;
import org.jnetstream.packet.Packet;

/**
 * <P>
 * Formats a packet for text output to an {@link java.lang.Appendable} output.
 * Appendable interface is implemented by standard {@link java.io.PrintStream}
 * (i.e. System.out) and standard {@link java.lang.StringBuilder}, plus of
 * course any custom objects. The packet contents are formatted according to the
 * default PacketFormat definition and sent to the output (Appendable).
 * </P>
 * <P>
 * There are actually 2 separate instances of PacketFormatter to handle
 * PrintStream type output and for formatting into a string buffer. You can
 * retrieve each of these formatters using
 * {@link PacketFormatter#getPrintFormatter()} and
 * {@link PacketFormatter#getStringFormatter()} static methods. The
 * {@link Packet.format} and {@link Packet.toString} methods each use one of the
 * 2 formatters. Packet.format uses the platformFormatter that outputs to a
 * PrintStream by default and the Packet.toString method uses the
 * platformFormatter that output to an internal string buffer which content can
 * be retrieved using the PacketFormatter.toString() method.
 * </P>
 * <P>
 * A number of predefined formats are supplied and can be set as default
 * formatters or simply used directly to delagate packet content directly. The
 * supplied formats can be accessed using the {@link FormatType} enum
 * definition.
 * </P>
 * For example, lets switch the default delagate used from default
 * FormatType.Plain to FormatType.Html:
 * 
 * <pre>
 *     
 *     Packet packet = ... some packet we current have reference to
 *     PacketFormatter.setDefault(PacketFormatter.Type.Html);
 *     PacketFormatter.PacketFormat out = PacketFormatter.getDefault();
 *     out.format(packet);
 *     
 * </pre>
 * 
 * How about switching the output from default System.out to a cutom
 * StringBuilder buffer:
 * 
 * <pre>
 *     
 *     StringBuilder b = new StringBuilder(1024); 
 *     PacketFormatter.setDefault(b);
 *     Packet packet = ... some packet 
 *     PacketFormatter.PacketFormat out = PacketFormatter.getDefault(); 
 *     out.format(packet);
 *     System.out.println(&quot;Our nicely platformFormatter packet:\n&quot; + b.toString());
 *     
 * </pre>
 * 
 * The defaults after initialization are as follows. Default delagate type is
 * {@value PacketFormatter#DEFAULT_FORMAT_TYPE}, default output is System.out.
 * The method {@link Packet#toString()} uses the default platformFormatter as
 * retrieved using the static {@link PacketFormatter#getPrintFormatter()} method
 * call and to the values the deafault platformFormatter is initialized to.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class PacketFormatter implements Closeable, Flushable {

	/**
	 * Builds a new instance of a PacketFormatter.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface Builder {
		public PacketFormatter newInstance(Formatter f, Appendable a, Locale l);
	}

	/**
	 * Default output type
	 */
	private final static FormatterType DEFAULT_FORMAT_TYPE = FormatterType.Plain;

	private final static Map<FormatterType, Builder> registry = new HashMap<FormatterType, Builder>();

	/**
	 * @param formatType
	 * @return
	 */
	public static boolean hasRegisteredBuilder(final FormatterType formatType) {
		return PacketFormatter.registry.get(formatType) != null;
	}

	public static void registerBuilder(final FormatterType formatType,
	    final Builder builder) {
		PacketFormatter.registry.put(formatType, builder);
	}

	private PacketFormatter delagate;

	private final IOException ioException = null;

	private Locale locale;

	private Appendable out;

	private Formatter platformFormatter;

	/**
	 * <P>
	 * Constructs a new packet formatter.
	 * </P>
	 * <P>
	 * The destination of the formatted output is a StringBuilder which may be
	 * retrieved by invoking out() and whose current content may be converted into
	 * a string by invoking toString(). The locale used is the default locale for
	 * this instance of the Java virtual machine.
	 * </P>
	 */
	public PacketFormatter() {
		JNetStreamInitializer.init();
		this.platformFormatter = new Formatter();
		this.out = this.platformFormatter.out();
		this.locale = this.platformFormatter.locale();
		this.delagate = PacketFormatter.registry.get(
		    PacketFormatter.DEFAULT_FORMAT_TYPE).newInstance(
		    this.platformFormatter, this.out, this.locale);
	}

	/**
	 * <P>
	 * Constructs a new formatter with the specified destination.
	 * </P>
	 * <P>
	 * The locale used is the default locale for this instance of the Java virtual
	 * machine.
	 * </P>
	 * 
	 * @param a
	 *          Destination for the formatted output. If a is null then a
	 *          StringBuilder will be created.
	 */
	public PacketFormatter(final Appendable a) {
		JNetStreamInitializer.init();
		this.platformFormatter = new Formatter(a);
		this.out = a;
		this.locale = this.platformFormatter.locale();
		this.delagate = PacketFormatter.registry.get(
		    PacketFormatter.DEFAULT_FORMAT_TYPE).newInstance(
		    this.platformFormatter, this.out, this.locale);
	}

	/**
	 * Constructs a new formatter with the specified destination and locale.
	 * 
	 * @param a
	 *          Destination for the formatted output. If a is null then a
	 *          StringBilder will be created.
	 * @param l
	 *          The locale to apply during formatting. If l is null then no
	 *          localization is applied.
	 */
	public PacketFormatter(final Appendable a, final Locale l) {
		JNetStreamInitializer.init();
		this.platformFormatter = new Formatter(a, l);
		this.out = a;
		this.locale = l;
		this.delagate = PacketFormatter.registry.get(
		    PacketFormatter.DEFAULT_FORMAT_TYPE).newInstance(
		    this.platformFormatter, this.out, this.locale);
	}

	/**
	 * <P>
	 * Constructs a new packet formatter.
	 * </P>
	 * <P>
	 * The destination of the formatted output is a StringBuilder which may be
	 * retrieved by invoking out() and whose current content may be converted into
	 * a string by invoking toString(). The locale used is the default locale for
	 * this instance of the Java virtual machine.
	 * </P>
	 */
	protected PacketFormatter(final boolean init) {
		this.delagate = this;
	}

	public PacketFormatter(final FormatterType formatType) {
		JNetStreamInitializer.init();
		this.platformFormatter = new Formatter();
		this.out = this.platformFormatter.out();
		this.locale = this.platformFormatter.locale();
		this.delagate = PacketFormatter.registry.get(formatType).newInstance(
		    this.platformFormatter, this.out, this.locale);
	}

	public PacketFormatter(final FormatterType formatType, final Appendable a) {
		JNetStreamInitializer.init();
		this.out = a;
		this.platformFormatter = new Formatter(a);
		this.locale = this.platformFormatter.locale();
		this.delagate = PacketFormatter.registry.get(formatType).newInstance(
		    this.platformFormatter, this.out, this.locale);
	}

	public PacketFormatter(final FormatterType formatType, final Appendable a,
	    final Locale l) {
		JNetStreamInitializer.init();
		this.out = a;
		this.locale = l;
		this.platformFormatter = new Formatter(a, l);
		this.delagate = PacketFormatter.registry.get(formatType).newInstance(
		    this.platformFormatter, this.out, this.locale);
	}

	/**
	 * @see java.util.Formatter#close()
	 */
	public void close() {
		this.platformFormatter.close();
		this.delagate.close();
		this.delagate = null;
	}

	/**
	 * @see java.util.Formatter#flush()
	 */
	public void flush() {
		this.platformFormatter.flush();
		this.delagate.flush();
	}

	public PacketFormatter format(final Locale l, final Packet packet) {
		return this;
	}

	/**
	 * @param l
	 * @param delagate
	 * @param args
	 * @return
	 * @see java.util.Formatter#format(java.util.Locale, java.lang.String,
	 *      java.lang.Object[])
	 */
	public Formatter format(final Locale l, final String format,
	    final Object... args) {
		return this.platformFormatter.format(l, format, args);
	}

	/**
	 * @param header
	 * @return
	 * @throws IOException
	 * @see org.jnetstream.packet.format.PacketFormat#format(MetaPacket,
	 *      org.jnetstream.packet.MetaHeader)
	 */
	public PacketFormatter format(final Header header) throws IOException {
		this.delagate.format(null, header);

		return this;
	}

	/**
	 * @param packet
	 * @return
	 * @throws IOException
	 * @see org.jnetstream.packet.format.PacketFormat#format(org.jnetstream.packet.MetaPacket)
	 */
	public PacketFormatter format(final Packet packet) {

		this.delagate.format(packet);

		return this;
	}

	/**
	 * @param delagate
	 * @param args
	 * @return
	 * @see java.util.Formatter#format(java.lang.String, java.lang.Object[])
	 */
	public Formatter format(final String format, final Object... args) {
		return this.platformFormatter.format(format, args);
	}

	/**
	 * @return
	 * @see java.util.Formatter#ioException()
	 */
	public IOException ioException() {
		if (this.ioException != null) {
			return this.ioException;
		} else if (this.platformFormatter.ioException() != null) {
			return this.platformFormatter.ioException();
		} else {
			return this.delagate.ioException();
		}
	}

	/**
	 * @return
	 * @see java.util.Formatter#locale()
	 */
	public Locale locale() {
		return this.locale;
	}

	/**
	 * @return
	 * @see java.util.Formatter#out()
	 */
	public Appendable out() {
		return this.platformFormatter.out();
	}

	protected Locale setLocale(final Locale l) {
		final Locale old = this.locale;
		this.locale = l;

		return old;
	}

	protected void setOut(final Appendable o) {
		this.out = o;
	}

	protected void setPlatformFormatter(final Formatter f) {
		this.platformFormatter = f;
	}

	/**
	 * Returns the contents of the internal string buffer and resets the buffer.
	 * If the PacketFormatter output is not the internal string buffer then the
	 * current Appendable.toString() output is returned. That is the default
	 * toString invokation on the current output.
	 * 
	 * @return contents of the internal string buffer
	 */
	@Override
	public String toString() {
		return this.platformFormatter.out().toString();
	}
}
