/**
 * Copyright (C) 2007 Sly Technologies, Inc.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jnetstream.capture.file.nap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.List;

import org.jnetstream.capture.CapturePacket;
import org.jnetstream.capture.FileFormatException;
import org.jnetstream.capture.file.Record;


import com.slytechs.utils.collection.IOList;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class NAPModel   {
	
	/**
	 * A pattern used to identify the "NAP" block record is
	 * in NAP format. This is a 28-bit unsigned integer field.
	 */
	public static final long MAGIC_NUMBER = 0x2AA3BB4;
	
	/**
	 * The pattern used to identify by the first 4 octets of the
	 * block header. This value contains the Record Type field + 
	 * the Magic Number field for a convenient 32-bit value.
	 */
	public static final long FULL_MAGIC_NUMBER = 0x12AA3BB4;
	
	/**
	 * A byte pattern used to identify the "NAP" block record as NAP
	 * format. This is a 28-bit masked byte array.
	 */
	public static final byte[] MAGIC_PATTERN = {0x02, (byte) 0xAA, 0x3b, (byte) 0xb4};
	
	/**
	 * The pattern used to identify by the first 4 octets of the
	 * block header. This value contains the Record Type field + 
	 * the Magic Number field for a convenient 4 byte pattern array.
	 * Full 32-bit mask.
	 */
	public static final byte[] FULL_MAGIC_PATTERN = {0x12, (byte) 0xAA, 0x3b, (byte) 0xb4};
	
	/**
	 * Default blocking factor for BlockRecords
	 */
	public static final long BLOCKING_FACTOR = 512 * 1024;
	
	/**
	 * Default padding boundary. That is all records of all types must be padded
	 * upto 64-bit or 8 byte boundary
	 */
	public static final int PADDING_BOUNDARY = 8;
	
	/**
	 * Mandatory byte encoding for all record headers and meta record contents.
	 * PacketRecord and Vendor record contents may contain data that is native to
	 * the OS it was stored in, and is outside of the NAP specification.
	 */
	public static final ByteOrder MANDATORY_ENCODING = ByteOrder.BIG_ENDIAN;
	
	public static NAPModel create(File file) throws IOException {
		return null;
	}
	
	public abstract FileChannel getChannel();

	
	public abstract List<Record> getAllRecords();
	
	public abstract IOList<NAPBlockRecord> getBlockRecords();
	
	public abstract List<? extends CapturePacket> getPacketRecords();
	
	public abstract NAPBlockRecord createBlockRecord();
	
	public abstract NAPPacket createPacketRecord();
	
	public abstract NAPBinding createBindingMetaRecord();
	
	public abstract NAPProtocol createProtocolMetaRecord();
	
	public abstract NAPNoOp createNoOpRecord();
	
	public abstract NAPRouting createRoutingMetaRecord();
	
	public abstract NAPCounter createCounterMetaRecord();
	
	public abstract NAPEvent createEventMetaRecord();
	
	public abstract NAPExpert createExpertMetaRecord();
	
	public abstract Record readRecord(Record parent) throws IOException, FileFormatException;

	/**
	 * @return
	 */
	public abstract RandomAccessFile getFile();

	/**
	 * @return
	 */
	public abstract String getFilename();
	
}
