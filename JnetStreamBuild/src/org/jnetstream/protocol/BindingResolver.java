package org.jnetstream.protocol;

import com.slytechs.utils.memory.BitBuffer;

/**
 * Resolves a binding to a protocol. The implementing object supplies the
 * neccessary logic to figure out if the binding either passes the
 * {@link #resolve} test or not.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface BindingResolver {

	/**
	 * Runs the condition check of the binding.
	 * 
	 * @param buffer
	 *          buffer containing packet data
	 * @param source
	 *          offset of the source protocol
	 * @param sink
	 *          offset of the sink protocol
	 * @return the length of the sink header or -1 if the binding condition
	 *         failed
	 */
	public int resolve(BitBuffer buffer, int source, int sink);
}