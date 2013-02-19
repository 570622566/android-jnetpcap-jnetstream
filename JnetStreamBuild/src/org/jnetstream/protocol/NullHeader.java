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
package org.jnetstream.protocol;

import java.io.IOException;
import java.util.Iterator;

import org.jnetstream.packet.DataField;
import org.jnetstream.packet.Field;
import org.jnetstream.packet.Header;
import org.jnetstream.packet.HeaderElement;
import org.jnetstream.protocol.codec.CodecException;

import com.slytechs.utils.memory.BitBuffer;

/**
 * An empty, no action, stub stand-in. All methods throw
 * {@link UnsupportedOperationException}.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public class NullHeader implements Header {

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Header#getBuffer()
	 */
	public BitBuffer getBuffer() throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Header#getField(java.lang.Class)
	 */
	public <T extends Field<?>> T getField(Class<T> c) throws CodecException,
	    IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Header#getLength()
	 */
	public int getLength() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Header#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Header#getOffset()
	 */
	public int getOffset() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Header#getProperty(org.jnetstream.packet.Header.CommonStaticProperty)
	 */
	public <T> T getProperty(StaticProperty key) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Header#getProperty(org.jnetstream.packet.Header.CommonDynamicProperty)
	 */
	public <T> T getProperty(DynamicProperty key) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Header#getType()
	 */
	public Class<? extends Header> getType() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.packet.Header#isTruncated()
	 */
	public boolean isTruncated() throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
   * @see org.jnetstream.packet.Header#getProperty(java.lang.String)
   */
  public <T> T getProperty(String name) {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see org.jnetstream.packet.Header#getProtocol()
   */
  public Protocol getProtocol() {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see org.jnetstream.packet.Header#getAllFields()
   */
  public Field<?>[] getAllFields() {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see org.jnetstream.packet.Header#getAllHeaders()
   */
  public Header[] getAllHeaders() {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see java.lang.Iterable#iterator()
   */
  public Iterator<HeaderElement> iterator() {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see org.jnetstream.packet.Header#getField(org.jnetstream.packet.DataField)
   */
  public <T> Field<T> getField(DataField field) throws CodecException,
      IOException {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

	/* (non-Javadoc)
   * @see org.jnetstream.packet.HeaderElement#getFieldConstant()
   */
  public DataField getFieldConstant() {
	  // TODO Auto-generated method stub
	  throw new UnsupportedOperationException("Not implemented yet");
  }

}
