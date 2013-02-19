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
 * <P>A Job interface.</P>
 * 
 * <P>Jobs are work that should be performed by a worker. Jobs can be added
 * to a JobWorker's queue for execution. When the Job's turn comes up its
 * default run() method is executed to do actual work. The JobWorker will
 * set the amount of time the job took to execute and will lastly notify
 * the baseclass worker of job completion using the JobWorker.jobCompleted(Job)
 * method.</P>
 * 
 * <P>Job's method's are executed in the following order and are guarranteed to do so.
 * 
 * <OL>
 *  <LI> {@see #jobPrepare(JobWorker)}
 *  <LI> {@see #run()}
 *  <LI> {@see #setJobCompletionTime(long)}
 *  <LI> {@see #jobCleanup(JobWorker)}
 *  <LI> {@see #acknowledgeCompletion(Object)} - this call is optional, but is executed in this sequence if called
 * </OL>
 * </P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Job {
	
	public enum JobStatus {
		PENDING,
		RUNNING,
		SUCCESS,
		FAILURE	
	}
	
	/**
	 * Unique id assigned to the job when the job was scheduled.
	 * 
	 * @return unique job id
	 */
	public int getJobId();
	
	/**
	 * ResolvableName of the job 
	 * @return name of the job.
	 */
	public String getJobName();
	
	/**
	 * Amount of time in millis that the job took to execute.
	 * 
	 * @return amount of time in millis that the job took to execute.
	 */
	public long getJobCompletionTime();
	
	/**
	 * Called right before the job is to execute.
	 * 
	 * @param jobExecutor the worker that executed the job
	 */
	abstract boolean jobPrepare(JobWorker jobExecutor);
	
	/**
	 * Job's should do all their work in the run() method.
	 */
	abstract void run();
	
	/**
	 * Called right after the job executed. Job completion time is already set
	 * by the time the clean up method is called.
	 * 
	 * @param jobExecutor the worker that executed the job
	 */
	abstract void jobCleanup(JobWorker jobExecutor);
	
	/**
	 * Called externally to acknolege the job completion. This could be aknoledgement
	 * by the original job creator or submitter.
	 * 
	 * @param userObject any user object
	 * @return by default the userObject supplied. Can be overriden to 
	 * return any implementation/job specific completion information.
	 */
	public abstract Object acknowledgeCompletion(Object userObject);
	
	public Exception getJobException();
	
	/**
	 * Allows the JobWorker to set amount of time the Job took to execute.
	 * @param timeInMillis
	 */
	abstract void setJobCompletionTime(long timeInMillis);
	
	public boolean isJobCompleted();
	
	public JobStatus getJobStatus();
	
	abstract void setJobStatus(JobStatus status);
	
	abstract void setJobCompleted(boolean status);

}
