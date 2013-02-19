/**
 * $Id$
 *
 * Copyright (C) 2006 Mark Bednarczyk, Sly Technologies, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place,
 * Suite 330, Boston,
 * MA 02111-1307 USA
 * 
 */
package com.slytechs.utils.work;

/**
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Worker {

	/**
	 * Starts up the worker if its not already running.
	 */
	public void start();

	/**
	 * Sets a flag to indicate that thread needs to exit. 
	 * 
	 * Next time the worker does any work it will quit, but until then
	 * the thread will remain blocked until work is performed again.	 *
	 */
	public void stop();
	
	public boolean isAlive();

	/**
	 * @return Returns the workerName.
	 */
	public String getWorkerName();

	/**
	 * @param workerName The workerName to set.
	 */
	public void setWorkerName(String workerName);
	
	abstract void run();

}