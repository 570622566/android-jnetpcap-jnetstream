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
package org.jnetstream.protocol.codec;

import java.io.IOException;

import org.jnetstream.packet.Field;
import org.jnetstream.packet.Header;
import org.jnetstream.protocol.ProtocolInfo;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface HeaderRuntime<H extends Header> extends CodecRuntime {

	/**
	 * @return
	 */
	int getOffset();

	/**
	 * @param destinationField
	 */
	public <T extends Field> void add(T field);

	/**
	 * The get properly cast header container object.
	 * 
	 * @return header container
	 */
	public H getSource();

	/**
	 * @return
	 * @throws IOException 
	 */
	public boolean decode() throws IOException;

	/**
   * @return
   */
  public ProtocolInfo<? extends Header> getProtocol();

}
