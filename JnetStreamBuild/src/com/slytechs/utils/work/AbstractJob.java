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
public abstract class AbstractJob implements Job {

	private int jobId;
	
	private String jobName;
	
	private long jobCompletionTime;
	
	private Exception jobException;
	
	private boolean jobCompleted = false;
	
	private JobStatus jobStatus = JobStatus.PENDING;
	
	private Object userObject;
	
	/**
	 * 
	 */
	public AbstractJob(String jobName) {
		this.jobName = jobName;
		
		init();
	}
	
	protected void init() {
		/* Empty */
	}

	/**
	 * Allows the user to override the Job id for specific tasks such as checking 
	 * and making sure duplicate jobs based on some user id are not duplicated.
	 * 
	 * @param jobId
	 */
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.work.Job#getJobId()
	 */
	public int getJobId() {
		if (jobId == 0) {
			jobId = ((Long)System.currentTimeMillis()).hashCode() + (int)(Math.random() * 1000);
		}
		return jobId;
	}
	
	public int hashCode() {
		return getJobId();
	}
	
	public boolean equals(Object o) {
		if (o instanceof Integer)  {
			Integer i = (Integer)o;
			return i == getJobId();
			
		} else if (o instanceof Job) {
			Job job = (Job) o;
			
			return job.getJobId() == getJobId();
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.work.Job#getJobName()
	 */
	public String getJobName() {
		return jobName;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.work.Job#getJobCompletionTime()
	 */
	public long getJobCompletionTime() {
		return jobCompletionTime;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.work.Job#jobPrepare(com.slytechs.utils.work.JobWorker)
	 */
	public boolean jobPrepare(JobWorker jobExecutor) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.work.Job#jobCleanup(com.slytechs.utils.work.JobWorker)
	 */
	public void jobCleanup(JobWorker jobExecutor) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.work.Job#acknoledgeCompletion(java.lang.Object)
	 */
	public Object acknowledgeCompletion(Object userObject) {
		return userObject;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.work.Job#setJobCompletionTime(long)
	 */
	public void setJobCompletionTime(long timeInMillis) {
		jobCompletionTime = timeInMillis;
	}

	/**
	 * @return Returns the jobException.
	 */
	public Exception getJobException() {
		return jobException;
	}

	/**
	 * @param jobException The jobException to set.
	 */
	public void setJobException(Exception exception) {
		this.jobException = exception;
	}

	/**
	 * @return Returns the jobCompleted.
	 */
	public boolean isJobCompleted() {
		return jobCompleted;
	}

	/**
	 * @param jobCompleted The jobCompleted to set.
	 */
	public void setJobCompleted(boolean jobCompleted) {
		this.jobCompleted = jobCompleted;
	}

	/**
	 * @return Returns the jobStatus.
	 */
	public JobStatus getJobStatus() {
		return jobStatus;
	}

	/**
	 * @param jobStatus The jobStatus to set.
	 */
	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}
	
	/**
	 * @return Returns the userObject.
	 */
	public Object getUserObject() {
		return userObject;
	}

	/**
	 * @param userObject The userObject to set.
	 */
	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}


}
