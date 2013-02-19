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
package com.slytechs.packet.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jnetstream.packet.format.FoldState;
import org.jnetstream.packet.format.FormatString;


import com.slytechs.utils.namespace.Named;
import com.slytechs.utils.namespace.Path;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public class FormatConfig<E> implements Named {

	public static final String ROOT = "root";
	
	private String name;
	private FormatConfig<E> parent; // Root
	private List<FormatConfig<E>> children;
	private FoldState foldState;
	private List<E> options;
	private Map<String, FormatString> formatStrings;
	
	public FormatConfig() {
		parent = null;
		name = ROOT;
		
		children = new ArrayList<FormatConfig<E>>(5);
		foldState = FoldState.Expanded;
		options = new ArrayList<E>(20);
		formatStrings = new HashMap<String, FormatString>();
	}
	
	
	public FormatConfig(FormatConfig<E> parent, String name) {
		this.parent = parent;
		this.name = name;
	}

	/**
   * @return the parent
   */
  public final FormatConfig<E> getParent() {
  	return this.parent;
  }
  
  public final FormatConfig<E> findMost(Path path) {
  	return findMost(path, 0);
  }
  
  public final FormatConfig<E> findMost(Path path, int index) {
  	if (index != 0 && index >= path.size()) {
  		return null;
  	}
  	
  	if (path.equals(index, name)) {
  		for (FormatConfig<E> n: children) {
  			FormatConfig<E> r = 	n.findMost(path, index +1);
  			if (r != null) {
  				return r;
  			}
  		}
  	}
  	
  	return this;
  }

	/**
   * @param parent the parent to set
   */
  public final void setParent(FormatConfig<E> parent) {
  	this.parent = parent;
  }

	/**
   * @return the children
   */
  public final List<FormatConfig<E>> getChildren() {
  	return this.children;
  }

	/**
   * @param children the children to set
   */
  public final void setChildren(List<FormatConfig<E>> children) {
  	this.children = children;
  }

	/**
   * @return the foldState
   */
  public final FoldState getFoldState() {
  	
  	if (foldState == null) {
  		return parent.getFoldState();
  	}
  	
  	return this.foldState;
  }

	/**
   * @param foldState the foldState to set
   */
  public final void setFoldState(FoldState foldState) {
  	this.foldState = foldState;
  }


	/**
   * @return the formatStrings
   */
  public final Map<String, FormatString> getFormatStrings() {
  	
  	if (formatStrings == null) {
  		return getParent().getFormatStrings();
  	}
  	
  	return this.formatStrings;
  }

	/**
   * @param formatStrings the formatStrings to set
   */
  public final void setFormatStrings(Map<String, FormatString> formatStrings) {
  	this.formatStrings = formatStrings;
  }

	/**
   * @return the options
   */
  public final List<E> getOptions() {
  	
  	if (options == null) {
  		return getParent().getOptions();
  	}
  	return this.options;
  }

	/**
   * @param options the options to set
   */
  public final void setOptions(List<E> options) {
  	this.options = options;
  }
  
  public final void setOptions(E ... options) {
  	
  	this.options.clear();
  	
  	for (E e: options) {
  		this.options.add(e);
  	}
  	
  }

	/* (non-Javadoc)
   * @see com.slytechs.utils.namespace.Named#getName()
   */
  public String getName() {
	  return name;
  }
	

}
