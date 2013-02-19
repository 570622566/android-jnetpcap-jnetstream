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
package com.slytechs.utils.number;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <P>Utility class which implements the concept of Integer value ranges. That is you can use a string to specify
 * a range of values. You can use '-' and ',' to create blocks of value ranges. Then using Iterator<Integer> you
 * can iterate over the entire created range.
 * </P><P>
 * Example: 
 * <PRE>
 * 
 * </PRE>
 *  
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class IntegerRange implements Iterable<Integer> {
	
	/**
	 * Holds value of property rangeDefinition.
	 */
	private String rangeDefinition;
	private int size = 0;
	
	private boolean removeDuplicates = false;


	/**
	 * @param definition
	 * @throws IllegalRangeFormatException 
	 */
	public IntegerRange(String definition) throws IllegalRangeFormatException {
		setRangeDefinition(definition);
	}

	/** 
	 * Creates a new instance of IntegerRange 
	 */
	public IntegerRange() {
		/* Empty constructor */
	}

	/**
	 * Utility field used by bound properties.
	 */
	private java.beans.PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(
			this);
	
	private static int maxExpandToStringCount = 50;

	/**
	 * Adds a PropertyChangeListener to the listener list.
	 * 
	 * @param l
	 *            The listener to add.
	 */
	public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {

		propertyChangeSupport.addPropertyChangeListener(l);
	}

	/**
	 * Removes a PropertyChangeListener from the listener list.
	 * 
	 * @param l
	 *            The listener to remove.
	 */
	public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {

		propertyChangeSupport.removePropertyChangeListener(l);
	}

	/**
	 * Getter for property rangeDefinition.
	 * 
	 * @return Value of property rangeDefinition.
	 */
	public String getRangeDefinition() {

		return this.rangeDefinition;
	}

	/**
	 * Setter for property rangeDefinition.
	 * 
	 * @param rangeDefinition
	 *            New value of property rangeDefinition.
	 */
	public void setRangeDefinition(String rangeDefinition) throws IllegalRangeFormatException {
		
		/*
		 * Check for syntax errors in range definition. An exception will be thrown if error exists.
		 * And at the same time count the number of elements.
		 */
		RangeIterator ri = new RangeIterator(rangeDefinition);
		while(ri.hasNext()) {
			size ++;
			ri.next();
		}
		/* Cool, no errors, then proceed */

		String oldRangeDefinition = this.rangeDefinition;
		this.rangeDefinition = rangeDefinition;
		propertyChangeSupport.firePropertyChange("rangeDefinition",
				oldRangeDefinition, rangeDefinition);
	}

	public Iterator<Integer> iterator() {
		try {
			Iterator<Integer> i = new RangeIterator(rangeDefinition);

			return i;
			
		} catch (IllegalRangeFormatException e) {
			throw new IllegalStateException(
					"Invalid range definition was specified. This should have been checked before calling iterator()",
					e);
		}
	}

	private final class RangeIterator implements Iterator<Integer> {
		private List<Iterator<Integer>> elements = new ArrayList<Iterator<Integer>>();

		private int ptr = 0;

		private Integer next = null;

		public RangeIterator(String rangeDefinition)
				throws IllegalRangeFormatException {

			if (rangeDefinition != null) {
				String[] components = rangeDefinition.trim().split(",");

				for (final String c : components) {

					final String[] dash = c.trim().split("-");
					Integer rs;
					Integer re;

					if (dash.length == 0) {
						continue;

					} else if (dash.length == 1) {
						if (c.trim().equals("")) {
							continue;
						}
						
						try {
							rs = re = Integer.valueOf(c.trim());
						} catch (NumberFormatException e) {
							throw new IllegalRangeFormatException(
									"Illegal format for integer range definition '"
											+ c + "'");
						}
					} else if (dash.length == 2) {

						try {
							rs = Integer.valueOf(dash[0].trim());
							re = Integer.valueOf(dash[1].trim());
							
							/*
							 * Swap values if end is smaller then start
							 */
							if (re < rs) {
								Integer t = rs;
								
								rs = re;
								re = t;
							}
						} catch (NumberFormatException e) {
							throw new IllegalRangeFormatException(
									"Illegal format for integer range definition '"
											+ c + "'");
						}

					} else {
						throw new IllegalRangeFormatException(
								"Illegal format for integer range definition '"
										+ c + "'");
					}

					final Integer rangeStart = rs;
					final Integer rangeEnd = re;

					elements.add(new Iterator<Integer>() {

						private Integer start = rangeStart;

						private Integer end = rangeEnd;

						private int ptr1 = start;

						public boolean hasNext() {
							return ptr1 >= start && ptr1 <= end;
						}

						public Integer next() {
							ptr1++;
							return ptr1 - 1;
						}

						public void remove() {
							/* TODO: Implement remove element from range menthod */
						}

						public String toString() {
							if (start.equals(end)) {
								return "" + start;
							} else {
								return "" + start + "-" + end;
							}
						}
					});
				}
			}
		}
		
		private String compact() {
			StringBuilder buf = new StringBuilder();
			
			String sep = "";
			for(Iterator<Integer> i: elements) {
				buf.append(sep);
				buf.append(i.toString());
				
				sep = ",";
			}
			
			return buf.toString();
		}

		public boolean hasNext() {

			if (next == null) {
				next = getNext();
			}

			return next != null;
		}

		private Integer getNext() {

			if (ptr >= elements.size()) {
				return null;
			}

			if (elements.get(ptr).hasNext() == false) {
				ptr++;

				return getNext();
			}

			return elements.get(ptr).next();
		}

		public Integer next() {

			if (next == null) {
				next = getNext();
			}

			if (next == null) {
				throw new IllegalStateException("No more values in this range");
			}

			Integer retValue = next;
			next = null;

			return retValue;
		}

		public void remove() {
			/* TODO: Implement remove element from range menthod */
		}

	}
	
	/**
	 * Returns the format definition string that has been rebuild from resultant range.
	 * That is if there are any extra empty ',,' range definitions or ranges that evaluate
	 * to a single value such as '1-1' are refactored into '1' and ',,' empty ones removed.
	 * A new string is constructed that has the most compact definition possible for the specified
	 * range.
	 * 
	 * @return range definition string used to setup the range.
	 */
	public String toString() {
		
		RangeIterator ri = null;
		try {
			ri = new RangeIterator(rangeDefinition);
		} catch (IllegalRangeFormatException e) {
			// The syntax has already been checked, so no worries
			e.printStackTrace();
		}

		return ri.compact();
	}		

	/**
	 * Returns expanded list of integer values as string for the entire range.
	 * <P>
	 * Since very large ranges can easily be specified, causing severe problems when
	 * expanding to a single expanded string representation, the method implements
	 * protection to allow only a certain amount of values from the beginning of the
	 * range to be displayed and then a '...' string is appened and the rest of the
	 * output is truncated. This ensures that strings returned are reasonable length.
	 * </P><P>
	 * The max value for this protection can be set using a static method 
	 * {@see #setMaxExpandToStringCount(int)}.
	 * </P>
	 * 
	 * The count is the number of values, not the number of characters. So the string 
	 * in length can be much greater then the number of values. Especially when the range
	 * is in the high range such as 1000000 or more digits.
	 * 
	 * @return expanded representation of the entire range set. 
	 */
	public String toStringExpanded() {
		StringBuilder buf = new StringBuilder();
		String sep = "";
		
		int c = 0;
		
		for(Integer i: this) {
			buf.append(sep);
			buf.append(i);
			
			if (++ c >= maxExpandToStringCount ) {
				buf.append("...");
				break;
			}
			
			sep = ",";
		}
		
		return buf.toString();
	}
	
	public static void main(String[] args) {
		
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		String s;
		try {
			while ( (s = r.readLine()) != null) {
				IntegerRange range = new IntegerRange(s);
				System.out.println("" + range.getSize() + ":('" + range.toString() + "'):"+ range.toStringExpanded());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalRangeFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getSize() {
		return size;
	}

	/**
	 * Set to true to remove duplicate overlapping values defined within the specified range.
	 * i.e. 1,1,2,3 will remove and duplicate 1 and simply return the range 1,2,3. When set to false,
	 * the same example will return 1,1,2,3 values in that order.
	 * 
	 * @return true when duplicates are being removed, otherwise false.
	 */
	public boolean isRemoveDuplicates() {
		return removeDuplicates;
	}

	/**
	 * Set to true to remove duplicate overlapping values defined within the specified range.
	 * i.e. 1,1,2,3 will remove and duplicate 1 and simply return the range 1,2,3. When set to false,
	 * the same example will return 1,1,2,3 values in that order.
	 * 
	 * @param removeDuplicates
	 */
	@SuppressWarnings("unused")
	private void setRemoveDuplicates(boolean removeDuplicates) {
		this.removeDuplicates = removeDuplicates;
		// TODO: Implement the logic to sort and remove duplicates from Integer ranges.
	}

	/**
	 * Returns the maximum count of values that are returned as string using the
	 * @see #toStringExpanded() method.
	 * @return
	 */
	public static int getMaxExpandToStringCount() {
		return maxExpandToStringCount;
	}

	/**
	 * Sets the maximum number of values to be returned as a string from the call
	 * to {@see #toStringExpanded()}. The count is the number of values, not characters
	 * within the returned string.
	 * 
	 * @param maxExpandToStringCount
	 */
	public static void setMaxExpandToStringCount(int maxExpandToStringCount) {
		IntegerRange.maxExpandToStringCount = maxExpandToStringCount;
	}
}
