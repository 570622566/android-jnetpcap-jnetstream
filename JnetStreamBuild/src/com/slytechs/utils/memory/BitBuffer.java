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
package com.slytechs.utils.memory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <p>
 * A ByteBuffer backed buffer that allows bit level access and positioning. Bit
 * buffer also provides enhanced position management. Instead of the typical
 * mark/reset operations, positions can be pushed and popped. The contents are
 * already in a buffer no additional buffering is required. The buffer follows
 * the same rules as a normal ByteBuffer, that is it has the same properties
 * such as position, limit and capacity. The capacity property is immutable and
 * can not be changed once the buffer has been created. You can however
 * duplicate and slice the buffer like its byte based counter part.
 * </p>
 * <p>
 * Using the {@link #push} method stores the entire state of the buffer on a
 * stack and {@link #pop} restores the state for the buffer. The state saved
 * includes byte-order, position and limit properties.
 * </p>
 * 
 * <pre>
 * BitBuffer b = new BitBuffer(ByteBuffer.allocate(10));
 * b.position(5);
 * b.order(ByteOrder.LITTLE_ENDIAN);
 * 
 * b.push(); // Saves entire state of the buffer
 * 
 * b.position(6);
 * b.order(ByteOrder.BIG_ENDIAN);
 * 
 * System.out
 *     .printf(&quot;Position=%d, order=%s\n&quot;, b.position(), b.order().toString());
 * 
 * </pre>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class BitBuffer {

	private static class PositionEntry {
		public int bits;

		public int bytes;
	}

	private static final ThreadLocal<BitBuffer> cache =
	    new ThreadLocal<BitBuffer>() {

		    /*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.ThreadLocal#initialValue()
				 */
		    @Override
		    protected BitBuffer initialValue() {
			    return new BitBuffer();
		    }

	    };

	private static final int M = 0x000000FF;

	/**
	 * Amount by which stack array length is expanded when needed
	 */
	private static final int STACK_INC = 50;

	/**
	 * @param defaultBufSize
	 * @return
	 */
	public static BitBuffer allocate(int capacity) {
		return wrap(ByteBuffer.allocate(capacity));
	}

	/**
	 * Wraps a byte array into a buffer. The new buffer will be backed by the
	 * given byte array; that is, modifications to the buffer will cause the array
	 * to be modified and vice versa. The new buffer's capacity and limit will be
	 * array.length in bits, its position will be zero, and its mark will be
	 * undefined. Its backing array will be the given array, and its array offset
	 * will be zero.
	 * 
	 * @param a
	 *          array to use a data content
	 * @return a new bit buffer
	 */
	public static BitBuffer wrap(byte[] a) {
		return new BitBuffer(ByteBuffer.wrap(a));
	}

	/**
	 * Wraps a byte buffer into a bit buffer. The new buffer will be backed by the
	 * given byte buffer; that is, modifications to the buffer will cause the
	 * array to be modified and vice versa. Changes to this buffer's content will
	 * be visible in the new buffer, and vice versa; the two buffers' position,
	 * limit, and mark values will be independent.
	 * 
	 * @param buffer
	 *          byte buffer that will back this buffer
	 * @return a new bit buffer
	 */
	public static BitBuffer wrap(final ByteBuffer buffer) {
		return new BitBuffer(BufferUtils.duplicate(buffer));
	}

	/**
	 * Wraps a byte buffer into a bit buffer on a per thread basis. Each thread
	 * will get a new version of bit buffer, backed by the supplied byte buffer.
	 * The returned bit buffers are thread-local cached. The new buffer will be
	 * backed by the given byte buffer; that is, modifications to the buffer will
	 * cause the array to be modified and vice versa. Changes to this buffer's
	 * content will be visible in the new buffer, and vice versa; the two buffers'
	 * position, limit, and mark values will be independent.
	 * 
	 * @param buffer
	 * @return
	 */
	public static BitBuffer wrapThreadLocal(final ByteBuffer buffer) {
		final BitBuffer front = cache.get();

		/* Initialize the cached entry to be the front-end of the back buffer */
		front.delagate = buffer;
		front.current = 0;
		front.posBits = 0;
		front.posBytes = 0;
		front.posCapacity = 0;
		front.posIndex = 0;

		return front;
	}

	/**
	 * Contains the current byte's value
	 */
	private byte current;

	/**
	 * Main buffer backing this buffer
	 */
	private ByteBuffer delagate;

	/**
	 * Zero-based fragment position within a byte in bits.
	 */
	private int posBits;

	/**
	 * Zero-based position in bytes
	 */
	private int posBytes;

	private int posCapacity = STACK_INC;

	private int posIndex;

	private PositionEntry[] posStack = new PositionEntry[posCapacity];

	/**
	 * Special constructor which allows empty uninitialized bit buffers to be
	 * created so that they can be cached and pulled when needed to wrap around
	 * byte buffers, very efficiently.
	 */
	private BitBuffer() {
		// empty
	}

	/**
	 * @param delagate
	 */
	public BitBuffer(final ByteBuffer delagate) {
		this.delagate = delagate;
		fetch(); // prefetch first 4 bytes
	}

	/**
	 * @return
	 * @see java.nio.Buffer#array()
	 */
	public byte[] array() {
		return this.delagate.array();
	}

	/**
	 * @return offset into array in bits
	 * @see java.nio.Buffer#arrayOffset()
	 */
	public int arrayOffset() {
		return this.delagate.arrayOffset() << 3; // array offset in bits
	}

	/**
	 * @return
	 * @see java.nio.ByteBuffer#asReadOnlyBuffer()
	 */
	public BitBuffer asReadOnlyBuffer() {
		return new BitBuffer(delagate.asReadOnlyBuffer());
	}

	/**
	 * @return
	 * @see java.nio.ByteBuffer#capacity()
	 */
	public int capacity() {
		return this.delagate.capacity() << 3; // capacity * 8
	}

	/**
	 * @return
	 * @see java.nio.ByteBuffer#compact()
	 */
	public ByteBuffer compact() {
		return this.delagate.compact();
	}

	/**
	 * @param that
	 * @return
	 * @see java.nio.ByteBuffer#compareTo(java.nio.ByteBuffer)
	 */
	public int compareTo(ByteBuffer that) {
		return this.delagate.compareTo(that);
	}

	/**
	 * @return
	 * @see java.nio.ByteBuffer#duplicate()
	 */
	public BitBuffer duplicate() {
		return new BitBuffer(delagate);
	}

	/**
	 * @param ob
	 * @return
	 * @see java.nio.ByteBuffer#equals(java.lang.Object)
	 */
	public boolean equals(Object ob) {
		return this.delagate.equals(((BitBuffer) ob).toByteBuffer());
	}

	private final void fetch() {
		if (this.posBits == 8) {
			this.posBytes++;
			this.posBits = 0;
		}

		this.current = this.delagate.get(posBytes);
	}

	/**
	 * Relative get signed 8-bit integer on the buffer. The current position may
	 * be in a middle of another byte in the backing buffer. If the value needs to
	 * cross byte boundariers, then its appropriately calculated to do so.
	 * 
	 * @return 8-bit signed integer as read from the backing buffer
	 * @see java.nio.ByteBuffer#get()
	 */
	public byte get() {
		return (byte) getBits(8);
	}

	/**
	 * @param dst
	 * @return
	 * @see java.nio.ByteBuffer#get(byte[])
	 */
	public BitBuffer get(byte[] dst) {
		if (this.posBits >>> 29 != 0) {
			throw new IllegalArgumentException(
			    "Can not read array of bytes from non-byte-aligned position");
		}

		this.posBits = 0;
		this.delagate.position(posBytes);
		this.delagate.get(dst);

		this.posBytes = this.delagate.position();

		return this;
	}

	/**
	 * @param dst
	 * @param offset
	 * @param length
	 * @return
	 * @see java.nio.ByteBuffer#get(byte[], int, int)
	 */
	public BitBuffer get(byte[] dst, int offset, int length) {
		this.delagate.get(dst, offset, length);

		return this;
	}

	/**
	 * @param index
	 * @return
	 * @see java.nio.ByteBuffer#get(int)
	 */
	public byte get(int index) {

		return (byte) getBits(index, 8);
	}
	
	/**
	 * @param dst
	 * @return
	 * @see java.nio.ByteBuffer#get(byte[])
	 */
	public BitBuffer get(int index, byte[] dst) {
		if (this.posBits >>> 29 != 0) {
			throw new IllegalArgumentException(
			    "Can not read array of bytes from non-byte-aligned position");
		}

		push();
		position(index);
		get(dst);
		pop();

		return this;
	}

	/**
	 * @param dst
	 * @param offset
	 * @param length
	 * @return
	 * @see java.nio.ByteBuffer#get(byte[], int, int)
	 */
	public BitBuffer get(int index, byte[] dst, int offset, int length) {

		push();
		position(index);
		get(dst, offset, length);
		pop();

		return this;
	}

	/**
	 * @return the delagate
	 */
	public final ByteBuffer getBackingBuffer() {
		return this.delagate;
	}

	final int getBits(int count) {
		if (this.delagate.order() == ByteOrder.LITTLE_ENDIAN) {
			return getBitsLE(count);
		} else {
			return getBitsBE(count);
		}
	}

	/**
	 * Allows absolute read of variable bit count. Position is temporarily pushed
	 * on the stack, then changed, bits are read, position restored from the stack
	 * and the read value returned.
	 * 
	 * @param position
	 *          absolute position to read bits from
	 * @param count
	 *          number of bits to read
	 * @return integer value of the bits read
	 */
	public final int getBits(int position, int count) {
		push();
		position(position);

		final int r = getBits(count);

		pop();

		return r;
	}

	/**
   * Gets a sequence of bits and returns them as a signed byte which is
   * represented as an int.
   * 
   * @param count
   *          number of bits to return that are between 0 and 32
   */
  final int getBitsBE(final int count) {
  	/*
  	 * No need to check parameter boundaries, this is a private method.
  	 */
  
  	int left = count;
  
  	if (posBits == 8) {
  		fetch();
  	}
  
  	/*
  	 * Most common cases, make them as quick as we can
  	 */
  	if (posBits == 0) {
  		switch (left) {
  			case 8:
  				this.posBits = 8;
  				return current;
  
  			case 16:
  				this.posBytes += 1;
  				this.posBits = 8;
  				return this.delagate.getShort(this.posBytes - 1);
  
  			case 32:
  				this.posBytes += 3;
  				this.posBits = 8;
  				return this.delagate.getInt(this.posBytes - 3);
  		}
  	}
  
  	return readBitsOneAtATime(count);
  }

	/**
	 * Gets a sequence of bits and returns them as a signed byte which is
	 * represented as an int.
	 * 
	 * @param count
	 *          number of bits to return that are between 0 and 32
	 */
	@SuppressWarnings("unused")
  private final int getBitsBEByteAtATime(final int count) {
		/*
		 * No need to check parameter boundaries, this is a private method.
		 */

		int left = count;

		if (posBits == 8) {
			fetch();
		}

		/*
		 * Most common cases, make them as quick as we can
		 */
		if (posBits == 0) {
			switch (left) {
				case 8:
					this.posBits = 8;
					return current;

				case 16:
					this.posBytes += 1;
					this.posBits = 8;
					return this.delagate.getShort(this.posBytes - 1);

				case 32:
					this.posBytes += 3;
					this.posBits = 8;
					return this.delagate.getInt(this.posBytes - 3);
			}
		}

		/*
		 * Read values upto 32-bits at a time instead of the old 8-bit at a time.
		 * The fetch method prefetches data into the "current" variable at 32-bit
		 * boundaries.
		 */

		int r = 0; // Result value
		int offset = 0; // bit position within r

		while (true) {
			if (this.posBits == 8) {
				fetch();
			}

			final int remaining = 8 - this.posBits;

			if (left <= remaining) {
				final int l = 24 + remaining - left;
				final int mask = M << l >>> l;

				final int v = (current & mask) >>> this.posBits;

				r |= v << offset;
				this.posBits += left;
				break;

			} else {
				final int mask = M >>> this.posBits << this.posBits;
				final int v = (current & mask) >>> this.posBits;

				r |= v << offset;
				offset += remaining;
				this.posBits += remaining;
				left -= remaining;
			}
		}

		final int q;

		if (count <= 8) {
			q = r;
		} else if (count <= 16) {
			q = (r & 0x0000FF00) >>> 8 | (r & 0x000000FF) << 8;

		} else if (count <= 24) {
			q = (r & 0x00FF0000) >>> 16 | (r & 0x000000FF) << 16;

		} else if (count <= 32) {
			q =
			    (r & 0xFF000000) >>> 24 | (r & 0x000000FF) << 24
			        | (r & 0x00FF0000) >>> 8 | (r & 0x0000FF00) << 8;

		} else {
			throw new IllegalStateException("Shouldn't be here");
		}

		return q;
	}

	/**
	 * Gets a sequence of bits and returns them as a signed byte which is
	 * represented as an int.
	 * 
	 * @param count
	 *          number of bits to return that are between 0 and 32
	 */
	private final int getBitsLE(int count) {
		/*
		 * No need to check parameter boundaries, this is a private method.
		 */

		if (posBits == 8) {
			fetch();
		}

		/*
		 * Most common cases, make them as quick as we can
		 */
		if (posBits == 0) {
			switch (count) {
				case 8:
					this.posBits = 8;
					return current;

				case 16:
					this.posBytes += 1;
					this.posBits = 8;
					return this.delagate.getShort(this.posBytes - 1);

				case 32:
					final int r = this.delagate.getInt(this.posBytes);

					this.posBytes += 3;
					this.posBits = 8;

					return r;
			}
		}

		return readBitsOneAtATime(count);
	}

	/**
   * Gets a sequence of bits and returns them as a signed byte which is
   * represented as an int.
   * 
   * @param count
   *          number of bits to return that are between 0 and 32
   */
  @SuppressWarnings("unused")
  private final int getBitsLEByteAtATime(int count) {
  	/*
  	 * No need to check parameter boundaries, this is a private method.
  	 */
  
  	if (posBits == 8) {
  		fetch();
  	}
  
  	/*
  	 * Most common cases, make them as quick as we can
  	 */
  	if (posBits == 0) {
  		switch (count) {
  			case 8:
  				this.posBits = 8;
  				return current;
  
  			case 16:
  				this.posBytes += 1;
  				this.posBits = 8;
  				return this.delagate.getShort(this.posBytes - 1);
  
  			case 32:
  				final int r = this.delagate.getInt(this.posBytes);
  
  				this.posBytes += 3;
  				this.posBits = 8;
  
  				return r;
  		}
  	}
  
  	/*
  	 * Read values upto 32-bits at a time instead of the old 8-bit at a time.
  	 * The fetch method prefetches data into the "current" variable at 32-bit
  	 * boundaries.
  	 */
  
  	int r = 0; // Result value
  	int offset = 0; // bit position within r
  
  	while (true) {
  		if (this.posBits == 8) {
  			fetch();
  		}
  
  		final int remaining = 8 - this.posBits;
  
  		if (count <= remaining) {
  			final int l = 24 + remaining - count;
  			final int mask = M << l >>> l;
  
  			final int v = (current & mask) >>> this.posBits;
  
  			r |= v << offset;
  			this.posBits += count;
  			break;
  
  		} else {
  			final int mask = M >>> this.posBits << this.posBits;
  			final int v = (current & mask) >>> this.posBits;
  
  			r |= v << offset;
  			offset += remaining;
  			this.posBits += remaining;
  			count -= remaining;
  		}
  	}
  
  	return r;
  }

	/**
	 * 
	 * @return
	 */
	public byte getByte() {
		return (byte) getBits(8);
	}

	/**
	 * @param index
	 * @return
	 * @see java.nio.ByteBuffer#get(int)
	 */
	public byte getByte(int index) {

		return (byte) getBits(index, 8);
	}

	/**
	 * @return
	 * @see java.nio.ByteBuffer#getChar()
	 */
	public char getChar() {
		final char c = (char) getBits(16);

		return c;
	}

	/**
	 * @param index
	 * @return
	 * @see java.nio.ByteBuffer#getChar(int)
	 */
	public char getChar(int index) {
		return (char) getBits(index, 16);
	}

	/**
	 * @return
	 * @see java.nio.ByteBuffer#getDouble()
	 */
	public double getDouble() {
		return this.delagate.getDouble();
	}

	/**
	 * @param index
	 * @return
	 * @see java.nio.ByteBuffer#getDouble(int)
	 */
	public double getDouble(int index) {
		return this.delagate.getDouble(index);
	}

	/**
	 * @return
	 * @see java.nio.ByteBuffer#getFloat()
	 */
	public float getFloat() {
		return this.delagate.getFloat();
	}

	/**
	 * @param index
	 * @return
	 * @see java.nio.ByteBuffer#getFloat(int)
	 */
	public float getFloat(int index) {
		return this.delagate.getFloat(index);
	}

	/**
	 * @return
	 * @see java.nio.ByteBuffer#getInt()
	 */
	public int getInt() {
		return getBits(32);
	}

	/**
	 * @param index
	 * @return
	 * @see java.nio.ByteBuffer#getInt(int)
	 */
	public int getInt(int index) {
		return getBits(index, 32);
	}

	/**
	 * @return
	 * @see java.nio.ByteBuffer#getLong()
	 */
	public long getLong() {
		long low = getBits(32);
		long high = getBits(32);

		if (this.delagate.order() == ByteOrder.BIG_ENDIAN) {
			return low << 32 | high;
		} else {
			return high << 32 | low;
		}
	}

	/**
	 * @param index
	 * @return
	 * @see java.nio.ByteBuffer#getLong(int)
	 */
	public long getLong(int index) {
		long low = getBits(index, 32);
		long high = getBits(index + 32, 32);

		if (this.delagate.order() == ByteOrder.BIG_ENDIAN) {
			return low << 32 | high;
		} else {
			return high << 32 | low;
		}
	}

	/**
	 * @return
	 * @see java.nio.ByteBuffer#getShort()
	 */
	public short getShort() {
		return (short) getBits(16);
	}

	/**
	 * @param index
	 * @return
	 * @see java.nio.ByteBuffer#getShort(int)
	 */
	public short getShort(int index) {
		return (short) getBits(index, 16);
	}

	public int getUByte() {
		return getBits(8);
	}

	/**
	 * @return
	 * @see java.nio.Buffer#hasArray()
	 */
	public boolean hasArray() {
		return this.delagate.hasArray();
	}

	/**
	 * @return
	 * @see java.nio.ByteBuffer#hashCode()
	 */
	public int hashCode() {
		return this.delagate.hashCode();
	}

	/**
	 * @return
	 * @see java.nio.ByteBuffer#isDirect()
	 */
	public boolean isDirect() {
		return this.delagate.isDirect();
	}

	/**
	 * @return
	 * @see java.nio.Buffer#isReadOnly()
	 */
	public boolean isReadOnly() {
		return this.delagate.isReadOnly();
	}

	/**
	 * Returns the current limit position within the bit buffer in bits.
	 * 
	 * @return limit property in units of bits
	 */
	public int limit() {
		return this.delagate.limit() << 3; // limit * 8
	}

	public BitBuffer limit(int value) {

		this.delagate.limit(value / 8);

		return this;
	}

	/**
	 * @return
	 */
	public ByteOrder order() {
		return this.delagate.order();
	}

	/**
	 * @param littleEndian
	 */
	public void order(ByteOrder order) {
		this.delagate.order(order);
	}

	public final void pop() {
		if (posIndex == 0) {
			throw new StackOverflowError("Can't pop empty stack");
		}

		posIndex--;

		final PositionEntry e = posStack[posIndex];

		position(e.bytes, e.bits);
	}

	public int position() {
		return this.posBytes << 3 + posBits;
	}

	/**
	 * Sets a new position.
	 * 
	 * @param position
	 *          in bits
	 */
	public void position(int position) {

		int oldPosByte = this.posBytes;
		this.posBytes = position >>> 3; // same as divide by 2^3 or 8
		this.delagate.position(posBytes);

		this.posBits = (M >>> 5) & position; // same as % 8

		if (posBits != 0 || oldPosByte != this.posBytes) {
			fetch();
		}
	}

	/**
	 * Special method used to efficiently restore previous state
	 * 
	 * @param bytes
	 * @param bits
	 */
	private void position(int bytes, int bits) {

		this.posBytes = bytes;

		this.posBits = bits;

		fetch();
	}

	public final void push() {
		/*
		 * This looks scarier then it actually is. Its actually very very efficient
		 * if you look at it closely. Everything is reused and it boils down to a
		 * single array lookup, incrementing index and setting 2 properties directly
		 * without a function call, for the common case of push/pop. So, we do this
		 * ourselves instead of using Stack container, to have more efficiency.
		 * Specifically we reuse the Entry elements on the stack, only only create
		 * them when the stack wonders out into new territory.
		 */

		/**
		 * Check if we need to expand the stack capacity.
		 */
		if (posIndex == posCapacity) {
			final PositionEntry n[] = new PositionEntry[posCapacity + STACK_INC];
			System.arraycopy(posStack, 0, n, 0, posCapacity);

			posStack = n;
			posCapacity += STACK_INC;
		}

		PositionEntry e = posStack[posIndex];
		if (e == null) {
			e = posStack[posIndex] = new PositionEntry();
		}

		e.bytes = posBytes;
		e.bits = posBits;

		posIndex++;

	}

	/**
	 * @param b
	 * @return
	 * @see java.nio.ByteBuffer#put(byte)
	 */
	public BitBuffer put(byte b) {
		this.delagate.put(b);

		return this;
	}

	/**
	 * @param src
	 * @param offset
	 * @param length
	 * @return
	 * @see java.nio.ByteBuffer#put(byte[], int, int)
	 */
	public BitBuffer put(byte[] src, int offset, int length) {
		this.delagate.put(src, offset, length);

		return this;
	}

	/**
	 * @param src
	 * @return
	 * @see java.nio.ByteBuffer#put(java.nio.ByteBuffer)
	 */
	public BitBuffer put(ByteBuffer src) {
		this.delagate.put(src);

		return this;
	}

	/**
	 * @param index
	 * @param b
	 * @return
	 * @see java.nio.ByteBuffer#put(int, byte)
	 */
	public BitBuffer put(int index, byte b) {
		this.delagate.put(index, b);

		return this;
	}

	/**
	 * @param src
	 * @param offset
	 * @param length
	 * @return
	 * @see java.nio.ByteBuffer#put(byte[], int, int)
	 */
	public BitBuffer put(int index, byte[] src, int offset, int length) {
		push();
		position(index);
		this.delagate.put(src, offset, length);

		pop();

		return this;
	}

	/**
	 * @param i
	 * @param j
	 * @param value
	 */
	public void putBits(int i, int j, byte value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/**
	 * @param value
	 * @return
	 * @see java.nio.ByteBuffer#putChar(char)
	 */
	public BitBuffer putChar(char value) {
		this.delagate.putChar(value);

		return this;
	}

	/**
	 * @param index
	 * @param value
	 * @return
	 * @see java.nio.ByteBuffer#putChar(int, char)
	 */
	public BitBuffer putChar(int index, char value) {
		this.delagate.putChar(index, value);

		return this;
	}

	/**
	 * @param value
	 * @return
	 * @see java.nio.ByteBuffer#putDouble(double)
	 */
	public BitBuffer putDouble(double value) {
		this.delagate.putDouble(value);

		return this;
	}

	/**
	 * @param index
	 * @param value
	 * @return
	 * @see java.nio.ByteBuffer#putDouble(int, double)
	 */
	public BitBuffer putDouble(int index, double value) {
		this.delagate.putDouble(index, value);

		return this;
	}

	/**
	 * @param value
	 * @return
	 * @see java.nio.ByteBuffer#putFloat(float)
	 */
	public BitBuffer putFloat(float value) {
		this.delagate.putFloat(value);

		return this;
	}

	/**
	 * @param index
	 * @param value
	 * @return
	 * @see java.nio.ByteBuffer#putFloat(int, float)
	 */
	public BitBuffer putFloat(int index, float value) {
		this.delagate.putFloat(index, value);

		return this;
	}

	/**
	 * @param value
	 * @return
	 * @see java.nio.ByteBuffer#putInt(int)
	 */
	public BitBuffer putInt(int value) {
		this.delagate.putInt(value);

		return this;
	}

	/**
	 * @param index
	 * @param value
	 * @return
	 * @see java.nio.ByteBuffer#putInt(int, int)
	 */
	public BitBuffer putInt(int index, int value) {
		this.delagate.putInt(index, value);

		return this;
	}

	/**
	 * @param index
	 * @param value
	 * @return
	 * @see java.nio.ByteBuffer#putLong(int, long)
	 */
	public BitBuffer putLong(int index, long value) {
		this.delagate.putLong(index, value);

		return this;
	}

	/**
	 * @param value
	 * @return
	 * @see java.nio.ByteBuffer#putLong(long)
	 */
	public BitBuffer putLong(long value) {
		this.delagate.putLong(value);

		return this;
	}

	/**
	 * @param index
	 * @param value
	 * @return
	 * @see java.nio.ByteBuffer#putShort(int, short)
	 */
	public BitBuffer putShort(int index, short value) {
		this.delagate.putShort(index, value);

		return this;
	}

	/**
	 * @param value
	 * @return
	 * @see java.nio.ByteBuffer#putShort(short)
	 */
	public BitBuffer putShort(short value) {
		this.delagate.putShort(value);

		return this;
	}

	private int readBitsOneAtATime(int length) {


		if (length == 0) {
			return 0;
		}

		if (length > 32) {
			throw new IndexOutOfBoundsException("0 >= range <= 32bits");
		}

		int returnValue = 0;
		;

		/**
		 * Loop through and copy 1 bit at a time numOfBits amount of bits into
		 * returnValue integer.
		 */
		while (length >= 0) {

			int bitsLeft = 8 - posBits;
			/**
			 * Check to see if we have any unprocessed bits in the cache. if not fetch
			 * the next byte and set the bitsLeft counter to 8. 8 is the number of
			 * bits in the byte since we just fetched a single byte.
			 */
			if (posBits == 8) // empty cache
			{
				fetch();
			}


			/**
			 * Read the bit at the current offset position out of the cache and
			 * advance the pointer but reducing number of bits left.
			 */
			int bit = current & (0x1 << bitsLeft);
			if (bit != 0) {
				returnValue |= 1 << length;
			}
//			System.out.println("[" + length + ":" + (0x1 << bitsLeft) + "=" + bit
//			    + "]");
			
			length--;
			posBits ++;
		}

		return returnValue;

	}

	/**
	 * @return
	 * @see java.nio.ByteBuffer#slice()
	 */
	public BitBuffer slice() {
		return new BitBuffer(this.delagate.slice());
	}

	/**
	 * <p>
	 * Creates a new byte buffer that shares this bit buffer's content. The
	 * content of the new buffer will be that of this buffer. Changes to this
	 * buffer's content will be visible in the new buffer, and vice versa; the two
	 * buffers' position, limit, and mark values will be independent.
	 * </p>
	 * The new buffer's capacity, limit, position, and mark values will be
	 * comparable to those of this buffer in their respective units (bits vs.
	 * bytes.) The new buffer will be direct if, and only if, this buffer is
	 * direct, and it will be read-only if, and only if, this buffer is read-only.
	 * 
	 * @return a byte buffer that is wrapped around this buffers data
	 */
	public ByteBuffer toByteBuffer() {
		ByteBuffer b = delagate.duplicate();

		b.limit(delagate.limit());
		b.position(delagate.position());

		return b;
	}

	/**
	 * @return
	 * @see java.nio.ByteBuffer#toString()
	 */
	public String toString() {
		return this.delagate.toString();
	}

	public void writeBits(final int count, final int value) {

		int offset = 0;

		while (true) {
			final int remaining = 8 - this.posBits;
			final int l = offset + remaining;
			final int mask = M << l >>> l;

		}
	}

	/**
   * @param bits
   * @return
   */
  public static int tou(byte value) {
	  return (value < 0)? + Byte.MAX_VALUE * 2 + value + 2: value;
  }
  
  public static int tou(short value) {
	  return (value < 0)? + Short.MAX_VALUE * 2 + value + 2: value;
  }
  
  public static long tou(int value) {
	  return (value < 0)? + Integer.MAX_VALUE * 2 + value + 2: value;
  }

}
