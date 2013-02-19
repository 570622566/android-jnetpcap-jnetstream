/**
 * Copyright (C) 2007 Sly Technologies, Inc. This library is free software; you
 * can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version. This
 * library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.jnetstream.filter.bpf;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jnetstream.capture.file.pcap.PcapDLT;
import org.jnetstream.filter.Filter;
import org.jnetstream.filter.FilterException;
import org.jnetstream.filter.FilterExpression;
import org.jnetstream.filter.FilterNotFoundException;
import org.jnetstream.filter.FilterSyntaxError;
import org.jnetstream.filter.FilterTarget;
import org.jnetstream.packet.ProtocolFilterTarget;

/**
 * Berkley Packet Filter (BFP) filter program. The BPF filter is natively
 * supported in the kernel by most unix implementations and is executed as a
 * special byte-code interpreted program with a small virtaul machine on byte
 * buffers. The jNetPCAP framework also provides two additional BPF filter
 * interpreters, native "userland" BPF interpreter from tcpdump library and java
 * based "userland" BPF provided as a fall back if none of the other
 * interpreters are available. The library chooses the interpreters in the
 * following order:
 * <UL>
 * <LI> For live captures - kernel level BPF
 * <LI> For filtering capture files - tcpdump level BPF if native jNetPCAP
 * package (based on C version of libpcap) is detected
 * <LI> For filtering capture files - if native jNetPCAP package is not
 * available the supplied java interpreter is invoked to process the filter.
 * </UL>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class BPFFilter<T extends FilterTarget> implements Filter<T> {

	public static class BPFProtocolFilter
	    extends BPFFilter<ProtocolFilterTarget> {

		/**
		 * Initializes the filter for a very specific target only with the provided
		 * program.
		 * 
		 * @param program
		 *          program to run for only this target
		 * @param target
		 *          the target for which the program was built
		 */
		public BPFProtocolFilter(BPFProgram program) {
			super(program, PcapDLT.EN10.asProtocol());
		}

		/**
		 * @param program
		 * @param target
		 */
		public BPFProtocolFilter(BPFProgram program, ProtocolFilterTarget target) {
			super(program, target);
			// TODO Auto-generated constructor stub
		}

		/**
		 * @param expression
		 * @throws FilterSyntaxError
		 * @throws FilterNotFoundException
		 */
		public BPFProtocolFilter(FilterExpression<BPFProgram> expression)
		    throws FilterSyntaxError, FilterNotFoundException {
			super(expression);
			// TODO Auto-generated constructor stub
		}

	}

	private static final Log logger = LogFactory.getLog(BPFFilter.class);

	private final Map<FilterTarget, BPFProgram> map = new HashMap<FilterTarget, BPFProgram>();

	private T target;

	private final FilterExpression<BPFProgram> expression;

	/**
	 * Initializes the filter for a very specific target only with the provided
	 * program.
	 * 
	 * @param program
	 *          program to run for only this target
	 * @param target
	 *          the target for which the program was built
	 */
	public BPFFilter(final BPFProgram program, final T target) {
		this.target = target;
		this.expression = null;
		
		/*
		 * We put in the program right into our target->program map
		 */
		map.put(target, program);
	}

	/**
	 * Initializes the BPF filter with the specified filter expression in what
	 * ever syntax the user chooses. The expression is compiled and applied
	 * appropriately for each target.
	 * 
	 * @param expression
	 *          expression to be compiled to binary BPF program
	 * @throws FilterSyntaxError
	 * @throws FilterNotFoundException
	 */
	public BPFFilter(final FilterExpression<BPFProgram> expression)
	    throws FilterSyntaxError, FilterNotFoundException {

		this.expression = expression;
	}
	
	/**
	 * Only used when subclassed. The subclass must override compile 
	 * 
	 */
	protected BPFFilter() {
		this.expression = null;
	}

	/*
	 * Intended to be overriden by subclass.
	 */
	public BPFProgram compile(FilterTarget target) throws FilterSyntaxError,
	    FilterNotFoundException {
		return expression.compile(target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.filter.Filter#accept(java.nio.ByteBuffer,
	 *      org.jnetstream.filter.FilterTarget)
	 */
	public boolean accept(final ByteBuffer buffer, final FilterTarget target)
	    throws FilterException {

		return execute(buffer, target) != 0;
	}

	/**
	 * @throws FilterNotFoundException
	 *           if a program could not be generated or found for the specified
	 *           target
	 * @see com.slytechs.capture.filter.Filter#execute(java.nio.ByteBuffer)
	 */
	public long execute(final ByteBuffer buffer, final FilterTarget target)
	    throws FilterException {

		BPFProgram program = map.get(target);

		if (program == null) {
			program = compile(target);
			map.put(target, program);
		}

		try {

			/*
			 * Pick either the static program or use the expression to compile to BPF
			 * program for each different target. We rely on expression object to
			 * cache filters, of course.
			 */
			final BPFProgram p;
			if (expression != null) {
				p = expression.compile(target);

			} else if (program != null && this.target == target) {
				p = program;

			} else {
				return 0; // Automatic rejection, no filter specified for this type of
				// target
			}

			final long length = BpfFactory.getForThread().execute(p, buffer, 0,
			    buffer.limit());

			return length;

		} catch (final Exception e) {
			logger.error("Invalid BPF instruction encountered", e);
			throw new IllegalStateException("Invalid BPF instruction", e);
		}
	}
}
