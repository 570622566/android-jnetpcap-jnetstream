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
package org.jnetstream.filter.bpf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * 
 */
public class BpfFactory {
	
	private static final Log logger = LogFactory.getLog(BpfFactory.class);
	
	public static final String DEFAULT_BPF_IMPLEMENTATION = 
		"com.slytechs.filter.bpf.vm.BPFVirtualMachine";

	private static ThreadLocal<BpfVM> global = new ThreadLocal<BpfVM>() {

		/* (non-Javadoc)
     * @see java.lang.ThreadLocal#initialValue()
     */
    @Override
    protected BpfVM initialValue() {
      BpfVM vm;
      try {

				vm = (BpfVM) Class.forName(DEFAULT_BPF_IMPLEMENTATION).newInstance();
      } catch (Exception e) {
      	logger.error("Unable to find BPF VM", e);
      	throw new IllegalStateException("Unable to find BPF VM class", e);
      }
			return vm;    }

	};

	private BpfFactory() {
		// Empty
	}

	public static BpfVM getForThread() {
		return global.get();
	}

}
