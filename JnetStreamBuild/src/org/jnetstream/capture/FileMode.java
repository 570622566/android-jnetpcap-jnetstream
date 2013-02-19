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

/**
 * <p>
 * Defines a file mode for either open or new files created. The file mode is
 * only usable with file captures and no other type. There are a number of file
 * modes available with some default behaviours defined under each mode
 * constant. Typical usage is as follows:
 * 
 * <pre>
 * FileCapture capture;
 * capture = Captures.openFile(new File(&quot;temp.pcap&quot;), FileMode.ReadOnly);
 * capture.close();
 * </pre>
 * 
 * or
 * 
 * <pre>
 * FileCapture capture;
 * capture = Captures.newFile(new File(FormatType.Pcap, &quot;temp.pcap&quot;, FileMode.ReadWrite);
 * capture.close();
 * </pre>
 * 
 * For convenience both of these modes are available with no FileMode version of
 * each of the above 2 methods
 * {@link org.jnetstream.capture.Captures#openFile(java.io.File)} and
 * {@link org.jnetstream.capture.Captures#newFile(FormatType, java.io.File) and
 * other variations. You can of course specify any of the constants defined
 * below in order to open the file in another mode.
 * </p>
 * <p>
 * Another important feature of the FileMode selection is the associated memory
 * model with the file mode. The critical aspect is the usage of memory mapped
 * files. All modern operating systems provide a very efficient means of
 * accessing contents of a file by mapping its contents directly to memory. This
 * is typically done in hardware by a memory controller, which provides the
 * contents of a file as if they were normal RAM memory. This is a very low
 * level function that is typically much more efficient then tranditional read
 * data into a user provided buffer. Some of the constants below provide
 * different options of when to use the map function of the kernel.
 * </p>
 * <h2>Warning about mapped files</h2>
 * <p>
 * There is a known caviat with using any of the modes that map file contents
 * directly to memory using an OS call "map". Sun Microsystems has an
 * outstanding bug (Sun Bug ID# <A
 * HREF="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4724038">4724038</A>)
 * which prevents files from being truncated, renamed or deleted after a map
 * function has been used on a file. The bug more specifically, does not allow a
 * previously mapped file to be unmapped which keeps the file handle open in the
 * OS and file contents locked, even after a <code>close</code> call in java.
 * It may take some time for the file contents to become unmapped and until
 * then, the above mentioned file operations will fail. Therefore the default
 * readonly mode, assumes that since the file is being opened in readonly mode
 * by the user, that no immediate file modifications will take place upon the
 * file. Eventually though, the file will be cleared of any mappings and the
 * file operations will succeed. The amount of time needed for this to happen is
 * undefined, even when all references to any mapped file buffers are released
 * by the user, i.e. references through packets or records, directly to buffers
 * as returned by {org.jnetstream.packet.Packet#getBuffer()} call. Therefore the
 * user is advised to choose a different readonly mode that explicitely does not
 * use file mapping function if some kind of file manipulation will be required
 * relatively soon after the FileCapture is to be closed. No file manipulation
 * is possible when file capture is open wheather mapped or not.
 * </p>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum FileMode {
	/**
	 * <p>
	 * File and file editor is opened in read only mode. All mutable operations
	 * are disabled and the buffer operated on and returned to the user is read
	 * only as well.
	 * </p>
	 * <p>
	 * In readonly mode, file contents are fetched using 3 different algorithms:
	 * <ul>
	 * <li> Into a byte[] backed ByteBuffer - for small file sizes, around a few
	 * kilo bytes, a simple byte array backed ByteBuffer is created. The data is
	 * read into the buffer. The buffer is cached and returned as readonly and no
	 * further modifications are possible.</li>
	 * <li> Into a direct ByteBuffer - for larger files, upto 10 mega bytes, a
	 * direct byte buffer is created which is slower to instantiate but provides
	 * native level access speed to its contents. The OS is instructed to read the
	 * file contents into the direct buffer which is all done outside of java
	 * space at native speeds.</li>
	 * <li> File content is mapped to memory - for even larger files, larger then
	 * 10 mega bytes, the file is mapped to memory using a kernel call "map". The
	 * mapping itself is much slower then allocating the memory in the above 2
	 * steps, but once the file is mapped access to it is increadibly fast. So
	 * this is the reason why mapping the file only makes sense for very large
	 * files. It may not be possible to map the entire file to memory all at once,
	 * and the file is mapped in large chunks. Each mapped buffer is cached and
	 * returned readonly to the user.</li>
	 * </ul>
	 * Each of these algorithms is applied accrodingly to each request for a
	 * portion of a file. That is if only 100 bytes were left to be read in at the
	 * end of a multi-giga byte file, the byte[] algorithm would be chosen to read
	 * in those bytes, instead of memory mapping them.
	 * </p>
	 * <h2>Warning about mapped files</h2>
	 * <p>
	 * There is a known caviat with this mode. Sun Microsystems has an outstanding
	 * bug (Sun Bug ID# <A
	 * HREF="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4724038">4724038</A>)
	 * which prevents files from being truncated, renamed or deleted after a map
	 * function has been used on a file. The bug more specifically, does not allow
	 * a previously mapped file to be unmapped which keeps the file handle open in
	 * the OS and file contents locked, even after a <code>close</code> call in
	 * java. It may take some time for the file contents to become unmapped and
	 * until then, the above mentioned file operations will fail. Therefore the
	 * default readonly mode, assumes that since the file is being opened in
	 * readonly mode by the user, that no immediate file modifications will take
	 * place upon the file. Eventually though, the file will be cleared of any
	 * mappings and the file operations will succeed. The amount of time needed
	 * for this to happen is undefined, even when all references to any mapped
	 * file buffers are released by the user, i.e. references through packets or
	 * records, directly to buffers as returned by
	 * {org.jnetstream.packet.Packet#getBuffer()} call. Therefore the user is advised
	 * to choose another mode that explicitely does not use file mapping function
	 * if some kind of file manipulation will be required relatively soon after
	 * the FileCapture is to be closed. No file manipulation is possible when file
	 * capture is open wheather mapped or not.
	 * </p>
	 */
	ReadOnly(true, false, false, false),

	/**
	 * <p>
	 * File and file editor is opened in read only mode without using memory map
	 * OS function. All mutable operations are disabled and the buffer operated on
	 * and returned to the user is read only as well. In this mode the file
	 * mapping to memory OS function is not used and this mode does not suffer any
	 * of the file locking or manipulation issues described in the main
	 * description.
	 * </p>
	 * <p>
	 * In readonly mode, file contents are fetched using 2 different algorithms:
	 * <ul>
	 * <li> Into a byte[] backed ByteBuffer - for small file sizes, around a few
	 * kilo bytes, a simple byte array backed ByteBuffer is created. The data is
	 * read into the buffer. The buffer is cached and returned as readonly and no
	 * further modifications are possible.</li>
	 * <li> Into a direct ByteBuffer - for larger files, upto any size, a direct
	 * byte buffer is created which is slower to instantiate but provides native
	 * level access speed to its contents. The OS is instructed to read the file
	 * contents into the direct buffer which is all done outside of java space at
	 * native speeds.</li>
	 * </ul>
	 * Each of these algorithms is applied accrodingly to each request for a
	 * portion of a file. That is if only 100 bytes were left to be read in at the
	 * end of a multi-giga byte file, the byte[] algorithm would be chosen to read
	 * in those bytes, instead of allocating a direct buffer for them.
	 * </p>
	 */

	ReadOnlyNoMap(false, false, false, false),

	/**
	 * <p>
	 * The file is opened in read-write mode and no memory map functions are
	 * utilized. All mutable operations are allowed in this mode. All changes have
	 * to be flushed by a call to <code>flush()</code>, <code>close()</code>
	 * or flushed automatically by the autoflush mechanism. No memory map
	 * functions are used to map file contents to memory for direct manipulation
	 * in this default mode. Therefore this mode does not suffer any of the file
	 * manipulation issues as described in the description.
	 * </p>
	 * <p>
	 * In read-write mode, file contents are fetched using 2 different algorithms:
	 * <ul>
	 * <li> Into a byte[] backed ByteBuffer - for small file sizes, around a few
	 * kilo bytes, a simple byte array backed ByteBuffer is created. The data is
	 * read into the buffer.</li>
	 * <li> Into a direct ByteBuffer - for larger files, upto any size, a direct
	 * byte buffer is created which is slower to instantiate but provides native
	 * level access speed to its contents. The OS is instructed to read the file
	 * contents into the direct buffer which is all done outside of java space at
	 * native speeds.</li>
	 * </ul>
	 * On the initial read, the buffer is returned by the API as readonly. An
	 * immediate call to <code>getBuffer</code> on any of the returned packets
	 * or records the buffer returned there, are readonly. The first call to any
	 * of the mutable methods in the API with autoedit enabled will convert the
	 * buffer to read-write and mark the buffer as dirty so that it may eventually
	 * be sunchronized with the physical file using a <code>flush</code> or a
	 * <code>close</code> call. You may also use the
	 * {@link org.jnetstream.packet.Packet#edit()} or
	 * {@link jnetstrea.capture.file.Record#edit()} calls to manually put the
	 * buffer in read-write mode.
	 * </p>
	 * <p>
	 * Note that in this mode, any changes that have been made to any of the
	 * records or file structure (i.e. removed records or packets, insert packets,
	 * etc..) can be undone if they have not been flushed yet. Since with
	 * autoflush enabled it may not be possible to determine or prevent edits from
	 * being flushed unexpectidely, you may need to set the autoflush mode with
	 * {@link org.jnetstream.capture.FileCapture#setAutoflush} method to false. This
	 * will require the user to manually call flush every so often, since all the
	 * changes are kept in memory and eventually the system may run out of memory.
	 * </p>
	 */
	ReadWrite(false, true, true, true),

	/**
	 * File is opened for read and write operations while utilizing the very
	 * efficient file memory mapping feature, but no operations which result in
	 * structural changes such as add, remove, swap, retain, etc. Any change that
	 * does not result in structural change is allowed. The limitation comes from
	 * the fact that once a structural change has been made to the record within
	 * the capture file it may be neccessary to perform a function on the physical
	 * file which is not allowed when used with file memory mapping. Please read
	 * section in the main description about the map limitation.
	 */
	ReadWriteWithMap(true, true, false, false),

	/**
	 * <p>
	 * File is opened for general read and write operations, which have portions
	 * of the file memory mapped and with the limitation that only structural
	 * change peration that is permitted is <code>append</code> or add at the
	 * end of the file. Operations such as remove, retain, swap or replace are not
	 * allowed. The buffers returned are read write and allow to be modified. Any
	 * modifications to buffer is synchronized with the file immediately and no
	 * flush is required. A flush is required when data has been appended to the
	 * file. Initialy those changes are kept in memory but will be synched with
	 * the physical file by a <code>flush</code> or <code>close</code>
	 * operation.
	 * </p>
	 * <p>
	 * Since portions of the file are memory mapped, it may not be possible to do
	 * certain operations on the main file, even after the FileCapture has been
	 * closed. Please read the section in the main description about the map
	 * limitation.
	 * </p>
	 */
	Append(true, true, false, true),

	/**
	 * File is opened for general read and no write operations and only structural
	 * change peration that is permitted is <code>append</code> or add at the
	 * end of the file. Operations such as remove, retain, swap or replace are not
	 * allowed. The buffers returned are readonly and are not allowed to be
	 * modified. A flush is required when data has been appended to the file.
	 * Initialy those changes are kept in memory but will be synched with the
	 * physical file by a <code>flush</code>, <code>close</code> operation or
	 * autoflush if enabled.
	 * </p>
	 */
	AppendNoMap(false, false, false, true), ;

	private final boolean map;

	private final boolean content;

	private final boolean structure;

	private final boolean append;

	/**
	 * @param map
	 *          are maps permitted
	 * @param content
	 *          is content modification permitted
	 * @param structure
	 *          is structure modification permitted in general
	 * @param append
	 *          is the only structure modification permitted append
	 */
	private FileMode(boolean map, boolean content, boolean structure,
	    boolean append) {
		this.map = map;
		this.content = content;
		this.structure = structure;
		this.append = append;

	}

	/**
	 * Checks if append structural operation is permitted. Append operation is an
	 * add operation that is done at the end of the file. No other operation,
	 * qualifies as an append operation.
	 * 
	 * @return true means that append operation is permitted, otherwise false
	 */
	public final boolean isAppend() {
		return this.append;
	}

	/**
	 * Checks if the file content is allowed to be modified, non structuraly. The
	 * content may be modified as long as the record's structure remains the same
	 * length.
	 * 
	 * @return true means the content can be modified otherwise false
	 */
	public final boolean isContent() {
		return this.content;
	}

	/**
	 * Checks if capture file is memory mapped for efficiency purposes. This flag
	 * in conjuction with the flag {@link #isContent()} determines if the memory
	 * map will be READ_ONLY or READ_WRITE. If content is not modifiable the
	 * memory map will be READ_ONLY resulting in READ_ONLY ByteBuffers.
	 * 
	 * @return true means that file contents will be memory mapped otherwise
	 *         memory map function will never be used
	 */
	public final boolean isMap() {
		return this.map;
	}

	/**
	 * Determines if record structure can be modified. Record structure is
	 * modified by methods such as add, remove, retain, swap or replace.
	 * 
	 * @return if true, any of the structure modification operations are
	 *         permitted, otherwise false
	 */
	public final boolean isStructure() {
		return this.structure;
	}

}
