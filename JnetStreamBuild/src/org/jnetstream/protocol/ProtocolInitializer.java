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
package org.jnetstream.protocol;

import org.jnetstream.protocol.lan.Lan;
import org.jnetstream.protocol.tcpip.Tcpip;

import com.slytechs.utils.module.Initializer;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public class ProtocolInitializer implements Initializer {

	/* (non-Javadoc)
   * @see com.slytechs.utils.module.Initializer#init()
   */
  public void init() {
  	
  	for (Lan p: Lan.values()) {
  		ProtocolRegistry.addProtocol(p);
  	}
  	
  	for (Tcpip p: Tcpip.values()) {
  		ProtocolRegistry.addProtocol(p);
  	}
  }

}
