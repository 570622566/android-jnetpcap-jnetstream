///**
// * $Id$
// *
// * Copyright (C) 2006 Mark Bednarczyk, Sly Technologies, Inc.
// *
// * This library is free software; you can redistribute it and/or
// * modify it under the terms of the GNU Lesser General Public License
// * as published by the Free Software Foundation; either version 2.1
// * of the License, or (at your option) any later version.
// *
// * This library is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// * See the GNU Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this library; if not, write to the
// * Free Software Foundation, Inc.,
// * 59 Temple Place,
// * Suite 330, Boston,
// * MA 02111-1307 USA
// * 
// */
//package com.slytechs.utils.swing;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.swing.event.TreeModelEvent;
//import javax.swing.event.TreeModelListener;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
///**
// * 
// * @author Mark Bednarczyk
// * @author Sly Technologies, Inc.
// */
//public class TreeModelSupport {
//	
//	public static final Log logger = LogFactory.getLog(TreeModelSupport.class);
//	
//	private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
//	private final Object source;
//
//	public TreeModelSupport(Object source) {
//		this.source = source;
//	}
//	
//	public boolean isEmpty() {
//		return listeners.size() == 0;
//	}
//	
//	public int size() {
//		return listeners.size();
//	}
//	
//	public void addTreeModelListener(TreeModelListener listener) {
//		listeners.add(listener);
//		
//		logger.trace("Added TreeModel listener " + listener.toString());
//	}
//	
//	public void removeTreeModelListener(TreeModelListener listener) {
//		listeners.remove(listener);
//	}
//	
//	public void fireTreeNodesChanged(Object[] path, int[] childIndices, Object[] children) {	
//		fireTreeNodesChanged(new TreeModelEvent(source, path, childIndices, children));
//	}
//	
//	public void fireTreeNodesInserted(Object[] path, int[] childIndices, Object[] children) {
//		fireTreeNodesInserted(new TreeModelEvent(source, path, childIndices, children));
//	}
//	
//	public void fireTreeNodesRemoved(Object[] path, int[] childIndices, Object[] children) {		
//		fireTreeNodesRemoved(new TreeModelEvent(source, path, childIndices, children));
//	}
//	
//	public void fireTreeStructureChanged(Object[] path, int[] childIndices, Object[] children) {
//		fireTreeStructureChanged(new TreeModelEvent(source, path, childIndices, children));
//	}
//	
//	public void fireTreeNodesChanged(TreeModelEvent event) {
//		
//		for(TreeModelListener listener: listeners) {
//			listener.treeNodesChanged(event);
//		}		
//	}
//
//	
//	public void fireTreeNodesInserted(TreeModelEvent event) {
//
//		for(TreeModelListener listener: listeners) {
//			listener.treeNodesInserted(event);
//		}		
//	}
//	
//	public void fireTreeNodesRemoved(TreeModelEvent event) {
//
//		for(TreeModelListener listener: listeners) {
//			listener.treeNodesRemoved(event);
//		}		
//	}
//	
//	public void fireTreeStructureChanged(TreeModelEvent event) {
//
//		for(TreeModelListener listener: listeners) {
//			listener.treeStructureChanged(event);
//		}	
//	}
//
//	public TreeModelListener[] getTreeModelListeners() {
//		return listeners.toArray(new TreeModelListener[listeners.size()]);
//	}
//}
