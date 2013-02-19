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
package com.slytechs.tools;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

/**
 * File abstraction for tools. In this context, file means an abstraction of
 * regular files and other sources of data. For example, a file object can be
 * used to represent regular files, memory cache, or data in databases. All
 * methods in this interface might throw a SecurityException if a security
 * exception occurs. Unless explicitly allowed, all methods in this interface
 * might throw a NullPointerException if given a null argument.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface FileObject {

	/**
	 * Deletes this file object. In case of errors, returns false.
	 * 
	 * @return true if and only if this file object is successfully deleted; false
	 *         otherwise
	 */
	public boolean delete();

	/**
	 * Gets the character content of this file object, if available. Any byte that
	 * cannot be decoded will be replaced by the default translation character. In
	 * addition, a diagnostic may be reported unless ignoreEncodingErrors is true.
	 * 
	 * @param ignoreEncodingErrors
	 *          ignore encoding errors if true
	 * @return a CharSequence if available; null otherwise
	 * @throws IllegalStateException
	 *           if this file object was opened for writing and does not support
	 *           reading
	 * @throws UnsupportedOperationException
	 *           if this kind of file object does not support character access
	 *           IOException - if an I/O error occurred
	 */
	public CharSequence getCharContent(boolean ignoreEncodingErrors);

	/**
	 * Gets the time this file object was last modified. The time is measured in
	 * milliseconds since the epoch (00:00:00 GMT, January 1, 1970).
	 * 
	 * @return the time this file object was last modified; or 0 if the file
	 *         object does not exist, if an I/O error occurred, or if the
	 *         operation is not supported
	 */
	public long getLastModified();

	/**
	 * Gets a user-friendly name for this file object. The exact value returned is
	 * not specified but implementations should take care to preserve names as
	 * given by the user. For example, if the user writes the filename "<code>BobsApp\Test.java</code>"
	 * on the command line, this method should return "<code>BobsApp\Test.java</code>"
	 * whereas the {@link #toUri} method might return
	 * <code>file:///C:/Documents%20and%20Settings/UncleBob/BobsApp/Test.java</code>.
	 * 
	 * @return a user-friendly name
	 */
	public String getName();

	/**
	 * Gets an InputStream for this file object.
	 * 
	 * @return an InputStream
	 * @throws IllegalStateException
	 *           if this file object was opened for writing and does not support
	 *           reading
	 * @throws UnsupportedOperationException
	 *           if this kind of file object does not support byte access
	 * @throws IOException
	 *           if an I/O error occurred
	 */
	public InputStream openInputStream();

	/**
	 * Gets an OutputStream for this file object.
	 * 
	 * @return an OutputStream
	 * @throws IllegalStateException
	 *           if this file object was opened for reading and does not support
	 *           writing
	 * @throws UnsupportedOperationException
	 *           if this kind of file object does not support byte access
	 * @throws IOException
	 *           if an I/O error occurred
	 */
	public OutputStream openOutputStream();

	/**
	 * Gets a reader for this object. The returned reader will replace bytes that
	 * cannot be decoded with the default translation character. In addition, the
	 * reader may report a diagnostic unless ignoreEncodingErrors is true.
	 * 
	 * @param ignoreEncodingErrors
	 *          ignore encoding errors if true
	 * @return a Reader
	 * @throws IllegalStateException
	 *           if this file object was opened for writing and does not support
	 *           reading
	 * @throws UnsupportedOperationException
	 *           if this kind of file object does not support character access
	 * @throws IOException
	 *           if an I/O error occurred
	 * @param ignoreEncodingErrors
	 */
	public Reader openRead(boolean ignoreEncodingErrors);

	/**
	 * Gets a Writer for this file object.
	 * 
	 * @return a Writer
	 * @throws IllegalStateException
	 *           if this file object was opened for reading and does not support
	 *           writing
	 * @throws UnsupportedOperationException
	 *           if this kind of file object does not support character access
	 * @throws IOException
	 *           if an I/O error occurred
	 */
	public Writer openWriter();

	/**
	 * Returns a URI identifying this file object.
	 * 
	 * @return a URI
	 */
	public URI toUri();
}
