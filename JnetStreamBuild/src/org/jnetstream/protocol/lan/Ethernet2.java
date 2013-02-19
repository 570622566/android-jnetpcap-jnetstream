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
package org.jnetstream.protocol.lan;

import java.io.IOException;

import org.jnetstream.packet.DataField;
import org.jnetstream.packet.Field;
import org.jnetstream.packet.Header;

import com.slytechs.jnetstream.protocol.ProtocolUtils;
import com.slytechs.utils.memory.BitBuffer;
import com.slytechs.utils.net.EUI48;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Ethernet2 extends Header {

	/**
	 * Non-static fields that have constant length but variable offset only
	 * determinable at runtime or are all together optional. These fields require
	 * that specific offset into the header supplied. They do not contain any
	 * logic to calculate the offset.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public enum DynamicField {
		CRC(32);

		private final int length;

		private DynamicField(int length) {
			this.length = length;
		}

		public int readInt(BitBuffer buffer, int offset) {
			return ProtocolUtils.readInt(buffer, offset, length);
		}
	}
	
	public enum DynamicProperty {
		
	}
	
	/**
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface Source extends Field<EUI48> {
		public final static int LENGTH = 48;

		public final static String NAME = "source";

		public static final int OFFSET = 48;

		public static final boolean SIGNED = false;


		public static final String VALUE_DESCRIPTION = null;

		public static final String VALUE_UNITS = null;

	}

	/**
	 * Static fields that have constant offset into the header and length
	 */
	public enum StaticField implements DataField {
		DESTINATION(0, 48),
		SOURCE(48, 48),
		TYPE(96, 16);

		private int length;
		private final int offset;

		private StaticField(int offset, int length) {
			this.offset = offset;
			this.length = length;
		}

		/* (non-Javadoc)
     * @see com.slytechs.jnetstream.packet.DataField#getLength()
     */
    public int getLength() {
	    return length;
    }

		/* (non-Javadoc)
     * @see com.slytechs.jnetstream.packet.DataField#getOffset()
     */
    public int getOffset() {
	    return offset;
    }
	}

	public enum StaticProperty {
		
	}

	/**
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface Type extends Field<Short> {
		public final static int LENGTH = 16;

		public final static String NAME = "type";

		public static final int OFFSET = 96;

		public static final boolean SIGNED = false;

		public final static Class<?> TABLE = EtherType.class;

		public static final String VALUE_DESCRIPTION = null;

		public static final String VALUE_UNITS = null;

	}

	public final static int LENGTH = 48 * 2 + 16;

	public final static String NAME = "Ethernet";

	public EUI48 destination() throws IOException;

	public void destination(EUI48 value) throws IOException;

	public byte[] getDestinationRaw() throws IOException;

	public byte[] getDestinationRaw(byte[] a) throws IOException;

	public <T> T getProperty(Ethernet2.DynamicProperty property);

	public <T> T getProperty(Ethernet2.StaticProperty property);

	public byte[] getSourceRaw() throws IOException;

	public byte[] getSourceRaw(byte[] a) throws IOException;

	public void setDestinationRaw(byte[] value) throws IOException;

	public void setSourceRaw(byte[] value) throws IOException;
	
	public EUI48 source() throws IOException;

	public void source(EUI48 value) throws IOException;
	public short type() throws IOException;

	public void type(short value) throws IOException;
	
	public EtherType typeEnum() throws IOException;

	public void typeEnum(EtherType value) throws IOException;

}
