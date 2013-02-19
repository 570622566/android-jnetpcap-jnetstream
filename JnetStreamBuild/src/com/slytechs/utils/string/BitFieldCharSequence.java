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
package com.slytechs.utils.string;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public class BitFieldCharSequence implements CharSequence {
	
	private char NOT_USED_CHAR = '.';
	private static final char PAD_CHAR = '0';
	private final int max;
	private final int start;
	private final int end;
	private final int padEnd;
	private final CharSequence bin;
	
	public BitFieldCharSequence(char padChar, int max, int start, int size, Number value) {
		this(max, start, size, value);
		NOT_USED_CHAR = padChar;
	}


	public BitFieldCharSequence(int max, int start, int size, Number value) {
		this.max = max;
		this.start = start;
		this.end = start + size;
	
		String s = Integer.toBinaryString(value.intValue());
		this.bin = s.subSequence(0, s.length());
		
		this.padEnd = start + (size - s.length());
		
	}

	/* (non-Javadoc)
	 * @see java.lang.CharSequence#charAt(int)
	 */
	public final char charAt(int index) {
		if (index < start || index >= end) {
			return NOT_USED_CHAR;
			
		} else if (index < padEnd) {
			return PAD_CHAR;
			
		} else {
			return bin.charAt(index - padEnd);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.CharSequence#length()
	 */
	public final int length() {
		return max;
	}

	/* (non-Javadoc)
	 * @see java.lang.CharSequence#subSequence(int, int)
	 */
	public final CharSequence subSequence(int start, int end) {
		final char[] b = new char[end - start];
		for (int i = start; i <= end; i ++) {
			b[i] = charAt(i);
		}
		
		return new String(b).subSequence(0, b.length);
	}
	
	public final String toString() {
		final char[] b = new char[max];
		for (int i = 0; i < max; i ++) {
			b[i] = charAt(i);
		}
		return new String(b);
	}

}
