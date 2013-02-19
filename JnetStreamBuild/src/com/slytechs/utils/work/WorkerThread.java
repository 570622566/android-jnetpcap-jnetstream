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
 * Base class for all workers. It sets up the worker thread so it can work in background.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class WorkerThread implements Runnable, Worker {
	
	private String workerName;
	
	private Thread workerThread = null;
	
	private final Worker worker;
	
	public WorkerThread(String name, Worker worker) {
		this.workerName = name;
		
		this.worker = worker;
	}	
	
	/**
	 * Checks the flag which indicates if this thread should exit or not.
	 * @return
	 */
	public boolean isAlive(){
		return Thread.currentThread() == workerThread;
	}
	
	/* (non-Javadoc)
	 * @see com.slytechs.utils.work.Worker#start()
	 */
	public void start() {
		if (workerThread != null){
			return;
		}
		
		workerThread = new Thread(this, workerName);
		workerThread.start();
	}
	
	/* (non-Javadoc)
	 * @see com.slytechs.utils.work.Worker#stop()
	 */
	public void stop() {
		workerThread = null; // Indicate that we should exit.
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.work.Worker#getWorkerName()
	 */
	public String getWorkerName() {
		return workerName;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.work.Worker#setWorkerName(java.lang.String)
	 */
	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	public void run() {
		worker.run();
	}

}
