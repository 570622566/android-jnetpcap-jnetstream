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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.slytechs.utils.work.Job.JobStatus;


/**
 * Base class for all workers that execute jobs. This
 * class contains the job execution method in its run() method.
 * 
 * Jobs are executed 1 at a time from the job queue. Their Job.run() method
 * is invoked to do work and then the amount of time it took to complete the
 * work is recorded getJobTimeCompletion().
 * 
 * Lastly the jobCompleted(Job job) method is called to allow each subclass implement
 * its own notification of a completed job.
 * 
 * Worker will wait when there are no more jobs on the queue until one shows up. If
 * multiple workers are processing the same queue, there is no guarrantee exactly which
 * worker will be used to process the job.
 * 
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class JobWorker implements Worker {
	public  Log logger = LogFactory.getLog(JobWorker.class);
	private static final int JOB_QUEUE_SIZE = 100;
	
	protected final WorkerThread thread;
	
	private AtomicReference<Job> activeJob = new AtomicReference<Job>();		


	private BlockingQueue<Job> queue;
	
	/**
	 * Constructor that allows a shared jobQueue to be used. This must
	 * be a BlockingQueue which is synchronized so that multiple workers
	 * are allowed to process the queue.
	 * @param name ResolvableName of the worker and execution thread
	 * @param jobQueue shared job queue
	 */
	public JobWorker(String name, BlockingQueue<Job> jobQueue) {
		this.thread = new WorkerThread(name, this);
		this.queue = jobQueue;
	}

	/**
	 * Constructor for JobWorker that uses its own private job queue
	 * @param name ResolvableName of the worker and execution thread
	 */
	public JobWorker(String name) {
		this.thread = new WorkerThread(name, this);
		
		queue = new ArrayBlockingQueue<Job>(JOB_QUEUE_SIZE);
	}

	/**
	 * Main job queue processing method
	 * 
	 * Processes the job queue executing one job at a time.
	 */
	public void run() {
		
		while(isAlive()) {
			try {
				activeJob.set(null);
				
				activeJob.set(queue.take());
				
			} catch (InterruptedException e) {
				continue;
			}
			
			if (activeJob.get() == null){
				logger.warn("Error: received NULL job!");
				continue;
			}
			

			logger.trace("Start job=" + activeJob.get().getJobName());
			
			
			if (activeJob.get().jobPrepare(this) == false) {
				logger.trace("Abort job=" + activeJob.get().getJobName());
				
				
				continue;
			}
			
			activeJob.get().setJobStatus(JobStatus.RUNNING);
			long t = System.currentTimeMillis();
			activeJob.get().run();
			activeJob.get().setJobCompletionTime(System.currentTimeMillis() - t);
			
			
			if (activeJob.get().getJobException() == null) {
				activeJob.get().setJobStatus(JobStatus.SUCCESS);
			} else {
				activeJob.get().setJobStatus(JobStatus.FAILURE);
			}
			
			activeJob.get().jobCleanup(this);
			
			activeJob.get().setJobCompleted(true);
			jobCompleted(activeJob.get());
		}
	}
	
	protected  void jobCompleted(Job job) {
		logger.trace("Completed job=" + job.getJobName() + " in " + job.getJobCompletionTime() + " ms");
	}
	
	/**
	 * Add a new job to the job queue. Job will be executed when worker is
	 * running isAlive() == true and when the jobs turns comes up on the blocking FIFO queue.
	 * 
	 * @param job job to add to the queue. 
	 * @return TODO
	 */
	public Job addJob(Job job) {
		
		if (job == null) {
			throw new IllegalArgumentException("Must specify a job: job=null");
		}
		queue.offer(job);
		
		return job;
	}
	
	/**
	 * Checks if the spcified job is already scheduled. The method checks its
	 * job queue for Job using the job.equals() method using the job's job Id. 
	 * Each new job is allocated a new uniqueue job ID, therefore if you may 
	 * want to assign user specific job ID that is somehow not unique between
	 * exactly same jobs.
	 * 
	 * @param job Job to check for.
	 * @return true if job is already on the queue, otherwise false.
	 */
	public boolean hasJob(Job job) {
		return activeJob.get() != null && activeJob.get().equals(job) || queue.contains(job);
	}
	
	/**
	 * Checks if the spcified job is already scheduled. The method checks its
	 * job queue for Job using the job.equals() method using the job's job Id. 
	 * Each new job is allocated a new uniqueue job ID, therefore if you may 
	 * want to assign user specific job ID that is somehow not unique between
	 * exactly same jobs.
	 * 
	 * @param jobId numerical job id to check for.
	 * @return true if job is already on the queue, otherwise false.
	 */
	public boolean hasJob(final int jobId) {
		
		if (activeJob.get() != null && activeJob.get().equals(jobId)) {
			return true;
		}
		
		for(Job job: queue) {
			if(job.equals(jobId)) {
				return true;
			}
		}

		return  false;
	}
	
	/* (non-Javadoc)
	 * @see com.slytechs.utils.work.WorkerThread#getWorkerName()
	 */
	public String getWorkerName() {
		return thread.getWorkerName();
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.work.WorkerThread#isAlive()
	 */
	public boolean isAlive() {
		return thread.isAlive();
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.work.WorkerThread#setWorkerName(java.lang.String)
	 */
	public void setWorkerName(String workerName) {
		thread.setWorkerName(workerName);
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.work.WorkerThread#start()
	 */
	public void start() {
		thread.start();
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.work.WorkerThread#stop()
	 */
	public void stop() {
		thread.stop();
	}

	public Log getLogger() {
		return logger;
	}

	public void setLogger(Log logger) {
		this.logger = logger;
	}

}
