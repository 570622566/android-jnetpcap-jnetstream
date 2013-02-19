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

package org.jnetstream.protocol.codec;

import java.io.IOException;
import java.util.Set;

import org.jnetstream.packet.Header;
import org.jnetstream.packet.Packet;

/**
 * A protocol coder/decoder. Codecs are responsible for decoding/dissecting
 * binary data into an object structure. Each protocol can have one or more
 * codecs assigned to it. Some codecs are implemented for a specific protocol
 * in machine or java code. Some codecs are created from NPL definitions such
 * as NPL code is compiled to produce java prepresentation of the source NPL
 * definition. There is also an NPL interpreter codec that inteprets the NPL
 * definition at runtime  to produce 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public interface Codec {
	
	/**
	 * Describe certain characteristics of a codec.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 *
	 */
	public enum Characteristic {
		
		/**
		 * Codec has been implemented natively in "machine code". The protocol
		 * implementation is in machine native code and the implementation uses
		 * java JNI interface to access its services.
		 */
		Native,
		
		/**
		 * A custom codec has been implemented in pure java. The protocol definition
		 * is encoded in java code and not interpreted.
		 */
		Java,
		
		/**
		 * Codec that was generated from an NPL definition. This type of codec is
		 * generated directly from an NPL definition by special NPL compiler that
		 * emmits java code to do exactly what the NPL definition describes. 
		 */
		CrossCompiled,
		
		/**
		 * Codec has been implemented as an intepreter of the
		 * protocol definition.
		 */
		Interpreted,
		;
	}


	public Set<Codec.Characteristic> getCharacteristics();
	
}
