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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jnetstream.capture.FormatType.Detail;
import org.jnetstream.capture.file.nap.NapFile;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;
import org.jnetstream.packet.format.PacketFormatter;

import com.slytechs.utils.collection.IOIterator;
import com.slytechs.utils.collection.IOSkippableIterator;
import com.slytechs.utils.factory.FactoryLoader;

/**
 * <P>
 * Static factory methods for creating and accessing capture sessions. This
 * class provides a number of static methods for manipulating capture files such
 * as concatenating multiple files into one, or reverse and splitting a single
 * large file into multiple smaller ones. Easily get the capture file type or
 * validate its contents. The most imporant methods though are {@link #newFile},
 * {@link #openFile} and {@link #openLive} which create new blank capture files,
 * open existing capture file or open network interfaces for live network packet
 * capture where packets are received directly from the network and passed to
 * the user.
 * </P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public final class Captures {
	/**
	 * A logger used for debuging and tracing. To enable you must enable debug or
	 * trace levels and enable appenders for Captures.class path. All implementing
	 * methods for Captures.Factory, Captures.LocalFactory and
	 * Captures.RemoteFactory methods use this common logger.
	 */
	public static final Log logger = LogFactory.getLog(Captures.class);

	/**
	 * A factory interface for interfacing with implementation of jNetStream API.
	 * Factory interfaces are made up of 3 parts. A common part and 1 remote and 1
	 * local part. The common part is this interface, while LocalFactory provides
	 * additional methods which are only available on a local system and are not
	 * available as part of the RemoteFactory interface. RemoteFactory interface
	 * provides additional methods which may be used remotely, while it will not
	 * contain methods that should only be called locally.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface Factory extends LiveCaptureFactory {

		/**
		 * Copies all of the CapturePackets returned by the source into the already
		 * open capture file.
		 * 
		 * @param dst
		 *          capture file to write the packets into
		 * @param srcs
		 *          capture source to read packets from
		 * @return TODO
		 * @throws IOException
		 *           any IO errors
		 */
		public abstract long append(FileCapture<? extends FilePacket> dst,
		    Capture... srcs) throws IOException;

		/**
		 * Concatenate all the files into the
		 * 
		 * @param type
		 *          TODO
		 * @param dst
		 *          file to store all the concatenated data
		 * @param srcs
		 *          array of source files to concatenate together
		 * @throws IOException
		 */
		public abstract long catFile(FormatType type, File dst, Capture... srcs)
		    throws IOException;

		/**
		 * Concatenate all the files into the
		 * 
		 * @param type
		 *          TODO
		 * @param dst
		 *          file to store all the concatenated data
		 * @param srcs
		 *          array of source files to concatenate together
		 * @return TODO
		 * @throws IOException
		 * @throws FileNotFoundException
		 */
		public abstract long catFile(FormatType type, File dst, File... srcs)
		    throws FileNotFoundException, IOException;

		/**
		 * @param dst
		 * @return
		 * @throws IOException
		 * @throws FileFormatException
		 */
		public long countPackets(File dst) throws IOException, FileFormatException;

		/**
		 * Determines the format type of the supplied file. Checks the format of the
		 * supplied file and expects it to be in one of the supported format types
		 * such as Pcap, etc..
		 * 
		 * @param file
		 *          file to check and return format type
		 * @return format type of the supplied input or null if format type is
		 *         unknown or not supported
		 */
		public abstract FormatType formatType(File file) throws IOException;

		/**
		 * Determines the format type of the supplied "input". Checks the format of
		 * the supplied input and expects it to be in one of the supported format
		 * types such as Pcap, etc..
		 * 
		 * @param in
		 *          input to check and return format type
		 * @return format type of the supplied input or null if format type is
		 *         unknown or not supported
		 */
		public abstract FormatType formatType(ReadableByteChannel in)
		    throws IOException;

		/**
		 * Creates a new empty file of the user specified type.
		 * 
		 * @param file
		 *          file to create
		 * @param type
		 *          type of file to create
		 * @return mutable instance of the empty file just created
		 * @throws IOException
		 *           any IO errors
		 * @throws FileFormatException
		 */
		public abstract <T extends FileCapture<? extends FilePacket>> T newFile(
		    Class<T> c, File file) throws IOException, FileFormatException;

		/**
		 * Creates a new file and dumps all of the packet from capture to the new
		 * file in the proper format.
		 * 
		 * @param f
		 *          file to create
		 * @param capture
		 *          source of CapturePackets which will be dumped into the new file
		 * @param type
		 *          the type of file to create
		 * @return open instance of this new file
		 * @throws IOException
		 *           any IO errors
		 */
		public abstract <T extends FileCapture<? extends FilePacket>> T newFile(
		    Class<T> t, File f, Capture<? extends CapturePacket> capture)
		    throws IOException;

		/**
		 * @param type
		 * @param file
		 * @return
		 * @throws IOException
		 */
		public FileCapture<? extends FilePacket> newFile(FormatType type, File file)
		    throws IOException;

		/**
		 * Creates a new file and dumps all of the packet from capture to the new
		 * file in the proper format.
		 * 
		 * @param type
		 *          the type of file to create
		 * @param file
		 *          file to create
		 * @param capture
		 *          source of CapturePackets which will be dumped into the new file
		 * @return open instance of this new file
		 * @throws IOException
		 *           any IO errors
		 */
		public abstract <T extends FileCapture<? extends FilePacket>> T newFile(
		    FormatType type, File file, Capture<? extends CapturePacket> capture)
		    throws IOException;

		public abstract <T extends FileCapture<? extends FilePacket>> T openFile(
		    Class<T> t, File file) throws IOException, FileFormatException;

		public abstract <T extends FileCapture<? extends FilePacket>> T openFile(
		    Class<T> type, File file, FileMode mode,
		    Filter<ProtocolFilterTarget> filter) throws IOException,
		    FileFormatException;

		public abstract FileCapture<? extends FilePacket> openFile(File file)
		    throws IOException;

		/**
		 * Gets a mutable FileCapture instance for the specified file. The
		 * MutableFileCapture is ready to read or modify records within the
		 * specified file. The supplied filter is applied to packet record data and
		 * only records matching the filter are returned by the MutableFileCapture
		 * instance.
		 * 
		 * @param file
		 *          filename to open for reading and writting
		 * @param mode
		 *          TODO
		 * @return Mutable instance of FileCapture
		 * @throws IOException
		 *           any IO or format errors
		 */
		public abstract FileCapture<? extends FilePacket> openFile(File file,
		    FileMode mode, Filter<ProtocolFilterTarget> protocolFilter)
		    throws IOException;

		public void registerListener(CaptureListener listener);

		public void removeRegisteredListener(CaptureListener listener);

		/**
		 * <P>
		 * Splits the file into smaller files according to default rules defined for
		 * each file format. For NAP the file will be split with each Block Record
		 * being split into its own seperate file. For other files, the defaults are
		 * to split the files into {@value NapFile#BLOCKING_FACTOR} byte files.
		 * </P>
		 * <P>
		 * The base filename supplied is used as the base filename for all newly
		 * created files with the -XXXX appended to them.
		 * <P>
		 * <P>
		 * The source file is unmodified
		 * </p>
		 * 
		 * @param file
		 *          file to be split
		 * @return list of newly created files
		 */
		public abstract List<File> splitFile(File file) throws IOException;

		/**
		 * <P>
		 * Split the specified file into smaller files containing specified number
		 * of packets each from the source file. New files are created to hold only
		 * the specified number of packets and associated meta records. The supplied
		 * filename is used as a base filename for all newly created files with the
		 * post fix of -XXXX appended to them.
		 * </P>
		 * <P>
		 * The source file is unmodified
		 * </P>
		 * 
		 * @param file
		 *          source file to split
		 * @param packetCount
		 *          split using this many packets from the source file copied into
		 *          the newly created files
		 * @param maxCompression
		 *          true means produce the smallest possible file, while false means
		 *          leave it upto the default algorithm for each spcific file type.
		 *          For example NAP files pad their files to 512Kb by default which
		 *          means that files containing even only a single packet are of
		 *          minimum size 512 Kb, but this can be overriden by setting
		 *          maxCompression to true. Notice that it will be harder to split
		 *          the NAP file with regular unix commands if default padding is
		 *          not used.
		 * @return list of all the new files created
		 */
		public abstract List<File> splitFile(File file, long packetCount,
		    boolean maxCompression) throws IOException;

		/**
		 * Checks if the specified file is in a proper format 100% compabile with
		 * specification.
		 * 
		 * @param file
		 *          file to validate
		 * @return true if file is valid with the specification, otherwise false,
		 *         even if minor infringements are found
		 */
		public abstract boolean validateFile(File file) throws IOException;

	}

	/**
	 * Factory classes which create live capture sessions and transmit packets.
	 * Since live captures are inherantly platform specific, this part of the
	 * overall factory implementation is optional and may be ommitted. If
	 * implementation for LiveCaptureFactory is not included, all openLive and
	 * openTransmitter methods will trow UnsuportedOperationException. Other
	 * methods such as listCaptureDevices and newCaptureDevice will not, but will
	 * default to using java.net package to fulfill the request. The
	 * implementation can be specified using system variable
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface LiveCaptureFactory {

		/**
		 * System property which can be used to override the default implementation
		 * class for LiveCaptureFactory. By specifying this system property, before
		 * the jNetStream library is loaded, will cause it to look for the class
		 * named in this property and loading it to serve all LiveCaptureFactory
		 * requests.
		 */
		public static final String LIVE_CAPTURE_FACTORY_CLASS_PROPERTY =
		    "org.jnetstream.livecapture";

		/**
		 * Gets a list of all the devices capable of doing live capture on this or
		 * remote system.
		 * 
		 * @return list of capture capable devices
		 * @throws IOException
		 *           any IO errors
		 */
		public LiveCaptureDevice[] listCaptureDevices() throws IOException;

		/**
		 * Creates a new empty, uninitialized CaptureDevice instance. You must use
		 * its set methods to set all the properties yourself.
		 * 
		 * @return empty instance of a capture device
		 */
		public CaptureDevice newCaptureDevice();

		/**
		 * Opens up all interfaces, except loopback and dialup interfaces, for live
		 * capture. The captured packets can be iterated over using the inherited
		 * methods {@link IOIterator#hasNext} and {@link IOIterator#next}. There is
		 * no guarrantee as to the order in which captured packets are returned
		 * between multiple interfaces, with the exception that for each interface
		 * the packets will be returned in the order they were captured on that
		 * interface.
		 * 
		 * @return a capture capturing packets on all interfaces, with the above
		 *         specified exceptions
		 * @throws IOException
		 *           any IO errors
		 */
		public LiveCapture openLive() throws IOException;

		public LiveCapture openLive(CaptureDevice... nics) throws IOException;

		public LiveCapture openLive(Collection<CaptureDevice> nics)
		    throws IOException;

		/**
		 * <P>
		 * Opens up all interfaces, except loopback and dialup interfaces, for live
		 * capture. The captured packets can be iterated over using the inherited
		 * methods {@link IOIterator#hasNext} and {@link IOIterator#next}. There is
		 * no guarrantee as to the order in which captured packets are returned
		 * between multiple interfaces, with the exception that for each interface
		 * the packets will be returned in the order they were captured on that
		 * interface.
		 * </P>
		 * <P>
		 * The filter is used to limit the number of packets captured to ones that
		 * match the filter criteria. Each packet after capture is matched by the
		 * filter. For efficiency this is done at the kernel level for operating
		 * systems that support this feature. On operating systems that do not, the
		 * filter match is performed in "userland" or user space which is less
		 * efficient
		 * 
		 * @param fiter
		 * @return
		 * @throws IOException
		 */
		public LiveCapture openLive(Filter fiter) throws IOException;

		public LiveCapture openLive(Filter filter, CaptureDevice... nics)
		    throws IOException;

		public LiveCapture openLive(Filter filter, Collection<CaptureDevice> nics)
		    throws IOException;

		/**
		 * Opens up all interfaces, except loopback and dialup interfaces, for live
		 * capture. The captured packets can be iterated over using the inherited
		 * methods {@link IOIterator#hasNext} and {@link IOIterator#next}. There is
		 * no guarrantee as to the order in which captured packets are returned
		 * between multiple interfaces, with the exception that for each interface
		 * the packets will be returned in the order they were captured on that
		 * interface.
		 * 
		 * @param count
		 *          number of packet to capture and then exit
		 * @return a capture capturing packets on all interfaces, with the above
		 *         specified exceptions
		 * @throws IOException
		 *           any IO errors
		 */
		public LiveCapture openLive(long count) throws IOException;

		/**
		 * Opens a live session with the network interface for packet tranmition.
		 * The default interface is opened.
		 * 
		 * @return open transmitter with the default network interface
		 * @throws IOException
		 *           any IO errors while opening the connection to interface
		 */
		public NetTransmitter openTransmitter() throws IOException;

		/**
		 * Opens a live session with the network interface for packet tranmition.
		 * The user specified interface is opened.
		 * 
		 * @return open transmitter with the user specified network interface
		 * @throws IOException
		 *           any IO errors while opening the connection to interface
		 */
		public NetTransmitter openTransmitter(NetworkInterface netInterface)
		    throws IOException;

	}

	/**
	 * Factory interface for local sessions. This is a factory interface which
	 * provides access to jNetStream implementation of this API. The LocalFactory
	 * interface provides additional methods, besides the ones found in
	 * {@link Factory}, that may only be used locally on a system. You can use
	 * {@link Captures#getLocal()} method to aquire a reference to the default
	 * factory that creates all local instances of capture framework objects.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface LocalFactory extends Factory {

		/**
		 * Gets the type of underlying file format the input stream is in. The
		 * format of the stream is expected to be in any one of the supported file
		 * formats as if the stream was simply opened with standard java
		 * FileInputStream class.
		 * 
		 * @param in
		 *          stream that has the data to read from
		 * @return read-only FileCapture instance that is used to decode the stream
		 *         contents
		 * @throws IOException
		 */
		public FormatType formatType(ReadableByteChannel in) throws IOException;

		/**
		 * @param f
		 * @return
		 * @throws IOException
		 */
		public Detail formatTypeDetail(File f) throws IOException;

		/**
		 * @param in
		 * @return
		 * @throws IOException
		 */
		public Detail formatTypeDetail(ReadableByteChannel in) throws IOException;

		/**
		 * Creates a capture session which reads from a formatted file. The file has
		 * to be formatted to one of the supported file formats such as Pcap, etc.
		 * The file can be in compressed format or not.
		 * 
		 * @param t
		 *          the returned result is class t type specific
		 * @param in
		 *          input stream
		 * @return stream based capture session
		 * @throws IOException
		 *           any IO errors
		 */
		public InputCapture<? extends CapturePacket> newInput(final File file,
		    Filter<ProtocolFilterTarget> filter) throws IOException;

		/**
		 * Creates a capture session which reads from a formatted file. The file has
		 * to be formatted to one of the supported file formats such as Pcap, etc.
		 * The file can be in compressed format or not.
		 * 
		 * @param t
		 *          the returned result is class t type specific
		 * @param in
		 *          input stream
		 * @return stream based capture session
		 * @throws IOException
		 *           any IO errors
		 */
		public <T extends InputCapture<? extends FilePacket>> T newInput(
		    final Class<T> t, final File file, Filter<ProtocolFilterTarget> filter)
		    throws IOException;

		/**
		 * Creates a capture session which reads from a formatted input stream. The
		 * stream has to be formatted to one of the supported file formats such as
		 * Pcap, etc. The stream based capture session is always read-only, as input
		 * stream is read-only as well.
		 * 
		 * @param t
		 *          the returned result is class t type specific
		 * @param in
		 *          input stream
		 * @return stream based capture session
		 * @throws IOException
		 *           any IO errors
		 */
		public <T extends InputCapture<? extends FilePacket>> T newInput(
		    final Class<T> t, final ReadableByteChannel in) throws IOException;

		/**
		 * Creates a capture session which reads from a formatted input stream. The
		 * stream has to be formatted to one of the supported file formats such as
		 * Pcap, etc. The stream based capture session is always read-only, as input
		 * stream is read-only as well.
		 * 
		 * @param t
		 *          the returned result is class t type specific
		 * @param in
		 *          input stream
		 * @param filter
		 *          default packet filter
		 * @return stream based capture session
		 * @throws IOException
		 *           any IO errors
		 */
		public <T extends InputCapture<? extends FilePacket>> T newInput(
		    final Class<T> t, final ReadableByteChannel in,
		    final Filter<ProtocolFilterTarget> filter) throws IOException;

		/**
		 * Creates a capture session which reads from a formatted channel. The
		 * stream has to be formatted to one of the supported file formats such as
		 * Pcap, etc. The stream based capture session is always read-only, as input
		 * stream is read-only as well.
		 * 
		 * @param in
		 *          input stream
		 * @return stream based capture session
		 * @throws IOException
		 *           any IO errors
		 */
		public InputCapture<? extends CapturePacket> newInput(
		    final ReadableByteChannel in) throws IOException;

		/**
		 * Creates a capture session which reads from a formatted channel. The
		 * stream has to be formatted to one of the supported file formats such as
		 * Pcap, etc. The stream based capture session is always read-only, as input
		 * stream is read-only as well.
		 * 
		 * @param in
		 *          input stream
		 * @param filter
		 *          a filter to filter certain elements from the stream
		 * @return stream based capture session
		 * @throws IOException
		 *           any IO errors
		 */
		public InputCapture<? extends CapturePacket> newInput(
		    final ReadableByteChannel in, final Filter<ProtocolFilterTarget> filter)
		    throws IOException;

		/**
		 * Creates an outbound capture session that is connected to the the supplied
		 * WritableByteChannel.
		 * 
		 * @param t
		 *          format type to format the data into
		 * @param out
		 *          output stream to where to send the serialized formatted byte
		 *          stream
		 * @return output capture session
		 * @throws IOException
		 *           any IO errors
		 */
		public <T extends OutputCapture> T newOutput(Class<T> t,
		    WritableByteChannel out) throws IOException;

		/**
		 * Creates an outbound capture session that is connected to the the supplied
		 * OutputStream.
		 * 
		 * @param type
		 *          format type to format the data into
		 * @param out
		 *          output stream to where to send the serialized formatted byte
		 *          stream
		 * @return output capture session
		 * @throws IOException
		 *           any IO errors
		 */
		public OutputCapture newOutput(FormatType type, OutputStream out)
		    throws IOException;

		/**
     * @param file
     * @param mode
     * @return
		 * @throws IOException 
		 * @throws FileFormatException 
     */
    public FileCapture<? extends FilePacket> openFile(File file, FileMode mode) throws FileFormatException, IOException;

	}

	/**
	 * <p>
	 * Factory interface for remote sessions. This is a factory interface that
	 * provides implementation of jNetStream API. This factory interface contains
	 * only methods that may be invoked on remote sessions and are not applicable
	 * for a local session. The methods in this interface provide information
	 * about remote session itself. Notice that this interface subclasses the main
	 * {@link Factory} interface and makes all the methods there available for
	 * remote sessions.
	 * </p>
	 * <p>
	 * Currently there are no such methods, but may be introduced in the future.
	 * This is because all remote methods can be also provided locally while the
	 * inverse is not true.
	 * </p>
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface RemoteFactory extends Factory {

	}

	/**
	 * Default packet formatter that is used when
	 * {@link org.jnetstream.packet.Packet#format()} method is invoked. By default
	 * this formatter is assigned {@link java.lang.System#out} as its default
	 * output or appender.
	 */
	private static PacketFormatter defaultFormatter;

	/**
	 * Main factory instance for local sessions.
	 */
	private static LocalFactory factory;

	/**
	 * Classname of the default factory implementation for jNetStream's
	 * <code>Captures</code> factory class.
	 */
	public static final String FACTORY_CLASS_DEFAULT =
	    "com.slytechs.capture.DefaultCaptureFactory";

	/**
	 * The name of the system property which controls instantiation of the main
	 * factory implementation for this <code>Captures</code> factory class. If
	 * this property is not set, {@link #FACTORY_CLASS_DEFAULT} is used as the
	 * default.
	 */
	public static final String FACTORY_CLASS_PROPERTY =
	    "org.jnetstream.capture.factory";

	/**
	 * Variable used to hold the last capture returned by any of the factory
	 * methods. Calling on the static method {@link #close} will close the last
	 * capture session returned by this factory. This is useful in situations when
	 * a capture method was invoked from which a tight java1.5 loop and there is
	 * no explict reference to a capture object to be closed. Therefore you can
	 * use the method {@link #last} to aquire a reference to the last capture
	 * session returned.
	 */
	private static Capture lastCapture = null;

	/**
	 * Factory instatiation handler. It reads the system property or the default
	 * to create an instance of the {@link LocalFactory} interface.
	 */
	private static final FactoryLoader<LocalFactory> local =
	    new FactoryLoader<LocalFactory>(logger,
	        Captures.FACTORY_CLASS_PROPERTY, Captures.FACTORY_CLASS_DEFAULT);

	/**
	 * Packet formatter used to format packet's information into a string. This
	 * formatter is used by default whenever
	 * {@link org.jnetstream.packet.Packet#toString()} method is called. By
	 * default it uses a <code>StringBuilder</code> instance to output the
	 * formatted text to and then is typically used by
	 * <code>toString: String</code> to convert to a string to be returned.
	 */
	private static PacketFormatter stringFormatter;

	/**
	 * Copies all of the CapturePackets returned by the source capture sessions
	 * into the already open capture file. The order in which the CapturePackets
	 * are read from the sources is unspecified as various muxing algorithms may
	 * be used in order to avoid blocking on a single source. If all the sources
	 * are file based, the packets will be copied in the order they were supplied
	 * as varargs.
	 * 
	 * @param srcs
	 *          capture sources to read packets from
	 * @param dst
	 *          mutable capture to write the packets into
	 * @return number of packet successfully copied into destination
	 * @throws IOException
	 *           any IO errors
	 */
	public static long append(final FileCapture<? extends FilePacket> dst,
	    final Capture... srcs) throws IOException {

		return Captures.getLocal().append(dst, srcs);
	}

	/**
	 * Copies all of the CapturePackets returned by the source a destination
	 * capture file. The order in which the CapturePackets are read from the
	 * sources is unspecified as various muxing algorithms may be used in order to
	 * avoid blocking on a single source.
	 * 
	 * @param type
	 *          TODO
	 * @param file
	 *          destination file to write the packets into
	 * @param sources
	 *          capture sources to read packets from
	 * @throws IOException
	 *           any IO errors
	 */
	public static long catFile(final FormatType type, final File file,
	    final Capture... sources) throws IOException {
		return Captures.getLocal().catFile(type, file, sources);
	}

	/**
	 * Concatenate all the files into single dst file
	 * 
	 * @param type
	 *          TODO
	 * @param dst
	 *          destination file to concatenate the src files to
	 * @param src
	 *          array of source files to concatenate contents from
	 * @return TODO
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static long catFile(final FormatType type, final File dst,
	    final File... src) throws FileNotFoundException, IOException {
		return Captures.getLocal().catFile(type, dst, src);
	}

	/**
	 * Closes the last capture that was returned by this factory class.
	 * 
	 * @throws IOException
	 *           any IO errors during closure
	 */
	public static void close() throws IOException {
		if (Captures.lastCapture != null) {
			Captures.lastCapture.close();
			Captures.lastCapture = null;
		}
	}

	/**
	 * Generic method for counting elements of any kind given a skippable
	 * iterator. Skippable iterator works just like a normal iterator but also
	 * provides a <code>skip</code> operation which is much more efficient at
	 * advancing to the next element then calling on <code>next</code> is. No
	 * objects have to be created or returned. The elements could be packets,
	 * record, buffers or anything else.
	 * 
	 * @param source
	 *          source iterator over which to count all the elements
	 * @return number of elements counted in the source
	 */
	public static long count(final IOSkippableIterator<?> source)
	    throws IOException {

		long count = 0;

		while (source.hasNext()) {
			source.skip();

			count++;
		}

		return count;
	}

	/**
	 * Counts packets in a file. The file is opened and packets within it are
	 * counted. The implementation uses the most efficient means available to
	 * accomplish the counting task. The count returned will be 100% accurate as
	 * no statistical or other types of analysis are used. Compressed file are OK
	 * and will be decompressed at runtime as a stream and decoded appropriately.
	 * 
	 * @param file
	 *          file to have its packets counted
	 * @return number of packets found in the file
	 * @throws IOException
	 *           any IO errors
	 * @throws FileFormatException
	 *           if the format of the file can not be recognized
	 */
	public static long countPackets(final File file) throws IOException,
	    FileFormatException {
		return Captures.getLocal().countPackets(file);
	}

	/**
	 * Gets the default formatter. Default formatter that is used by all types of
	 * packets when their <code>format</code> method is invoked.
	 * 
	 * @return formatter used in packet <code>format</code> methods
	 */
	public static PacketFormatter defaultFormatter() {
		if (Captures.defaultFormatter == null) {
			Captures.defaultFormatter = new PacketFormatter(System.out);
		}

		return Captures.defaultFormatter;

	}

	/**
	 * Method is used to set a new default formatter. Default formatter is used in
	 * formatting packet content in human readable format to be sent to some
	 * output.
	 * 
	 * @param formatter
	 *          new formatter
	 * @return old formatter, null if there was none
	 */
	public static PacketFormatter defaultFormatter(final PacketFormatter formatter) {
		final PacketFormatter old = Captures.defaultFormatter;

		Captures.defaultFormatter = formatter;

		return old;
	}

	/**
	 * Determines the format type of the supplied file. Checks the format of the
	 * supplied file and expects it to be in one of the supported format types
	 * such as Pcap, etc..
	 * 
	 * @param file
	 *          file to check and return format type
	 * @return format type of the supplied input or null if format type is unknown
	 *         or not supported
	 */
	public static FormatType formatType(final File file) throws IOException {
		return Captures.getLocal().formatType(file);
	}

	/**
	 * Determines the format type of the supplied "input". Checks the format of
	 * the supplied input and expects it to be in one of the supported format
	 * types such as Pcap, etc..
	 * 
	 * @param in
	 *          input to check and return format type
	 * @return format type of the supplied input or null if format type is unknown
	 *         or not supported
	 */
	public static FormatType formatType(final InputStream in) throws IOException {
		return Captures.getLocal().formatType(Channels.newChannel(in));
	}

	/**
	 * Determines the format type of the supplied "input". Checks the format of
	 * the supplied input and expects it to be in one of the supported format
	 * types such as Pcap, etc..
	 * 
	 * @param in
	 *          input to check and return format type
	 * @return format type of the supplied input or null if format type is unknown
	 *         or not supported
	 */
	public static FormatType formatType(final ReadableByteChannel in)
	    throws IOException {
		return Captures.getLocal().formatType(in);
	}

	/**
	 * Determines the format type of the supplied file. Checks the format of the
	 * supplied file and expects it to be in one of the supported format types
	 * such as Pcap, etc.. This method provides additional information about the
	 * format type especially for <code>FormatType.Other</code>. You can use
	 * {@link org.jnetstream.capture.FormatType.Detail#getDetailedName()} which
	 * will provide a known name for the format.
	 * 
	 * @param file
	 *          file to check and return format type
	 * @return format type of the supplied input or null if format type is unknown
	 *         or not supported
	 * @throws IOException
	 */
	public static Detail formatTypeDetail(File f) throws IOException {
		return Captures.getLocal().formatTypeDetail(f);
	}

	/**
	 * Determines the format type of the supplied "input". Checks the format of
	 * the supplied input and expects it to be in one of the supported format
	 * types such as Pcap, etc.. This method provides additional information about
	 * the format type especially for <code>FormatType.Other</code>. You can
	 * use {@link org.jnetstream.capture.FormatType.Detail#getDetailedName()}
	 * which will provide a known name for the format.
	 * 
	 * @param in
	 *          input to check and return format type
	 * @return format type of the supplied input or null if format type is unknown
	 *         or not supported
	 */
	public static FormatType.Detail formatTypeDetail(final ReadableByteChannel in)
	    throws IOException {
		return Captures.getLocal().formatTypeDetail(in);

	}

	/**
	 * <p>
	 * Returns an instance of the current local capture factory which is
	 * responsible for creating local capture instances. This is synonymous with
	 * the RemoteSession counter part which extends the RemoteFactory interface.
	 * The methods between Factory, LocalFactory and RemoteFactory are partitioned
	 * in a such a way to only allow operations that make sense with the given
	 * interface. For example, the RemoteLocalFactory interface does not contain
	 * any of the methods that LocalFactory does to open a live network capture
	 * using standard java.net.NetworkInterface objects. This is because
	 * NetworkInterface objects don't make sense on client machine, they are only
	 * instantiated by runtime environment for local machines. Therefore you need
	 * to use one of the methods that are allowed for remote capture such as
	 * {@link Factory#openLive(CaptureDevice)} method where CaptureDevice is this
	 * frameworks concept and is allowed to be shared accross multiple machines.
	 * </P>
	 * 
	 * @return current local capture factory
	 */
	public static Captures.LocalFactory getLocal() {
		return Captures.local.getFactory();
	}

	/**
	 * Returns the last capture session returned by this factory method.
	 * 
	 * @return last capture session returned or null if none had been created yet
	 *         or if {@link #close()} had been called which clears out the last
	 *         capture
	 */
	public static Capture<?> last() {
		return Captures.lastCapture;
	}

	/**
	 * Returns a list of local capture devices available for live capture. Use
	 * {@link #openLive} method calls to open a live network capture while
	 * supplying 1 or more of these capture devices returned. These capture
	 * devices differ from ones obtained from packets as in
	 * {@link org.jnetstream.packet.Packet#getCaptureDevice} that are obtained and
	 * contain information about actual live network interfaces on a particular
	 * system. CaptureDevices aquire from file based packets or captures only
	 * contain information about network interface that may or may not exist any
	 * longer.
	 * 
	 * @return a list of 0 or more capture devices available to be opened
	 */
	public static LiveCaptureDevice[] listCaptureDevices() throws IOException {
		return Captures.getLocal().listCaptureDevices();
	}

	/**
	 * <P>
	 * Creates a new file of the request type. The new file will contain the
	 * appropriate block/file header, but other then that is completely empty.
	 * This means that file size will not be equal to zero, but no packet or data
	 * records exists.
	 * </P>
	 * 
	 * @param <T>
	 *          one of the supported class types that is a subclass of
	 *          FileCapture, such as PcapFile, SnoopFile, NapFile, etc...
	 * @param t
	 *          type of file to create specified by passing one of the supported
	 *          class object of the file type to be created
	 * @param f
	 *          the file to create, the file must not exist
	 * @return open Capture of the specified type that can be used to append data
	 *         to the otherwise empty file
	 * @throws IOException
	 *           any IO errors
	 * @throws FileFormatException
	 */
	public static <T extends FileCapture<? extends FilePacket>> T newFile(
	    final Class<T> t, final File f) throws IOException, FileFormatException {
		final T x = Captures.getLocal().newFile(t, f);
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * A convenience utility method which creates a new file and dumps all of the
	 * packets from capture to the new file in the proper format.
	 * 
	 * @param f
	 *          file to create
	 * @param type
	 *          the type of file to create
	 * @param c
	 *          source of CapturePackets which will be dumped into the new file
	 * @return open instance of this new file
	 * @throws IOException
	 *           any IO errors
	 */
	public static <T extends FileCapture<? extends FilePacket>> T newFile(
	    final Class<T> t, final File f, final Capture<? extends CapturePacket> c)
	    throws IOException {

		final T x = Captures.getLocal().newFile(t, f, c);
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * <P>
	 * Creates a new file of the request type. The new file will contain the
	 * appropriate block/file header, but other then that is completely empty.
	 * This means that file size will not be equal to zero, but no packet or data
	 * records exists.
	 * </P>
	 * 
	 * @param <T>
	 *          one of the supported class types that is a subclass of
	 *          FileCapture, such as PcapFile, SnoopFile, NapFile, etc...
	 * @param t
	 *          type of file to create specified by passing one of the supported
	 *          class object of the file type to be created
	 * @param f
	 *          the file to create, the file must not exist
	 * @return open Capture of the specified type that can be used to append data
	 *         to the otherwise empty file
	 * @throws IOException
	 *           any IO errors
	 * @throws FileFormatException
	 */
	public static <T extends FileCapture<? extends FilePacket>> T newFile(
	    final Class<T> t, final String f) throws IOException, FileFormatException {
		final T x = Captures.getLocal().newFile(t, new File(f));
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * A convenience utility method which creates a new file and dumps all of the
	 * packet from capture to the new file in the proper format.
	 * 
	 * @param f
	 *          file to create
	 * @param type
	 *          the type of file to create
	 * @param c
	 *          source of CapturePackets which will be dumped into the new file
	 * @return open instance of this new file
	 * @throws IOException
	 *           any IO errors
	 */
	public static <T extends FileCapture<? extends FilePacket>> T newFile(
	    final Class<T> t, final String f, final Capture<CapturePacket> c)
	    throws IOException {

		final T x = Captures.getLocal().newFile(t, new File(f), c);
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * @param pcap
	 * @param src
	 * @return
	 * @throws IOException
	 */
	public static FileCapture<? extends FilePacket> newFile(
	    final FormatType type, final File file) throws IOException {
		final FileCapture<? extends FilePacket> x =
		    Captures.getLocal().newFile(type, file);
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * @param pcap
	 * @param string
	 * @return
	 * @throws IOException
	 */
	public static FileCapture<? extends FilePacket> newFile(final FormatType t,
	    final String file) throws IOException {
		final FileCapture<? extends FilePacket> x =
		    Captures.getLocal().newFile(t, new File(file));
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * @param name
	 * @param temp1Compressed
	 * @return
	 * @throws IOException
	 */
	public static <T extends InputCapture<? extends FilePacket>> T newInput(
	    final Class<T> t, final File in) throws IOException {
		return Captures.getLocal().newInput(t, in, null);
	}

	/**
	 * @param <T>
	 * @param t
	 * @param in
	 * @return
	 * @throws IOException
	 * @see org.jnetstream.capture.Captures.LocalFactory#newInput(java.lang.Class,
	 *      java.io.InputStream)
	 */
	public static <T extends InputCapture<? extends FilePacket>> T newInput(
	    final Class<T> t, final InputStream in) throws IOException {
		return Captures.getLocal().newInput(t, Channels.newChannel(in));
	}

	/**
	 * @param <T>
	 * @param t
	 * @param in
	 * @param filter
	 * @return
	 * @throws IOException
	 * @see org.jnetstream.capture.Captures.LocalFactory#newInput(java.lang.Class,
	 *      java.io.InputStream, Filter)
	 */
	public static <T extends InputCapture<? extends FilePacket>> T newInput(
	    final Class<T> t, final InputStream in,
	    final Filter<ProtocolFilterTarget> filter) throws IOException {
		return Captures.getLocal().newInput(t, Channels.newChannel(in), filter);
	}

	public static <T extends InputCapture<? extends FilePacket>> T newInput(
	    final Class<T> t, final ReadableByteChannel in) throws IOException {
		return Captures.getLocal().newInput(t, in);
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static InputCapture<? extends CapturePacket> newInput(File file)
	    throws IOException {
		final InputCapture<? extends CapturePacket> x =
		    Captures.getLocal().newInput(file, null);
		Captures.lastCapture = x;

		return x;

	}

	/**
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static InputCapture<? extends CapturePacket> newInput(File file,
	    Filter<ProtocolFilterTarget> filter) throws IOException {
		final InputCapture<? extends CapturePacket> x =
		    Captures.getLocal().newInput(file, filter);
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * @param in
	 * @return
	 * @throws IOException
	 * @see org.jnetstream.capture.Captures.LocalFactory#newInput(java.io.InputStream)
	 */
	public static InputCapture<? extends CapturePacket> newInput(
	    final InputStream in) throws IOException {
		return Captures.getLocal().newInput(Channels.newChannel(in));
	}

	/**
	 * @param in
	 * @param filter
	 * @return
	 * @throws IOException
	 * @see org.jnetstream.capture.Captures.LocalFactory#newInput(java.io.InputStream,
	 *      Filter)
	 */
	public static InputCapture<? extends CapturePacket> newInput(
	    final InputStream in, final Filter<ProtocolFilterTarget> filter)
	    throws IOException {
		return Captures.getLocal().newInput(Channels.newChannel(in), filter);
	}

	/**
	 * @param <T>
	 * @param t
	 * @param out
	 * @return
	 * @throws IOException
	 * @see org.jnetstream.capture.Captures.LocalFactory#newOutput(java.lang.Class,
	 *      java.io.OutputStream)
	 */
	public static <T extends OutputCapture> T newOutput(final Class<T> t,
	    final OutputStream out) throws IOException {
		return Captures.getLocal().newOutput(t, Channels.newChannel(out));
	}

	/**
	 * @param name
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public static <T extends OutputCapture> T newOutput(final Class<T> t,
	    final WritableByteChannel out) throws IOException {
		return Captures.getLocal().newOutput(t, out);
	}

	/**
	 * @param type
	 * @param out
	 * @return
	 * @throws IOException
	 * @see org.jnetstream.capture.Captures.LocalFactory#newOutput(org.jnetstream.capture.FormatType,
	 *      java.io.OutputStream)
	 */
	public static OutputCapture newOutput(final FormatType type,
	    final OutputStream out) throws IOException {
		return Captures.getLocal().newOutput(type, out);
	}

	public static <T extends FileCapture<? extends FilePacket>> T openFile(
	    final Class<T> t, final File file) throws IOException,
	    FileFormatException {
		final T x = Captures.getLocal().openFile(t, file);
		Captures.lastCapture = x;

		return x;
	}

	public static <T extends FileCapture<? extends FilePacket>> T openFile(
	    final Class<T> t, final File file, final FileMode mode)
	    throws IOException, FileFormatException {
		final T x = Captures.getLocal().openFile(t, file, mode, null);
		Captures.lastCapture = x;

		return x;
	}

	public static <T extends FileCapture<? extends FilePacket>> T openFile(
	    final Class<T> t, final String file) throws IOException,
	    FileFormatException {
		final T x = Captures.getLocal().openFile(t, new File(file));
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * Opens a file for reading and writting.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws FileFormatException
	 *           TODO
	 */
	public static FileCapture<? extends FilePacket> openFile(final File file)
	    throws IOException, FileFormatException {
		final FileCapture<? extends FilePacket> x =
		    Captures.getLocal().openFile(file);
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * Opens a file using the specified file mode.
	 * 
	 * @param file
	 *          file to open
	 * @param mode
	 *          FileMode with which to open the file with
	 * @return open file in the specified mode
	 * @throws IOException
	 *           any IO errors
	 * @throws FileFormatException
	 *           thrown if file containted any format errors
	 */
	public static FileCapture<? extends FilePacket> openFile(final File file,
	    final FileMode mode) throws IOException, FileFormatException {
		final FileCapture<? extends FilePacket> x =
		    Captures.getLocal().openFile(file, mode);
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * Opens a file for reading and writting while applying a filter to limit what
	 * is returned.
	 * 
	 * @param file
	 * @param protocolFilter
	 * @return
	 * @throws IOException
	 */
	public static FileCapture<? extends FilePacket> openFile(final File file,
	    final Filter<ProtocolFilterTarget> protocolFilter) throws IOException {
		final FileCapture<? extends FilePacket> x =
		    Captures.getLocal().openFile(file, FileMode.ReadOnly, protocolFilter);
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * @param string
	 * @return
	 * @throws FileFormatException
	 * @throws IOException
	 */
	public static FileCapture<? extends FilePacket> openFile(final String file)
	    throws IOException, FileFormatException {
		final FileCapture<? extends FilePacket> x =
		    Captures.openFile(new File(file));
		Captures.lastCapture = x;

		return x;
	}

	public static FileCapture<? extends FilePacket> openFile(final String file,
	    final Filter<ProtocolFilterTarget> protocolFilter) throws IOException {
		final FileCapture<? extends FilePacket> x =
		    Captures.openFile(new File(file), protocolFilter);
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * Opens up a network interfaces for live packet capture. All available
	 * network interfaces are opened for capture with the exception of any
	 * interfaces that carry the loopback address of 127.0.0.0/8. LiveCapture
	 * extends the Capture interface which provides simple iterator through which
	 * all the captured packets are returned one after the other. There is no
	 * guarrantee as to the order in which packets are returned when more then one
	 * interface is used for packet capture at the same time. Its simply left upto
	 * the underlying implementation to determine the order. Each CapturePacket
	 * maintains a reference to a CaptureDevice which is associated with the
	 * interface that captured the particular packet.
	 * 
	 * @return reference to a live capture session
	 * @throws IOException
	 *           any IO errors
	 */
	public static LiveCapture openLive() throws IOException {
		final LiveCapture x = Captures.getLocal().openLive();
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * <P>
	 * Opens up a network interface for live packet capture. The specified network
	 * interface will be opened for live network packet capture. LiveCapture
	 * extends the Capture interface which provides simple iterator through which
	 * all the captured packets are returned one after the other. There is no
	 * guarrantee as to the order in which packets are returned when more then one
	 * interface is used for packet capture at the same time. Its simply left upto
	 * the underlying implementation to determine the order. Each CapturePacket
	 * maintains a reference to a CaptureDevice which is associated with the
	 * interface that captured the particular packet.
	 * </P>
	 * 
	 * @param nic
	 * @return
	 * @throws IOException
	 */
	public static LiveCapture openLive(final CaptureDevice nic)
	    throws IOException {
		final LiveCapture x = Captures.getLocal().openLive(nic);
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * <P>
	 * Opens up a network interfaces for live packet capture. The specified
	 * network interfaces are opened for capture with including loopback interface
	 * if specified. LiveCapture extends the Capture interface which provides
	 * simple iterator through which all the captured packets are returned one
	 * after the other. There is no guarrantee as to the order in which packets
	 * are returned when more then one interface is used for packet capture at the
	 * same time. Its simply left upto the underlying implementation to determine
	 * the order. Each CapturePacket maintains a reference to a CaptureDevice
	 * which is associated with the interface that captured the particular packet.
	 * </P>
	 * 
	 * @param nics
	 *          collection of network interfaces to open, including loopbacks if
	 *          part of the collection
	 * @return a single capture session which captures packets from all of the
	 *         opened interface at the same time
	 * @throws IOException
	 *           any IO errors
	 */
	public static LiveCapture openLive(final CaptureDevice... nics)
	    throws IOException {
		final LiveCapture x = Captures.getLocal().openLive(nics);
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * <P>
	 * Opens up a network interfaces for live packet capture. The specified
	 * network interfaces are opened for capture with including loopback interface
	 * if specified. LiveCapture extends the Capture interface which provides
	 * simple iterator through which all the captured packets are returned one
	 * after the other. There is no guarrantee as to the order in which packets
	 * are returned when more then one interface is used for packet capture at the
	 * same time. Its simply left upto the underlying implementation to determine
	 * the order. Each CapturePacket maintains a reference to a CaptureDevice
	 * which is associated with the interface that captured the particular packet.
	 * </P>
	 * 
	 * @param nics
	 *          collection of network interfaces to open, including loopbacks if
	 *          part of the collection
	 * @return a single capture session which captures packets from all of the
	 *         opened interface at the same time
	 * @throws IOException
	 *           any IO errors
	 */
	public static LiveCapture openLive(final Collection<CaptureDevice> nics)
	    throws IOException {
		final LiveCapture x = Captures.getLocal().openLive(nics);
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * <P>
	 * Opens up a network interfaces for live packet capture with a filter. All
	 * available network interfaces are opened for capture with the exception of
	 * any interfaces that carry the loopback address of 127.0.0.0/8. LiveCapture
	 * extends the Capture interface which provides simple iterator through which
	 * all the captured packets are returned one after the other. There is no
	 * guarrantee as to the order in which packets are returned when more then one
	 * interface is used for packet capture at the same time. Its simply left upto
	 * the underlying implementation to determine the order. Each CapturePacket
	 * maintains a reference to a CaptureDevice which is associated with the
	 * interface that captured the particular packet.
	 * </P>
	 * <P>
	 * Filter is applied directly by the kernel, this feature is operating system
	 * dependent, to efficiently accept or reject packets as close to the hardware
	 * level as possible with no extraneous in memory copies. Most modern
	 * operating system support this feature. If this feature is not available the
	 * filter is applied in "userland" or in user space which is less efficient.
	 * </P>
	 * 
	 * @param filter
	 *          the filter to apply to the capture session
	 */
	public static LiveCapture openLive(final Filter filter) throws IOException {
		final LiveCapture x = Captures.getLocal().openLive(filter);
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * <P>
	 * Opens up a network interface for live packet capture with a filter. The
	 * specified network interface will be opened for live network packet capture.
	 * LiveCapture extends the Capture interface which provides simple iterator
	 * through which all the captured packets are returned one after the other.
	 * There is no guarrantee as to the order in which packets are returned when
	 * more then one interface is used for packet capture at the same time. Its
	 * simply left upto the underlying implementation to determine the order. Each
	 * CapturePacket maintains a reference to a CaptureDevice which is associated
	 * with the interface that captured the particular packet.
	 * </P>
	 * <P>
	 * Filter is applied directly by the kernel, this feature is operating system
	 * dependent, to efficiently accept or reject packets as close to the hardware
	 * level as possible with no extraneous in memory copies. Most modern
	 * operating system support this feature. If this feature is not available the
	 * filter is applied in "userland" or in user space which is less efficient.
	 * </P>
	 * 
	 * @param nic
	 * @param filter
	 * @return
	 * @throws IOException
	 */
	public static LiveCapture openLive(final Filter filter,
	    final CaptureDevice nic) throws IOException {
		final LiveCapture x = Captures.getLocal().openLive(filter, nic);
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * <P>
	 * Opens up a network interfaces for live packet capture with a filter. The
	 * specified network interfaces are opened for capture with including loopback
	 * interface if specified. LiveCapture extends the Capture interface which
	 * provides simple iterator through which all the captured packets are
	 * returned one after the other. There is no guarrantee as to the order in
	 * which packets are returned when more then one interface is used for packet
	 * capture at the same time. Its simply left upto the underlying
	 * implementation to determine the order. Each CapturePacket maintains a
	 * reference to a CaptureDevice which is associated with the interface that
	 * captured the particular packet.
	 * </P>
	 * <P>
	 * Filter is applied directly by the kernel, this feature is operating system
	 * dependent, to efficiently accept or reject packets as close to the hardware
	 * level as possible with no extraneous in memory copies. Most modern
	 * operating system support this feature. If this feature is not available the
	 * filter is applied in "userland" or in user space which is less efficient.
	 * </P>
	 * 
	 * @param nics
	 * @param filter
	 * @return
	 * @throws IOException
	 */
	public static LiveCapture openLive(final Filter filter,
	    final Collection<CaptureDevice> nics) throws IOException {
		final LiveCapture x = Captures.getLocal().openLive(filter, nics);
		Captures.lastCapture = x;

		return x;

	}

	/**
	 * Opens up a network interfaces for live packet capture. All available
	 * network interfaces are opened for capture with the exception of any
	 * interfaces that carry the loopback address of 127.0.0.0/8. LiveCapture
	 * extends the Capture interface which provides simple iterator through which
	 * all the captured packets are returned one after the other. There is no
	 * guarrantee as to the order in which packets are returned when more then one
	 * interface is used for packet capture at the same time. Its simply left upto
	 * the underlying implementation to determine the order. Each CapturePacket
	 * maintains a reference to a CaptureDevice which is associated with the
	 * interface that captured the particular packet.
	 * 
	 * @param count
	 *          number of packets to capture and exit afterwards
	 * @return reference to a live capture session
	 * @throws IOException
	 *           any IO errors
	 */
	public static LiveCapture openLive(final long count) throws IOException {
		final LiveCapture x = Captures.getLocal().openLive(count);
		Captures.lastCapture = x;

		return x;
	}

	/**
	 * Opens a live session with the network interface for packet tranmition. The
	 * default interface is opened.
	 * 
	 * @return open transmitter with the default network interface
	 * @throws IOException
	 *           any IO errors while opening the connection to interface
	 */
	public static NetTransmitter openTransmitter() throws IOException {
		return Captures.factory.openTransmitter();
	}

	/**
	 * Opens a live session with the network interface for packet tranmition. The
	 * user specified interface is opened.
	 * 
	 * @return open transmitter with the user specified network interface
	 * @throws IOException
	 *           any IO errors while opening the connection to interface
	 */
	public static NetTransmitter openTransmitter(
	    final NetworkInterface netInterface) throws IOException {
		return Captures.factory.openTransmitter(netInterface);
	}

	/**
	 * Registers a listener for capture session events with the current factory.
	 * 
	 * @param listener
	 *          listener to notify when new capture session events are generated
	 */
	public static void registerListener(final CaptureListener listener) {
		Captures.getLocal().registerListener(listener);
	}

	/**
	 * Removes a previously registered capture session listener
	 * 
	 * @param listener
	 *          listener to remove from the list of listeners
	 */
	public static void removeRegisteredListener(final CaptureListener listener) {
		Captures.getLocal().removeRegisteredListener(listener);
	}

	/**
	 * Allows complete override of the implementation of Capture Framework. User
	 * can supply its own implementation of the comprehensive LocalFactory which
	 * is called to full fill "capture framework's" operations.
	 * 
	 * @param local
	 *          new local factory for all capture framework's operations
	 */
	public static void setLocalCaptureFactory(final LocalFactory local) {
		Captures.factory = local;
	}

	/**
	 * <P>
	 * Splits the file into smaller files according to default rules defined for
	 * each file format. For NAP the file will be split with each Block Record
	 * being split into its own seperate file. The defaults are to split the files
	 * into rough 1Meg segments, if the file is greater then 1 Meg otherwise into
	 * rough 100K segments if file is less then 1Meg but greater then 100K. If the
	 * file is smaller then 100K, nothing is split. Where K = 1024 bytes.
	 * </P>
	 * <P>
	 * The base filename supplied is used as the base filename for all newly
	 * created files with the -XXXX appended to them.
	 * <P>
	 * <P>
	 * The source file is unmodified
	 * </p>
	 * 
	 * @param file
	 *          file to be split
	 * @return list of newly created files
	 */
	public static List<File> splitFile(final File file) throws IOException {
		return Captures.getLocal().splitFile(file);
	}

	/**
	 * <P>
	 * Split the specified file into smaller files containing specified number of
	 * packets each from the source file. New files are created to hold only the
	 * specified number of packets and associated meta records. The supplied
	 * filename is used as a base filename for all newly created files with the
	 * post fix of -XXXX appended to them.
	 * </P>
	 * <P>
	 * The source file is unmodified
	 * </P>
	 * 
	 * @param file
	 *          source file to split
	 * @param packetCount
	 *          split using this many packets from the source file copied into the
	 *          newly created files
	 * @param maxCompression
	 *          true means produce the smallest possible file, while false means
	 *          leave it upto the default algorithm for each spcific file type.
	 *          For example NAP files pad their files to 512Kb by default which
	 *          means that files containing even only a single packet are of
	 *          minimum size 512 Kb, but this can be overriden by setting
	 *          maxCompression to true. Notice that it will be harder to split the
	 *          NAP file with regular unix commands if default padding is not
	 *          used.
	 * @return list of all the new files created
	 */
	public static List<File> splitFile(final File file, final long packetCount,
	    final boolean maxCompression) throws IOException {
		return Captures.getLocal().splitFile(file, packetCount, maxCompression);
	}

	/**
	 * Retrieves the default packet formatter used in formating output to a
	 * string. This formatter is used by all types of packets to format the output
	 * to a string.
	 * 
	 * @return formatter used to format packets to a string
	 */
	public static PacketFormatter stringFormatter() {

		if (Captures.stringFormatter == null) {
			Captures.stringFormatter = new PacketFormatter();
		}

		return Captures.stringFormatter;
	}

	/**
	 * Method is used to set a new string formatter. String formatter is used in
	 * formatting packet content in human readable format to be returned as a
	 * string.
	 * 
	 * @param formatter
	 *          new formatter
	 * @return old formatter, null if there was none
	 */
	public static PacketFormatter stringFormatter(final PacketFormatter formatter) {
		final PacketFormatter old = Captures.stringFormatter;

		Captures.stringFormatter = formatter;

		return old;
	}

	/**
	 * Checks if the specified file is in a proper format 100% compabile with
	 * specification.
	 * 
	 * @param file
	 *          file to validate
	 * @return true if file is valid with the specification, otherwise false, even
	 *         if minor infringements are found
	 */
	public static boolean validateFile(final File file) throws IOException {
		return Captures.getLocal().validateFile(file);
	}

	/**
	 * Prevent anyone from instantiating
	 */
	private Captures() {
		// Empty
	}
}
