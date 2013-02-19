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

package org.jnetstream.capture;

/**
 * A listener that can be registered with a Capture object, to be notified
 * of capture events.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public interface CaptureListener {

	/**
	 * Notifies that a new capture session has been opened.
	 * 
	 * @param source
	 *   source factory that opened the capture session
	 *   
	 * @param capture
	 *   new capture session just opened
	 */
	public void processOpenCapture(Captures.Factory source, Capture<?> capture);
	
	/**
	 * Notifies that an existing capture session has been closed and is no longer active.
	 * 
	 * @param source
	 *   source factory that the capture session was opened with
	 *   
	 * @param capture
	 *   capture that was closed
	 */
	public void processCloseCapture(Captures.Factory source, Capture<?> capture);
}
