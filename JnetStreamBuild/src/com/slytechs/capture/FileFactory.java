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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.jnetstream.capture.Capture;
import org.jnetstream.capture.CapturePacket;
import org.jnetstream.capture.Captures;
import org.jnetstream.capture.FileCapture;
import org.jnetstream.capture.FileFormatException;
import org.jnetstream.capture.FileMode;
import org.jnetstream.capture.FilePacket;
import org.jnetstream.capture.FormatType;
import org.jnetstream.capture.InputCapture;
import org.jnetstream.capture.PacketIterator;
import org.jnetstream.capture.Captures.LocalFactory;
import org.jnetstream.capture.file.pcap.PcapFile;
import org.jnetstream.capture.file.snoop.SnoopFile;
import org.jnetstream.filter.Filter;
import org.jnetstream.packet.Packet;
import org.jnetstream.packet.ProtocolFilterTarget;

import com.slytechs.file.pcap.PcapFileCapture;
import com.slytechs.file.snoop.SnoopFileCapture;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class FileFactory implements LocalFactory {

	private static final Log logger = Captures.logger;

	public FormatType formatType(final File file) throws IOException {

		try {
			new PcapFileCapture(file, FileMode.ReadOnlyNoMap, null).close();

			if (logger.isTraceEnabled()) {
				logger.trace(file.getName() + ", type=" + FormatType.Pcap);
			}

			return FormatType.Pcap;
		} catch (final Exception e) {
		}

		try {
			new SnoopFileCapture(file, FileMode.ReadOnlyNoMap, null).close();

			if (logger.isTraceEnabled()) {
				logger.trace(file.getName() + ", type=" + FormatType.Pcap);
			}

			return FormatType.Snoop;
		} catch (final Exception e) {
		}

		/*
		 * Now try InputCapture which may also yield a known format
		 */
		ReadableByteChannel channel = new RandomAccessFile(file, "r").getChannel();
		FormatType type = formatType(channel);
		channel.close();

		if (logger.isTraceEnabled()) {
			logger.trace(file.getName() + ", type=" + FormatType.Pcap);
		}

		return type;
	}

	public FormatType.Detail formatTypeDetail(final File file) throws IOException {

		try {
			new PcapFileCapture(file, FileMode.ReadOnlyNoMap, null).close();

			if (logger.isTraceEnabled()) {
				logger.trace(file.getName() + ", type=" + FormatType.Pcap);
			}

			return new DefaultFormatTypeDetail(FormatType.Pcap);
		} catch (final Exception e) {
		}

		try {
			new SnoopFileCapture(file, FileMode.ReadOnlyNoMap, null).close();

			if (logger.isTraceEnabled()) {
				logger.trace(file.getName() + ", type=" + FormatType.Pcap);
			}

			return new DefaultFormatTypeDetail(FormatType.Snoop);
		} catch (final Exception e) {
		}

		/*
		 * Now try InputCapture which may also yield a known format
		 */
		ReadableByteChannel channel = new RandomAccessFile(file, "r").getChannel();
		FormatType.Detail detail = formatTypeDetail(channel);
		channel.close();

		if (logger.isTraceEnabled()) {
			logger.trace(file.getName() + ", type=" + FormatType.Pcap + ", detail="
			    + detail);
		}

		return detail;
	}

	public <T extends FileCapture<? extends FilePacket>> T newFile(
	    final Class<T> c, final File file) throws IOException,
	    FileFormatException {

		if (c == PcapFile.class) {
			final FileCapture pcap =
			    PcapFileCapture.createFile(file, FileMode.ReadWrite,
			        ByteOrder.BIG_ENDIAN, null);
			return c.cast(pcap);
		}
		if (c == SnoopFile.class) {
			final FileCapture snoop =
			    SnoopFileCapture.createFile(file, FileMode.ReadWrite, null);
			return c.cast(snoop);
		} else {
			throw new FileFormatException("Unknown file format " + c.getName());
		}
	}

	public <T extends FileCapture<? extends FilePacket>> T newFile(
	    final Class<T> type, final File file,
	    final Capture<? extends CapturePacket> capture) throws IOException {

		if (logger.isDebugEnabled()) {
			logger.debug(file.getName() + ", type=" + type.getName()
			    + (capture == null ? "" : ", src=" + capture));
		}

		final T dst = newFile(type, file);
		append(dst, capture);

		return dst;
	}

	public FileCapture<? extends FilePacket> newFile(final FormatType type,
	    final File file) throws IOException {

		final FileCapture<? extends FilePacket> f;
		if (type == FormatType.Pcap) {
			f =
			    PcapFileCapture.createFile(file, FileMode.ReadWrite,
			        ByteOrder.BIG_ENDIAN, null);

		} else if (type == FormatType.Snoop) {
			f = SnoopFileCapture.createFile(file, FileMode.ReadWrite, null);

		} else {
			throw new IllegalArgumentException("Unknown file format type [" + type
			    + "] specified");
		}

		return f;
	}

	@SuppressWarnings("unchecked")
  public <T extends FileCapture<? extends FilePacket>> T newFile(final FormatType type,
	    final File file, Capture<? extends CapturePacket> capture)
	    throws IOException {

		if (logger.isDebugEnabled()) {
			logger.debug(file.getName() + ", type=" + type.getName()
			    + (capture == null ? "" : ", src=" + capture));
		}

		final FileCapture<? extends FilePacket> dst = newFile(type, file);

		append(dst, capture);

		return (T) dst;
	}

	public <T extends FileCapture<? extends FilePacket>> T openFile(
	    final Class<T> t, final File file) throws IOException,
	    FileFormatException {

		return openFile(t, file, FileMode.ReadOnly, null);
	}

	public <T extends FileCapture<? extends FilePacket>> T openFile(
	    final Class<T> type, final File file, FileMode mode,
	    Filter<ProtocolFilterTarget> filter) throws IOException,
	    FileFormatException {

		if (logger.isDebugEnabled()) {
			logger.debug(file.getName() + ", type=" + type.getName()
			    + (filter == null ? "" : ", filter=" + filter));
		}

		FileCapture capture;
		if (type == PcapFile.class) {
			capture = new PcapFileCapture(file, mode, null);
		} else if (type == SnoopFile.class) {
			capture = new SnoopFileCapture(file, mode, null);
		} else {
			throw new FileFormatException("Unsupported file format type, "
			    + type.getName());
		}

		return type.cast(capture);
	}

	public FileCapture<? extends FilePacket> openFile(final File file)
	    throws IOException, FileFormatException {
		return openFile(file, FileMode.ReadOnly, null);
	}

	public FileCapture<? extends FilePacket> openFile(final File file,
	    final FileMode mode, final Filter<ProtocolFilterTarget> filter)
	    throws IOException, FileFormatException {

		if (logger.isDebugEnabled()) {
			logger.debug(file.getName() + ", mode=" + mode
			    + (filter == null ? "" : ", filter=" + filter));
		}

		final FormatType type = this.formatType(file);
		FileCapture<? extends FilePacket> capture = null;

		if (type == null) {
			return null;
		}

		switch (type) {
			case Pcap:
				capture = new PcapFileCapture(file, mode, filter);
				break;
			case Snoop:
				capture = new SnoopFileCapture(file, mode, filter);
				break;
			case Nap:
				break;

			default:
				return null;
		}

		return capture;
	}

	public List<File> splitFile(final File file) throws IOException {

		final long count = countPackets(file);
		final long size = file.length();
		final long megs = size / (1024 * 1024);
		final long kilos = megs / 1024;

		if (megs > 1) {
			long packets = count / megs;
			return splitFile(file, packets, true);

		} else if (kilos > 100) {
			long packets = count / kilos;
			return splitFile(file, packets, true);

		} else {
			return splitFile(file, count, true);
		}
	}

	public List<File> splitFile(final File file, final long packetCount,
	    final boolean maxCompression) throws IOException {

		final FileCapture<? extends FilePacket> source = openFile(file);
		final FormatType type = source.getFormatType();

		final List<File> files = new ArrayList<File>(100);
		final List<Packet> packets = new ArrayList<Packet>((int) packetCount);

		int fileCount = 0;
		int count = 0; // packet count

		final PacketIterator<? extends FilePacket> i = source.getPacketIterator();
		FileCapture<? extends FilePacket> dst = null;
		while (i.hasNext()) {
			if (count % packetCount == 0 && packets.isEmpty() == false) {
				File f = new File(file.getCanonicalFile() + "-" + fileCount);
				files.add(f);
				fileCount++;

				dst = newFile(type, f);
				try {
					PacketIterator<? extends FilePacket> pi = dst.getPacketIterator();

					pi.addAll(packets);
					packets.clear();
				} finally {
					dst.close();
				}
			}

			final Packet packet = i.next();

			packets.add(packet);
			count++;
		}

		/*
		 * Don't forget the remainder
		 */
		if (packets.isEmpty() == false) {
			File f = new File(file.getCanonicalFile() + "-" + fileCount);
			files.add(f);
			fileCount++;

			dst = newFile(type, f);
			try {
				PacketIterator<? extends FilePacket> pi = dst.getPacketIterator();

				pi.addAll(packets);
				packets.clear();
			} finally {
				dst.close();
			}
		}

		source.close();

		if (logger.isTraceEnabled()) {
			logger.trace(file.getName() + ", count=" + packetCount + ", compression="
			    + maxCompression + ", files=" + files);
		}

		return files;
	}

	public boolean validateFile(final File file) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public long countPackets(File dst) throws IOException, FileFormatException {
		final FormatType type = formatType(dst);
		if (type == null) {
			throw new FileFormatException("Unrecognized file format");
		}

		final long count;

		switch (type) {
			case Other:
				final ReadableByteChannel channel =
				    new RandomAccessFile(dst, "r").getChannel();
				final InputCapture<? extends CapturePacket> input =
				    newInput(channel, null);
				count = Captures.count(input.getPacketIterator());
				input.close();
				break;

			default:
				final FileCapture capture = openFile(dst);
				count = capture.getPacketCount();
				capture.close();

				break;
		}

		return count;
	}

	public FileCapture<? extends FilePacket> openFile(File file, FileMode mode)
	    throws FileFormatException, IOException {
		return openFile(file, mode, null);
	}

}
