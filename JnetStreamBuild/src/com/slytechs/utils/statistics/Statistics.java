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
//import java.util.Hashtable;
//import java.util.TimerTask;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
///**
// * 
// * @author Mark Bednarczyk
// * @author Sly Technologies, Inc.
// */
//public abstract class Statistics {
//	public static final String MODULE = "statistics";
//	public static final Log logger = LogFactory.getLog(MODULE);
//	
//	public static final String ROOT_NAME = "root";
//	public static final long UPDATE_REPEAT = 1 * 1000; // 1 sec
//	private static Statistics rootStatistics = new GStatistics(ROOT_NAME);
//	private String name;
//	private Statistics parent;
//	
//	
//	protected Statistics(String name) {
//		this.name = name;
//		this.parent = null;
//		
//	}
//	
//	protected Statistics(String name, Statistics parent) {
//		this.name = name;
//		this.parent = parent;
//	}
//
//	public static Statistics getRootStatistics() {
//		return rootStatistics;
//	}
//	
//	public static Statistics getStatistics(String moduleName){
//		Hashtable<String, Statistics> children = rootStatistics.getChildren();
//		Statistics module = children.get(moduleName);
//		
//		if (module == null){
//			module = new GStatistics(moduleName, rootStatistics);
//			children.put(moduleName, module);
//		}
//		
//		return module;	
//	}
//
//
//	public abstract void addTimerTask(String taskName, long repeatInterval,
//			TimerTask task);
//	
//	public abstract void cancelAllTimers();
//	public abstract void startAllTimers();
//	
//
//	/**
//	 * Adds a new counter to the statistics object to keep track off.
//	 * 
//	 * Counters are grouped by modules and then indexed by counter index
//	 * returned by addCounter.
//	 * 
//	 * @param moduleName
//	 *            name of the module to group this counter under.
//	 * @param counterName
//	 *            name of the counter. Human readable name to present along with
//	 *            the value of the counter.
//	 * @return index of this counter. Index numbers are not uniqeue between
//	 *         modules. Each module index may start from 0 and up.
//	 */
//	public abstract Counter addCounterLong(String counterName, String units);
//	public abstract Counter addCounterDouble(String counterName, String units);
//	
//	public abstract void showFrame();
//	public abstract void hideFrame();
//
//	/**
//	 * Gets a specific counter within a specific module.
//	 * 
//	 * @param moduleName
//	 *            module group to lookup the counter.
//	 * @param index
//	 *            index number of the counter as returned during the
//	 *            addCounter() method call.
//	 * @return returns a reference to the thread safe counter or null if either
//	 *         the module name is not found.
//	 * @exception IndexOutOfBoundsException
//	 *                when counter index for this particular module is out of
//	 *                range. Each module can have any number of counters.
//	 *                This exception is also thrown when the module lookup fails by 
//	 *                name. This is also a out of bounds exception.
//	 * 
//	 */
//	public abstract Counter getCounter(int index)
//			throws IndexOutOfBoundsException;
//
//	protected abstract Hashtable<String, Statistics> getChildren();
//	
//	public abstract void update(long dt);
//	
//	protected Statistics getParent() {
//		return parent;
//	}
//
//	
//	public String getFullName() {
//		if (parent != null) {
//			return parent.getName() + "." + getName();
//		}
//		
//		return getName();
//	}
//	
//	protected String getName() {
//		return name;
//	}
//
//	protected void setName(String name) {
//		this.name = name;
//	}
//	
//	public abstract boolean removeCounter(Counter counter);
//
//	public static boolean remove(Counter counter) {
//
//		return rootStatistics.removeCounter(counter);
//		
//	}
//
//
//
//}