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
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.nio.ByteOrder;

import org.jnetstream.capture.file.BlockRecord;
import org.jnetstream.capture.file.PacketCounterModel;
import org.jnetstream.capture.file.RawIndexer;
import org.jnetstream.capture.file.RawIterator;
import org.jnetstream.capture.file.Record;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.capture.file.RecordIndexer;
import org.jnetstream.capture.file.RecordIterator;
import org.jnetstream.filter.Filter;


import com.slytechs.utils.collection.IOSkippableIterator;
import com.slytechs.utils.number.Version;

/**
 * <P>
 * File capture extends the basic Capture interface and adds several capabilites
 * which can only be done with files, as opposed to live captures for example.
 * You can modify the contents of the file using FileCapture API and its
 * supporting interface. You also have access to lower level API which works
 * more closely with the structure of the file, such as records. At this lower
 * level some knowledge of the file structure is neccessary and is explained in
 * various API documentation sections. The file structure is still fairely
 * abstracted and you may continue to work with generic records instead of
 * specified ones.
 * </P>
 * <P>
 * Capture files contain network packet data captured from a network interface
 * and stored in the file. The FileCapture interface provides an abstraction to
 * the possible formats for capture files.
 * </P>
 * <P>
 * FileCapture extends the standard Capture interface and adds several methods
 * that provide file related information.
 * 
 * <pre>
 * RecordIterator&lt;BlockRecord&gt; i = fileCapture.getBlockRecordIterator();
 * 
 * if (i.hasNext() == false) {
 * 	return;
 * }
 * 
 * BlockRecord block = i.next();
 * </pre>
 * 
 * All capture files utilize a similar format that there is atleast 1 block
 * record which contains 0 or more data records. Data records are typically
 * packet records. There can also be other record types which hold various meta
 * information such as counters and properties.
 * 
 * <pre>
 *                                       
 *                                     RecordIterator&lt;BlockRecord&gt; blocks = fileCapture.getBlockRecordIterator();
 *                                       
 *                                     while (blocks.hasNext() {
 *                                     
 *                                      BlockRecord block = blocks.next();
 *                                      
 *                                      RecordIterator&lt;DataRecord&gt; i = block.getRecordIterator();
 *                                     
 *                                      while (i.hasNext()) {
 *                                      	DataRecord record = i.next();
 *                                       
 *                                      	System.out.printf(&quot;Record type=%s&quot;, record.getRecordType().toString());
 *                                      }
 *                                     }
 *                                       
 * </pre>
 * 
 * The reason you aquire the block as an iterator is the certain formats have
 * more than one block in the file. Most only have 1 though, so if you know that
 * you are going to be working with a PCAP or SNOOP file, you can simply just
 * aquire the block record immediately without putting everything in a loop.
 * Notice that this extra level of inderection with the Block records is only
 * neccessary when accessing records. The PacketIterator returns and manipulates
 * packet records no matter which block they physicaly reside. You aquire the
 * PacketIterator directly from the FileCapture.
 * 
 * <pre>
 * try {
 * 	PacketIterator&lt;FilePacket&gt; i = fileCapture.getPacketIterator();
 * 
 * 	while (i.hasNext()) {
 * 		FilePacket packet = i.next();
 * 		System.out.println(packet.toString());
 * 	}
 * } catch (IOException e) {
 * 	e.printStackTrace();
 * }
 * </pre>
 * 
 * or the more compact Java 5 version:
 * 
 * <pre>
 *                                   try {
 *                                     for (FilePacket packet: fileCapture) {
 *                                       System.out.println(packet.toString());
 *                                     }
 *                                   catch (IORuntimeException e) {
 *                                     e.ioException().printStackTrace();
 *                                   }
 * </pre>
 * 
 * Notice that the second version throws IORuntimeException while the first
 * throws the plain old IOException if any IO errors are encountered. This is
 * because the normal java.util.Iterator and java.lang.Iterable interfaces do
 * not provide a way to allow and applications throw any exceptions besides
 * runtime exceptions. You still need to surround your loops around an iterator
 * with try/catch statements in order to catch any IO errors thrown as a
 * IORuntimeException.
 * </P>
 * <P>
 * Another way to access packets within a capture file is using the
 * CaptureIndexer interface. The indexer provides a type of a List view of all
 * the packets within the capture file. You use indexes to access a specific
 * packet found within the file. Use the getPacketIndexer method to aquire a
 * reference to an indexer of all the packets within the file.
 * </P>
 * <p>
 * The indexer only provides a view of the file, its typically not able to load
 * all packets into memory from a file as some capture files can be very large.
 * So the indexer pulls packets in and out of memory as neccessary when
 * requested. For efficiency is goes a lot of caching of packets using
 * SoftReferences. You must keep in mind, that any operation on the packet
 * indexer may result in an IO operation, this has a performance and resource
 * impact.
 * </p>
 * <p>
 * Both methods of accessing packets and records provide methods for modifying
 * the capture file. Packets and records can be removed, switched, replaced and
 * inserted anywhere within the file. All changes are first stored in memory and
 * at certain points flushed to physical medium which is when the backend file
 * gets physically modified. At anytime you can call on undoAllChanges method
 * which discards all changes that have not been flushed and reverts to the
 * original state of the file. If any changes had already been flushed to the
 * file, those changes are not undone.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface FileCapture<T extends FilePacket> extends Closeable,
    Flushable, Capture<T> {

	/**
	 * <P>
	 * Method which abandons all currently cached and pending changes to the file
	 * contents. Initialy when a file is opened, no pending changes are waiting to
	 * be flushed, invoking the {@link #abort} method has no effect at this time.
	 * Once changes accumulate they continue to be cached until flushed, they can
	 * be aborted at any time and the file state is reverted back to the last
	 * flush call.
	 * </P>
	 * <P>
	 * Any flushed changes that resulted in changes in the underlying capture file
	 * can not be undone and will remain. Only changes still in memory will be
	 * undone.
	 * </P>
	 * 
	 * @throws IOException
	 *           any IO errors
	 */
	public void abortChanges() throws IOException;

	/**
	 * Changes to file content are cached for efficiency reasons. This flush
	 * method forces all the changes to be flushed to the file. This behaviour is
	 * file format specific as certain file formats provide different levels of
	 * capabilities and efficiency natively.
	 */
	public void flush() throws IOException;

	/**
	 * Each capture file is organized so that there exists atleast 1 block record,
	 * usually at the beginning of the capture file. Block record, also sometimes
	 * called <i>file header</i>, contains 0 or more data records which hold some
	 * kind of information. The most common record type is <i>packet record</i>
	 * which has a record header and record data content which is the raw packet
	 * data.
	 * 
	 * @return record iterator that will iterate over all block records within the
	 *         capture file
	 * @throws IOException
	 */
	public IOSkippableIterator<? extends BlockRecord> getBlockIterator()
	    throws IOException;

	/**
	 * Returns the underlying file object that is associated with this
	 * FileCapture.
	 * 
	 * @return underlying file object
	 */
	public File getFile();

	/**
	 * Gets the format type of this file.
	 * 
	 * @return format of this file.
	 */
	public FormatType getFormatType();

	/**
	 * <p>
	 * Returns the length of the entire file including any changes that have been
	 * made. This value may be different from the value returned from
	 * <code>getFile().length()</code> as some changes may still reside in
	 * memory and have not been flushed to physical storage.
	 * </p>
	 * <p>
	 * However if you call {@link #abortChanges()} and immediately after the call
	 * <code>getFile().length() == getLength()</code>.
	 * </p>
	 * 
	 * @return real length of the entire file including any changes that have been
	 *         made to it but not flushed.
	 */
	public long getLength();

	/**
	 * <P>
	 * Returns the number of packets within the file. This only includes records
	 * that hold packet data and not any additional meta data records. The method
	 * uses the default PacketCounter. If estimated packet counter is acceptable
	 * you can use one of of several other PacketCounterModels to calculate
	 * estimated packet count using the
	 * {@link #getPacketCount(PacketCounterModel)} method.
	 * </P>
	 * <P>
	 * The default PacketCounterModel is file type specific. The model at minimum
	 * returns an accurate count of packet records within the capture file, but no
	 * guarrantees about performance can be made and the performance will vary
	 * from format to format. You can use the more explicit
	 * {@link #getPacketCount(PacketCounterModel)} method to counter packets.
	 * 
	 * @return total number of packets within the file using the default model
	 * @throws IOException
	 *           any io errors
	 */
	public long getPacketCount() throws IOException;

	/**
	 * Gets the packet count using a different algorithm. There are several
	 * different types of algorithms or PacketCounterModels to choose from, each
	 * has its own benefits and drawback. The reason that multiple algorithms are
	 * provided is that counting packets in a capture file can be extremely time
	 * consuming in large capture files since most capture files (with the
	 * exception of NAP) do not provide any sort of packet indexing or maintain a
	 * global count of packets within the entire file. Therefore the packets have
	 * to be in most cases iterated and counted. One of the models allows a
	 * statistical calculation upon the file which does not return a true packet
	 * count but is very close to the real number and in certain situations may be
	 * good enough.
	 * 
	 * @param model
	 *          model/algorithm to use to count packets
	 * @return number of packets calculated by the model
	 * @throws IOException
	 *           any io exceptions
	 */
	public long getPacketCount(PacketCounterModel model) throws IOException;

	/**
	 * Indexer which accesses packets by index. The elements returned by this
	 * indexer are FilePacket based objects.
	 * 
	 * @return packet indexer
	 * @throws IOException
	 *           any IO errors
	 */
	public PacketIndexer<T> getPacketIndexer() throws IOException;

	/**
	 * <p>
	 * Packet iterator provides methods for mutation, searches and regular
	 * iteration over the entire file based capture. Iterator keeps a current
	 * position in form of a <b>cursor</b>. Most of the methods work with the
	 * cursor either adjusting its position or using the <b>cursor</b> to learn
	 * the exact location where some operation should take place.
	 * </p>
	 * <p>
	 * There is a special postion at the end of the file, past the last byte of
	 * file data. This location has a restriction on which operations may operate
	 * at this <b>cursor</b> position. For example you can no remove any elements
	 * at this location, but you can add which in effect is an append operation.
	 * </p>
	 * <p>
	 * There may also be some restrictions on which operations are supported based
	 * on the <code>FileMode</code> using which the file capture was opened. In
	 * <code>FileMode.ReadWrite</code> or any such related modes, all operations
	 * are functional. In <code>FileMode.ReadOnly</code> or any such related
	 * modes, any operations which makes modifications may fail with a
	 * <code>ReadonlyBuffer</code> exception. You can use <code>isMutable</code>
	 * to check if the current file capture is opened in read-write mode.
	 * </p>
	 * 
	 * @return PacketIterator over FilePacket based elements
	 */
	public PacketIterator<T> getPacketIterator() throws IOException;

	/**
	 * Raw ByteBuffer based record indexer. The elements returned by this indexer
	 * are ByteBuffers which have the limit and position properties aligned to the
	 * beginning and end of recods. You access these records using list-like
	 * indexes. Raw indexer also allows some low level mutable methods.
	 * 
	 * @return indexer which accesses raw records
	 * @throws IOException
	 *           any IO errors
	 */
	public RawIndexer getRawIndexer() throws IOException;

	/**
	 * Gets an iterator that will return raw contents of the records contained in
	 * the underlying capture file.
	 * 
	 * @return raw record iterator
	 * @throws IOException
	 *           any IO errors
	 */
	public RawIterator getRawIterator() throws IOException;

	/**
	 * Gets an iterator that will return raw contents of the records contained in
	 * the underlying capture file.
	 * 
	 * @return raw record iterator
	 * @param filter
	 *          record filter which will be applied to records during iteration
	 * @throws IOException
	 *           any IO errors
	 */
	public RawIterator getRawIterator(Filter<RecordFilterTarget> filter)
	    throws IOException;

	/**
	 * Indexer which accesses records by index. The elements returned by this
	 * indexer are Record based objects.
	 * 
	 * @return record indexer
	 * @throws IOException
	 *           any IO errors
	 */
	public RecordIndexer<? extends Record> getRecordIndexer() throws IOException;

	/**
	 * Iterator which iterates over every record within the file capture. The
	 * iterator will iterate has range over every single record including the
	 * block record (i.e. file header) at the beginning of the file.
	 * 
	 * @return
	 * @throws IOException
	 */
	public RecordIterator<? extends Record> getRecordIterator()
	    throws IOException;

	/**
	 * Iterator which iterates over every record within the file capture. The
	 * iterator will iterate has range over every single record including the
	 * block record (i.e. file header) at the beginning of the file.
	 * 
	 * @param filter
	 *          a filter that will be applied to match packets during iteration
	 * @return
	 * @throws IOException
	 */
	public RecordIterator<? extends Record> getRecordIterator(
	    Filter<RecordFilterTarget> filter) throws IOException;

	/**
	 * Returns the first file version found. There may be multiple blocks within
	 * the file at different versions.
	 * 
	 * @return version of the file
	 * @throws IOException
	 */
	public Version getVersion() throws IOException;

	/**
	 * Tells if the capture file contains 0 packets. This does not mean that the
	 * size of the file is 0, it still needs to contain valid Block record
	 * (file-header), but that it is a valid capture file with 0 packet records in
	 * it.
	 * 
	 * @return true if file contains 0 records, with the exception of the block
	 *         record which is required to establish a file type.
	 * @throws IOException
	 *           any IO errors
	 */
	public boolean isEmpty() throws IOException;

	/**
	 * Checks if the current capture has an open connection to a physical file. No
	 * IO operations are possible once the channel is closed.
	 * 
	 * @return true means that the channel is open, false means its closed
	 */
	public boolean isOpen();

	/**
	 * Gets the byte order of the underlying capture file. The byte ordering only
	 * applies to the headers on various records that are used to store packet
	 * data. The byte ordering within the packet's data is protocol dependent and
	 * has no connection with the underlying storage's byte ordering scheme.
	 * 
	 * @return byte ordering of the underlying storage
	 */
	public ByteOrder order();
}
