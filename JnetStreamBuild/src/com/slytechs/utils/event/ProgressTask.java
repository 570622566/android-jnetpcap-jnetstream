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

import java.util.List;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface ProgressTask {
	
	public void finish();
	
	public int getChildCount();
	
	public long getDuration();
	
	public String getName();

	/**
	 * The integer representation of the percentage the task is complete. If this
	 * task has subtask, then the percentage is a composite of all the subtasks
	 * progress.
	 * 
	 * @return valid range returned is from 0 to 100, specifying the percetange
	 *         complete
	 */
	public int getPercentageComplete();

	/**
	 * Every task can have subtasks that break up a larger job into smaller tasks.
	 * 
	 * @return
	 */
	public List<ProgressTask> getTasks();

	public long getTotal();
  
  public boolean hasSubtasks();
  
  public boolean isComplete();
  
  public boolean isReady();
  
  /**
   * @param superProgressTask
   */
  public void setParent(ProgressTask parent);

	/**
   * @param i
   */
  public void update();

  /**
   * @param i
   */
  public void update(long i);
  
  void updateComplete(ProgressTask child, long complete);
  
  void updateTotal(ProgressTask child, long total);

}
