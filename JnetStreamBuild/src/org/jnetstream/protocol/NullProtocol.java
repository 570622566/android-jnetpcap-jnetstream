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

import java.util.Set;

import org.jnetstream.protocol.codec.Codec;
import org.jnetstream.protocol.codec.HeaderCodec;

import com.slytechs.utils.memory.BitBuffer;

/**
 * An empty, no action, stub stand-in. All methods throw
 * {@link UnsupportedOperationException}.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public class NullProtocol implements ProtocolInfo {

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#addBinding(org.jnetstream.protocol.Protocol.Binding)
	 */
	public void addBinding(Binding binding) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#getAvailableCodecs()
	 */
	public Codec[] getAvailableCodecs() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#getBindings()
	 */
	public Binding[] getBindings() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#getBitIndex()
	 */
	public int getBitIndex() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#getCharacteristics()
	 */
	public Set getCharacteristics() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#getCharacteristicsValue()
	 */
	public int getCharacteristicsValue() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#getCodec()
	 */
	public HeaderCodec getCodec() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#getID()
	 */
	public ID getID() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#getLength(com.slytechs.utils.memory.BitBuffer, int)
	 */
	public int getLength(BitBuffer bits, int offset) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#getProtocolReferences()
	 */
	public Set getProtocolReferences() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#getSuite()
	 */
	public Suite getSuite() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#getType()
	 */
	public Class getType() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#isLoaded()
	 */
	public boolean isLoaded() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#isProtocolGroup()
	 */
	public boolean isProtocolGroup() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#prioritiseBindings()
	 */
	public boolean prioritiseBindings() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#pushCodec(org.jnetstream.protocol.codec.HeaderCodec)
	 */
	public void pushCodec(HeaderCodec codec) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.jnetstream.protocol.Protocol#setBindingWeight(int)
	 */
	public int setBindingWeight(int weight) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

}
