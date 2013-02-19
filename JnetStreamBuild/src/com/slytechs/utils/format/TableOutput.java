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
package com.slytechs.utils.format;

import java.io.Flushable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * 
 */
public class TableOutput implements Appendable, Flushable {
	private final Appendable out;

	private List<ColumnOutput> columns = new LinkedList<ColumnOutput>();

	private int position = 0;
	
	private ColumnOutput column;

	/**
	 * Takes a simple appendable and adapts it for table type output. The default
	 * is to create a single column of output.
	 * 
	 * @param out
	 *          simple appendable that will be adapted to table type output
	 * 
	 */
	public TableOutput(Appendable out) {
		this(out, 1);
	}

	/**
	 * Takes a simple appendable and adapts it for table type output.
	 * 
	 * @param out
	 *          simple appendable that will be adapted to table type output
	 * 
	 * @param count
	 *          number of columns to create within the table output
	 */
	public TableOutput(Appendable out, int count) {
		this.out = out;
		setColumnCount(count);
		position = 0;
		column = columns.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.format.TableOutput#getColumn(int)
	 */
	public ColumnOutput getColumn(int index) {
		return columns.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.format.TableOutput#getPosition()
	 */
	public int getPosition() {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.format.TableOutput#hasNextColumn()
	 */
	public boolean hasNextColumn() {
		return position < columns.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.format.TableOutput#next()
	 */
	public void nextColumn() throws IOException {
		if (position + 1 >= columns.size()) {
			throw new IllegalStateException(
			    "Please call hasNext() method to verify the presence of further columns");
		}
		setPosition(position +1);
	}

	/**
	 * Advances the row position within the current column to the next row. That
	 * is the output is displayed one line below the previous line but within the
	 * same column.
	 * 
	 * @see com.slytechs.utils.format.TableOutput#nextRow()
	 */
	public void nextRow() throws IOException {
		column.flush();
		out.append("\n");
		printPadToColumn(position);
	}

	/**
	 * Advances the row position to the next row while changing the column to
	 * specified. The output is displayed one line below the previous line
	 * starting from the specified column position.
	 * 
	 * @param firstColumn
	 *          index of the first column where the new row output will start
	 * 
	 * @see com.slytechs.utils.format.TableOutput#nextRow(int)
	 */
	public void nextRow(int index) throws IOException {
		column.flush();
		out.append('\n');
		position = 0;
		
		position = index;
		
		printPadToColumn(index);
		
	}
	
	private void printPadToColumn(int index) throws IOException {
		for (int i = 0; i < index; i ++) {
			getColumn(i).appendPad();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.format.TableOutput#replaceColumn(com.slytechs.utils.format.ColumnOutput,
	 *      int)
	 */
	public void replaceColumn(ColumnOutput column, int index) {
		columns.remove(index);
		columns.add(column);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.format.TableOutput#setColumnCount(int)
	 */
	public void setColumnCount(int count) {
		if (count < 1) {
			throw new IllegalArgumentException("Column count can not be less then 0");
		}
		position = 0;
		if (count < columns.size()) {
			for (int i = 0; i < columns.size() - count; i++) {
				columns.remove(columns.size() - 1);
			}
		}

		for (int i = columns.size(); i < count; i++) {
			ColumnOutput c = new ColumnOutput(out, i);
			columns.add(c);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.format.TableOutput#setPosition(int)
	 */
	public TableOutput setPosition(final int index) throws IOException {
		final int previous = position;
		position = index;
		
		for (int i = previous; i < index; i ++) {
			getColumn(i).flush();
		}

		column = getColumn(index);

		return this;
	}

	public ColumnOutput getColumn() throws IOException {

		return getColumn(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.format.FormatOutput#append(java.lang.String)
	 */
	public Appendable append(String string) throws IOException {
		getColumn().append(string);

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.format.FormatOutput#append(java.lang.CharSequence)
	 */
	public Appendable append(CharSequence chars) throws IOException {
		getColumn().append(chars);

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.utils.format.FormatOutput#append(char)
	 */
	public Appendable append(char c) throws IOException {
		getColumn().append(c);

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Appendable#append(java.lang.CharSequence, int, int)
	 */
	public Appendable append(CharSequence csq, int start, int end)
	    throws IOException {
		getColumn().append(csq, start, end);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Flushable#flush()
	 */
	public void flush() throws IOException {
		getColumn().flush();
	}

	public void setPad() throws IOException {
		getColumn().setPad();
	}

	/**
   * @param i
   */
  public void setSpanColumns(int count) {

  	int newWidth = 0;
	  for (int i = position; i <= position + count; i ++) {
	  	newWidth += getColumn(i).getWidth();
	  }
	  
	  column.setOverrideWidth(newWidth);
  }

	/**
   * 
   */
  public void clearSpanColumns() {
 		column.clearOverrideWidth();
  }

	/**
   * @return the out
   */
  public final Appendable getOut() {
  	return this.out;
  }
}
