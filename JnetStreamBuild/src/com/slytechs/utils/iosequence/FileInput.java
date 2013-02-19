/**
 * $Id$
 * Copyright (C) 2006 Mark Bednarczyk
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;


/**
 * @author Mark Bednarczyk
 *
 */
public class FileInput<T> implements Input<T> {
	
	private final FileHandler<T> handler;
	
	public FileInput(Class<T> c, File file) throws FileNotFoundException, IOException, HandlerNotFound {
		handler = Handler.getHandler(FileHandler.TYPE, c);
		
		if (handler == null) {
			throw new HandlerNotFound("Can not find handler for sequence of class type" + c.toString());
		}
		
		handler.openFile(file);
	}
	
	public FileInput(Class<T> c, String file) throws FileNotFoundException, IOException, HandlerNotFound {
		this(c, new File(file));
	}

	public T get() throws InterruptedException {
		return handler.get();
	}
	
	public boolean isCountDeterministic() {
		return handler.isCountDeterministic();
	}
	
	public int getCount() {
		return handler.getCount();
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator() {
		return handler.iterator();
	}

	/* (non-Javadoc)
	 * @see com.slytechs.jnetstream.iosequence.Input#close()
	 */
	public void close() {
		// TODO Auto-generated method stub
		
	}
}
