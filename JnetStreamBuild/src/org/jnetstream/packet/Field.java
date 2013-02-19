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

import java.io.IOException;

import com.slytechs.utils.memory.BitBuffer;

/**
 * A field within a packet after its been decoded.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Field<T> extends HeaderElement, Iterable<Field<?>> {

	public interface StaticField extends Field<Object> {

		public <T> T read(BitBuffer buffer, int header);

		public <T> void write(BitBuffer buffer, int header, T value);

		public <T> T read(BitBuffer buffer, int header, int field, int length);

		public <T> void write(BitBuffer buffer, int header, int field, int length,
		    T value);

		public Class<?> getType();
	}

	public interface DynamicField<T> extends Field<T> {

		public T getValue() throws IOException;

		public void setValue(T value) throws IOException;
		
		public boolean hasField(Class<? extends DynamicField<?>> c);
		public boolean hasField(StaticField field);
		public boolean hasCompleteField(Class<? extends DynamicField<?>> c);
		public boolean hasCompleteField(StaticField c);
		public <C extends DynamicField<?>> C getField(Class<C> c);
		public <C> DynamicField<C> getField(StaticField field);
	}

	public boolean isSigned();

	public int getOffset();

	public int getLength();

	/**
   * @return
	 * @throws IOException 
   */
  public T getValue() throws IOException;

	/**
   * @return
   */
  public String getValueDescription();

	/**
   * @return
   */
  public Object format();

	/**
   * @return
   */
  public String getValueUnits();

	/**
   * @return
	 * @throws IOException 
   */
  public T getValueInUnits() throws IOException;

	/**
   * @return
   */
  public Field<?>[] getAllFields();

}
