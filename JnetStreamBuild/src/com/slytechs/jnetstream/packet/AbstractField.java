/**
 * Copyright (C) 2008 Sly Technologies, Inc.
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
package com.slytechs.jnetstream.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jnetstream.packet.DataField;
import org.jnetstream.packet.Field;

import com.slytechs.utils.io.IORuntimeException;
import com.slytechs.utils.memory.BitBuffer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public abstract class AbstractField<T> extends AbstractData implements Field<T> {
	
	protected final List<Field<?>> elements = new ArrayList<Field<?>>();
	protected final int offset;
	protected final String name;
	protected final boolean signed;
	protected final int length;
	private final DataField constant;

	/**
   * @param constant TODO
	 * @param offset
	 * @param signed
   */
  public AbstractField(BitBuffer buffer, DataField constant, int offset, boolean signed) {
  	super (buffer, offset);
		this.constant = constant;
	  this.name = constant.toString();
	  this.offset = offset;
	  this.signed = signed;
	  this.length = constant.getLength();
  }

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Field#format()
	 */
	public Object format() {
		try {
			if (signed) {
				return getValue();
			} else {
				return toUnsigned(getValue());
			}
    } catch (IOException e) {
	    throw new IORuntimeException(e);
    }
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Field#getAllFields()
	 */
	public Field<?>[] getAllFields() {
		return new Field<?>[0];
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Field#getLength()
	 */
	public int getLength() {
		return length;
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Field#getOffset()
	 */
	public int getOffset() {
		return offset;
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Field#getValueDescription()
	 */
	public String getValueDescription() {
		return "";
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Field#getValueInUnits()
	 */
	public T getValueInUnits() throws IOException {
		return getValue();
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Field#getValueUnits()
	 */
	public String getValueUnits() {
		return "";
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Field#isSigned()
	 */
	public boolean isSigned() {
		return signed;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.namespace.Named#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Field<?>> iterator() {
		return new Iterator<Field<?>>() {

			public boolean hasNext() {
	      return false;
      }

			public Field<?> next() {
	      // TODO Auto-generated method stub
	      throw new UnsupportedOperationException("Not implemented yet");
      }

			public void remove() {
	      // TODO Auto-generated method stub
	      throw new UnsupportedOperationException("Not implemented yet");
      }
			
		};
	}

	public DataField getFieldConstant() {
	  return constant;
  }

}
