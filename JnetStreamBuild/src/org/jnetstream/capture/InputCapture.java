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
import java.nio.channels.ReadableByteChannel;

import org.jnetstream.capture.FormatType.Detail;
import org.jnetstream.capture.file.Record;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;

import com.slytechs.utils.memory.channel.BufferedReadableByteChannel;

/**
 * Formatted InputStream based capture session. This object itself is not an
 * InputStream and can not be call chained with other InputStream objects. The
 * inputstream has to be formatted and conform to one of the supported file
 * format standards. The stream only allows read-only iterators to accessed
 * which will decoded the formatted stream and produce either FilePacket, Record
 * or raw ByteBuffers read from the stream.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface InputCapture<T extends CapturePacket> extends Capture<T> {

	/**
	 * Factory interface used by InputCapture factories to load NPL based files
	 * who's format is <code>FormatType.Other</code>. The defaut factory for
	 * this interface can be overriden using the
	 * <code>InputCapture.PROPERTY_INPUTCAPTURE_OTHER_FACTORY</code>.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface FormatTypeOtherFactory {

		/**
		 * Checks the format type of this channel. If this is acceptable file format
		 * <code>FileFormat.Other</code> will be returned. More detailed file
		 * format can be queried using the method
		 * {@link InputCapture#getFormatName()}.
		 * 
		 * @param in
		 *          input channel to scan for format type
		 * @return type of format this is
		 * @throws IOException
		 *           any IO errors
		 */
		public FormatType formatType(final ReadableByteChannel in)
		    throws IOException;

		/**
		 * Create new input capture of file format <code>FormatType.Other</code>.
		 * The file format can not be more specifically subclassed as these file
		 * formats are all NPL based and not based on the "capture framework's" core
		 * supplied file formats.
		 * 
		 * @param in
		 *          channel from which to decode
		 * @param filter
		 *          packet filter to apply
		 * @return InputCapture which will decode the in channel
		 * @throws IOException
		 *           any IO errors
		 */
		public InputCapture<CapturePacket> newInput(final ReadableByteChannel in,
		    final Filter<ProtocolFilterTarget> filter) throws IOException;

		/**
		 * @param b
		 * @return
		 */
		public Detail formatTypeDetail(BufferedReadableByteChannel in)
		    throws IOException;

	}

	/**
	 * Name of the system property used used in loading
	 * <code>FormatType.Other</code> file formats. You can use the system
	 * property {@link #PROPERTY_INPUTCAPTURE_OTHER_FACTORY} to override its
	 * default value which is {@value #PROPERTY_INPUTCAPTURE_OTHER_FACTORY}.
	 */
	public static final String PROPERTY_INPUTCAPTURE_OTHER_FACTORY =
	    "" + "org.jnetstream.capture.inputcapture.other";

	public static final String DEFAULT_INPUTCAPTURE_OTHER_FACTORY =
	    "com.slytechs.jnetstream.packet.DefaultFormatOtherStreamFactory";

	/**
	 * Counts the number of packets in the input. All the input is consumed and
	 * can not be rewound.
	 * 
	 * @return number of packets within the input
	 * @throws IOException
	 *           any IO errors
	 */
	public long countPackets() throws IOException;

	/**
	 * Gets an iterator that will iterate and return CapturePacket objects.
	 * 
	 * @retrun iterator of CapturePacket objects or its subclassed cousins
	 * @throws IOException
	 *           any IO errors
	 */
	public InputIterator<T> getPacketIterator() throws IOException;

	/**
	 * Gets an iterator that will iterate and return CapturePacket objects.
	 * 
	 * @param filter
	 *          a packet filter to apply to the interator
	 * @retrun iterator of CapturePacket objects or its subclassed cousins
	 * @throws IOException
	 *           any IO errors
	 */
	public InputIterator<T> getPacketIterator(Filter<ProtocolFilterTarget> filter)
	    throws IOException;

	/**
	 * Gets an iterator that will iterate and return Record objects, just like a
	 * normal file based capture would produce except these records are read-only.
	 * 
	 * @return iterator of record
	 * @throws IOException
	 *           any IO errors
	 */
	public InputIterator<? extends Record> getRecordIterator() throws IOException;

	/**
	 * Gets an iterator that will iterate and return Record objects, just like a
	 * normal file based capture would produce except these records are read-only.
	 * 
	 * @param filter
	 *          filter to apply to the iterator
	 * @return iterator of record
	 * @throws IOException
	 *           any IO errors
	 */
	public InputIterator<? extends Record> getRecordIterator(
	    Filter<RecordFilterTarget> filter) throws IOException;

	/**
	 * Gets an iterator that will iterate and return raw ByteBuffer based record
	 * read from the inputstream. These ByteBuffer will contents of each record.
	 * The buffer itself is readonly and can not be modified.
	 * 
	 * @return iterator of raw record contents in ByteBuffers
	 * @throws IOException
	 *           any IO errors
	 */
	public InputIterator<ByteBuffer> getRawIterator() throws IOException;

	/**
	 * Gets an iterator that will iterate and return raw ByteBuffer based record
	 * read from the inputstream. These ByteBuffer will contents of each record.
	 * The buffer itself is readonly and can not be modified.
	 * 
	 * @param filter
	 *          filter to apply to the iterator
	 * @return iterator of raw record contents in ByteBuffers
	 * @throws IOException
	 *           any IO errors
	 */
	public InputIterator<ByteBuffer> getRawIterator(
	    Filter<RecordFilterTarget> filter) throws IOException;

	/**
	 * Closes the capture session and the underlying inputstream.
	 */
	public void close() throws IOException;

	/**
	 * Gets the name of this format. This method is especially useful for file
	 * formats of type <code>FormatType.Other</code> as it will give a more
	 * detailed name of the file format.
	 * 
	 * @return possibly more detailed file format name
	 */
	public String getFormatName();

}
