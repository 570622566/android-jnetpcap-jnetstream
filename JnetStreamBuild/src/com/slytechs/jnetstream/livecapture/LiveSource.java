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
package com.slytechs.jnetstream.livecapture;

import java.io.Closeable;
import java.io.IOException;

/**
 * Interface which provides support API for a live source to its iterators.
 * Iterators rely on this interface to communicate with the source of the live
 * capture which is providing them with packets and information. The two main
 * methods provided are <code>close</code> and <code>isOpen</code>. The
 * client uses these to close the source or check if the source is still open.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface LiveSource extends Closeable {

	/**
	 * Checks if the source is open and can receive more packets.
	 * 
	 * @return true means that there could be more packets comming, otherwise no
	 *         more packets can be received.
	 * @throws IOException
	 *           any IO errors
	 */
	public boolean isOpen() throws IOException;

	/**
	 * Checks if current LiveSource is actually running the dispatcher thread
	 * which is dispatching captured packets. The capture session could still be
	 * opened but not capturing.
	 * 
	 * @return true dispatcher thread running, otherwise false
	 */
	public boolean isRunning();

	/**
	 * Checks if there is an IO exception pending
	 * 
	 * @return true there is a pending exception, otherwise false
	 */
	public boolean hasIOException();

	/**
	 * Gets the pending exception.
	 * 
	 * @return any pending exceptions, null if none
	 */
	public IOException getIOException();
}
