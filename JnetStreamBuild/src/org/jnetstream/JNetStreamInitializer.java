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
package org.jnetstream;

import org.jnetstream.protocol.ProtocolRegistry;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public final class JNetStreamInitializer {
	
	private static boolean initialized = false;
	
	public static int coreBitCounter = 0;
	
	static {
		init();
	}
	
	public static void init() {
		if (initialized) {
			return;
		}
		
		try {
	    initialized = true;
	    Class.forName("com.slytechs.jnetstream.JNetStreamInitializer").newInstance();
	    ProtocolRegistry.init();

    } catch (InstantiationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    } catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    } catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
	}

}
