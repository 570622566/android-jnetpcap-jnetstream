/**
 * $Id$ Copyright (C) 2006 Mark Bednarczyk, Sly Technologies, Inc. This library
 * is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version. This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.slytechs.utils.string;

import java.util.List;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class StringUtils {

	private static final int HASH_CODE = 0x18346487;

	public static final CharSequence FS = System.getProperty("file.separator");

	public static int encodeCodePoint(int c, int position) {

		int d = 0;

		for (int i = 0; i < 24; i += 8) {
			d |= c & (0x0F << i);
			d |= c & (0xF0 << i);
		}

		return d ^ (HASH_CODE >> ((5 - position) % 4));
		// return c ^ 0xA85D9B21;
	}

	public static int decodeCodePoint(int c, int position) {
		c = c ^ (HASH_CODE >> ((5 - position) % 4));
		int d = 0;

		for (int i = 0; i < 24; i += 8) {
			d |= c & (0x0F << i);
			d |= c & (0xF0 << i);
		}

		return d;
	}

	/**
	 * Returns the number of times the character is found within the string
	 * 
	 * @param s
	 * @param c
	 */
	public static int countChars(String s, char c) {

		int count = 0;
		for (int i = 0; i != -1; i = s.indexOf(c, i + 1)) {
			count++;
		}

		return count;
	}

	/**
	 * @param pkg
	 * @param sequence
	 */
	public static String combineUsing(List<String> pkg, CharSequence sequence) {

		final StringBuilder b = new StringBuilder();

		for (String c : pkg) {
			if (b.length() != 0) {
				b.append(sequence).append(c);
			} else {
				b.append(c);
			}
		}

		return b.toString();
	}

	/**
	 * Changes the first character to be upper case. The remaining chars are
	 * unaffected.
	 * 
	 * @param label string to capitalize
	 * @return capitalized string
	 */
	public static String capitalize(String label) {

		final char firstChar = Character.toUpperCase(label.charAt(0));
		label = label.replaceFirst("^.", "" + firstChar);

		return label;
	}

}
