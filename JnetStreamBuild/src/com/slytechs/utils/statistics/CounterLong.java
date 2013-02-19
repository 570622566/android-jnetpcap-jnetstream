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
package com.slytechs.utils.statistics;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class for registering a new counter.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class CounterLong extends Counter {
	
	private AtomicLong counter = new AtomicLong();
	private int averageCount = 1000;
	private long defaultValue;
	private long lastValue;
	private BlockingQueue<Long> valueQueue = new ArrayBlockingQueue<Long>(averageCount);
	private long averageSumTotal = 0;
	
	/**
	 * @param name
	 * @param counter
	 */
	public CounterLong(String name, String units, int index, long defaultValue) {
		this(name, units, index);
		
		this.defaultValue = defaultValue;
		this.lastValue = defaultValue;

	}

	/**
	 * @param name
	 * @param counter
	 */
	public CounterLong(String name, String units, int index) {
		super(name, units, index);

	}

	public long longValue() {
		return counter.longValue();
	}

	public void inc() {
		counter.incrementAndGet();
	}
	
	public double doubleValue(){
		return counter.floatValue();
	}

	public void add(long delta) {
			counter.addAndGet(delta);
	}
	
	public void set(long value) {
		counter.set(value);
	}
	
	public void set(float value){
		counter.set((long)value);
	}

	public void reset() {
		counter.set(defaultValue);
	}
	public String toString() {
		return getName() + "=" + counter.toString();
	}

	@Override
	public void add(double delta) {
		add((long)delta);
		
	}

	@Override
	public void set(double value) {
		set((long)value);
		
	}

	@Override
	public void update(long dt) {
		double t = (double)dt / 1000.;
//		double r = (getRatio() + ((doubleValue() - (double)lastValue)/t))/2;
//		long rr = (long)(r * 10);
//		r = (double)rr / 10;
		setRatio((doubleValue() - (double)lastValue)/t);	
		lastValue = longValue();
	}

	@Override
	public Object getValue() {
		return counter.longValue();
	}

	@Override
	public void average(long value) {
		
		
		if (valueQueue.remainingCapacity() == 0) {
			try {
				averageSumTotal -= valueQueue.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		valueQueue.offer(value);
		averageSumTotal += value;
		
		counter.set(averageSumTotal / valueQueue.size());
	}

	/**
	 * @return Returns the averageCount.
	 */
	protected int getAverageCount() {
		return averageCount;
	}

	/**
	 * @param averageCount The averageCount to set.
	 */
	protected void setAverageCount(int averageCount) {
		
		/*
		 * First copy all old queue contents into new queue
		 */
		ArrayBlockingQueue<Long> q = new ArrayBlockingQueue<Long>(averageCount);
		q.addAll(valueQueue);
		valueQueue = q;		
		
		this.averageCount = averageCount;
	}
	
}