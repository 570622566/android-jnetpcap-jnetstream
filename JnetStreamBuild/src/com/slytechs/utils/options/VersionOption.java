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
package com.slytechs.utils.options;

import com.slytechs.utils.number.Version;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class VersionOption
    extends Version implements Option {

	public static final VersionOption Ver1 = new VersionOption(1);
	public static final VersionOption Ver1d1 = new VersionOption(1, 1);
	public static final VersionOption Ver1d2 = new VersionOption(1, 2);
	public static final VersionOption Ver1d3 = new VersionOption(1, 3);
	public static final VersionOption Ver1d4 = new VersionOption(1, 4);
	public static final VersionOption Ver1d5 = new VersionOption(1, 5);
	public static final VersionOption Ver2 = new VersionOption(2);
	public static final VersionOption Ver2d1 = new VersionOption(2, 1);
	public static final VersionOption Ver2d2 = new VersionOption(2, 2);
	public static final VersionOption Ver2d3 = new VersionOption(2, 3);
	public static final VersionOption Ver2d4 = new VersionOption(2, 4);
	public static final VersionOption Ver2d5 = new VersionOption(2, 5);
	public static final VersionOption Ver3 = new VersionOption(2, 6);

	/**
	 * @param expression
	 */
	public VersionOption(String expression) {
		super(expression);
	}

	/**
	 * @param major
	 */
	public VersionOption(int major) {
		super(major);
	}

	/**
	 * @param major
	 * @param minor
	 */
	public VersionOption(int major, int minor) {
		super(major, minor);
	}

}
