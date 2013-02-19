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
//package com.slytechs.utils.statistics;
//
//import java.awt.BorderLayout;
//import java.awt.Dimension;
//import java.awt.GridLayout;
//import java.util.TimerTask;
//
//import javax.swing.BorderFactory;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTable;
//import javax.swing.border.BevelBorder;
//import javax.swing.table.AbstractTableModel;
//
///**
// * 
// * @author Mark Bednarczyk
// * @author Sly Technologies, Inc.
// */
//public class GStatistics extends DefaultStatistics {
//	
//	
//	JFrame frame;
//	private  AbstractTableModel model;
//	
//	protected GStatistics(String name) {
//		super(name);
//		
//		addTimerTask("Statistics update", UPDATE_REPEAT, new TimerTask() {
//			
//			public void run() {
////				logger.trace("Update");
//				update(UPDATE_REPEAT);
//			}
//		});
//
//		startAllTimers();
//
//
//	}
//
//	public GStatistics(String name, Statistics parent) {
//		super(name, parent);
//		// TODO Auto-generated constructor stub
//	}
//	
//	public void showFrame() {
//		
//		if (frame == null){
//			frame = createGUI();
//		}
//		
//		frame.add(createStatisticsPanel());
//		
//		frame.pack();
//		frame.setVisible(true);
//		
//	}
//	
//	public void hideFrame() {
//		if (frame != null){
//			frame.setVisible(false);
//		}
//	}
//
//	private JFrame createGUI() {
//		JFrame frame = new JFrame(getFullName());
//		
//		
//		return frame;
//	}
//	
//	private JPanel createStatisticsPanel() {
//		
//		JPanel panel = new JPanel();
//		
//		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
//		panel.setLayout(new BorderLayout());
//		
////		if(getCounters().isEmpty() == false) {	
//			panel.add(new JLabel(getFullName(), JLabel.CENTER), BorderLayout.PAGE_START);	
//			panel.add(createStatisticsTable(), BorderLayout.CENTER);
////		}
//		int columns;
//		if(getChildren().size() > 1) {
//			columns = 2;
//		} else {
//			columns = 1;
//		}
//		
//		JPanel spanel = new JPanel(new GridLayout(0, columns));
//		panel.add(spanel, BorderLayout.PAGE_END);
//		
//		for(Statistics child: getChildren().values()) {
//			GStatistics gs = (GStatistics)child;
//			
//			spanel.add(gs.createStatisticsPanel());
//		}
//		
//		return panel;
//	}
//	
//	@SuppressWarnings("serial")
//	private JScrollPane createStatisticsTable() {
//		
//		model = new AbstractTableModel() {
//			
//			private String[] columnNames = { "Counter", "Value" };
//
//			public int getRowCount() {
//				return getCounters().size();				
//			}
//
//			public int getColumnCount() {
//				return 2;
//			}
//			
//			public String getColumnName(int col){
//				return columnNames[col];
//			}
//
//			public Object getValueAt(int y, int x) {
//				if (x == 0){
//					return getCounters().get(y).getName();
//				} else {
//					Counter counter = getCounters().get(y);
//					return "(" + counter.getRatio() + " " + counter.getUnits() + "/sec) " + counter.getValue() + " " + counter.getUnits();
//				}
//
//			}
//			
//		};
//		JTable table = new JTable(model); 
//		table.setPreferredScrollableViewportSize(new Dimension(400,200));
//		
//		JScrollPane scrollPane = new JScrollPane(table);
//		
//		scrollPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
//		
//		return scrollPane;
//	}
//	
//	public void update(long dt) {
//		super.update(dt);
//
//		if (model != null) {
//			model.fireTableDataChanged();
//		}
//	}
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//	}
//
//}
