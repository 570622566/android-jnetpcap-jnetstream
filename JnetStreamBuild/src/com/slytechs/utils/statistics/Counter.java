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


/**
 * Utility class for registering a new counter.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class Counter {
	private String name;
	int index;
	private String units;
	private double ratio;


	/**
	 * @param name
	 * @param counter
	 */
	/**
	 * @param name
	 * @param counter
	 */
	public Counter(String name, String units, int index) {

		this.name = name;
		this.index = index;
		this.units = units;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public abstract Object getValue();
	public abstract long longValue();

	public abstract void inc();
	
	public abstract double doubleValue();

	public abstract void add(long delta);
	
	public abstract void add(double delta);
	
	public abstract void average(long value);

	public abstract void set(long value);
	
	public abstract void set(double value);

	public abstract void reset();
	
	public abstract void update(long dt);

	public String toString() {
		return name;
	}
	
	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public double getRatio() {
		return ratio;
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}

}