/**
 * $Id$
 *
 * Copyright (C) 2006 Mark Bednarczyk, Sly Technologies, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place,
 * Suite 330, Boston,
 * MA 02111-1307 USA
 * 
 */
package com.slytechs.utils.format;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Enumeration;

import com.slytechs.utils.net.Address;
import com.slytechs.utils.net.IpAddress;
import com.slytechs.utils.net.MacAddress;

/**
 * Utility class with various number formating routines.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class NumberUtils {
	public static final String NONPRINTABLE_CHAR = ".";
	public static final String NEWLINE_CHAR = "\n";
	public static final String SPACE_CHAR = " "; // Could be different in the future

	public static final String space =
		"                                                                           " 
		+ "                                                                           "
		+ "                                                                           "
		+ "                                                                           ";


	/**
	 * String padding routine
	 */
	public static String pad(String s, int count) {
		if (s.length() < count) {
			return (s + space.substring(0, count - s.length()));
		}

		return s;
	}

	/**
	 * String padding routine
	 */
	public static String padSuffix(String s, int count) {
		if (s.length() < count) {
			return (space.substring(0, count - s.length()) + s);
		}

		return s;
	}


	/******************************************************
	 ***    number parsing
	 ******************************************************/

	public static long longValue(byte[] b) {

		long v = 0L;

		for(int i = 0; i < b.length & i < 8; i ++)
			v |= (b[i] < 0 ? (long)b[i] + 256 : (long)b[i]) << ((b.length - i -1) * 8);

		return(v);
	}

	public static Address parseAddress(String word) {

		Address r = null;
		/**
		 * Try parsing Ipv4 address.
		 */
		String[] c;
		if ( (c = word.split("\\.")).length == 4) {
			r = new IpAddress(parseIntoArray(new byte[4], c, 10));

		} else if ( (c = word.split(":")).length == 16) {
			r = new IpAddress(parseIntoArray(new byte[16], c, 16));

		} else if ( (c = word.split("-")).length == 6 || (c = word.split(":")).length == 6) {
			r = new MacAddress(parseIntoArray(new byte[6], c, 16));

		} else if (c.length > 1) { // Catch all. Allow any type of address separated :
			r = new Address(parseIntoArray(new byte[c.length], c, 16));
		}
		else
			return null;
		 
		return r;
	}

	public static byte[] parseIntoArray(byte[] r, String[] c, int radix) {
		try {
			for(int i = 0; i < r.length; i ++)
				r[i] = (byte)Integer.parseInt(c[i], radix);
		}
		catch(NumberFormatException n) {
			return null;
		}

		return r;
	}

	public static Number parseNumber(String word) {

		if(word == null || word.length() == 0)
			return(null);

		int sign = 1;
		int radix = 10;
		Number number = null;
		char last;

		/**
		 * Check for negative number.
		 */
		if(word.charAt(0) == '-') {
			sign = -1;
			word = word.substring(1);
		}

		if(word.length() == 0)
			return(null);


		/**
		 * Check for positive number
		 */
		if(word.charAt(0) == '+') {
			sign = 1;
			word = word.substring(1);
		}

		if(word.length() == 0)
			return(null);

		/**
		 * Now make sure we might have a number.
		 */
		if(Character.isDigit(word.charAt(0)) == false)
			return(null);

		if(word.charAt(0) == '0' && word.length() > 1) {
			if(word.charAt(1) == 'x')
				radix = 16;

			else if(word.charAt(1) == 'o')
				radix = 8;

			else if(word.charAt(1) == 'b')
				radix = 2;

			if(radix != 10)
				word = word.substring(2);

			if(word.length() == 0)
				return(null);

		}



		/**
		 * Check for floating point.
		 */
		else if(word.indexOf('.') != -1 || word.indexOf('f') != -1 || word.indexOf('e') != -1) {
			last = word.charAt(word.length()-1);

			/**
			 * Check for float
			 */
			if(last == 'f' || last == 'F') {
				word = word.substring(0, word.length() - 1);

				if(word.length() == 0)
					return(null);


				try { return(number = new Float(Float.parseFloat(word) * sign)); }
				catch(NumberFormatException n) { 
					return(null); 
				}

			}

			try { number = new Double(Double.parseDouble(word) * sign); } 
			catch(NumberFormatException n) { 
				return(null); 
			}

			return(number);

		}

		last = word.charAt(word.length()-1);
		if(last == 'l' || last == 'L') {
			word = word.substring(0, word.length() - 1);

			if(word.length() == 0)
				return(null);


			try { return(number = new Long(Long.parseLong(word, radix) * sign)); }
			catch(NumberFormatException n) { 
				return(null); 
			}
		}

		try { return(number = new Integer(Integer.parseInt(word, radix) * sign)); }
		catch(NumberFormatException n) { 
			return(null); 
		}
	}



	/******************************************************
	 ***    HEXDUMP methods.
	 ******************************************************/

	public static Enumeration hexdumpEnumerator(byte[] data) {
		return(hexdumpEnumerator(data, "", true, 0, 0));
	}
	public static Enumeration hexdumpEnumerator(byte[] data, String prefix, boolean indentFirstLine) {
		return(hexdumpEnumerator(data, prefix, indentFirstLine, 0, 0));
	}
	public static Enumeration hexdumpEnumerator(byte[] data, String prefix, boolean indentFirstLine, int addressOffset, int dataOffset) {
		return(new HexdumpEnumerator(data, prefix, indentFirstLine, addressOffset, dataOffset));
	}

	public static String hexdump(byte[] a) {
		return(hexdump(a, "", true, 0, 0));
	}

	public static String hexdump(byte[] a, String prefix, boolean indentFirstLine) {
		return(hexdump(a, prefix, indentFirstLine, 0, 0));
	}

	private static String hexdump(byte[] a, String prefix, boolean indentFirstLine, int addressOffset, int dataOffset) {
		String s = "";

		for(int i = 0; i + dataOffset < a.length; i += 16) {
			if(i == 0 && indentFirstLine == false)
				s += hexLine(a, "", i + addressOffset, i + dataOffset);
			else
				s += hexLine(a, prefix, i + addressOffset, i + dataOffset);

			if( i + dataOffset +16 < a.length)
				s += NEWLINE_CHAR;
		}

		return(s);
	}

	public static String hexLine(byte[] a, String prefix, int address, int i) {
		String s = "";

		s += prefix;
		s += hexLineAddress(address);
		s += ":" + SPACE_CHAR;

		s += hexLineData(a, i);
		s += SPACE_CHAR;
		s += SPACE_CHAR;
		s += SPACE_CHAR;

		s += hexLineText(a, i);

		return(s);
	}


	public static String hexLineText(byte[] data, int offset) {

		String s = "";

		int i;
		for(i = 0; i + offset < data.length && i < 16; i ++) {
			s += table[data[i + offset] & 0xFF];

//			if(Character.isLetterOrDigit(table[data[i + offset] & 0xFF]) ||
//			   (table[data[i + offset] & 0xFF]) == ' ')
//				s += " " + table[data[i + offset] & 0xFF];
//			else
//				s += " " + NONPRINTABLE_CHAR;
		}

		/**
		 * Continue the loop and fill in any missing 
		 * data less than 16 bytes.
		 */
		for(; i < 16; i ++) {
			s += SPACE_CHAR;
		}

		return(s);
	}

	public static String hexLineAddress(int address) {
		String s = "";
			
		s = Integer.toHexString(address);

		for(int i = s.length(); i < 4; i ++)
			s = "0" + s;

		return(s);
	}

	public static String hexLineData(byte[] data, int offset) {
		String s = "";

		int i = 0;
		for(i = 0; i + offset < data.length && i < 16; i ++) {

			/**
			 * Insert a space every 4 characaters.
			 */
			if(i % 4 == 0 && i != 0)
				s += SPACE_CHAR;

			s += toHexString(data[i + offset]);
		}

		/**
		 * Continue the loop and append spaces to fill in
		 * the missing data.
		 */
		for(; i < 16; i ++) {
			/**
			 * Insert a space every 4 characaters.
			 */
			if(i % 4 == 0 && i != 0)
				s += SPACE_CHAR;

			s += SPACE_CHAR + SPACE_CHAR;
		}

		return(s);
	}


	public static String toHexString(byte b) {
		String s = Integer.toHexString(((int)b) & 0xFF);

		if(s.length() == 1)
			return("0" + s);

		return(s);
	}

	static String[] table = new String[256];
	static {

		for(int i = 0; i < 31; i ++) {
			table[i] = "\\" + Integer.toHexString(i);
			if(table[i].length() == 2) table[i] += " ";
		}

		for(int i = 31; i < 127; i ++)
			table[i] = new String(new byte[] { (byte)i, ' ', ' ' });

		for(int i = 127; i < 256; i ++) {
			table[i] = "\\" + Integer.toHexString(i);
			if(table[i].length() == 2) table[i] += " ";
		}

		table[0] = "\\0 "; 
		table[7] = "\\a "; 
		table[11] = "\\v "; 
		table['\b'] = "\\b "; 
		table['\t'] = "\\t "; 
		table['\n'] = "\\n "; 
		table['\f'] = "\\f "; 
		table['\r'] = "\\r "; 
	}


	/**
	 * Test function for NumberUtils
	 * @param args command line arguments
	 */
	public static void main(String [] args) {

		System.out.println(parseAddress(args[0]));

		if(true)
			return;

		Number number = parseNumber(args[0]);

		System.out.println("number=" + number);

		if(true)
			return;

		byte[] a = new byte[256];
		byte[] b = new byte[8];

		for(int i = 0; i < a.length; i ++) {
			a[i] = (byte)(i + 'A');
			if(i < 8)
				b[i] = (byte)(i + 'A');
		}

		byte[] c = new byte[32];
		InputStream in = null;
		try {
			in = new FileInputStream("abc");
//			in.read(c, 0, c.length);
		}
		catch(FileNotFoundException f) {
			f.printStackTrace();
		}





//		System.out.println("hexLineAddress=" + hexLineAddress(32));
//		System.out.println("hexLineData=" + hexLineData(a, 8));
//		System.out.println("hexLineText=" + hexLineText(a, 8));

//		System.out.println(hexLineAddress(32) + ": " + hexLineData(a, 8) + " " + hexLineText(a, 8));
//		System.out.println(hexLineAddress(0) + ": " + hexLineData(b, 4) + " " + hexLineText(b, 4));

//		System.out.println(hexdump(c));

//		Enumeration h = hexdumpEnumerator(c);
//		while(h.hasMoreElements()) {
//			System.out.println(h.nextElement());
//		}

			try {
			int i = 0;
			while( in.read(c, 0, c.length) != -1) {
				System.out.println(hexLine(c, "", i, 0));

				i += 16;
			}
			}
			catch(IOException ioe) {
				ioe.printStackTrace();
			}
	}

	/**
	 * Utility class that manipulates Timestamps
	 *
	 * @return micros (10e6) seconds as the delta betwen t2 to and t1.
	 */
	public static long timestampSubtract(Timestamp t1, Timestamp t2) {
		long milli1 = t1.getTime();
		int nano1 = t1.getNanos();

		long milli2 = t2.getTime();
		int nano2 = t2.getNanos();

		long microDelta = (((milli2 / 1000) - (milli1 / 1000)) * 1000000);

		microDelta += ((nano2 / 1000) - (nano1 / 1000));

		return(microDelta);
	}

	public static String timestampDeltaFormat(Timestamp t1, Timestamp t2) {
		return(timestampDeltaFormat(timestampSubtract(t1, t2)));
	}

	public static String timestampDeltaFormat(long micros) {
		
		if (micros < 0) {
			return "out-of-range";
		}
		String s = "";

		long secs = micros / 1000000;
		s += "" + (micros - (secs * 1000000));

		for(; s.length() < 6; s = "0" + s);

		s = "" + (secs) + "." + s;

		return(s);
	}


} /* END OF: NumberUtils */


/**
 * Supporting top-level class to do Enumeration 
 * Enumerating through long lists of byte[] prevents
 * huge strings since formated data is returned as strings.
 *
 */
class HexdumpEnumerator implements Enumeration {
	private byte[] data = null;

	private int i = 0; 
	private String prefix = "";
	private int addressOffset = 0;
	private int dataOffset = 0;
	private boolean indentFirstLine = true;

	protected HexdumpEnumerator(byte[] data, String prefix, boolean indentFirstLine, int addressOffset, int dataOffset) {
		this.data = data;
		this.prefix = prefix;
		this.addressOffset = addressOffset;
		this.dataOffset = dataOffset;
	}

	public boolean hasMoreElements() {
		return(i + dataOffset < data.length);
	}

	public Object nextElement() {

		String s = "";

		if(i == 0 && indentFirstLine == false)
			s += NumberUtils.hexLine(data, "", i + addressOffset, i + dataOffset);
		else
			s += NumberUtils.hexLine(data, prefix, i + addressOffset, i + dataOffset);

		i += 16;

		return(s);
	}
};
