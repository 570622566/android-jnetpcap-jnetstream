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
package com.slytechs.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.slytechs.utils.Size;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class FileUtils {

	/**
	 * Copies the contents from src file to dst file. The dst file is removed
	 * prior to the copy if it exists.
	 * 
	 * @param src
	 *          source file to copy from
	 * @param dst
	 *          destination file to copy to
	 * @throws IOException
	 *           any IO errors
	 */
	public static void copy(File src, File dst) throws IOException {
		if (dst.exists()) {
			if (dst.delete() == false) {
				throw new IOException("Unable to delete dst file [" + dst.getName()
				    + "] before copy");

			}
		}

		if (dst.createNewFile() == false) {
			throw new FileNotFoundException("Unable to create dst file ["
			    + dst.getName() + "]");
		}

		final FileChannel srcC = new RandomAccessFile(src, "r").getChannel();

		final FileChannel dstC = new RandomAccessFile(dst, "rw").getChannel();

		srcC.transferTo(0, srcC.size(), dstC);

		srcC.close();
		dstC.close();
	}

	public static void gzip(File src, File dst) throws IOException {

		final byte[] b = new byte[(int) Size.OneMeg.bytes()];

		final InputStream in = new FileInputStream(src);

		final GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(dst));

		int length = 0;
		while ((length = in.read(b)) != -1) {
			out.write(b, 0, length);
		}

		in.close();
		out.close();
	}

	public static void gunzip(File src, File dst) throws IOException {
		final byte[] b = new byte[(int) Size.OneMeg.bytes()];

		final GZIPInputStream in = new GZIPInputStream(new FileInputStream(src));

		final OutputStream out = new FileOutputStream(dst);

		int length = 0;
		while ((length = in.read(b)) != -1) {
			out.write(b, 0, length);
		}

		in.close();
		out.close();

	}

	public static long gzipUncompressedSize(File src) throws IOException {
		FileChannel in = new RandomAccessFile(src, "r").getChannel();

		ByteBuffer b = ByteBuffer.allocate(128);
		b.order(ByteOrder.LITTLE_ENDIAN);

		in.read(b, in.size() - b.remaining());

		final long size = b.getInt(b.limit() - 4);

		in.close();

		return size;
	}

	public static long gzipUncompressedCRC32(File src) throws IOException {
		FileChannel in = new RandomAccessFile(src, "r").getChannel();

		ByteBuffer b = ByteBuffer.allocate(128);
		b.order(ByteOrder.LITTLE_ENDIAN);

		in.read(b, in.size() - b.remaining());

		final long size = b.getInt(b.limit() - 8);

		in.close();

		return (size < 0 ? ((long) Integer.MAX_VALUE )* 2L + 1 + size : size);
	}
	
	public static long gzipCalculateCRC32(File src) throws IOException {
		final byte[] b = new byte[(int) Size.OneMeg.bytes()];

		final GZIPInputStream in = new GZIPInputStream(new FileInputStream(src), 100);

		final CRC32 crc = new CRC32();
		
		int count = 0;
		int length = 0;
		while ((length = in.read(b)) != -1) {
			System.out.printf("#%d: len=%d\n", count, length);
			crc.update(b, 0, length);
			count ++;
		}

		in.close();
		
		return crc.getValue();

	}


}
