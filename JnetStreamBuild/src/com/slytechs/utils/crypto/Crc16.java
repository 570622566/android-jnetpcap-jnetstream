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
package com.slytechs.utils.crypto;

import java.nio.ByteBuffer;
import java.util.zip.Checksum;

//import sun.misc.CRC16;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public class Crc16 {
	
	private int crc;
	private int computed;
	
	public Crc16(int validation) {
		this.crc = validation;
		this.computed = 0;
		
	}

	/* (non-Javadoc)
   * @see java.util.zip.Checksum#getValue()
   */
  public long getValue() {
	  return crc;
  }
  
  public long getComputed() {
  	return computed;
  }

	/* (non-Javadoc)
   * @see java.util.zip.Checksum#reset()
   */
  public void reset() {
	  crc = 0;
	  computed = 0;
  }
  
  public void compute(ByteBuffer b, int offset, int length, int skip) {
  	int sum = 0;
  	
  	for (int i = 0; i < length; i ++) {
  		if (i == skip || i == skip + 1) {
  			continue;
  		}
  		
  		sum += b.get(offset + i);
  	}
  	
    sum = (short) ((sum >> 16) + (sum & 0xFFFF));
    sum += sum >> 16;

    /*
     * Return the inverted 16-bit result.
     */
    computed = ~sum;

  }
  
  
  public String toString() {
//  	if (getValue() == getComputed()) {
  		return String.format("0x%x", getValue());
//  	} else {
//  		return String.format("0x%x NOT OK! computed=0x%x", getValue(), getComputed());
//  	}
  }

}
