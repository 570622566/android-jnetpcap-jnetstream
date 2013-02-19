/**
 * Copyright (C) 2007 Sly Technologies, Inc. This library is free software; you
 * can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version. This
 * library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package com.slytechs.utils.collection;

/**
 * Interface designed to allow an arbitrary exception to be relayed to another
 * object that implements this interface. For example, a standard iterator which
 * does not allow IOException to be thrown, upon encountering IO exception
 * internally can build a special object to be returned from the iteration that
 * carries or relays the IO exception cought using the {@link #check} method
 * which will rethrow the original exception.
 * 
 * <pre>
 * public class MyObject implements Exceptional&lt;IOException&gt; {
 * 	public IOException ioException = null;
 * 
 * 	public void check() throws IOException {
 * 		if (ioException != null) {
 * 			throw ioException;
 * 		} else {
 * 			// Do nothing, nothing to report
 * 		}
 * 	}
 * }
 * </pre>
 * 
 * ... and then ....
 * 
 * <pre>
 *    Iterable&lt;MyObject&gt; i = // get the iterator from somewhere
 *    
 *    for (MyObject m: i) { // May catch IO exception within the iterator
 *      m.check();          // Will rethrow previously cought IO exception
 *      // Do something with &quot;m&quot;
 *    }
 * </pre>
 * 
 * The iterator needs to construct a special object (MyObject in the above
 * example), that can carry the
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Exceptional<E extends Exception> {

	public <T> T check() throws E;
}
