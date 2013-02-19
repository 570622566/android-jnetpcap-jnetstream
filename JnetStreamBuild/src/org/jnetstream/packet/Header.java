/**
 * $Id$ Copyright (C) 2006 Mark Bednarczyk This library is free software; you
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

import java.io.IOException;
import java.util.Set;

import org.jnetstream.protocol.Protocol;
import org.jnetstream.protocol.codec.CodecException;

import com.slytechs.utils.info.OSILayer;
import com.slytechs.utils.memory.BitBuffer;

/**
 * <P>
 * Super interface of all headers contained within a packet. Each packet contans
 * 0 or more protocol specific headers that are typically daisy chained. The
 * header are part of the packet data as returned by live packet capture or as
 * read from a capture/trace file.
 * </P>
 * <P>
 * This header interface does not provide any mutable methods outside of
 * returning a mutable ByteBuffer with the header's content. The reason for this
 * is that the header structure is built based upon the contents of the buffer
 * and the header's NPL definition. If the header's contents have been modified
 * structurally the entire has to be redecoded again.
 * </P>
 * <P>
 * The interface does not provide any methods with dealing with fields within
 * this header or sub headers if they exist. You use the type specific header
 * interface to check for existance of fields and sub headers and then access
 * those fields and sub headers.
 * </P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Header extends HeaderElement, Iterable<HeaderElement> {

	/**
	 * Constants which define various automatic and user defined properties. These
	 * properties are exported by CODECs as well as set directly by the source NPL
	 * definition itself.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public enum StaticProperty {

		/**
		 * Special property that is used to initialize fields when packet is created
		 * from memory. Type <code>PacketInitializer.class</code>
		 */
		Init(PacketInitializer.class, true),

		/**
		 * Frame and physical characteristics of this header. Type
		 * <code>Set<Characteristic>.class</code>.
		 */
		Characteristics(Set.class, true),

		/**
		 * A label that is a more common name for this header. This name is usually
		 * very short compared to the full name of a prototocol header. Type Type
		 * <code>String.class</code>.
		 */
		Common(String.class, true),

		/**
		 * The OSI layer of this header. Type <code>OSILayer.class</code>.
		 */
		OSILayer(OSILayer.class, true),

		/**
		 * Header summary line that summarizes the most important field values on 1
		 * lines, as short as possible. Type <code>String.class</code>.
		 */
		Summary(String.class, false),

		/**
		 * Another even shorter header that summarizes even more tersly the contents
		 * of this header. Type <code>String.class</code>.
		 */
		ShortHeader(String.class, false),
		LongSummary(String.class, false),
		ShortSummary(String.class, false),
		Description(String.class, false);

		private final Class<?> cl;

		private final boolean isStatic;

		private StaticProperty(Class<?> c, boolean isStatic) {
			this.cl = c;
			this.isStatic = isStatic;
		}

		/**
		 * Returns the class that must be used to hold the value of this property
		 * 
		 * @return class object of the value type
		 */
		public Class<?> getValueType() {
			return cl;
		}

		/**
		 * Tells if this property is a static property or dynamic. A static property
		 * is defined once for all instances of a header of this type. A dynamic
		 * property that exists per instance of a header of this type.
		 * 
		 * @return true means that there is only a single instance of this property
		 *         for a header of this type, otherwise the property is dynamic
		 */
		public boolean isStatic() {
			return isStatic;
		}
	}

	/**
	 * Common properties found in every header that are dynamic. Usually these
	 * properties are initialized by the runtime.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public enum DynamicProperty {
		LENGTH,
		OFFSET, ;
	}

	/**
	 * Returns the NPL name of this header.
	 * 
	 * @return name of this header
	 */
	public String getName();

	/**
	 * Tells if this header has been truncated or if all of the headers contents
	 * are contained within the packet buffer. Only what is considered as header
	 * content is included in this check as defined by the underlying NPL
	 * definition. Any data past the header content is not included in this check.
	 * 
	 * @return true if header contents have been truncated and entire header could
	 *         not be decoded, otherwise false
	 * @throws IOException
	 */
	public boolean isTruncated() throws IOException;

	/**
	 * Position or offset from the start of the packet buffer this buffer belongs
	 * to.
	 * 
	 * @return 0-based offset of the start of this header in octets from the
	 *         beginning of the packet buffer
	 */
	public int getOffset();

	/**
	 * Returns the length of this header in bytes.
	 * 
	 * @return number of octets this header occupies
	 */
	public int getLength();

	/**
	 * Returns buffer containing the header's data. The buffer is backed by the
	 * packet buffer which contains all of the contents of the packet. The
	 * returned buffer position, limit and capacity properties are set to
	 * <Code>getPosition()</Code>, <Code>getPosition() + getLength()</Code>
	 * and <Code>getPosition() +
	 * getLength()</Code> respectively. No new data is
	 * allocated and any changes to the returned header buffer are also reflected
	 * in the backing packet buffer.
	 * 
	 * @return buffer which contains just the header data and is backed by the
	 *         underlying packet buffer
	 */
	public BitBuffer getBuffer() throws IOException;

	/**
	 * Retrieves field's runtime environment.
	 * 
	 * @param <T>
	 *          value type of the field
	 * @param c
	 *          class of the field's runtime environment
	 * @return the runtime evironment of the field
	 * @throws IOException
	 * @throws CodecException
	 */
	public <T> Field<T> getField(DataField field) throws CodecException,
	    IOException;

	/**
	 * @return
	 */
	public Class<? extends Header> getType();

	/**
	 * @param key
	 * @return
	 */
	public <T> T getProperty(StaticProperty key);
	
	/**
	 * @param key
	 * @return
	 */
	public <T> T getProperty(DynamicProperty key);

	/**
	 * 
	 * @param <T>
	 * @param name
	 * @return
	 */
	public <T> T getProperty(String name);
	
	/**
	 * 
	 * @return
	 */
	public Protocol getProtocol();

	/**
   * @return
   */
  public Field<?>[] getAllFields();
  
  /**
   * 
   * @return
   */
  public Header[] getAllHeaders();
}
