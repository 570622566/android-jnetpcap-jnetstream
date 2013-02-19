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

import java.util.Locale;

/**
 * Interface for diagnostics from tools. A diagnostic usually reports a problem
 * at a specific position in a source file. However, not all diagnostics are
 * associated with a position or a file. A position is a zero-based character
 * offset from the beginning of a file. Negative values (except NOPOS) are not
 * valid positions. Line and column numbers begin at 1. Negative values (except
 * NOPOS) and 0 are not valid line or column numbers.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Diagnostic<T> {

	/**
	 * Used to signal that no position is available.
	 */
	public static long NOPOS = -1;

	/**
	 * Kinds of diagnostics, for example, error or warning.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public enum Kind {

		/**
		 * Problem which prevents the tool's normal completion.
		 */
		ERROR,

		/**
		 * Problem similar to a warning, but is mandated by the tool's
		 * specification. For example, the Java™ Language Specification, 3rd Ed.
		 * mandates warnings on certain unchecked operations and the use of
		 * deprecated methods.
		 */
		MANDATORY_WARNING,

		/**
		 * Informative message from the tool.
		 */
		NOTE,

		/**
		 * Diagnostic which does not fit within the other kinds.
		 */
		OTHER,

		/**
		 * Problem which does not usually prevent the tool from completing normally.
		 */
		WARNING

	}

	/**
	 * Gets a diagnostic code indicating the type of diagnostic. The code is
	 * implementation-dependent and might be null.
	 * 
	 * @return a diagnostic code
	 */
	public String getCode();

	/**
	 * Gets a localized message for the given locale. The actual message is
	 * implementation-dependent. If the locale is null use the default locale.
	 * 
	 * @param locale
	 *          a locale; might be null
	 * @return a localized message
	 */
	public String getMessage(Locale locale);

	/**
	 * Gets a character offset from the beginning of the source object associated
	 * with this diagnostic that indicates the location of the problem. In
	 * addition, the following must be true:
	 * 
	 * <pre>
	 * getStartPosition() &lt;= getPosition()
	 * 
	 * getPosition() &lt;= getEndPosition()
	 * </pre>
	 * 
	 * @return
	 */
	public long getPosition();

	/**
	 * Gets the column number of the character offset returned by
	 * {@link #getPosition()}.
	 * 
	 * @return character offset from beginning of source; {@link #NOPOS} if
	 *         {@link #getSource()} would return null or if no location is
	 *         suitable
	 */
	public long getColumnNumber();

	/**
	 * Gets the character offset from the beginning of the file associated with
	 * this diagnostic that indicates the end of the problem.
	 * 
	 * @return offset from beginning of file; {@link #NOPOS} if and only if
	 *         {@link #getPosition()} returns {@link #NOPOS}
	 */
	public long getEndPosition();

	/**
	 * Gets the character offset from the beginning of the file associated with
	 * this diagnostic that indicates the start of the problem.
	 * 
	 * @return offset from beginning of file; {@link #NOPOS} if and only if
	 *         {@link #getPosition()} returns {@link #NOPOS}
	 */
	public long getStartPosition();

	/**
	 * Gets the kind of this diagnostic, for example, error or warning.
	 * 
	 * @return the kind of this diagnostic
	 */
	public Diagnostic.Kind getKind();

	/**
	 * Gets the line number of the character offset returned by getPosition().
	 * 
	 * @return a line number or {@link #NOPOS} if and only if
	 *         {@link #getPosition()} returns {@link #NOPOS}
	 */
	public long getLineNumber();

}
