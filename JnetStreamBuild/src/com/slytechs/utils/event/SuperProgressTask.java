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

import java.util.ArrayList;
import java.util.List;

/**
 * A task progress monitor for a group of tasks. This class does not keep track
 * of work for itself, only sub-tasks. The class is also responsible for
 * dispatching progress update notifications to monitors. If no monitors have
 * been defined, a ConsoleProgressMonitor is used to display progress
 * information.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class SuperProgressTask implements ProgressTask {

	private static ProgressMonitor defaultMonitor;

	private long complete;

	private long endTime;

	private List<ProgressMonitor> monitors = new ArrayList<ProgressMonitor>();

	private final String name;

	private ProgressTask parent;

	private long startTime;

	private List<ProgressTask> tasks = new ArrayList<ProgressTask>();

	private long total = 0;

	/**
	 * Sets up a progress monitor of last resort. If no other monitors are
	 * connected, the {@link SuperProgressTask} node will use the default monitor
	 * if its not null.
	 * 
	 * @param monitor
	 *          setup the default monitor or null to turn off default monitoring
	 */
	public static void setDefaultMonitor(ProgressMonitor monitor) {
		defaultMonitor = monitor;
	}

	public SuperProgressTask(String name) {
		this.name = name;
		this.startTime = System.currentTimeMillis();

	}

	public boolean addMonitor(ProgressMonitor o) {
		return this.monitors.add(o);
	}

	public ProgressTask addTask(ProgressTask task) {
		tasks.add(task);
		task.setParent(this);

		return task;
	}

	public ProgressTask addTask(String name, long total) {
		SimpleProgressTask task = new SimpleProgressTask(name, total);

		return addTask(task);
	}
	
	public ProgressTask addTask(String name, long total, long complete) {
		SimpleProgressTask task = new SimpleProgressTask(name, total, complete, 1);

		return addTask(task);
	}
	
	public ProgressTask addTask(String name, long total, long complete, long aggregate) {
		SimpleProgressTask task = new SimpleProgressTask(name, total, complete, aggregate);

		return addTask(task);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#finish()
	 */
	public void finish() {
		for (ProgressTask task : tasks) {
			task.finish();
		}
	}

	private void fireUpdate(ProgressTask child) {
		final int size = monitors.size();

		/*
		 * By default progress is sent to a Console reporter
		 */
		if (size == 0 && defaultMonitor != null) {
			defaultMonitor.progressUpdate(this, child);
			return;

		}

		for (int i = 0; i < size; i++) {
			ProgressMonitor monitor = monitors.get(i);

			monitor.progressUpdate(this, child);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#getChildCount()
	 */
	public int getChildCount() {
		return tasks.size();
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
		if (total == 0) {
			return 0;
		}

		return (int) (complete * 100 / total);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#getTasks()
	 */
	public List<ProgressTask> getTasks() {
		return tasks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#getTotal()
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
		return tasks.isEmpty();
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

	public void removeAllMonitor() {
		this.monitors.clear();
	}

	public boolean removeMonitor(Object o) {
		return this.monitors.remove(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#setParent(com.slytechs.utils.event.ProgressTask)
	 */
	public void setParent(ProgressTask parent) {
		this.parent = parent;

		if (parent != null) {
			parent.updateTotal(this, total);
			parent.updateComplete(this, complete);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#update()
	 */
	public void update() {
		throw new UnsupportedOperationException(
		    "you can not update work on a super task");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#update(long)
	 */
	public void update(long i) {
		throw new UnsupportedOperationException(
		    "you can not update work on a super task");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#updateComplete(com.slytechs.utils.event.ProgressTask,
	 *      long)
	 */
	public void updateComplete(ProgressTask child, long delta) {
		this.complete += delta;

		if (complete == total) {
			endTime = System.currentTimeMillis();
		}

		if (parent != null) {
			parent.updateComplete(this, delta);
		} else {
			fireUpdate(child);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressTask#updateTotal(com.slytechs.utils.event.ProgressTask,
	 *      long)
	 */
	public void updateTotal(ProgressTask child, long delta) {
		this.total += delta;

		if (parent != null) {
			parent.updateTotal(this, delta);
		}
	}

}
