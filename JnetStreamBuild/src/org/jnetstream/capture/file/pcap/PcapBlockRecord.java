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
package org.jnetstream.capture.file.pcap;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jnetstream.capture.file.BlockRecord;
import org.jnetstream.capture.file.Record;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.filter.Filter;
import org.jnetstream.filter.FilterException;


import com.slytechs.utils.format.NumberUtils;

/**
 * Pcap Block Record (file header). Each Pcap file contains a single block
 * record at the beginning of the file which identifies the file as Pcap type by
 * means of a magic number or pattern made up of 4 octets, and also defines some
 * global properties such as timezone, accuracy of the timestamp field within
 * each packet record, snap len and dlt.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface PcapBlockRecord extends BlockRecord, PcapRecord {

	/**
	 * Defines the static header of a block pcap record. The constants represent
	 * each individual field that makes up a Pcap block header (file header).
	 * <P>
	 * Please note that this method can easily throw invalid ClassCastException
	 * since type cast checking is supressed inorder to convert values read and
	 * written to user type. The methods are very flexible but give up the type
	 * safety, but since most types are ints, short and longs, its easy to utilize
	 * these methods.
	 * </P>
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public enum PcapBlockHeader implements Record.RecordHeaderField {
		Accuracy(12, 4 * 8),
		Linktype(20,
		    4 * 8),
		Magicnumber(0, 4 * 8) {
			@Override
			@SuppressWarnings("unchecked")
			public <T> T read(final ByteBuffer buffer, final int start) {
				final byte[] b = new byte[4];
				buffer.get(b);

				return (T) b;
			}

			@Override
			public String toStringDebug() {
				return super.name() + "@" + getOffset() + ":" + getLength() + "="
				    + NumberUtils.hexdump((byte[]) read());
			}

			@Override
			public <T> void write(final ByteBuffer buffer, final int start,
			    final T value) {
				final byte[] b = (byte[]) value;

				buffer.position(getOffset());
				buffer.put(b);

				buffer.position(getOffset());
			}
		},
		MajorVersion(4, 2 * 8) {
			@Override
			@SuppressWarnings("unchecked")
			public <T> T read(final ByteBuffer buffer, final int start) {
				Short v = buffer.getShort(start + getOffset());
				Long r = (Long) v.longValue();
				return (T) r;
			}

			@Override
			public <T> void write(final ByteBuffer buffer, final int start,
			    final T value) {
				final short i = ((Long) value).shortValue();

				buffer.putShort(start + getOffset(), i);
			}

		}, MinorVersion(6, 2 * 8) {
			@Override
			@SuppressWarnings("unchecked")
			public <T> T read(final ByteBuffer buffer, final int start) {
				Short v = buffer.getShort(start + getOffset());
				Integer r = (Integer) v.intValue();
				return (T) r;
			}

			@Override
			public <T> void write(final ByteBuffer buffer, final int start,
			    final T value) {
				final short i = ((Integer) value).shortValue();

				buffer.putShort(start + getOffset(), i);
			}
		}, Snaplen(16, 4 * 8), Timezone(8, 4 * 8), ;

		public static ByteBuffer buffer;

		public static int bufferOffset = 0;

		private final int length;

		private final int offset;

		private PcapBlockHeader(final int offset, final int length) {
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
		 * @return Returns the offset from the start of the header of the field.
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
		@SuppressWarnings("unchecked")
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
		@SuppressWarnings("unchecked")
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
			final int i = buffer.getInt(offset);
			final long v = (long) i;
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
			final long i = (Long) value;
			final int offset = start + this.offset;
			final int v = (int) i;

			buffer.putInt(offset, v);
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

	/**
	 * Predefined filter that accepts only <b>Block</b> record types. All other
	 * types of records are filtered out.
	 */
	public static final Filter<RecordFilterTarget> FILTER = new Filter<RecordFilterTarget>() {

		public boolean accept(ByteBuffer buffer, RecordFilterTarget target)
		    throws FilterException {
			
			return target == PCAPRecordType.BlockRecord;
		}

		public long execute(ByteBuffer buffer, RecordFilterTarget target)
		    throws FilterException {
			
			return (accept(buffer, target) ? HEADER_LENGTH : 0);
		}

	};

	public static final int HEADER_LENGTH = 24;

	public long getAccuracy() throws IOException;

	public long getLinktype() throws IOException;

	public byte[] getMagicPattern() throws IOException;

	public long getMajorVersion() throws IOException;

	public int getMinorVersion() throws IOException;

	public long getSnaplen() throws IOException;

	public long getTimeZone() throws IOException;

	public void setAccuracy(long accuracy) throws IOException;

	public void setLinktype(long linktype) throws IOException;

	public void setMagicPattern(byte[] magicNumber) throws IOException;

	public void setMajorVersion(long majorVersion) throws IOException;

	public void setMinorVersion(int minorVersion) throws IOException;

	public void setSnaplen(long snpalen) throws IOException;

	public void setTimezone(long timezone) throws IOException;
}
