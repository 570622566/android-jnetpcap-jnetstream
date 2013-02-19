/**
 * Copyright (C) 2008 Sly Technologies, Inc. This library is free software; you
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
package com.slytechs.utils.event;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class ConsoleProgressReporter implements ProgressMonitor {

	public final static long REFRESH_RATE = 1000; // 1 sec

	private static ConsoleProgressReporter defaults;

	private long next;

	public static ConsoleProgressReporter getDefault() {
		if (defaults == null) {
			defaults = new ConsoleProgressReporter();
		}

		return defaults;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.event.ProgressMonitor#progressUpdate(com.slytechs.utils.event.ProgressTask,
	 *      com.slytechs.utils.event.ProgressTask)
	 */
	public void progressUpdate(ProgressTask parent, ProgressTask child) {
		if (parent.isComplete() == false && child.isComplete() == false
		    && checkRefresh()) {
			return;
		}

		if (parent.isReady() == false || child.isReady() == false) {
			return;
		}
		String ts = "";
		if (parent.isComplete()) {
			ts = "completed in " + (parent.getDuration() / 1000) + " seconds";
		}

		if (parent.getChildCount() == 1) {
			System.out.printf("progress: %s=%03d%% %s\n", child.getName(), child
			    .getPercentageComplete(), ts);

		} else {

			System.out.printf("progress: %s=%03d%% of %s=%03d%% %s\n", child.getName(),
			    child.getPercentageComplete(), parent.getName(), parent
			        .getPercentageComplete(), ts);
		}

		System.out.flush();
	}

	/**
	 * @return
	 */
	private boolean checkRefresh() {
		final boolean r = System.currentTimeMillis() >= next;

		if (r) {
			next = System.currentTimeMillis() + REFRESH_RATE;
		}

		return !r;
	}

}
