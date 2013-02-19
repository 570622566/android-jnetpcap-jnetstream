/**
 * Copyright (C) 2006 Sly Technologies, Inc. This library is free software; you
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
package com.slytechs.packet;

import java.io.IOException;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.jnetstream.packet.Field;
import org.jnetstream.packet.Header;
import org.jnetstream.packet.HeaderElement;
import org.jnetstream.packet.Packet;
import org.jnetstream.packet.format.FieldFormatString;
import org.jnetstream.packet.format.FieldOption;
import org.jnetstream.packet.format.HeaderFormatString;
import org.jnetstream.packet.format.HeaderOption;
import org.jnetstream.packet.format.PacketFormatString;
import org.jnetstream.packet.format.PacketFormatter;
import org.jnetstream.packet.format.PacketOption;
import org.jnetstream.packet.format.SubFieldFormatString;

import com.slytechs.packet.format.PacketFormattableOutput;
import com.slytechs.utils.format.Formattable.Justification;
import com.slytechs.utils.namespace.Named;
import com.slytechs.utils.string.BitFieldCharSequence;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class PlainFormat
    extends AbstractPacketFormat {

	private Appendable src;

	private PacketFormattableOutput out;

	private int largestHeaderNameWidth;

	private int largestFieldNameWidth;

	public PlainFormat() {
		super();
		this.src = System.out;
		setOut(this.src);

		initConfig();
	}

	/**
	 * @param f
	 * @param a
	 * @param l
	 */
	public PlainFormat(Formatter f, Appendable a, Locale l) {
		super();

		this.src = a;
		setPlatformFormatter(f);
		setOut(a);
		setLocale(l);

		initConfig();
	}

	private void initConfig() {
		getPacketConfig().setOptions(PacketOption.values());
		getPropertyConfig().setOptions(PacketOption.values());
		getHeaderConfig().setOptions(HeaderOption.values());
		getFieldConfig().setOptions(FieldOption.values());
		getSubFieldConfig().setOptions(FieldOption.values());

		for (PacketFormatString f : PacketFormatString.values()) {
			getPacketConfig().getFormatStrings().put(f.toString(), f);
		}

		getPacketConfig().getOptions().remove(PacketOption.ShortSummary);

		for (HeaderFormatString f : HeaderFormatString.values()) {
			getHeaderConfig().getFormatStrings().put(f.toString(), f);
		}

		getHeaderConfig().getOptions().remove(HeaderOption.ShortSummary);

		for (FieldFormatString f : FieldFormatString.values()) {
			getFieldConfig().getFormatStrings().put(f.toString(), f);
		}

		for (SubFieldFormatString f : SubFieldFormatString.values()) {
			getSubFieldConfig().getFormatStrings().put(f.toString(), f);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jnetstream.packet.PacketFormat#format(jnetstream.packet.Packet)
	 */
	public PacketFormatter format(Packet packet) {
		try {
			return doFormat(packet);
		} catch (IOException e) {
			ioException = e;
			return this;
		}
	}

	public PacketFormatter doFormat(Packet packet) throws IOException {
		out.append(PacketFormatString.Id, packet);

		out.nextColumn();
		/*
		 * Because options are an EnumSet we can rely on the ordering of the
		 * elements within the set. So the Option enum structure is the place where
		 * the ordering is actually defined, since the order of that enum structure
		 * is copied.
		 */
		for (PacketOption option : getPacketConfig().getOptions()) {

			switch (option) {
				case HeaderName:
					largestHeaderNameWidth =
					    Named.Util.getLargestLength(packet.getAllHeaders(), out.format(
					        PacketFormatString.Id, packet).length() + 1);
					break;

				case LongSummary:
					out.setSpanColumns(1);
					out.getColumn().setJustification(Justification.Left);
					out.append(PacketFormatString.LongSummary, packet);
					out.flush();
					out.clearSpanColumns();
					out.getColumn().setJustification(Justification.Right);
					out.nextRow(0);
					break;

				case ShortSummary:
					out.setSpanColumns(1);
					out.getColumn().setJustification(Justification.Left);
					out.append(PacketFormatString.ShortSummary, packet);
					out.flush();
					out.clearSpanColumns();
					out.getColumn().setJustification(Justification.Right);

					out.nextRow(0);
					break;

				case ShowHeader:

					for (Header h : packet) {
						format(packet, h);
					}
					break;
			}
		}

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jnetstream.packet.PacketFormat#format(jnetstream.packet.Header)
	 */
	public PacketFormatter format(Packet packet, Header header) {
		try {
			return doFormat(packet, header);
		} catch (IOException e) {
			ioException = e;
			return this;
		}

	}

	public PacketFormatter doFormat(Packet packet, Header header)
	    throws IOException {
		push(header);
		List<HeaderOption> options = getHeaderConfig(getPath()).getOptions();

		/*
		 * Because options are an EnumSet we can rely on the ordering of the
		 * elements within the set. So the Option enum structure is the place where
		 * the ordering is actually defined, since the order of that enum structure
		 * is copied.
		 */
		for (HeaderOption option : options) {

			switch (option) {

				case ShowValue:
					largestFieldNameWidth =
					    Named.Util.getLargestLength(header.getAllFields(), 12);
					out.getColumn(1).setWidth(largestFieldNameWidth + 3);
					break;

				case HeaderName:
					out.getColumn().setWidth(largestHeaderNameWidth);
					out.append(HeaderFormatString.HeaderName, packet, header);
					out.setPad();
					out.nextColumn();
					break;

				case LongSummary:
					if (header.getProperty(Header.StaticProperty.LongSummary) == null) {
						break;
					}

					out.setSpanColumns(1);
					out.getColumn().setJustification(Justification.Left);
					out.append(HeaderFormatString.LongSummary, packet, header);
					out.nextRow();

					out.clearSpanColumns();
					out.getColumn().setJustification(Justification.Right);

					break;

				case ShortSummary:
					if (header.getProperty(Header.StaticProperty.ShortSummary) == null) {
						break;
					}

					out.setSpanColumns(1);
					out.getColumn().setJustification(Justification.Left);
					out.append(HeaderFormatString.ShortSummary, packet, header);
					out.nextRow();

					out.clearSpanColumns();
					out.getColumn().setJustification(Justification.Right);
					break;

				case ShortDescription:
					if (header.getProperty(Header.StaticProperty.Description) == null) {
						break;
					}

					out.setSpanColumns(1);
					out.getColumn().setJustification(Justification.Left);
					out.append(HeaderFormatString.ShortDescription, packet, header);
					out.nextRow();

					out.clearSpanColumns();
					out.getColumn().setJustification(Justification.Right);
					break;

				case ShowHeader:

					out.nextRow();

					for (HeaderElement child : header) {
						if (child instanceof Header) {
							format(packet, (Header) child);
						} else if (child instanceof Field) {
							format(packet, header, (Field<?>) child);
						} else {
							throw new IllegalStateException(
							    "Found unsupported header child type "
							        + child.getClass().getName());
						}
						out.nextRow(1);

					}

					out.nextRow(0);
					break;

				default:
			}
		}

		pop();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jnetstream.packet.PacketFormat#format(jnetstream.packet.Field)
	 */
	public PacketFormatter format(Packet packet, Header header, Field<?> field) {
		try {
			return doFormat(packet, header, field);
		} catch (IOException e) {
			ioException = e;
			return this;
		}
	}

	public PacketFormatter doFormat(Packet packet, Header header, Field<?> field)
	    throws IOException {
		push(field);

		out.setPosition(1);
		/*
		 * Because options are an EnumSet we can rely on the ordering of the
		 * elements within the set. So the Option enum structure is the place where
		 * the ordering is actually defined, since the order of that enum structure
		 * is copied.
		 */
		for (FieldOption option : getFieldConfig(getPath()).getOptions()) {

			switch (option) {
				case LongSummary:
					break;

				case ShortSummary:
					break;

				case FieldName:
					out.append(FieldFormatString.FieldName, packet, header, field);
					out.nextColumn();

					break;

				case ShowValue:
					if (field.getAllFields().length != 0) {

						out.append(new BitFieldCharSequence(field.getAllFields().length, 0,
						    field.getAllFields().length, (Number) field.getValue()));
						out.append(FieldFormatString.MultiLineValue, packet, header, field);
					} else {
						out.append(FieldFormatString.UniLineValue, packet, header, field);
					}

					break;

				case ShowSubField:

					int p = 0;

					for (Field<?> child : field) {
						out.nextRow();
						formatSubField(packet, header, field, (Field<?>) child, field
						    .getAllFields().length, p);

						p += child.getAllFields().length;
					}

					break;
			}

		}

		pop();
		return this;
	}

	public PacketFormatter formatSubField(Packet packet, Header header,
	    Field<?> field, Field<?> subField, int overallSize, int start) {
		try {
			return doFormatSubField(packet, header, field, subField, overallSize,
			    start);
		} catch (IOException e) {
			ioException = e;
			return this;
		}
	}

	public PacketFormatter doFormatSubField(Packet packet, Header header,
	    Field<?> field, Field<?> subField, int overallSize, int start)
	    throws IOException {
		push(field);

		/*
		 * Because options are an EnumSet we can rely on the ordering of the
		 * elements within the set. So the Option enum structure is the place where
		 * the ordering is actually defined, since the order of that enum structure
		 * is copied.
		 */
		for (FieldOption option : getFieldConfig(getPath()).getOptions()) {

			switch (option) {
				case ShowSubFieldValue:
					out.append(new BitFieldCharSequence(overallSize, start, subField
					    .getAllFields().length, (Number) subField.getValue()));
					@SuppressWarnings("unused")
					String s = field.toString();
					out.append(SubFieldFormatString.SubFieldUniLineValue, packet, header,
					    field, subField);
					break;

			}

		}

		pop();

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jnetstream.packet.PacketFormat#setOut(java.lang.Appendable)
	 */
	public void setOut(Appendable out) {

		this.out = new PacketFormattableOutput(out, 3);

		this.out.getColumn(0).setJustification(Justification.Right);
		this.out.getColumn(0).setWidth(12);

		this.out.getColumn(1).setJustification(Justification.Right);
		this.out.getColumn(1).setWidth(12);

		this.out.getColumn(2).setJustification(Justification.Left);
		this.out.getColumn(2).setWidth(56);

		super.setOut(out);
	}

}
