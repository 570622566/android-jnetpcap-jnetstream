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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class AbstractNamespace implements Namespace {

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
	public static class DefaultLookupResult<T> implements PropertyChangeListener, LookupResult {
		
		/**
		 * A little utility structure to hold both thread and runnable reference within the same
		 * list.
		 * @author Mark Bednarczyk
		 *
		 */
		private static class Entry {
			public Thread originalThread;
			public Runnable runnable;
			
			public Entry(Thread o, Runnable r) {
				originalThread = o;
				runnable = r;
				
			}
		}
		
		private String lookupEntryName;
		private Object value;
		private Class<T> resultClass;
		
		private CountDownLatch resultAvailabilitySignal;
		
		private List<Entry> executeUponCompletion = new ArrayList<Entry>();

		public DefaultLookupResult(Class<T> resultClass, String name) {
			lookupEntryName = name;
			this.resultClass = resultClass;
		}
		
		public DefaultLookupResult(Class<T> resultClass, String name, Object value) {
			this.resultClass = resultClass;
			this.value = value;
		}

		public void propertyChange(PropertyChangeEvent evt) {

			if (evt.getPropertyName().equals(lookupEntryName)) {
				value = evt.getNewValue();
				Namespace space = (Namespace) evt.getSource();
				
				space.release(this);

				/*
				 * Now wake up a waiting thread since the entry value is now
				 * available. The sleeping, wainting thread will wake up and
				 * return results to the user.
				 */
				if (resultAvailabilitySignal != null) {
					
					resultAvailabilitySignal.countDown();
					resultAvailabilitySignal = null;
				}
				
				/*
				 * Execute our awaiting runnables.
				 */
				for(Entry entry: executeUponCompletion) {
					entry.runnable.run();
				}
			}
		}
		
		public boolean hasResult() {
			return value != null;
		}
		
		@SuppressWarnings("unchecked")
		public T getResult() {
			
			if (resultClass.isInstance(value) == false) {
				return null;
			}

			return (T) value;
		}
		
		public T waitForResult() {
			
			if (value != null) {
				return getResult();
			}
			
			/*
			 * Go to sleep and wait for entry to become available.
			 */
			try {
				if (resultAvailabilitySignal == null) {
					resultAvailabilitySignal = new CountDownLatch(1);
				}
				resultAvailabilitySignal.await();
				
				return getResult();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}

		/**
		 * <P>Schedules the runnable to be executed when results become available.
		 * More then 1 runnable can be added and each will be run in sequence.</P>
		 * 
		 * <P>The runnable is executed in the Namespace dispatcher thread, so you need
		 * to make sure that job execution of the runnable is short in duration and
		 * does not block the dispatcher.</P>
		 * 
		 * <P>Job is executed immediately if the results are available immediately.
		 * In this case, the runnable is executed in the requestors thread.</P>
		 * 
		 * @param runnable Runnable job to be executed when results of this lookup
		 * become available.
		 */
		public void executeUponCompletion(Runnable runnable) {
			
			/*
			 * If results are already available the run immediately.
			 */
			if (hasResult()) {
				runnable.run();
				
				return;
			}
			
			if (executeUponCompletion == null) {
				executeUponCompletion = new ArrayList<Entry>();
			}
			
			
			
			executeUponCompletion.add(new Entry(Thread.currentThread(), runnable));
		}

		public boolean haveIScheduledBefore() {
			for(Entry entry: executeUponCompletion) {
				if (entry.originalThread == Thread.currentThread()) {
					return true;
				}
			}
			return false;
		}
		

	}

	protected PropertyChangeSupport listeners;
	private String name;

	public AbstractNamespace() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void addListener(String entryName, PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(entryName, listener);
	}

	public void removeListener(String propertyName, PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(propertyName, listener);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
