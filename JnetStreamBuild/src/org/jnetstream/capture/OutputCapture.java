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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

import org.jnetstream.capture.file.Record;
import org.jnetstream.packet.Packet;


/**
 * <p>
 * A capture session that allows content to be sent to a formatted stream. The
 * OutputCapture itself is not an OutputStream object and can not be call
 * chained with other OutputStream objects. It only allows objects and elements
 * to be added to the capture session which in tern, formats the data and sends
 * it out in a suitable format into the underlying outputstream that this object
 * is connected to. The only mutable operation that can be performed on the
 * OutputCapture session is to add an element, a Packet, Record or
 * record's raw content as a ByteBuffer which will then be properly formatted,
 * serialized and send via the underlying output stream this capture session is
 * connected to.
 * </p>
 * <p>
 * The output stream produced is suitable to be dumped directly to physical
 * storage and read back in using a file based capture session. The output
 * stream is accurately produced to adhere to the correct file format.
 * 
 * <pre>
 * 
 * FileOutputStream out = new FileOutputStream(&quot;file.pcap&quot;);
 * OutputCapture dst = Captures.newStream(PcapFile.class, out);
 * 
 * for (LivePacket packet : Captures.openLive(10)) { // capture 10 live packets
 * 	dst.add(packet); // Serializes packets and stores then in file.pcap
 * }
 * Captures.close(); // Closes the last capture session returned, i.e. LiveCapture
 * dst.close();
 * 
 * for (PcapPacket packet : Captures.openFile(PcapFile.class, &quot;file.pcap&quot;)) {
 * 	System.out.println(packet.toString());
 * }
 * Captures.close(); // Closes the last capture session retruned, i.e. PcapFile
 * </pre>
 * 
 * </p>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface OutputCapture extends Capture<CapturePacket> {

	/**
	 * <p>
	 * Formats the contents of the supplied packet to the underlying format and
	 * serializes the data which is then sent to the output stream. The
	 * OutputCapture object does not user java's framework
	 * ObjectOutputStream class, but formats the data to be sent using the format
	 * that was specified during the instantiation of this object. You can use the
	 * method {@link #getFormatType()} to check exact format that will be used.
	 * </p>
	 * <p>
	 * The supplied packet can be any type of packet from any type of capture
	 * session. The packet's content will be extracted and reformatted to for the
	 * specific format type of this OutpuStreamCapture session.
	 * </p>
	 * 
	 * @param packet
	 *          packet to be formatted and serialized
	 * @throws IOException
	 *           any IO errors
	 */
	public void add(Packet packet) throws IOException;

	// TODO: add addAll methods, addAll(Packet...), addAll(Collection<Packet>)

	// TODO: add variation methods to override timestamp and other params
	
	/**
	 * <p>
	 * Formats the contents of the supplied record to the underlying format and
	 * serializes the data which is then sent to the output stream. The
	 * OutputCapture object does not user java's framework
	 * ObjectOutputStream class, but formats the data to be sent using the format
	 * that was specified during the instantiation of this object. You can use the
	 * method {@link #getFormatType()} to check exact format that will be used.
	 * </p>
	 * <p>
	 * The record must to the correct subclass for this type of format and may not
	 * be just any type of record. For example you can not mix SnoopPacketRecord
	 * object when the format of this outputstream is Pcap.
	 * 
	 * @param record
	 * @throws IOException
	 */
	public <T extends Record> void add(T record) throws IOException;

	/**
	 * Sends the contents of the supplied buffer to the formatted output stream.
	 * The buffer is assumed to contain valid record that is in the appropriate
	 * format for the format type of this outputstream. The buffer is scanned for
	 * a valid record header, but the scan is very minimal. It is just a quick
	 * sanity check of the supplied data.
	 * 
	 * @param raw
	 *          byte buffer containing the raw record in the correct format
	 * @throws IOException
	 *           any IO errors
	 */
	public void add(ByteBuffer raw) throws IOException;

	/**
	 * Gets the format type of this capture session. The
	 * 
	 * @return
	 */
	public FormatType getFormatType();

	/**
	 * This operation always returns an empty iterator for OutputCapture
	 * objects. The iterator's hasNext() method always returns false and its an
	 * UnsupportedOperation exception will be thrown if next() or remove()
	 * operations are called. This capture session is special in that it only
	 * allows data to be written out and since this data is not cached can not be
	 * read or iterated over.
	 * 
	 * @return empty iterator
	 */
	public Iterator<CapturePacket> iterator();

}
