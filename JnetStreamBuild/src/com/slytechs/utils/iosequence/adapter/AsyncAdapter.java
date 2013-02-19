/**
 * $Id$
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
package com.slytechs.utils.iosequence.adapter;

import com.slytechs.utils.iosequence.Input;
import com.slytechs.utils.iosequence.Output;
import com.slytechs.utils.iosequence.SequenceElement;

public class AsyncAdapter<I extends SequenceElement> extends Adapter<I, I> implements Runnable {
	
	private Thread worker;

	public AsyncAdapter(Input<I> in, Output<I> out) {
		super(in, out);
		
		start();
	}

	@Override
	public I convert(I i) {
		
		return i;
	}
	
	public void start() {
		
		worker = new Thread(this, "AsyncAdapter::worker");
		worker.start();
		loopCondition = true;
	}
	
	public void stop() {
		if (worker != null) {
			Thread t = worker;
			worker = null;
			t.interrupt(); // Interrupt the sleepy header if its asleep
			
			loopCondition = false;
		}
	}
	
	public void run() {
		try {
			loop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}