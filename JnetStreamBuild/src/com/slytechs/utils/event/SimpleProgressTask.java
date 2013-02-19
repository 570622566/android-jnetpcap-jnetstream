/**
 * Copyright (C) 2008 Sly Technologies, Inc. This library is free software; you
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
package com.slytechs.utils.event;

import java.util.Collections;
import java.util.List;

/**
 * Simple progress class. This class keeps track of work for a single task.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class SimpleProgressTask implements ProgressTask {

	private long aggregate;

	private long complete = 0; // Amount of work complete

	private long endTime;

	private final String name;

	private long next = 0;

	private ProgressTask parent = this;

	private long startTime;

	private long total = 0; // Amount of work to be done

	public SimpleProgressTask(String name) {
		this(name, 100);
	}

	public SimpleProgressTask(String name, long total) {
		this(name, total, 0, total/20 /* 5% */);
	}

	public SimpleProgressTask(String name, long total, long complete,
	    long aggregate) {
		this.name = name;
		this.total = total;
		this.complete = complete;
		this.aggregate = aggregate;
		this.startTime = System.currentTimeMillis();

		if (name == null) {
			throw new NullPointerException("task name can not be null");
		}

		if (total <= 0) {
			throw new IllegalArgumentException(
			    "total amount of work can not be zero or negative");
		}

		if (complete > total) {
			throw new IllegalArgumentException(
			    "amount of completed work can not be greater than the total");
		}

		if (parent != null) {
			updateTotal(this, total);
			updateComplete(this, this.complete);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#finish()
	 */
	public void finish() {
		update(total - complete);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#getChildCount()
	 */
	public int getChildCount() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#getDuration()
	 */
	public long getDuration() {
		if (isComplete()) {
			return endTime - startTime;
		} else {
			return System.currentTimeMillis() - startTime;
		}
	}

	public final String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#getPercentageComplete()
	 */
	public int getPercentageComplete() {
		return (int) (complete * 100 / total);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#getTasks()
	 */
	public List<ProgressTask> getTasks() {
		return Collections.emptyList();
	}

	/**
	 * @return
	 */
	public long getTotal() {
		return total;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#hasSubtasks()
	 */
	public boolean hasSubtasks() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#isComplete()
	 */
	public boolean isComplete() {
		return complete >= total;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#isReady()
	 */
	public boolean isReady() {
		return total != 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#setParent(com.slytechs.utils.event.SuperProgressTask)
	 */
	public void setParent(ProgressTask parent) {
		this.parent = parent;

		if (parent != null) {
			parent.updateTotal(this, total);
			parent.updateComplete(this, complete);
		}
	}

	public void update() {
		update(1);
	}

	public void update(long delta) {

		complete += delta;
		
		if (complete >= total) {
			delta = total - (complete - delta);
			complete = total;
			
			endTime = System.currentTimeMillis();
			parent.updateComplete(this, delta);
			return;
			
		} else 	if (complete < 0) {
			delta = -(complete - delta);
			complete = 0;
		}

		if (complete >= this.next) {
			this.next = complete + aggregate;
			parent.updateComplete(this, delta);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#updateComplete(com.slytechs.utils.event.ProgressTask,
	 *      long)
	 */
	public void updateComplete(ProgressTask child, long complete) {
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#updateTotal(com.slytechs.utils.event.ProgressTask,
	 *      long)
	 */
	public void updateTotal(ProgressTask child, long total) {
		return;
	}

}
