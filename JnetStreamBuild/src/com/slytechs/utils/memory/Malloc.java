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
package com.slytechs.utils.memory;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Malloc class allocates memory like old C malloc()
 * function used to do. In this case the memory is allocated using a large
 * nio.buffer either as a direct buffer or array based. This large buffer
 * acts as the heap from which smaller chunks are allocated as a view buffers
 * very efficiently. Since direct memory allocation has a fairely large penalty
 * this mechanism allocates the direct memory in frequently by allocating large
 * chunks of memory and then allocating and freeing portions of it as requested.
 * </P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * 
 */
public final class Malloc {
	private static final Log logger = LogFactory.getLog(Malloc.class);

	/**
	 * Class which contains information about allocated, or free block within
	 * the main heap memory. For every allocated chunk an entry is made in
	 * the allocated list and for all the free/available memory space atleast
	 * 1 such entry exists as well. As blocks are freed the entries as put
	 * back on the free list. If more then 1 element is adjacent to each other
	 * they are merged into a single larger element that contains the space
	 * of both separate ones.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 *
	 */
	public static class Entry {
		private int start;

		private int length;
		
		public String toString() {
			return "[" + getStart() + ", " + getLength() + "]";
		}

		/**
		 * Sets the starting offset of the chunk of memory this entry describes.
		 * 
		 * @param start the start to set
		 */
		public void setStart(int start) {
			this.start = start;
		}

		/**
		 * Gets the starting offset of the chunk of memory this entry describes.
		 * @return the start
		 */
		public int getStart() {
			return start;
		}

		/**
		 * Sets the length in bytes of the chunk of memory this entry describes.
		 * @param length the length to set
		 */
		public void setLength(int length) {
			this.length = length;
		}

		/**
		 * Gets the length in bytes of the chunk of memory this entry describes.
		 * @return the length
		 */
		public int getLength() {
			return length;
		}
	}

	public static final int HEAP_CAPACITY = 1024 * 1024;

	private ByteBuffer heap;

	private List<Malloc.Entry> freeList = new LinkedList<Malloc.Entry>();

	private ReferenceQueue<Buffer> freedQueue = new ReferenceQueue<Buffer>();

	private Map<Reference<? extends Buffer>, Malloc.Entry> allocated = 
		new HashMap<Reference<? extends Buffer>, Malloc.Entry>();

	private static Malloc malloc = new Malloc();

	public static Malloc getDefault() {
		return malloc;
	}

	/**
	 * 
	 */
	public Malloc() {
		heap = ByteBuffer.allocateDirect(HEAP_CAPACITY);
		Entry e = new Entry();
		e.setStart(0);
		e.setLength(HEAP_CAPACITY);
		freeList.add(e);
	}

	public ByteBuffer allocateBuffer(int length) {
		
		/*
		 * Process freed memory blocks from the freed queue
		 */
		processQueue();

		Entry fb = null;

		/*
		 * Find empty block large enough to hold the requested length
		 */
		for (Entry e : freeList) {
			if (e.getLength() >= length) {
				fb = e;
				break;
			}
		}

		/*
		 * Not enough free blocks to accomodate the length requested
		 */
		if (fb == null) {
			return null;
		}

		/*
		 * Create the block/buffer view 
		 */
		heap.limit(fb.getStart() + length);
		heap.position(fb.getStart());
		ByteBuffer block = heap.slice();
		
		/*
		 * Create an entry that remembers the details of the allocation
		 */
		Entry a = new Entry();
		a.setStart(fb.getStart());
		a.setLength(length);

		recordAllocatedBlock(a, block);
		
		fb.setStart(fb.getStart() + length);
		fb.setLength(fb.getLength() - length);

		return block;
	}
	
	public void recordAllocatedBlock(Entry a, Buffer block) {
		
		/*
		 * The software reference linked to reference queue "freedQueue". When
		 * the referenced buffer is released, the reference is recorded in
		 * the freeQueue so we can check and get a list of references (not the
		 * buffer's anymore) that have been freed up by garbage collector.
		 * 
		 * Since the reference released is also the key in our allocated Map,
		 * which stores the Entry value with the details of the allocated block 
		 * (But no references to the buffer), we can later expand our free
		 * blocks to reclaim the memory as its released.
		 */
		SoftReference<? extends Buffer> pr = new SoftReference<Buffer>(block,
				freedQueue);

		allocated.put(pr, a);
	}

	public void processQueue() {
		Reference<? extends Buffer> r;
		
		while ((r = freedQueue.poll()) != null) {
			Entry e = allocated.remove(r);
			
			logger.trace("Freeing block entry" + e);
			free(e);
		}
	}
	
	public void free(ByteBuffer buffer) {
		for (Reference<? extends Buffer> r: allocated.keySet()) {
			if (r.get() == buffer) {
				free(allocated.remove(r));
				break;
			}
		}
	}
	
	public void free(Entry entry) {
		/*
		 * Conveniece variables that hold the start and end of the freed block
		 */
		int start = entry.getStart();
		int end = entry.getStart() + entry.getLength();

		/*
		 * Find empty block for which we can apply the entry to. We expand
		 * the length of the free block by the length found in the entry. That
		 * is we are reclaiming freed space (described in Entry) into a free
		 * block. 
		 */
		int i = 0; // Index into the freelist
		for (Entry free: freeList) {
			/*
			 * Convenience variables
			 */
			int fs = free.getStart();
			int fe = free.getStart() + free.getLength();
			
			if (end < fs) {
				/*
				 * The freed block needs to be inserted into the free list
				 */
				freeList.add(i, entry);
				mergeContigeousEntries();
				return;
				
			} else if (start == fe) {
				/*
				 * The freed block needs to be merged with the current free
				 * node
				 */
				free.setLength(free.getLength() + entry.getLength());
				mergeContigeousEntries();
				return;
				
			} else if (end == fs) {
				/*
				 * The freed block needs to be merged with the current free
				 * node
				 */
				free.setStart(free.getStart() - entry.getLength());
				mergeContigeousEntries();
				return;
				
			} else if (end <= fe) {
				/*
				 * The freed entry ends in some reclaimed space already. This is
				 * an internal logic error
				 */
				throw new IllegalStateException("Freed node is already on the free list, this should not be");
			}
			
			i++;
		}
		
		throw new IllegalStateException("Unable to free node, no corresponding entry found in the free list");
	}
	
	/**
	 * Scan the free list for consecutive/contigeous records that should be
	 * combined together.
	 *
	 */
	private void mergeContigeousEntries() {
		
		Entry previous = null;
		Iterator<Malloc.Entry> i = freeList.iterator();
		
		while (i.hasNext()) {
			Entry current = i.next();
			
			if (previous == null) {
				previous = current;
				continue;
			}
			
			if (previous.start + previous.length == current.start) {
				previous.length += current.length;
				i.remove();
				continue;
			}
			
			previous = current;
		}

	}
	
	/**
	 * Method that returns the free list which contains a list of free block,
	 * or chunks available for allocation. The list is returned as immutable,
	 * but it does allow testing behaviour of the Malloc class or possibly 
	 * deciding on a certain memory allocation strategy. This method is mainly
	 * provided for testing purposes such as 
	 * {@link com.slytechs.utils.tests.TestMalloc}
	 * 
	 * @return
	 *   immutable list containing the free block available for allocation
	 */
	public List<Malloc.Entry> getFreeList() {
		return Collections.unmodifiableList(freeList);
	}

}
