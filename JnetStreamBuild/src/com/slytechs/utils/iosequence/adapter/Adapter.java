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
import com.slytechs.utils.iosequence.adapter.Bridge.DefaultBridge;



/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class Adapter<IN extends SequenceElement, OUT extends SequenceElement>  {

	protected final Input<IN> in;
	protected final Output<OUT> out;
	protected final Bridge<IN, OUT> bridge;
	protected boolean loopCondition = true;
	
	public Adapter(Input<IN> in, Output<OUT> out) {
		this.in = in;
		this.out = out;
		this.bridge = new DefaultBridge<IN, OUT>();
	}
	
	public Adapter(Input<IN> in, Output<OUT> out, Bridge<IN, OUT> bridge) {
		this.in = in;
		this.out = out;
		this.bridge = bridge;
	}

	public void loop() throws InterruptedException {
		while (loopCondition) {
			IN i = in.get();
			
			OUT o = convert(i);
			
			if (bridge.sendSource(i, o, in, out) == false) {
				loopCondition = false;
				break;
			}
		}
	}
	
	public abstract OUT convert(IN i);
}
