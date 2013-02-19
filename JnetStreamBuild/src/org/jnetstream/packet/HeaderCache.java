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

import org.jnetstream.protocol.ProtocolEntry;
import org.jnetstream.protocol.ProtocolInfo;
import org.jnetstream.protocol.codec.HeaderRuntime;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface HeaderCache extends Iterable<Header> {

	/**
	 * @param headerRT
	 */
	public abstract void add(Header headerRT);

	public abstract void add(final int index, final int offset);

	public abstract void add(final int index, final int offset,
	    final Header header);

	/**
	 * Reinitializes the map to initial empty state
	 */
	public abstract void clear();

	public boolean contains(final Class<? extends Header> h);

	/**
	 * Checks if the header entry of the protocol at the bit index exists
	 * 
	 * @return true if the header of the bit index exists
	 */
	public abstract boolean contains(final int index);

	/**
	 * Checks if the header entry of the protocol at the bit index exists
	 * 
	 * @return true if the header of the bit index exists
	 */
	public abstract boolean contains(final int index, final int instance);

	public abstract boolean contains(final ProtocolEntry protocol);

	public abstract Header getHeader(ProtocolEntry protocol);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public abstract Header getHeader(final int index);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public abstract Header getHeader(final int index, final int instance);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public abstract int getOffset(final int index);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public abstract int getOffset(final int index, final int instance);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#isEmpty()
	 */
	public abstract boolean isEmpty();

	public abstract void put(final int index, final int instance,
	    final Header header);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#size()
	 */
	public abstract int size();

	/**
	 * @param h
	 * @return
	 */
	public abstract Header[] toArray(Header[] h);

}