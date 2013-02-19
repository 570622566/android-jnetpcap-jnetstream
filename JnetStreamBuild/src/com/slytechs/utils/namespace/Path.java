/**
 * Copyright (C) 2007 Sly Technologies, Inc.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.slytechs.utils.namespace;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * <P>
 * A virtual path made up of PathElements. A path can be specified as an array
 * of PathElements where each PathElement specifies the sub-element of the
 * parent path.
 * <P>
 * 
 * <P>
 * To use an empty path, use the public static constant {@link #empty}, there
 * is no other way to instantiate an empty path.
 * </P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * 
 */
public class Path {

	public static final Path empty = new Path();

	public static class Element {
		public String name = "";

		public Element(String name) {
			this.name = (name != null)?name:"";
		}
	}

	private static final String DEFAULT_SEPARATOR = "\\.";

	private List<Element> path = new LinkedList<Element>();

	private String separator = DEFAULT_SEPARATOR;

	private int hashValue = 0;

	private boolean wildMatch = false;

	private Path() {
		hashValue = 1;
	}

	public Path(Path path) {
		Collections.copy(this.path, path.path);
		hashValue = path.hashValue;
		wildMatch = path.wildMatch;
	}

	public Path(Named... path) {
		for (Named e : path) {
			this.path.add(new Element(e.getName()));
		}

		for (Element e : this.path) {
			hashValue ^= e.name.hashCode();
		}
	}

	public Path(Element... path) {
		for (Element e : path) {
			this.path.add(e);
		}

		for (Element e : this.path) {
			hashValue ^= e.name.hashCode();
		}
	}

	public Path(String... path) {
		for (String e : path) {
			this.path.add(new Element(e));
		}

		for (Element e : this.path) {
			hashValue ^= e.name.hashCode();
		}

	}

	public Path(String path) {
		String[] c = path.split(separator);

		for (String e : c) {
			this.path.add(new Element(e));
		}

		for (Element e : this.path) {
			hashValue ^= e.name.hashCode();
		}
	}

	/**
	 * @param start 
	 *   0 based index of the first element on the path array
	 *   
	 * @param end
	 *   0 based index of the last element on the path array
	 *   
	 * @param path
	 *   array of elements of the path to be constructed
	 */
	public Path(int start, int end, Element[] path) {
		for (int i = start; i < end; i ++) {
	
			this.path.add(path[i]);
		}

		for (Element e : this.path) {
			hashValue ^= e.name.hashCode();
		}

	}

	public boolean setWildMatch(boolean state) {
		boolean o = wildMatch;
		wildMatch = state;

		return o;
	}

	public boolean isEmpty() {
		return this.path.isEmpty();
	}

	public int hashCode() {
		if (hashValue == 0) {

		}

		return hashValue;
	}

	public boolean equals(final Object o) {
		if (o instanceof Path) {
			final Path p = (Path) o;
			if (p.path.size() != this.path.size()) {
				return false;
			}

			// final int s = path.size();
			//			
			// for (int i = 0; i < s; i ++) {
			// if (p.path.get(i).equals(this.path.get(i)) == false) {
			// return false;
			// }
			// }

			/*
			 * Instead of comparing the entire path, character for character, a
			 * faster method is to just compare the hashCodes() of both if the
			 * depth of the two paths match
			 */

			return p.hashCode() == this.hashCode();
		} else {
			return false;
		}
	}
	
	public boolean equals(final int index, String component) {
		if (index >= path.size() || index < 0) {
			return false;
		}
		return path.get(index).equals(component);
	}

	private static final StringBuilder b = new StringBuilder();

	public static String format(String ... path) {
		b.setLength(0);
		
		for (String s: path) {
			if (b.length() != 0) {
				b.append('.');
			}
			b.append(s);
		}
		
		return b.toString();
	}

	/**
   * @return
   */
  public int size() {
	  return path.size();
  }
}
