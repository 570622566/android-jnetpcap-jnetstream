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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * A <code>PacketInputStream</code> deserializes <code>Packet</code> objects
 * into <code>DeserializedPacket</code> objects previously written using a
 * <code>PacketOutputStream</code>. <code>DeserializedPacket</code>
 * interface is a subclass of <code>CapturePacket</code> which adds several
 * new methods that contain information about the serialization process.
 * </p>
 * <p>
 * Here is an example:
 * 
 * <pre>
 * 
 * FileOutputStream fos = new FileOutputStream(&quot;test.stream&quot;);
 * PacketOutputStream cos = new PacketOutputStream(fos);
 * CapturePacket packet = //gotten from live network or file 
 *     cos.writePacket(packet);
 * cos.close();
 * 
 * FileInputStream fis = new FileInputStream(&quot;test.stream&quot;);
 * PacketInputStream cis = new PacketInputStream(fis);
 * packet = cis.readPacket();
 * cis.close();
 * 
 * </pre>
 * 
 * </p>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class PacketInputStream
    extends InputStream implements CapturePacketInput<CapturePacket>, Closeable {

	private static final Log logger = LogFactory.getLog(PacketInputStream.class);

	protected ObjectInputStream in;

	/**
	 * <P>
	 * Provide a way for subclasses that are completely reimplementing
	 * CapturePacketInputStream to not have to allocate private data just used by
	 * this implementation of CapturePacketInputStream.
	 * </P>
	 */
	protected PacketInputStream() {
		super();
	}

	/**
	 * Creates an ObjectInputStream that reads from the specified InputStream. A
	 * serialization stream header is read from the stream and verified. This
	 * constructor will block until the corresponding CapturePacketOutputStream
	 * has written and flushed the header.
	 * 
	 * @param in
	 *          input stream to read from
	 * @throws IOException
	 *           any usual I/O errors
	 */
	public PacketInputStream(InputStream in) throws IOException {
		this.in = new ObjectInputStream(in);

	}

	/**
	 * Gets the number of bytes available for read without blocking the operation.
	 * 
	 * @return number of byte available for immediate read
	 * @exception IOException
	 *              any IO errors
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		return in.available();
	}

	/**
	 * Closes this and the underlying input stream.
	 * 
	 * @exception IOException
	 *              any IO errors
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		in.close();
	}

	/**
	 * Marks the current position in this input stream. A subsequent call to the
	 * reset method repositions this stream at the last marked position so that
	 * subsequent reads re-read the same bytes. The readlimit arguments tells this
	 * input stream to allow that many bytes to be read before the mark position
	 * gets invalidated. The general contract of mark is that, if the method
	 * markSupported returns true, the stream somehow remembers all the bytes read
	 * after the call to mark and stands ready to supply those same bytes again if
	 * and whenever the method reset is called. However, the stream is not
	 * required to remember any data at all if more than readlimit bytes are read
	 * from the stream before reset is called. The mark method of InputStream does
	 * nothing.
	 * 
	 * @param readlimit
	 *          the maximum limit of bytes that can be read before the mark
	 *          position becomes invalid.
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public synchronized void mark(int readlimit) {
		in.mark(readlimit);
	}

	/**
	 * Tests if this input stream supports the mark and reset methods. Whether or
	 * not mark and reset are supported is an invariant property of a particular
	 * input stream instance. The markSupported method of InputStream returns
	 * false.
	 * 
	 * @return true if this stream instance supports the mark and reset methods;
	 *         false otherwise.
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	/**
	 * <P>
	 * Reads up to len bytes of data from the input stream into an array of bytes.
	 * An attempt is made to read as many as len bytes, but a smaller number may
	 * be read. The number of bytes actually read is returned as an integer.
	 * </P>
	 * <P>
	 * This method blocks until input data is available, end of file is detected,
	 * or an exception is thrown.
	 * </P>
	 * <P>
	 * If b is null, a NullPointerException is thrown.
	 * </P>
	 * <P>
	 * If off is negative, or len is negative, or off+len is greater than the
	 * length of the array b, then an IndexOutOfBoundsException is thrown.
	 * </P>
	 * <P>
	 * If len is zero, then no bytes are read and 0 is returned; otherwise, there
	 * is an attempt to read at least one byte. If no byte is available because
	 * the stream is at end of file, the value -1 is returned; otherwise, at least
	 * one byte is read and stored into b.
	 * </P>
	 * <P>
	 * The first byte read is stored into element b[off], the next one into
	 * b[off+1], and so on. The number of bytes read is, at most, equal to len.
	 * Let k be the number of bytes actually read; these bytes will be stored in
	 * elements b[off] through b[off+k-1], leaving elements b[off+k] through
	 * b[off+len-1] unaffected.
	 * </P>
	 * <P>
	 * In every case, elements b[0] through b[off] and elements b[off+len] through
	 * b[b.length-1] are unaffected.
	 * </P>
	 * <P>
	 * If the first byte cannot be read for any reason other than end of file,
	 * then an IOException is thrown. In particular, an IOException is thrown if
	 * the input stream has been closed.
	 * </P>
	 * <P>
	 * The read(b, off, len) method for class InputStream simply calls the method
	 * read() repeatedly. If the first such call results in an IOException, that
	 * exception is returned from the call to the read(b, off, len) method. If any
	 * subsequent call to read() results in a IOException, the exception is caught
	 * and treated as if it were end of file; the bytes read up to that point are
	 * stored into b and the number of bytes read before the exception occurred is
	 * returned. Subclasses are encouraged to provide a more efficient
	 * implementation of this method.
	 * </P>
	 * 
	 * @param b
	 *          the buffer into which the data is read
	 * @param off
	 *          the start offset in array b at which the data is written
	 * @param len
	 *          the maximum number of bytes to read
	 * @return the total number of bytes read into the buffer, or -1 if there is
	 *         no more data because the end of the stream has been reached
	 * @exception IOException
	 *              any IO errors
	 * @exception NullPointerException
	 *              if b is null
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	/**
	 * <P>
	 * Reads some number of bytes from the input stream and stores them into the
	 * buffer array b. The number of bytes actually read is returned as an
	 * integer. This method blocks until input data is available, end of file is
	 * detected, or an exception is thrown.
	 * </P>
	 * <P>
	 * If b is null, a NullPointerException is thrown. If the length of b is zero,
	 * then no bytes are read and 0 is returned; otherwise, there is an attempt to
	 * read at least one byte. If no byte is available because the stream is at
	 * end of file, the value -1 is returned; otherwise, at least one byte is read
	 * and stored into b.
	 * </P>
	 * <P>
	 * The first byte read is stored into element b[0], the next one into b[1],
	 * and so on. The number of bytes read is, at most, equal to the length of b.
	 * Let k be the number of bytes actually read; these bytes will be stored in
	 * elements b[0] through b[k-1], leaving elements b[k] through b[b.length-1]
	 * unaffected.
	 * </P>
	 * <P>
	 * If the first byte cannot be read for any reason other than end of file,
	 * then an IOException is thrown. In particular, an IOException is thrown if
	 * the input stream has been closed.
	 * </P>
	 * <P>
	 * The read(b) method for class InputStream has the same effect as:
	 * <CODE>read(b,
	 * 0, b.length)</code>
	 * </P>
	 */
	@Override
	public int read(byte[] b) throws IOException {
		return in.read(b);
	}

	/**
	 * Repositions this stream to the position at the time the mark method was
	 * last called on this input stream. The general contract of reset is:
	 * <UL>
	 * <LI>If the method markSupported returns true, then:
	 * <UL>
	 * <LI> If the method mark has not been called since the stream was created,
	 * or the number of bytes read from the stream since mark was last called is
	 * larger than the argument to mark at that last call, then an IOException
	 * might be thrown.
	 * <LI>If such an IOException is not thrown, then the stream is reset to a
	 * state such that all the bytes read since the most recent call to mark (or
	 * since the start of the file, if mark has not been called) will be
	 * resupplied to subsequent callers of the read method, followed by any bytes
	 * that otherwise would have been the next input data as of the time of the
	 * call to reset.
	 * </UL>
	 * <LI>If the method markSupported returns false, then:
	 * <UL>
	 * <LI>The call to reset may throw an IOException.
	 * <LI>If an IOException is not thrown, then the stream is reset to a fixed
	 * state that depends on the particular type of the input stream and how it
	 * was created. The bytes that will be supplied to subsequent callers of the
	 * read method depend on the particular type of the input stream.
	 * </UL>
	 * </UL>
	 * The method reset for class InputStream does nothing except throw an
	 * IOException.
	 * 
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public synchronized void reset() throws IOException {
		in.reset();
	}

	/**
	 * Skips over and discards n bytes of data from this input stream. The skip
	 * method may, for a variety of reasons, end up skipping over some smaller
	 * number of bytes, possibly 0. This may result from any of a number of
	 * conditions; reaching end of file before n bytes have been skipped is only
	 * one possibility. The actual number of bytes skipped is returned. If n is
	 * negative, no bytes are skipped. The skip method of InputStream creates a
	 * byte array and then repeatedly reads into it until n bytes have been read
	 * or the end of the stream has been reached. Subclasses are encouraged to
	 * provide a more efficient implementation of this method.
	 * 
	 * @param n
	 *          the number of bytes to be skipped.
	 * @return the actual number of bytes skipped.
	 * @Throws IOException -
	 *           if an I/O error occurs.
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	/*
	 * <P>Reads the next byte of data from the input stream. The value byte is
	 * returned as an int in the range 0 to 255. If no byte is available because
	 * the end of the stream has been reached, the value -1 is returned. This
	 * method blocks until input data is available, the end of the stream is
	 * detected, or an exception is thrown.</P> <P>A subclass must provide an
	 * implementation of this method.</P> @returns the next byte of data, or -1
	 * if the end of the stream is reached. @throws IOException - if an I/O error
	 * occurs.
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		return in.read();
	}

	/**
	 * Deserializes a packet from the underlying input stream that was previously
	 * serialized by a corresponding PacketOutputStream object.
	 * 
	 * @return a deserialized a packet
	 * @see com.slytechs.capture.stream.CapturePacketInput#readCapturePacket()
	 */
	public DeserializedPacket readPacket() throws IOException {
		try {
			DeserializedPacket packet = (DeserializedPacket) in.readObject();

			packet
			    .setDeserializedTimestamp(new Timestamp(System.currentTimeMillis()));

			return packet;
		} catch (ClassNotFoundException e) {
			logger.error("Can not deserialize a packet from InputStream "
			    + e.getMessage());
			throw new IOException("Can not deserialize a packet from InputStrea "
			    + e.getMessage());
		}
	}
}
