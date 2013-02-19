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
package com.slytechs.jnetstream.packet;

import java.util.Arrays;
import java.util.Iterator;

import org.jnetstream.packet.Header;
import org.jnetstream.packet.HeaderCache;
import org.jnetstream.protocol.ProtocolEntry;
import org.jnetstream.protocol.ProtocolInfo;
import org.jnetstream.protocol.ProtocolRegistry;
import org.jnetstream.protocol.codec.HeaderRuntime;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
@SuppressWarnings("unchecked")
public class FastHeaderCache implements HeaderCache {

	private static final int BIT_SHIFT = 20;

	private static final int COUNT = 10;

	private static final int INDEX_MASK = 0xFFF00000;

	private static final int VALUE_MASK = ~INDEX_MASK;

	private long bits = 0;

	private int last = -1; // Index of last entry into offsets

	private int[] offsets = new int[FastHeaderCache.COUNT];

	private Header[] refs = new Header[FastHeaderCache.COUNT];

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.FastHeaderCache#add(org.jnetstream.protocol.codec.HeaderRuntime)
	 */
	public void add(final Header header) {
		final int index = ProtocolRegistry.lookup(header.getProtocol()).getIndex();
		final int offset = header.getOffset();

		add(index, offset, header);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.packet.FastHeaderCache#add(int, int)
	 */
	public void add(final int index, final int offset) {
		final int mask = index << BIT_SHIFT;

		bits |= (1L << index);

		last++;

		if (last == offsets.length) {
			this.reallocate();
		}

		offsets[last] = mask | offset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.packet.FastHeaderCache#add(int, int,
	 *      org.jnetstream.protocol.codec.HeaderRuntime)
	 */
	public void add(final int index, final int offset, final Header header) {
		final int mask = index << BIT_SHIFT;
		bits |= (1L << index);

		last++;

		if (last == offsets.length) {
			this.reallocate();
		}

		offsets[last] = mask | offset;
		refs[last] = header;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.packet.FastHeaderCache#clear()
	 */
	public void clear() {
		bits = 0;
		Arrays.fill(offsets, 0);
		Arrays.fill(refs, null);
		last = -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.FastHeaderCache#contains(java.lang.Class)
	 */
	public boolean contains(Class<? extends Header> c) {

		final ProtocolEntry protocol = ProtocolRegistry.lookup(c);

		if (protocol == null) {
			return false;
		}

		final int index = protocol.getIndex();

		if (index == ProtocolInfo.NO_BIT_INDEX) {
			for (int i = 0; i < refs.length; i++) {
				final Header rt = refs[i];
				if (rt == protocol) {
					return true;
				}
			}

			return false;

		} else {
			return contains(index);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.packet.FastHeaderCache#contains(int)
	 */
	public boolean contains(final int index) {
		return (bits & (1L << index)) != 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.packet.FastHeaderCache#contains(int, int)
	 */
	public boolean contains(final int index, final int instance) {
		if ((bits & (1L << index)) == 0) {
			return false;
		}

		final int mask = index << BIT_SHIFT;
		int inst = instance;
		for (int i = 0; i < offsets.length; i++) {
			final int o = offsets[i];

			if ((o & INDEX_MASK) == mask) {
				if (inst-- == 0) {
					return true;
				}
			}
		}

		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.packet.FastHeaderCache#contains(org.jnetstream.protocol.Protocol)
	 */
	public boolean contains(final ProtocolEntry protocol) {
		final int index = protocol.getIndex();

		if (index == 0) {

			for (int i = 0; i < refs.length; i++) {
				if (refs[i].getProtocol() == protocol.getProtocol()) {
					return true;
				}
			}

			return false;

		} else {
			return contains(index);
		}
	}

	private int[] copyOf(int[] src, int size) {

		int[] dst = new int[size];
		if (size < src.length) {
			System.arraycopy(src, 0, dst, 0, size);

		} else {
			System.arraycopy(src, 0, dst, 0, src.length);
		}

		return (int[]) dst;

	}

	private <S extends Header> S[] copyOf(S[] src, int size) {

		Header[] dst = new Header[size];
		if (size < src.length) {
			System.arraycopy(src, 0, dst, 0, size);

		} else {
			System.arraycopy(src, 0, dst, 0, src.length);
		}

		return (S[]) dst;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.FastHeaderCache#getHeader(java.lang.Class)
	 */
	public Header getHeader(Class<? extends Header> c) {

		final ProtocolEntry protocol = ProtocolRegistry.lookup(c);

		if (protocol == null) {
			return null;
		}

		return getHeader(protocol);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.packet.FastHeaderCache#getHeader(int)
	 */
	public Header getHeader(final int index) {
		final int mask = index << BIT_SHIFT;

		for (int i = 0; i < offsets.length; i++) {
			final int o = offsets[i];

			if ((o & FastHeaderCache.INDEX_MASK) == mask) {
				return refs[i];
			}

		}

		throw new IndexOutOfBoundsException(
		    "Header information not found for index " + index
		        + ". You must use hasHeader first.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.packet.FastHeaderCache#getHeader(int, int)
	 */
	public Header getHeader(final int index, final int instance) {
		final int mask = index << BIT_SHIFT;
		int inst = instance;

		for (int i = 0; i < offsets.length; i++) {
			final int o = offsets[i];

			if ((o & FastHeaderCache.INDEX_MASK) == mask) {
				if (inst-- == 0) {
					return refs[i];
				}
			}

		}

		throw new IndexOutOfBoundsException(
		    "Header information not found for index " + index + "." + instance
		        + ". You must use hasHeader first.");
	}

	public Header getHeader(final ProtocolEntry protocol) {
		final int index = protocol.getIndex();

		if (index != ProtocolInfo.NO_BIT_INDEX) {
			return getHeader(index);
		}

		for (int i = 0; i < refs.length; i++) {
			final Header rt = refs[i];
			if (rt == protocol) {
				return rt;
			}

		}

		throw new IndexOutOfBoundsException(
		    "Header information not found for protocol " + protocol
		        + ". You must use hasHeader first.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.packet.FastHeaderCache#getOffset(int)
	 */
	public int getOffset(final int index) {
		final int mask = index << BIT_SHIFT;

		for (final int o : offsets) {
			if ((o & FastHeaderCache.INDEX_MASK) == mask) {
				return o & FastHeaderCache.VALUE_MASK;
			}

		}

		throw new IndexOutOfBoundsException(
		    "Offset information not found for index " + index
		        + ".0. You must use hasHeader first.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.packet.FastHeaderCache#getOffset(int, int)
	 */
	public int getOffset(final int index, final int instance) {
		final int mask = index << BIT_SHIFT;
		int inst = instance;

		for (final int o : offsets) {
			if ((o & FastHeaderCache.INDEX_MASK) == mask) {
				if (inst-- == 0) {
					return o & FastHeaderCache.VALUE_MASK;
				}
			}

		}

		throw new IndexOutOfBoundsException(
		    "Offset information not found for index " + index + "." + instance
		        + ". You must use hasHeader first.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#isEmpty()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.packet.FastHeaderCache#isEmpty()
	 */
	public boolean isEmpty() {
		return bits == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Header> iterator() {
		return new Iterator<Header>() {
			private int i = 0;

			public boolean hasNext() {
				return i <= last;
			}

			public Header next() {
				return refs[i++];
			}

			public void remove() {
				throw new UnsupportedOperationException(
				    "Remove operation not supported in fast header cache. "
				        + "Use Packet.remove instead.");
			}

		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.packet.FastHeaderCache#put(int, int,
	 *      org.jnetstream.protocol.codec.HeaderRuntime)
	 */
	public void put(final int index, final int instance, final Header header) {
		final int mask = index << BIT_SHIFT;
		bits |= (1L << index);
		int inst = instance;

		for (int i = 0; i < offsets.length; i++) {
			final int o = offsets[i];

			if ((o & FastHeaderCache.INDEX_MASK) == mask) {
				if (inst-- == 0) {
					refs[i] = header;
					return;
				}
			}
		}

		throw new IllegalArgumentException("Unable to find index " + index + "."
		    + instance + ". You must use hasHeader first.");
	}

	/**
	 * 
	 */
	private void reallocate() {
		offsets = copyOf(offsets, offsets.length + FastHeaderCache.COUNT);
		refs = copyOf(refs, refs.length + FastHeaderCache.COUNT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#size()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.packet.FastHeaderCache#size()
	 */
	public int size() {
		return last + 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.packet.FastHeaderCache#toArray(org.jnetstream.packet.Header[])
	 */
	public Header[] toArray(Header[] h) {
		for (int i = 0; i < last + 1; i++) {
			h[i] = refs[i];
		}

		return h;
	}

	public String toString() {
		return "" + bits + Arrays.toString(this.offsets);
	}

}
