/**
 * Copyright (C) 2008 Sly Technologies, Inc.
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
package com.slytechs.utils.time;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public class Stopwatch {
	private long start;
	private long end;
	
	public Stopwatch() {
		start();
	}
	
	public Stopwatch start() {
		start = System.currentTimeMillis();
		end = start;
		
		return this;
	}
	
	public Stopwatch end() {
		end = System.currentTimeMillis();
		
		return this;
	}
	
	public long delta() {
		return end - start;
	}
	
	public String toStringInUnits(int count, String unit) {
		double per = ((double) delta() / count);
		final String u;
		
		if (per >= 1000.0) {
			u = "s";
			per /= 1000.0;
			
		} else if (per < 0.001) {
			u = "ns";
			per *= 1000000.0;
		
		} else if (per < 1.0) {
			u = "us";
			per *= 1000.0;
		
		} else {
			u = "ms";
		}
		
		return "" + per + " " + u + "/" + unit;
	}
	
	public String toString() {
		return "" + delta() + " ms";
	}

}
