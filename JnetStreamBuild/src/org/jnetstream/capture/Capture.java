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
package org.jnetstream.capture;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;

import com.slytechs.utils.collection.IOSkippableIterator;

/**
 * <P>
 * Main capture session interface. The Capture interface provides general
 * services to any open and active sessions which aquire network packets from
 * either a live network interface, a file, a memory based collection or from
 * remote system over the network.
 * </P>
 * <P>
 * Capture is {@link java.lang.Iterable} which means that it can be placed in a
 * tight foreach loop. Care must be taken when the iterator functionality is
 * used as aquiring packets usually means that some kind of back-end IO
 * operation had to take place which can throw an IO exception at any time. The
 * standard Iterator interface does not provide a way of throwing an IO
 * exception therefore the Iterator will relay any IO exceptions as a runtime
 * exception. Runtime exception is one that does not have to have a specified
 * throws statement in its signature, but can throw an exception anyhow. The
 * iterator specifically, will throw a IORuntimeException which will relay the
 * original IO exception which caused an error. Care must be taken that even
 * though this iterator can be passed to other non IO exception aware methods,
 * that a runtime IO exception can occure at any time and cause the method and
 * iterator to abort.
 * 
 * <pre>
 * 
 * FileCapture capture = Captures.openFile(new File(&quot;capture.pcap&quot;));
 * for (FilePacket packet : capture) {
 * 	System.out.println(packet.toString());
 * }
 * 
 * </pre>
 * 
 * </P>
 * <P>
 * In addition to the convenient Iterator interface, a specialized IOIterator
 * interface is also provided which mimics its java.util.Iterator counter part
 * and provides exact same methods with exactly the same meanings, with the
 * exception that {@link com.slytechs.utils.collection.IOIterator#hasNext()} and
 * {@link com.slytechs.utils.collection.IOIterator#next()} methods also throw IO
 * exception immediately. The IOIterator can not be used in the foreach loop as
 * its not a the standard java.util.Iterator.
 * 
 * <pre>
 *        
 *        FileCapture capture = Captures.openFile(new File(&quot;capture.pcap&quot;));
 *        FileIterator i = capture.getFileIterator(); while (i.hasNext()) { // May
 *        throw IO exception FilePacket packet = i.next(); // May throw IO exception
 *        System.out.println(packet.toString()); }
 *        
 * </pre>
 * 
 * </P>
 * <P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Capture<T extends CapturePacket> extends Closeable,
    Iterable<T> {

	/**
	 * Tells if this capture session is local or remote. Local sessions are local
	 * to the system the calls are being made on such as one of its files or live
	 * network captures. Remote sessions are such that the capture sessions (file
	 * or live network interface) were opened on a remote system, a system other
	 * then where these methods are being invoked. Remote sessions utilize a
	 * communication protocol between this client and a remote server
	 * (RemoteServer class) which executes the user requests made on this local
	 * system (the client). If the capture is remote, this method returns true,
	 * then you can safely call on {@link #getRemoteSession} to retrieve the
	 * RemoteSession instance that initiated this capture and this capture is
	 * connected to.
	 * 
	 * @return true means this capture session is proxying its commands through a
	 *         remote server, otherwise this is a local session which executes
	 *         everything locally on this system
	 */
	// public boolean isCaptureRemote();
	/**
	 * Gets a reference to the RemoteSession instance that initated this capture
	 * session. The RemoteSession is connected to a RemoteServer on another server
	 * which executes the commands on behalf of this Capture instance. Using
	 * RemoteSession interface the user can open other capture sessions and issue
	 * certain remote commands to be executed on the remote server.
	 * 
	 * @return reference to the active remote session connected to a remote server
	 */
	// public RemoteSession getRemoteSession();
	/**
	 * Gets the currently active filter set on this capture session. Filters allow
	 * efficient way of filtering, or rejecting certain packets while allowing the
	 * rest that match the filter criteria to be returned. Filters are usually
	 * very efficient at rejecting packets at the source of the packets such as
	 * right at the live network interface where typically the kernel accepts or
	 * rejects the incomming packets without any copying of packet data. For files
	 * filters again are used to efficiently map portions of the capture files so
	 * that packets in the file can be accepted or rejected as efficiently as
	 * possible, usually with just the single kernel level copies into its
	 * buffers.
	 * 
	 * @return currently active filter
	 */
	public Filter<ProtocolFilterTarget> getFilter();

	/**
	 * Registers a listener for packet level events. Packet level events are
	 * dispatched when a new packet is received or when it goes through various
	 * decoding stages.
	 * 
	 * @param listener
	 *          listener to notify of packet level events
	 */
	// public void addListener(PacketListener listener);
	/**
	 * Retrieves an iterator {@see java.util.Iterator} which can iterate over all
	 * the packets of this capture session. The IO based iterator mimics the
	 * methods and behaviour of its counter part <CODE>Iterator</CODE> that it
	 * can throw IO exceptions in any of the methods.
	 * 
	 * @return Iterator capable of throwing IO exceptions
	 * @throws IOException
	 *           Any IO errors while retrieving a packet
	 */
	public IOSkippableIterator<T> getPacketIterator() throws IOException;

	/**
	 * Returns the type of file capture this is. The type can be exactly specified
	 * including the exact file format type.
	 * 
	 * @return type of capture
	 */
	public CaptureType getType();

	/**
	 * Removes an active listener from the dispatchers queue.
	 * 
	 * @param listener
	 *          listener to remove
	 */
	// public void removeListener(PacketListener listener);
	/**
	 * Tells if this capture session is mutable. Typically file based and pure
	 * memory based capture sessions are mutable, meaning the capture sessions
	 * content can be modified. LiveCaptures and read-only file based capture
	 * sessions are types of captures that are non-mutable and this method will
	 * return false.
	 * 
	 * @return true means this session is mutable, otherwise false
	 */
	public boolean isMutable();

	/**
	 * <P>
	 * Creates a standard java iterator which will iterate over all packets within
	 * the current capture session. Since all operations behind this iterator
	 * actually use IO operations to aquire data, an IO exception may be thrown at
	 * anytime. The IO exception is relayed using a special
	 * {@link com.slytechs.utils.io.IORuntimeException} runtime exception. This
	 * may cause the iterator from being created or returned. Runtime exceptions
	 * do not have to be declared in the methods signature to be thrown, and to
	 * conform to java's standard interface {@link java.lang.Iterable} this
	 * iterator getter does not declare one, but IORuntimeException might be
	 * thrown and must be caught when aquiring the iterator and later around the
	 * usage of the iterator returned.
	 * </p>
	 * <p>
	 * The iterator's remove method is optional method and is only available on
	 * capture session's who's {@link #isMutable} method returns boolean. If you
	 * try to invoke the {@link java.util.Iterator#remove} method on a non mutable
	 * capture session, and UnsupportedOperationException will be thrown to
	 * indicate that this is a non mutable capture session. I.e. LiveCaptures or
	 * read-only capture files are types of non mutable capture sessions.
	 * </p>
	 * 
	 * @return packet iterator that will iterate over all packet within the
	 *         capture session.
	 */
	public Iterator<T> iterator();

	// public Iterable<MetaPacket> getMetaPacketIterable();
	// public IOSkippableIterator<MetaPacket> getMetaPacketIterator();

	/**
	 * Sets a new filter on this active capture session. Filters allow efficient
	 * way of filtering, or rejecting certain packets while allowing the rest that
	 * match the filter criteria to be returned. Filters are usually very
	 * efficient at rejecting packets at the source of the packets such as right
	 * at the live network interface where typically the kernel accepts or rejects
	 * the incomming packets without any copying of packet data. For files filters
	 * again are used to efficiently map portions of the capture files so that
	 * packets in the file can be accepted or rejected as efficiently as possible,
	 * usually with just the single kernel level copies into its buffers.
	 * 
	 * @param filter
	 *          filter to apply to this capture session
	 * @throws IOException
	 *           any IO errors
	 */
	// public void setFilter(Filter filter) throws IOException;
}
