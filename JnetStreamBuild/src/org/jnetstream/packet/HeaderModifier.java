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
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Mutable interface which allows many different types of operations to be
 * performend upon a container such as adding, removing or clearing headers.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface HeaderModifier extends Updatable {

	public <T extends Header> T add(Class<T> c);

	public <T extends Header> T add(T t);

	public void addAll(Class<? extends Header> c1, Class<?>... c3);

	public void addAll(List<Class<? extends Header>> c);

	/**
	 * Clears all the buffer contents to zeros, effectively removing all headers
	 * from the packet. The difference between clearAll and removeAll is that
	 * removeAll removes all the headers and shrinks the packet buffer size down
	 * to zero length.
	 * @throws IOException 
	 */
	public void clearAll() throws IOException;

	/**
	 * Compacts the current data buffer's capacity to be exactly of current
	 * length. The compacted buffer will no extra capacity to expand. It can
	 * however still be expanded, but methods will require reallocation of buffer
	 * space to increase it capacity. If the compacted buffer is further shrunk,
	 * no copies of data will take place.
	 * 
	 * @return
	 * @throws IOException 
	 */
	public ByteBuffer compact() throws IOException;

	public ByteBuffer copy(byte[] buffer);

	/**
	 * Copies the contents from user supplied buffer to the existing packet
	 * buffer. The old buffer length is changed to match the new buffer lengths.
	 * If the capacity of the current packet buffer is too small to accomodate the
	 * data, a completely new buffer is allocated. All decoded state is cleared
	 * and needs to be rebuilt from scratch by calling on update method.
	 * 
	 * @param buffer
	 * @return
	 */
	public ByteBuffer copy(ByteBuffer buffer);

	/**
	 * Initializes each header by allowing it to pull any
	 * relavent information from the capture device. For example Ethernet2 header
	 * would pull the MAC address from the capture device and assign it to its
	 * source field. While IPv4 header would pull the IP address and assign that to 
	 * its source field. 
	 * @param device
	 */
	public void init(PacketInitializer initializer);

	public <T extends Header> T insert(Class<T> c,
	    Class<? extends Header> afterHeader);

	public <T extends Header> T insert(Class<T> c, int afterIndex);

	public <T extends Header> T remove(Class<T> c);

	public Header remove(int index);

	/**
	 * Removes all headers from the packet and effectively shrinking the packet
	 * data buffer to length 0. The difference between removeAll and clearAll is
	 * that clearAll does not shrink the buffer size, but zero's out all the
	 * values allowing the buffer to be reused.
	 */
	public Header[] removeAll();

	public ByteBuffer replace(byte[] buffer) throws IOException;

	/**
	 * Replaces the current buffer with the user supplied buffer. The internal
	 * buffer reference is replaced to reference the user supplied buffer. All of
	 * decoded state cleared completely and requires rebuilding by calling on
	 * update method.
	 * 
	 * @param buffer
	 *          new buffer which will replaced the old
	 * @return the new buffer
	 * @throws IOException 
	 */
	public ByteBuffer replace(ByteBuffer buffer) throws IOException;
}
