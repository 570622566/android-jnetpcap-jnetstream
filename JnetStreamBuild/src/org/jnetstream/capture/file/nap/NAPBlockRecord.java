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
package org.jnetstream.capture.file.nap;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;

import org.jnetstream.capture.file.BlockRecord;
import org.jnetstream.capture.file.DataRecord;
import org.jnetstream.capture.file.RecordIterator;


import com.slytechs.utils.number.Version;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface NAPBlockRecord extends BlockRecord {

	public static int HDR_MAGIC_NUMBER = 0;

	public static int HDR_MAJOR_VERSION = 8;

	public static int HDR_MINOR_VERSION = 9;

	public static int HDR_FLAGS = 10;

	public static int HDR_BLOCK_ID = 12;

	public static int HDR_NEXT_BLOCK_ID = 16;

	public static int HDR_PACKET_COUNT = 20;

	/**
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public enum Flag {
		PacketCount(0x0001), SecurityCode(0x0010), Scramble(0x0020), FullEncryption(
		    0x0040), ;

		private final int flag;

		private Flag(int flag) {
			this.flag = flag;
		}

		public static EnumSet<Flag> toEnumSet(int flags) {
			EnumSet<Flag> set = EnumSet.noneOf(Flag.class);

			for (Flag f : values()) {
				if ((f.flag & flags) != 0) {
					set.add(f);
				}
			}

			return set;
		}

		/**
		 * @param blockRecordFlags
		 */
		public static int toIntFlag(Set<Flag> blockRecordFlags) {

			int flags = 0;
			for (Flag f : blockRecordFlags) {
				flags |= f.flag;
			}

			return flags;
		}
	}

	public long getBlockId();

	public void setBlockId(long blockId);

	public long getNextBlockId();

	public void setNextBlockId(long id);

	public Version getVersion();

	public void setVersion(Version version);

	public boolean validateMagicNumber();

	/**
	 * Mutable set of flags. By modifying the flags in this set you enable/disable
	 * certain features for the entire block.
	 * 
	 * @return mutable set of block flags
	 */
	public Set<Flag> getFlags();

	public long getPacketCount();

	public void setPacketCount(int count);

	public RecordIterator<? extends DataRecord> getRecordIterator()
	    throws IOException;

	/**
	 * Retrieves the NAP properties record and converts it standard
	 * {@link java.util.Properties} object.
	 * 
	 * @return properties that were set/defined within this block
	 * @throws IOException
	 *           any IO errors
	 */
	public Properties getProperties() throws IOException;
}
