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
package com.slytechs.capture;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jnetstream.capture.CapturePacket;
import org.jnetstream.capture.FilePacket;
import org.jnetstream.capture.FormatType;
import org.jnetstream.capture.InputCapture;
import org.jnetstream.capture.OutputCapture;
import org.jnetstream.capture.Captures.LocalFactory;
import org.jnetstream.capture.file.pcap.PcapInput;
import org.jnetstream.capture.file.snoop.SnoopInput;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.ProtocolFilterTarget;

import com.slytechs.file.pcap.PcapInputCapture;
import com.slytechs.file.snoop.SnoopInputCapture;
import com.slytechs.utils.factory.FactoryLoader;
import com.slytechs.utils.memory.channel.BufferedReadableByteChannel;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class StreamFactory
    extends LiveFactory implements LocalFactory {

	private final static Log logger = LogFactory.getLog(StreamFactory.class);

	/**
	 * Factory for loading <code>FormatType.Other</code> which by default
	 * invokes an NPL based codec to decode the file format.
	 */
	private static final FactoryLoader<InputCapture.FormatTypeOtherFactory> factoryForOther =
	    new FactoryLoader<InputCapture.FormatTypeOtherFactory>(logger,
	        InputCapture.PROPERTY_INPUTCAPTURE_OTHER_FACTORY,
	        InputCapture.DEFAULT_INPUTCAPTURE_OTHER_FACTORY);

	public FormatType formatType(final ReadableByteChannel in) throws IOException {

		/*
		 * Wrap the original input in buffer channel so that we can rewind and check
		 * various types.
		 */

		final BufferedReadableByteChannel b = new BufferedReadableByteChannel(in);
		b.mark(24); // Max number of bytes of all the known block headers

		if (PcapInputCapture.checkFormat(b) != null) {
			return FormatType.Pcap;
		}

		b.reset();

		if (SnoopInputCapture.checkFormat(b) != null) {
			return FormatType.Snoop;
		}

		b.reset();

		if (factoryForOther.getFactory().formatType(b) != null) {
			return FormatType.Other;
		}

		return null;
	}

	public FormatType.Detail formatTypeDetail(final ReadableByteChannel in)
	    throws IOException {

		/*
		 * Wrap the original input in buffer channel so that we can rewind and check
		 * various types.
		 */

		final BufferedReadableByteChannel b = new BufferedReadableByteChannel(in);
		b.mark(24); // Max number of bytes of all the known block headers

		if (PcapInputCapture.checkFormat(b) != null) {
			return new DefaultFormatTypeDetail(FormatType.Pcap);
		}

		b.reset();

		if (SnoopInputCapture.checkFormat(b) != null) {
			return new DefaultFormatTypeDetail(FormatType.Snoop);
		}

		b.reset();

		final FormatType.Detail detail;

		detail = factoryForOther.getFactory().formatTypeDetail(b);

		return detail;
	}

	public <T extends InputCapture<? extends FilePacket>> T newInput(
	    final Class<T> t, final File file, Filter<ProtocolFilterTarget> filter)
	    throws IOException {

		final BufferedInputStream b =
		    new BufferedInputStream(new FileInputStream(file));
		b.mark(1024); // Buffer first 1K of stream so we can rewind

		/*
		 * Check the stream, without decompression first
		 */
		if (formatType(Channels.newChannel(b)) != null) {
			b.close();

			/*
			 * This is a plain uncompressed file, open up a FileChannel. It will be
			 * much faster
			 */
			return newInput(t, new RandomAccessFile(file, "rw").getChannel(), filter);
		}

		/*
		 * Try with gunziped stream, second
		 */
		b.reset(); // Rewind
		if (formatType(Channels.newChannel(new GZIPInputStream(b))) != null) {
			b.close();

			/*
			 * Now reopen the same file, but this time without the buffered
			 * inputstream in the middle. Try to make things as efficient as possible.
			 * TODO: implement much faster channel based GZIP decompression algorithm
			 */
			return newInput(t, Channels.newChannel(new GZIPInputStream(
			    new FileInputStream(file))), filter);
		}

		throw new IllegalArgumentException(
		    "File is not any compressed or decompressed known format ["
		        + file.getName() + "]");

	}

	public <T extends InputCapture<? extends FilePacket>> T newInput(
	    final Class<T> t, final ReadableByteChannel in) throws IOException {
		return this.newInput(t, in, null);
	}

	public <T extends InputCapture<? extends FilePacket>> T newInput(
	    final Class<T> t, final ReadableByteChannel in,
	    final Filter<ProtocolFilterTarget> filter) throws IOException {
		if (t == PcapInput.class) {
			final BufferedReadableByteChannel b = new BufferedReadableByteChannel(in);
			b.mark(4);

			ByteOrder order = PcapInputCapture.checkFormat(b);

			b.reset();
			return t.cast(new PcapInputCapture(b, order, filter));

		} else if (t == SnoopInput.class) {
			return t.cast(new SnoopInputCapture(in, filter));
		}

		throw new IllegalArgumentException("Unknown input stream format type ["
		    + t.getName() + "]");
	}

	public InputCapture<? extends CapturePacket> newInput(final File file,
	    final Filter<ProtocolFilterTarget> filter) throws IOException {

		final BufferedInputStream b =
		    new BufferedInputStream(new FileInputStream(file));
		b.mark(1024); // Buffer first 1K of stream so we can rewind

		/*
		 * Check the stream, without decompression first
		 */
		if (formatType(Channels.newChannel(b)) != null) {
			b.close();

			/*
			 * This is a plain uncompressed file, open up a FileChannel. It will be
			 * much faster
			 */
			return newInput(new RandomAccessFile(file, "rw").getChannel(), filter);
		}

		/*
		 * Try with gunziped stream, second
		 */
		b.reset(); // Rewind
		if (formatType(Channels.newChannel(new GZIPInputStream(b))) != null) {
			b.close();

			/*
			 * Now reopen the same file, but this time without the buffered
			 * inputstream in the middle. Try to make things as efficient as possible.
			 * TODO: implement much faster channel based GZIP decompression algorithm
			 */
			return newInput(Channels.newChannel(new GZIPInputStream(
			    new FileInputStream(file))), filter);
		}

		b.close();
		return factoryForOther.getFactory().newInput(
		    new RandomAccessFile(file, "r").getChannel(), filter);

	}

	/**
	 * 
	 */
	public InputCapture<? extends CapturePacket> newInput(
	    final ReadableByteChannel in) throws IOException {
		return this.newInput(in, null);
	}

	/**
	 * 
	 */
	public InputCapture<? extends CapturePacket> newInput(
	    final ReadableByteChannel in, final Filter<ProtocolFilterTarget> filter)
	    throws IOException {

		final BufferedReadableByteChannel b = new BufferedReadableByteChannel(in);
		b.mark(24);

		final FormatType type = this.formatType(b);

		switch (type) {
			case Pcap:
				b.reset();
				ByteOrder order = PcapInputCapture.checkFormat(b);

				b.reset();
				return new PcapInputCapture(b, order, filter);

			case Snoop:
				return new SnoopInputCapture(b, filter);

				/**
				 * Loads NPL based file formats. Use
				 * <code>InputCapture.getFormatName()</code> to get a more accurate
				 * name of the file format if its NPL based.
				 */
			case Other:
				b.reset();
				return factoryForOther.getFactory().newInput(b, filter);

			default:
				/*
				 * Otherwise throw an exception, we don't recognize the format
				 */
				throw new IllegalStateException("Unrecognized internal format type ["
				    + type + "]");

		}
	}

	public <T extends OutputCapture> T newOutput(final Class<T> t,
	    final OutputStream out) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public <T extends OutputCapture> T newOutput(final Class<T> t,
	    final WritableByteChannel out) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public OutputCapture newOutput(final FormatType type, final OutputStream out)
	    throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
