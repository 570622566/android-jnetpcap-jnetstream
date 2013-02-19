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
package org.jnetstream.capture.file.snoop;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jnetstream.capture.file.DataRecord;
import org.jnetstream.capture.file.Record;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.capture.file.SeekPattern;
import org.jnetstream.filter.Filter;
import org.jnetstream.filter.FilterException;


import com.slytechs.utils.time.TimeUtils;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface SnoopPacketRecord extends DataRecord, SnoopRecord {

	public enum SnoopPacketHeader implements Record.RecordHeaderField {

		/**
		 * 
		 */
		Drops(12, 32),

		/**
		 * 
		 */
		IncludedLength(0, 32),

		/**
		 * 
		 */
		Microseconds(20, 32),

		/**
		 * 
		 */
		OriginalLength(4, 32),

		/**
		 * 
		 */
		RecordLength(8, 32),

		/**
		 * 
		 */
		Seconds(16, 32), ;
		public static ByteBuffer buffer;

		public static int bufferOffset;

		private final int length;

		private final int offset;

		private SnoopPacketHeader(final int offset, final int length) {
			this.offset = offset;
			this.length = length;
		}

		/**
		 * @return the length of the field
		 */
		protected final int getLength() {
			return this.length;
		}

		/**
		 * @return Returns the offset.
		 */
		public final int getOffset() {
			return offset;
		}

		/**
		 * Reads the header field using the currently set debug buffer
		 * {@link #buffer}. The buffer can not be null otherwise
		 * IllegalStateException is thrown.
		 * 
		 * @param <T>
		 *          the return type of the field
		 * @return value of the field as read from the debug buffer
		 */
		public <T> T read() {
			if (buffer == null) {
				throw new IllegalStateException("Static buffer not initialized");
			}
			return (T) read(buffer, bufferOffset);
		}

		/**
		 * Gets the field value from the specified buffer using the global buffer
		 * offset of 0.
		 * 
		 * @param <T>
		 *          field's value type
		 * @param buffer
		 *          buffer to read the field's value from
		 * @return field value as read from the supplied buffer
		 */
		public <T> T read(final ByteBuffer buffer) {
			return (T) read(buffer, bufferOffset);
		}

		/**
		 * Gets the field value from the specified buffer using the global buffer
		 * offset start.
		 * 
		 * @param <T>
		 *          field's value type
		 * @param buffer
		 *          buffer to read the field's value from
		 * @param start
		 *          start or offset within the buffer to offset the field's value
		 *          read
		 * @return field value as read from the supplied buffer
		 */
		@SuppressWarnings("unchecked")
		public <T> T read(final ByteBuffer buffer, final int start) {
			final int offset = start + getOffset();
			long v = buffer.getInt(offset);
			v = ((v < 0) ? (v + (((long) Integer.MAX_VALUE) + 1) * 2) : (v));
			return (T) (Long) v;
		}

		/**
		 * Returns a special debug string for the field. The returned string
		 * contains the name of the field, the field's offset within the header, the
		 * length in bits of the field and lastly uses the debug buffer with global
		 * offset of 0 to read a a value. Therefore the debug buffer must be
		 * initialized to a buffer before this method is invoked.
		 * 
		 * @return specially formatted string for debug output
		 */
		public String toStringDebug() {
			return super.name() + "@" + offset + ":" + length + "="
			    + ((Object) read()).toString();
		}

		/**
		 * Writes the field value from the specified buffer using the global buffer
		 * offset of start.
		 * 
		 * @param <T>
		 *          field's value type
		 * @param buffer
		 *          buffer to write the field's value to
		 * @param start
		 *          the global offset within the buffer that marks the start of the
		 *          header
		 * @param value
		 *          the new vlue of the field
		 */
		public <T> void write(final ByteBuffer buffer, final int start,
		    final T value) {
			final int i = (Integer) value;

			buffer.putInt(start + offset, i);
		}

		/**
		 * Writes the field value from the specified buffer using the global buffer
		 * offset of 0.
		 * 
		 * @param <T>
		 *          field's value type
		 * @param buffer
		 *          buffer to write the field's value to
		 * @param value
		 *          the new vlue of the field
		 */
		public <T> void write(final ByteBuffer buffer, final T value) {
			write(buffer, bufferOffset, value);
		}

		/**
		 * Writes the header field using the currently set debug buffer
		 * {@link #buffer}. The buffer can not be null otherwise
		 * IllegalStateException is thrown.
		 * 
		 * @param <T>
		 *          the value type of the field
		 * @param value
		 *          the new value of the field
		 */
		public <T> void write(final T value) {
			write(buffer, bufferOffset, value);
		}

	}

	public static final int FIELD_MAX_DROPS = Integer.MAX_VALUE;

	public static final int FIELD_MAX_INCLUDED = 2 * 1024;

	public static final int FIELD_MAX_MICROS = 999999;

	public static final int FIELD_MAX_ORIGINAL = 2 * 1024;

	public static final int FIELD_MAX_RECORD = 2 * 1024;

	/**
	 * Predefined filter that accepts only <b>Packet</b> record types. All other
	 * types of records are filtered out.
	 */
	public static final Filter<RecordFilterTarget> FILTER = new Filter<RecordFilterTarget>() {

		public boolean accept(ByteBuffer buffer, RecordFilterTarget target)
		    throws FilterException {

			return target == SnoopRecordType.PacketRecord;
		}

		public long execute(ByteBuffer buffer, RecordFilterTarget target)
		    throws FilterException {

			return (accept(buffer, target) ? HEADER_LENGTH : 0);
		}

	};

	public static final int HEADER_LENGTH = 24;

	/**
	 * Defines a search pattern for SNOOP packet record header. The buffer is at
	 * current ByteBuffer.position() is checked against valid values for all
	 * fields until all header fields pass their acceptable values.
	 */
	public final static SeekPattern pattern = new SeekPattern() {
		public boolean match(ByteBuffer buffer) throws IOException {

			final TimeUtils.Interval timeframe = TimeUtils.Interval.THREE_DECADES;

			final long millis = System.currentTimeMillis();
			final long seconds = millis / 1000;
			final int earliest = (int) (seconds - timeframe.seconds());
			final int latest = (int) (seconds + timeframe.seconds());

			/*
			 * Included length
			 */
			int included = buffer.getInt();
			if (included < 32 || included > FIELD_MAX_INCLUDED) {
				return false;
			}

			int original = buffer.getInt();
			if (original < 32 || original > FIELD_MAX_ORIGINAL) {
				return false;
			}

			int record = buffer.getInt();
			if (record <= 54 || record > FIELD_MAX_RECORD) {
				return false;
			}
			
			if (included > record - SnoopPacketRecord.HEADER_LENGTH) {
				return false;
			}

			int drops = buffer.getInt();
			if (drops < 0 || drops > FIELD_MAX_DROPS) {
				return false;
			}

			/*
			 * Seconds
			 */
			int field = buffer.getInt();
			if (field <= earliest || field > latest) {
				return false;
			}

			/*
			 * Micros
			 */
			field = buffer.getInt();
			if (field < 0 || field > FIELD_MAX_MICROS) {
				return false;
			}

			return true;
		}

		public int minLength() {
			return HEADER_LENGTH;
		}

	};

	public int getDrops() throws IOException;

	public long getIncludedLength() throws IOException;

	public long getMicroseconds() throws IOException;

	public long getOriginalLength() throws IOException;

	public long getSeconds() throws IOException;

	public void setDrops(int drops) throws IOException;

	public void setIncludedLength(int length) throws IOException;

	public void setMicroseconds(long micros) throws IOException;

	public void setOriginalLength(int length) throws IOException;

	public void setRecordLength(int length) throws IOException;

	public void setSeconds(long seconds) throws IOException;

}
