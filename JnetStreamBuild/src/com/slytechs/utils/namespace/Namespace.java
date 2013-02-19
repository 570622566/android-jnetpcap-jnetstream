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


/**
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Namespace {
	
	/**
	 * <P>Handles the results of the lookup operation on Namespace. If the result
	 * of the lookup is available immediately then hasResult() and getResult() return
	 * immediately with the parametarized result.</P>
	 * 
	 * <P>If the result is not available immediately, then you can use any of the 4 methods available
	 * to wait until the result becomes available.
	 * <UL>
	 *  <LI> In a while() loop check hasResult() method if result became available and sleep for some time
	 *  in between.
	 *  <LI> Register a listener on the event that result become available. The listener will be notified
	 *  of the event.
	 *  <LI> Use the waitForResult() method which is a blocking call which will block the thread until the
	 *  result become available. This is very efficient means of waiting on the result. All inter thread
	 *  communication is handled automatically and guarrantees proper mutil thread semantics.
	 *  <LI> Add a java.lang.Runnable object using executeUponCompletion() method. The runnable will
	 *  be executed when the result become available.
	 * </UL></P>
	 * 
	 * <P>If you wish to cancel the lookup operation before the result becomes available, you must release the
	 * LookupResult object manually from the Namespace dispatcher. Otherwise a number of never successfull lookups
	 * could leak memory. To release the lookup result call Namespace.release(LookupResult) method.</P>
	 * 
	 * @author Mark Bednarczyk
	 *
	 * @param <T> Type of returned result.
	 */
	public interface LookupResult<T> {
		
		public boolean hasResult();
		
		public T getResult();
		
		public T waitForResult();

		public void executeUponCompletion(Runnable runnable);

		public boolean haveIScheduledBefore();
		
	}

	public static final String SEPARATOR = "\\.";
	
	public static final String ENTRY_LOOKUP_MISS = "$_entry_lookup.miss_$";
	public static final String NAMESPACE_GET_MISSED = "$_.namespace.get.miss_$";

	public Namespace add(String name, Object entry);

	public Namespace add(SimpleNamespace space);
	
	/**
	 * <P>Returns the namespace if available, otherwise null is returned.
	 * To lookup entries in a namespace, use the lookup() method.</P>
	 * 
	 * <P> The analogy between get() and lookup() is, 
	 * getDirectory() vs. getFile() for a filesystem based domain.</P>
	 * 
	 * @param name Namespace name to lookup.
	 * @return Namespace if exists, otherwise null.
	 */
	public Namespace get(String name);

	public void addListener(String entryName,
			PropertyChangeListener listener);

	public void removeListener(String propertyName,
			PropertyChangeListener listener);

	public String getName();

	public void setName(String name);
	
	public <T>LookupResult<T> lookup(String name, Class<T> c);
	
	public void release(LookupResult lookupResult);
	
	public Namespace createNamespace(String[] path, int offset);

}