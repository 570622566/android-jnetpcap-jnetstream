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
package com.slytechs.file.snoop;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jnetstream.capture.file.HeaderReader;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.capture.file.snoop.SnoopBlockRecord;
import org.jnetstream.capture.file.snoop.SnoopFile;
import org.jnetstream.capture.file.snoop.SnoopPacketRecord;
import org.jnetstream.filter.Filter;


import com.slytechs.capture.file.editor.AbstractRawIterator;
import com.slytechs.capture.file.editor.PartialLoader;
import com.slytechs.utils.collection.SeekResult;
import com.slytechs.utils.io.AutoflushMonitor;
import com.slytechs.utils.region.FlexRegion;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class SnoopRawIterator
    extends AbstractRawIterator {

	private final SnoopPacketRecord.SnoopPacketHeader SECONDS = SnoopPacketRecord.SnoopPacketHeader.Seconds;

	private final SnoopPacketRecord.SnoopPacketHeader MICROS = SnoopPacketRecord.SnoopPacketHeader.Microseconds;


	/**
	 * 
	 * @param edits
	 * @param lengthGetter
	 * @param autoflush
	 * @param closeable
	 * @throws IOException
	 */
	public SnoopRawIterator(FlexRegion<PartialLoader> edits, HeaderReader lengthGetter,
	    AutoflushMonitor autoflush, Closeable closeable) throws IOException {
		super(edits, SnoopFile.headerReader, autoflush, closeable, null);

		super.pattern = SnoopPacketRecord.pattern;
	}
	
	public SnoopRawIterator(FlexRegion<PartialLoader> edits, AutoflushMonitor autoflush,
	    Closeable closeable, Filter<RecordFilterTarget> filter) throws IOException {
		super(edits, SnoopFile.headerReader, autoflush, closeable, filter);

		super.pattern = SnoopPacketRecord.pattern;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jnetstream.capture.file.FileIterator#seek(long, long)
	 */
	public SeekResult seek(final long seconds, final long nanos)
	    throws IOException {
		// Convert nanos to micros since PCAP only supports micros
		final long micros = nanos / 1000;

		// Calculate the start of the first record as a percentage
		final double start = (double) SnoopBlockRecord.HEADER_LENGTH
		    / edits.getLength();

		/*
		 * Now search for the record starting at the first record and ending at the
		 * last record
		 */
		return seek(seconds, micros, start, 1. - start);
	}

	private SeekResult seek(final long seconds, final long micros, double start,
	    double length) throws IOException {
		ByteBuffer buffer;
		long s, m;

		seek(start);

		/*
		 * If the search area, the length as a percentage is smaller then typical
		 * SEARCH_LENGTH, then we simply iterate over packets until we find what we
		 * are looking for
		 */
		final long lengthInBytes = (long) ((double) edits.getLength() * length);
		if (lengthInBytes <= SEARCH_LENGTH) {
			return seekByIteration(seconds, micros);
		}

		/*
		 * Otherwise, We divide the file into 2 half sections. Then we check which
		 * half our timestamp should reside and we recursively call on ourselves to
		 * subdivide each of the halfs until we narrow in on the right record
		 */
		if (hasNext() == false) {
			throw new IllegalStateException(
			    "Exhausted all records while searching for timestamp");
		} else {
			buffer = next();
			s = (Long) SECONDS.read(buffer, buffer.position());
			m = (Long) MICROS.read(buffer, buffer.position());

		}

		// Check if we are not already passed it
		if (seconds < s || (seconds == s && micros < m)) {
			seek(start);
			return SeekResult.Fullfilled;
		}

		final double half = start + length / 2.;

		seek(half);

		if (hasNext() == false) {
			throw new IllegalStateException(
			    "Exhausted all records while searching for timestamp");
		} else {
			buffer = next();
			s = (Long) SECONDS.read(buffer, buffer.position());
			m = (Long) MICROS.read(buffer, buffer.position());
		}

		// Check if we are not already passed it
		if (seconds < s || (seconds == s && micros < m)) {
			return seek(seconds, micros, start, length / 2.);
		} else {
			return seek(seconds, micros, half, length / 2.);
		}
	}

	/**
	 * @param seconds
	 * @param micros
	 * @return
	 * @throws IOException
	 */
	private SeekResult seekByIteration(long seconds, long micros)
	    throws IOException {

		long p = getPosition();
		long s, m;

		while (hasNext()) {
			p = getPosition();
			ByteBuffer buffer = next();

			s = (Long) SECONDS.read(buffer, buffer.position());
			m = (Long) MICROS.read(buffer, buffer.position());

			if (s > seconds || (s == seconds && m >= micros)) {
				setPosition(p);

				return SeekResult.Fullfilled;
			}
		}

		/*
		 * If we go passed the end record, we simply align on the last record. That
		 * is if the timestamp is way in the future, past any timestamp in the file,
		 * then we simply use the last/oldest record as the closest one to the
		 * timestamp.
		 */

		setPosition(p);

		return SeekResult.Fullfilled;
	}

	/* (non-Javadoc)
   * @see com.slytechs.capture.file.editor.AbstractRawIterator#getRecordHeaderLength(java.nio.ByteBuffer)
   */
  @Override
  protected int getRecordHeaderLength(ByteBuffer buffer) {
	  return SnoopPacketRecord.HEADER_LENGTH;
  }

}
