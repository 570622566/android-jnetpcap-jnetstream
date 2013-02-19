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
import java.io.Flushable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;

import org.jnetstream.packet.Packet;
import org.jnetstream.packet.SerializedPacketFactory;
import org.jnetstream.protocol.ProtocolRegistry;

/**
 * <p>
 * A <code>PacketOutputStream</code> writes
 * <code>CapturePacket<code> objects to an <code>OutputStream</code>. The
 * <code>Packet</code> objects can be read (reconstituted) using an 
 * <code>PacketInputStream</code>.
 * Persistent storage of <code>CapturePackets</code> can be accomplished by using a file for
 * a stream. If the stream is a network socket stream, the CapturePackets can be
 * reconstitued on a nother host or in another process.
 * </p>
 * <p>
 * Here is an example:
 * 
 * <pre>
 * FileOutputStream fos = new FileOutputStream(&quot;test.stream&quot;);
 * PacketOutputStream cos = new PacketOutputStream(fos);
 * 
 * CapturePacket packet = // gotten from live network or file 
 *     cos.writePacket(packet);
 * cos.close();
 * 
 * FileInputStream fis = new FileInputStream(&quot;test.stream&quot;, &quot;r&quot;);
 * PacketInputStream cis = new PacketInputStream(fis);
 * 
 * packet = cis.readPacket();
 * cis.close();
 * </pre>
 * 
 * </p>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class PacketOutputStream
    extends OutputStream implements CapturePacketOutput<Packet>, Closeable,
    Flushable {

	protected ObjectOutputStream out;

	/**
	 * <P>
	 * Provide a way for subclasses that are completely reimplementing
	 * CapturePacketOutputStream to not have to allocate private data just used by
	 * this implementation of CaptureOutputOutputStream.
	 * </P>
	 */
	protected PacketOutputStream() {
		super();
	}

	/**
	 * Creates a new capture output stream bound to the supplied output stream.
	 * CapturePackets are serialized and sent as a byte stream accorss the output
	 * stream.
	 * 
	 * @param out
	 *          underlying output stream this object is bound to
	 * @throws IOException
	 *           any IO errors during transmittion
	 */
	public PacketOutputStream(OutputStream out) throws IOException {
		this.out = new ObjectOutputStream(out);

	}

	/**
	 * Closes the output stream.
	 */
	@Override
	public void close() throws IOException {
		out.close();
	}

	/**
	 * Flushes any buffered changes from this output stream to the underlying
	 * stream immediately.
	 */
	@Override
	public void flush() throws IOException {
		out.flush();
	}

	/**
	 * Writes the byte array into the underlying outputstream
	 * 
	 * @param b
	 *          byte array to write
	 * @param off
	 *          offset into the byte array to start reading the data
	 * @param len
	 *          number of bytes to write from the byte array to outputstream
	 * @exception IOException
	 *              any IO errors
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	/**
	 * Write the byte array into the underlying outputstream.
	 * 
	 * @param b
	 *          the byte array to write to the outputstream in its entirety
	 * @exception IOException
	 *              any IO errors
	 */
	@Override
	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	/**
	 * Writes a single byte to the output stream.
	 * 
	 * @param b
	 *          the byte, in form of an integer, to write to the stream
	 * @exception IOException
	 *              any IO errors
	 */
	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}

	/**
	 * Serializes and writes a packet to the output stream. The packet can be read
	 * back by a corresponding PacketInputStream connected to this outputstream.
	 * 
	 * @param packet
	 *          the packet to serialize and write out to the outputstream
	 * @see com.slytechs.capture.stream.CapturePacketOutput#writePacket(com.slytechs.capture.CapturePacket)
	 */
	public void writePacket(Packet packet) throws IOException {
		DeserializedPacket sp =
		    ProtocolRegistry.getPacketFactory(SerializedPacketFactory.class, "")
		        .newSerializedPacket(packet);

		sp.setSerializedTimestamp(new Timestamp(System.currentTimeMillis()));

		out.writeObject(sp);
	}

}
