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
package org.jnetstream.capture.file;

/**
 * Defines different types of models, or algorithms that can be used
 * to count packets in a capture file. 
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum PacketCounterModel {
	

	/**
	 * Does an accurate packet count even if it means that the entire file has be
	 * be traversed. Packet count is guarranteed to be accurate
	 */
	RealCount,

	/**
	 * Takes samples of certain sections through out the file and estimates the
	 * number of packets. The statistical approach is not 100% accurate, but the
	 * count returned is typically very close to the actual number. This type of
	 * model is much more efficient, especially on large files, and can return a
	 * count very quickly.
	 */
	StatisticalCount

}
