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
package com.slytechs.utils.namespace;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import com.slytechs.utils.number.IllegalRangeFormatException;
import com.slytechs.utils.number.IntegerRange;

/**
 * <P>Hierarchal name space. The namespace contains entries which can be looked up
 * and appropriate entry values returned. Very similar to a Map interface. The difference
 * is that name space can be hierarchal and various lookup methods can be utilized to resolve
 * a name to an entry within child or parent namespaces.</P>
 * 
 * <P> In addition, you can register listeners for changes to particular namespace within the hierarchy.</P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class SimpleNamespace extends AbstractNamespace {
	private static Namespace defaultNamespace;
	private Map<String, Object> entries;
	private Map<String, Namespace> children;
	private Map<String, LookupResult> unresolvedLookup;
	
	public SimpleNamespace(String name) {
		entries = new HashMap<String, Object>();
		children = new HashMap<String, Namespace>();
		
		listeners = new PropertyChangeSupport(this);
		
		unresolvedLookup = new HashMap<String, LookupResult>();
		
		setName(name);
	}
	
	/* (non-Javadoc)
	 * @see com.slytechs.utils.namespace.Namespace#add(java.lang.String, java.lang.Object)
	 */
	public Namespace add(String name, Object entry) {
		
		Object oldValue = entries.get(name);
		entries.put(name, entry);
		
		listeners.firePropertyChange(name, oldValue, entry);
		
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.slytechs.utils.namespace.Namespace#add(com.slytechs.utils.namespace.SimpleNamespace)
	 */
	public Namespace add(SimpleNamespace space) {
		children.put(space.getName(), space);
		
		return space;
	}
	
	public static Namespace getDefault(String path) {
		String[] elements = parsePath(path);
		
		return getDefault().createNamespace(elements, 0);
	}
	
	public Namespace createNamespace(String[] path, int offset) {

		/*
		 * Sanity assertion
		 */
		if (path[offset].equals(getName()) == false) {
			return null;
		}
		
		/*
		 * We've reached the end of the path, return that namespace
		 */
		if (offset == (path.length -1)) {
			return this;
		}
		
		/*
		 * Advance to the next component within the path
		 */
		offset ++;
		
		/*
		 * Get the sub namespace or create it if it doesn't exist
		 */
		Namespace childSpace = children.get(path[offset]);
		if (childSpace == null) {
			childSpace = new SimpleNamespace(path[offset]);
			children.put(path[offset], childSpace);
		}
		
		return childSpace.createNamespace(path, offset);
	}
	
	public static String[] parsePath(String path) {
		return path.split(SEPARATOR);
	}
	
	public static Namespace getDefault() {
		if (defaultNamespace == null) {
			defaultNamespace = new SimpleNamespace("root");
		}
		
		return defaultNamespace;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			Thread t1 = new Thread("t1") {
				public void run() {
					Namespace space = SimpleNamespace.getDefault();
					try {
						System.out.println("T1: sleeping for 10 seconds");
						System.out.flush();
						Thread.sleep(10 * 1000);
						
						System.out.println("T1: setting entry");
						System.out.flush();
						space.add("abc", new IntegerRange("10-20,,,,,100-1000"));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalRangeFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			Thread t2 = new Thread("t2") {
				public void run() {
					Namespace space = SimpleNamespace.getDefault();
					
					LookupResult<IntegerRange> result = space.lookup("abc", IntegerRange.class);
					
					System.out.println("T2: waiting on 'abc' lookup");
					System.out.flush();
					IntegerRange s = result.waitForResult();
					System.out.println("T2: s=" + s);
					System.out.flush();
					
				}
			};
			Thread t3 = new Thread("t3") {
				public void run() {
					Namespace space = SimpleNamespace.getDefault();
					
					LookupResult<IntegerRange> result = space.lookup("abc", IntegerRange.class);
					
					System.out.println("T3: waiting on 'abc' lookup");
					System.out.flush();
					IntegerRange s = result.waitForResult();
					System.out.println("T3: s=" + s);
					System.out.flush();
					
				}
			};
			
			t1.start();
			t2.start();
			t3.start();
			
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	
	@SuppressWarnings("unchecked")
	public <T> LookupResult<T> lookup(String name, Class<T> cl) {
		Object o = entries.get(name);
				
		if (o == null) {
			DefaultLookupResult<T> result = (DefaultLookupResult<T>) unresolvedLookup.get(name);
			
			if (result == null) {
				unresolvedLookup.put(name, result = new DefaultLookupResult<T>(cl, name));
				addListener(name, result);
				
				listeners.firePropertyChange(ENTRY_LOOKUP_MISS, null, name);
			}
			
			return result;
		} 

		return new DefaultLookupResult<T>(cl, name, o);
	}

	/**
	 * Releases the ResultLookup object from taking up resources. A lookup object
	 * is cached until the entry that its waiting on becomes available. Until then the
	 * object takes up resources. So if a result will never be successfully fulfilled, then 
	 * this object would never have a chance to be released.
	 * 
	 * @param lookupResult The LookupResult object to release. If lookup has already been fulfilled
	 * then this object has already been released and nothing happens in this method.
	 */
	public void release(LookupResult lookupResult) {
		
		for(String key: unresolvedLookup.keySet()) {
			if (unresolvedLookup.get(key) == lookupResult) {
				unresolvedLookup.remove(key);
			}
			
			removeListener(key, (PropertyChangeListener) lookupResult);
		}
	}

	public Namespace get(String name) {
		
		Namespace child = children.get(name);
		
		if (child == null) {
			listeners.firePropertyChange(NAMESPACE_GET_MISSED, null, name);
		}
		
		return child;
	}

}
