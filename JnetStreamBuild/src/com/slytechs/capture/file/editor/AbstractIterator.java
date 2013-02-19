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
package com.slytechs.capture.file.editor;

import java.io.IOException;

import org.jnetstream.capture.file.RawIterator;


import com.slytechs.utils.collection.IOPositional;
import com.slytechs.utils.collection.SeekResult;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public abstract class AbstractIterator {

	protected final RawIterator raw;
	/**
   * @param raw
	 * @throws IOException 
   */
  protected AbstractIterator(final RawIterator raw) throws IOException {
	  this.raw = raw;
	  
	  seekFirst();
  }

	public void abortChanges() throws IOException {
  	raw.abortChanges();
  }

	public void close() throws IOException {
  	raw.close();
  }

	public void flush() throws IOException {
  	raw.flush();
  }

	public long getPosition() throws IOException {
  	return raw.getPosition();
  }

	public boolean hasNext() throws IOException {
  	return raw.hasNext();
  }

	public void remove() throws IOException {
  	raw.remove();
  }

	public void removeAll(final long count) throws IOException {
  
  	for (int i = 0; i < count; i++) {
  		remove();
  	}
  }

	public void removeAll() throws IOException {
  	raw.removeAll();
  }

	public SeekResult seek(double percentage) throws IOException {
  	return raw.seek(percentage);
  }

	public SeekResult seek(long position) throws IOException {
  	return raw.seek(position);
  }

	public SeekResult seek(long seconds, long nanos) throws IOException {
  	return raw.seek(seconds, nanos);
  }

	public SeekResult seekEnd() throws IOException {
  	return raw.seekEnd();
  }

	public SeekResult seekFirst() throws IOException {
  	return raw.seekFirst();
  }

	public SeekResult seekLast() throws IOException {
  	return raw.seekLast();
  }

	public void setAutoflush(boolean state) throws IOException {
  	raw.setAutoflush(state);
  }

	public long setPosition(long position) throws IOException {
  	return raw.setPosition(position);
  }
	
	public long setPosition(IOPositional position) throws IOException {
		final long p = position.getPosition();
		
		return raw.setPosition(p);
	}

	public void skip() throws IOException {
  	raw.skip();
  }

	public SeekResult seekSecond() throws IOException {
    raw.seekFirst();
    
    if (raw.hasNext()) {
    	raw.skip();
    	
    	return (hasNext() ? SeekResult.Fullfilled : SeekResult.NotFullfilled);
    } else {
    	return SeekResult.NotFullfilled;
    }
  }

}
