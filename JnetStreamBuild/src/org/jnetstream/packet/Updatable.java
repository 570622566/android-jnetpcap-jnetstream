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
package org.jnetstream.packet;

import java.io.IOException;

/**
 * Interface allows update operation to be performed on an object. What happens
 * within the object is not specified by this interface. Some kind of state
 * change may happen upon invocation of this method.
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Updatable {

	/**
	 * Calls on the implementing object to reflect any changes that have been made
	 * to it. The object may or may not also be flushed to some storage medium or 
	 * some flushable representation. A flush operation will likely call on update method
	 * though.
	 * 
	 * @throws IOException
	 *  any storage related exception 
	 *  
	 * @throws UpdateException
	 *  update exception will report on any state related errors that are related
	 *  to unusable or unstable object state
	 */
	public void update() throws IOException, UpdateException;

}
