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
package com.slytechs.utils.number;


/**
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface BitFlag {
	
	public int bits();

	public int set(int bits);
	
	public int clear(int bits);
	
	public boolean isSet(int bits);
	
/*	
 *  Sample implementation, copy/paste into new *Flag type enum classes.
 *  
	public static enum FieldFlag implements BitFlag {
	 	UNSIGNED       (0x01),
		NULL_ALLOWED   (0x02),
		AUTO_INCREMENT (0x04);
		
		private int bits = 0x00;
		
		private FieldFlag(final int flag) {
			this.bits = flag;
		}
		
		public int bits() {
			return bits;
		}
		
		public int set(int bits) {
			return bits | this.bits;
		}
		
		public int clear(int bits) {
			return bits & ~this.bits;
		}
		
		public boolean isSet(int bits) {
			return (bits & this.bits) != 0;
		}
		
		public static String flagsToString(int flags) {
			return flagsToString(new StringBuilder(), flags).toString();
		}
		
		public static StringBuilder flagsToString(StringBuilder buf, int flags) {
			
			String separator = "";
			
			for(FieldFlag flag: values()) {
				
				if (flag.isSet(flags)) {
					buf.append(separator);
					buf.append(flag.toString());
					separator =", ";
				}
			}
			
			return buf;
		}
		
		public static String[] flagsToStringArray(int flags) {
			 First count bits set 
			
			int count = 0;
			for(FieldFlag flag: values()) {
				if (flag.isSet(flags)) {
					count ++;
				}
			}
			String[] sa = new String[count];
			for(FieldFlag flag: values()) {
				if (flag.isSet(flags)) {
					sa[--count] = flag.toString();
				}
			}
			
			return sa;
		}
	}
*/
}
