///**
// * $Id$
// *
// * Copyright (C) 2006 Mark Bednarczyk, Sly Technologies, Inc.
// *
// * This library is free software; you can redistribute it and/or
// * modify it under the terms of the GNU Lesser General Public License
// * as published by the Free Software Foundation; either version 2.1
// * of the License, or (at your option) any later version.
// *
// * This library is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// * See the GNU Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this library; if not, write to the
// * Free Software Foundation, Inc.,
// * 59 Temple Place,
// * Suite 330, Boston,
// * MA 02111-1307 USA
// * 
// */
//package com.slytechs.utils.statistics;
//
//import java.util.ArrayList;
//import java.util.Hashtable;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
///**
// * A class that keeps and reports on statistics.
// * 
// * This class is thread safe and can be used to keep any type of statistics. All
// * stats are kept as 64-bit longs.
// * 
// * Statistics class runs in its own thread and can be configured to write its
// * output periodically to console or database.
// * 
// * 
// * @author Mark Bednarczyk
// * @author Sly Technologies, Inc.
// */
//public abstract class DefaultStatistics extends Statistics {
//	
//	private static final int TIMER_LIST_SIZE = 10;
//
//	private Hashtable<String, Statistics> children = new Hashtable<String, Statistics>();
//	private List<Counter> counters = new ArrayList<Counter>();
//	private List<TimerTaskInfo> tasks = new ArrayList<TimerTaskInfo>(TIMER_LIST_SIZE);
//	private List<Timer> timers = new ArrayList<Timer>(TIMER_LIST_SIZE);
//
//	protected DefaultStatistics(String name){
//		super(name);
//		
//	}
//	
//	protected DefaultStatistics(String name, Statistics parent) {
//		super(name, parent);
//	}
//	
//	public static class TimerTaskInfo {
//		 TimerTask task;
//		 String name;
//		 long interval;
//		 Timer timer;
//		/**
//		 * @param name
//		 * @param interval
//		 * @param task
//		 */
//		public TimerTaskInfo(String name, long interval, TimerTask task, Timer timer) {
//			// TODO Auto-generated constructor stub
//			this.name = name;
//			this.interval = interval;
//			this.task = task;
//			this.timer = timer;
//		}
//		
//		
//	}
//	
//	/* (non-Javadoc)
//	 * @see com.slytechs.utils.Statistics#addTimerTask(java.util.TimerTask, java.lang.String, long)
//	 */
//	public void addTimerTask(String taskName, long repeatInterval, TimerTask task) {
//		Timer timer = new Timer(taskName);
//		
//		tasks.add(new TimerTaskInfo(taskName, repeatInterval, task, timer));
//		
//		logger.trace("Adding task: " + taskName);
//		
//	}
//	
//	public void startAllTimers() {
//		
//		for (TimerTaskInfo info: tasks) {
//			
//			if (timers.contains(info.timer) == true){
//				continue;
//			}
//			
//			info.timer.scheduleAtFixedRate(info.task, info.interval * 2, info.interval);		
//			timers.add(info.timer);
//			logger.trace("Starting task: " + info.name);
//		}
//		
//	}
//	
//	/**
//	 * Stop all timers
//	 *
//	 */
//	public void cancelAllTimers() {
//		for(Timer timer: timers) {
//			if(timer != null) {
//				timer.cancel();
//				timer = null;
//			}
//		}
//		
//	}
//	
//
//	/* (non-Javadoc)
//	 * @see com.slytechs.utils.Statistics#addCounter(java.lang.String, java.lang.String)
//	 */
//	public Counter addCounterLong(String counterName, String units) {
//
//		Counter counter = new CounterLong(counterName, units, counters.size());
//		return addCounter(counter);
//
//	}
//	
//	public Counter addCounterDouble(String counterName, String units) {
//
//		Counter counter = new CounterDouble(counterName, units, counters.size());
//		return addCounter(counter);
//	}
//	
//	protected Counter addCounter(Counter counter) {
//		counters.add(counter);
//
//		return counter;
//		
//	}
//	
//	public void reset() {
//		for(Counter counter: counters){
//			counter.reset();
//		}
//	}
//	
//	/* (non-Javadoc)
//	 * @see com.slytechs.utils.Statistics#getCounter(java.lang.String, int)
//	 */
//	public Counter getCounter(int index)
//			throws IndexOutOfBoundsException {
//		
//		return counters.get(index);
//
//	}
//
//	protected Hashtable<String, Statistics> getChildren() {
//		return children;
//	}
//
//
//	protected List<Counter> getCounters() {
//		return counters;
//	}
//	
//	public boolean removeCounter(Counter counter) {
//		if (counters.remove(counter) == true) {
//			return true;
//		}
//		
//		for(Statistics child: children.values()) {
//			if (child.removeCounter(counter)) {
//				return true;
//			}
//		}
//		
//		return false;
//	}
//	
//	public void update(long dt) {
//		
//		for(Statistics child: children.values()){
//			child.update(dt);
//		}
//		for(Counter counter: counters){
//			counter.update(dt);
//		}
//	}
//}
