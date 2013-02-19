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

import java.util.concurrent.atomic.AtomicReference;

/**
 * Utility class for registering a new counter.
 * 
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class CounterDouble extends Counter {
	
	private AtomicReference<Double> counter;
	private double defaultValue;
	private double lastValue;
	
	/**
	 * @param name
	 * @param counter
	 */
	public CounterDouble(String name, String units, int index, double defaultValue) {
		super(name, units, index);
		
		counter = new AtomicReference<Double>((Double)(this.defaultValue = defaultValue));
		lastValue = defaultValue;
	}

	/**
	 * @param name
	 * @param counter
	 */
	public CounterDouble(String name, String units, int index) {
		this(name, units, index, 0.);

	}

	public long longValue() {
		return counter.get().longValue();
	}

	public void inc() {
		double d = counter.get();
		d ++;
		counter.set(d);
	}
	
	public double doubleValue(){
		return counter.get().floatValue();
	}

	public void add(long delta) {
		add((double)delta);
	}
	
	public void set(long value) {
		counter.set((double)value);
	}
	
	public void set(double value){
		counter.set(value);
	}

	public void reset() {
		counter.set(defaultValue);
		lastValue = defaultValue;
	}
	public String toString() {
		return getName() + "=" + counter.toString();
	}

	@Override
	public void add(double delta) {
		counter.set(counter.get() + delta);
		
	}

	@Override
	public void update(long dt) {
		/* Calculate ratio of change for this counter */
		
		double t = (double)dt /1000.;
		
		setRatio((doubleValue() - lastValue) / t); 
		
		lastValue = doubleValue();
		
		
		
	}

	@Override
	public Object getValue() {
		return counter.get();
	}

	@Override
	public void average(long value) {
		double v = counter.get();
		
		v = (v + value)/2;
		
		counter.set(v);
		
	}
	
}