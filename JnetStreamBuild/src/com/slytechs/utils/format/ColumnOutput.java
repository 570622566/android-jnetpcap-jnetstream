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

import java.io.IOException;

import com.slytechs.utils.format.Formattable.Justification;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public class ColumnOutput {
	
	public static final int UnlimitedWidth = 80;

	private int width = UnlimitedWidth;
	
	private CharSequence pad = "";
	
	private Justification justification = Justification.Left;
	
	private StringBuilder b = new StringBuilder(1024);
	
	private final Appendable out;

	private int index;

	private int overrideWidth = -2;
	
	public ColumnOutput(Appendable out, int index) {
		this.out = out;
		this.index = index;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.format.ColumnOutput#getWidth()
	 */
	public int getWidth() {
		if (overrideWidth != -2) {
			return overrideWidth;
		}
		
		return width;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.format.ColumnOutput#setJustification(com.slytechs.utils.format.Formattable.Justification)
	 */
	public void setJustification(Justification justification) {
		this.justification = justification;

	}
	
	/**
	 * Sets the pad to be the current contents of the display buffer.
	 * 
	 * @return
	 *   this column output 
	 * @throws IOException 
	 */
	public ColumnOutput setPad() throws IOException {
		
		StringBuilder p = new StringBuilder((getWidth() == UnlimitedWidth)?b.length():getWidth());
		sendToOutput(p);
		pad = p.toString().subSequence(0, p.length());
		
		return this;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.format.ColumnOutput#setPad(char)
	 */
	public ColumnOutput setPad(char c, int size) {
		StringBuilder b = new StringBuilder(size);
	
		for (int i = 0; i < size; i ++) {
			b.append(c);
		}
		
		this.pad = b.subSequence(0, size);
		
		return this;

	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.format.ColumnOutput#setPad(java.lang.String)
	 */
	public ColumnOutput setPad(String pad) {
		this.pad = pad.subSequence(0, pad.length());
		
		return this;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.format.ColumnOutput#setPad(java.lang.CharSequence)
	 */
	public ColumnOutput setPad(CharSequence pad) {
		this.pad = pad;
		
		return this;
	}
	
	public void clearPad() {
		this.pad = null;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.format.ColumnOutput#setWidth(int)
	 */
	public int setWidth(int width) {
		int old = width;
		this.width = width;
		setPad(' ', width);
		
		return old;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.format.FormatOutput#append(java.lang.CharSequence)
	 */
  ColumnOutput append(CharSequence chars) throws IOException {
		return append(chars, 0, chars.length());
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.format.FormatOutput#append(char)
	 */
	ColumnOutput append(char c) throws IOException {
		if (c == '\n') {
			nextRow();
		}
		
		if (getWidth() == UnlimitedWidth || b.length() < getWidth()) {
			b.append(c);
		} else {
			
			switch (justification) {
				case Left:
					break;
					
				case Right:
					b.deleteCharAt(0);
					b.append(c);
					break;
					
				case Center:
					break;
			}
		}
		
		return this;
	}
	
	void flush() throws IOException {
		sendToOutput(this.out);
		b.setLength(0);
	}
	
	private void sendToOutput(Appendable out) throws IOException {
		int jw = getWidth() - b.length();
		Justification j = justification;
		
//		if (jw < 0) {
//			j = Justification.None;
//		}
		
		switch (j) {
			case None:
				out.append(b.subSequence(0, b.length()));
				break;
				
		case Right:
			for (int i = 0; i < jw; i ++) {
				out.append(getPadChar(i));
			}
			
			out.append(b.subSequence(0, b.length()));

			break;
			
		case Left:
			out.append(b.subSequence(0, b.length()));
			for (int i = 0; i < jw; i ++) {
				out.append(getPadChar(i));
			}
			break;

		case Center:
			for (int i = 0; i < jw/2; i ++) {
				out.append(getPadChar(i));
			}
			out.append(b.subSequence(0, b.length()));
			for (int i = 0; i < jw/2; i ++) {
				out.append(getPadChar(i));
			}

			break;
			
			default:
				throw new IllegalStateException("Unknown justification state");
		}

	}
	
	private char getPadChar(int index) {
		if (index < pad.length() && false) {
			return pad.charAt(index);
		} else {
			return ' ';
		}
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.format.ColumnOutput#nextRow()
	 */
	void nextRow() throws IOException {
		flush();
		out.append('\n');
		appendPad();

	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.format.FormatOutput#append(java.lang.String)
	 */
	ColumnOutput append(String string) throws IOException {
		append(string.subSequence(0, string.length()));
		
		return this;
	}

	/* (non-Javadoc)
	 * @see com.slytechs.utils.format.ColumnOutput#appendPad()
	 */
	ColumnOutput appendPad() throws IOException {
		out.append(pad);
		
		return this;
	}

	/* (non-Javadoc)
	 * @see java.lang.Appendable#append(java.lang.CharSequence, int, int)
	 */
	ColumnOutput append(CharSequence chars, int start, int end) throws IOException {
		for (int i = start; i < end; i ++) {
			append(chars.charAt(i));
		}
		return this;
	}

	/**
   * @return the index
   */
  final int getIndex() {
  	return this.index;
  }

	/**
   * @param index the index to set
   */
  final void setIndex(int index) {
  	this.index = index;
  }

	/**
   * 
   */
  public void clearOverrideWidth() {
	  this.overrideWidth = -2;
	  
  }

	/**
   * @param newWidth
   */
  public void setOverrideWidth(int newWidth) {
  	this.overrideWidth = newWidth;
	  
  }

}
