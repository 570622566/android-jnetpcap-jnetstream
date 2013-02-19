/**
 * $Id$
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
package com.slytechs.utils.iosequence;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class Handler {
	
	private String type = "";
	
	protected boolean isClosed = false;
	
	private static Map<String, Map<Class, Handler>> typeHandlers = new HashMap<String, Map<Class, Handler>>();
	
	static {
		/* Install default handlers */
		try {
			Class.forName("com.slytechs.products.jnetstream.protocol.STFilePacketInput");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Handler> T getHandler(String type, Class data) {
		
		Map<Class, Handler> handlers = typeHandlers.get(type);
		
		if (handlers == null) {
			return null;
		}
		
		return (T) handlers.get(data);
	}
	
	public static void setHandler(String type, Class data, Handler handler) {
		
		handler.setType(type);
		
		Map<Class, Handler> handlers = typeHandlers.get(type);
		if (handlers == null) {
			typeHandlers.put(type, handlers = new HashMap<Class, Handler>());
		}
		
		handlers.put(data, handler);
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	public abstract void close();

}
