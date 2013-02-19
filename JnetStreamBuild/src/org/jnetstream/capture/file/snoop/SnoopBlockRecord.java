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

import org.jnetstream.capture.file.BlockRecord;
import org.jnetstream.capture.file.Record;
import org.jnetstream.capture.file.RecordFilterTarget;
import org.jnetstream.filter.Filter;
import org.jnetstream.filter.FilterException;


import com.slytechs.utils.format.NumberUtils;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface SnoopBlockRecord extends BlockRecord, SnoopRecord {

	public enum SnoopBlockHeader implements Record.RecordHeaderField {

		/**
		 * 
		 */
		Linktype(12, 2 * 8),

		/**
		 * 
		 */
		Magicnumber(0, 8 * 8) {
			@Override
			@SuppressWarnings("unchecked")
			public <T> T read(final ByteBuffer buffer, final int start) {
				final byte[] b = new byte[8];
				buffer.get(b);

				return (T) b;
			}

			@Override
			public String toStringDebug() {
				return super.name() + "@" + this.getOffset() + ":" + this.getLength()
				    + "=" + NumberUtils.hexdump((byte[]) this.read());
			}

			@Override
			public <T> void write(final ByteBuffer buffer, final int start,
			    final T value) {
				final byte[] b = (byte[]) value;

				buffer.position(this.getOffset());
				buffer.put(b);

				buffer.position(this.getOffset());
			}
		},

		/**
		 * 
		 */
		MajorVersion(8, 2 * 8),

		;
		public static ByteBuffer buffer;

		public static int bufferOffset = 0;

		private final int length;

		private final int offset;

		private SnoopBlockHeader(final int offset, final int length) {
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
			return this.offset;
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
			if (SnoopBlockHeader.buffer == null) {
				throw new IllegalStateException("Static buffer not initialized");
			}
			return (T) this.read(SnoopBlockHeader.buffer, SnoopBlockHeader.bufferOffset);
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
			return (T) this.read(buffer, SnoopBlockHeader.bufferOffset);
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
			final int offset = start + this.getOffset();
			final int i = buffer.getInt(offset);
			final long v = i;
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
			return super.name() + "@" + this.offset + ":" + this.length + "="
			    + ((Object) this.read()).toString();
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
			this.write(buffer, SnoopBlockHeader.bufferOffset, value);
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
			this.write(SnoopBlockHeader.buffer, SnoopBlockHeader.bufferOffset, value);
		}
	}

	/**
	 * Predefined filter that accepts only <b>Block</b> record types. All other
	 * types of records are filtered out.
	 */
	public static final Filter<RecordFilterTarget> FILTER = new Filter<RecordFilterTarget>() {

		public boolean accept(ByteBuffer buffer, RecordFilterTarget target)
		    throws FilterException {

			return target == SnoopRecordType.BlockRecord;
		}

		public long execute(ByteBuffer buffer, RecordFilterTarget target)
		    throws FilterException {

			return (this.accept(buffer, target) ? SnoopBlockRecord.HEADER_LENGTH : 0);
		}

	};

	public static final int HEADER_LENGTH = 16;

	public static final byte[] MAGIC_PATTERN = { 0x73, 0x6E, 0x6F, 0x6F, 0x70,
	    0x00, 0x00, 0x00 };

	public static final long MAJOR_VERSION = 2;

	public long getLinktype() throws IOException;

	public byte[] getMagicNumber() throws IOException;

	public long getMajorVersion() throws IOException;

	public void setLinktype(long linktype) throws IOException;

	public void setMagicNumber(byte[] magic) throws IOException;

	public void setMajorVersion(long version) throws IOException;
}
