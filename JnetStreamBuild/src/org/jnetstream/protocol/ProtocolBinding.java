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
package org.jnetstream.protocol;


/**
 * <P>
 * Protocol binding is a link/binding between two different protocols. A source
 * protocol binds to a sink protocol. Then the sink protocol, when the time
 * comes, evaluates the expression within this binding to determine if the
 * source protocol is a candidate for the next protocol in the sequence of
 * protocol headers being decoded/dissected.
 * </P>
 * </P>
 * Use the getSourceName() to get the name of the protocol to which this binding
 * was definined. Use the getSinkName() to get the name of the protocol which
 * will evaluate the binding expression to determine if source protocol is
 * applicable as the next protocol in the sequence of headers.
 * </P>
 * <P>
 * You can also use the isLinked() method to determine if this binding has
 * actually been linked into the runtime environment and is ready to be applied.
 * Bindings may not neccessarily be linked as the Sink or the Source protocols
 * may not have been loaded yet. Most protocols are loaded ondemand, that is
 * only when they are needed. The bindings are applied when the protocol itself
 * is loaded, not before.
 * </P>
 * <P>
 * Use the Protocol.getSourceBindings() or Protocol.getSinkBindings() to get a
 * complete list of bindings for any given protocol. Of you can use
 * ProtocolRegistry.getBindings() to get a list of all the bindings currrently
 * loaded.
 * </P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface ProtocolBinding extends BindingResolver {
	
	public Protocol getSource();
	
	public Protocol getSink();
}
