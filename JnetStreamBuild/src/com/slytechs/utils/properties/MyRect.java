//package com.slytechs.utils.properties;
//
//import java.awt.Dimension;
//import java.awt.Point;
//import java.awt.Rectangle;
//
//public class MyRect extends Rectangle {
//
//	/**
//   * 
//   */
//  private static final long serialVersionUID = -4535886376496531701L;
//
//	public MyRect() {
//		super();
//		// TODO Auto-generated constructor stub
//	}
//
//	public MyRect(Rectangle arg0) {
//		super(arg0);
//		// TODO Auto-generated constructor stub
//	}
//
//	public MyRect(int arg0, int arg1, int arg2, int arg3) {
//		super(arg0, arg1, arg2, arg3);
//		// TODO Auto-generated constructor stub
//	}
//	
//	
//	public static MyRect valueOf(String exp) {
//		String c[] = exp.split(",");
//		
//		return new MyRect(
//				Integer.valueOf(c[0]),
//				Integer.valueOf(c[1]),
//				Integer.valueOf(c[2]),
//				Integer.valueOf(c[3]));
//	}
//
//	public MyRect(int arg0, int arg1) {
//		super(arg0, arg1);
//		// TODO Auto-generated constructor stub
//	}
//
//	public MyRect(Point arg0, Dimension arg1) {
//		super(arg0, arg1);
//		// TODO Auto-generated constructor stub
//	}
//
//	public MyRect(Point arg0) {
//		super(arg0);
//		// TODO Auto-generated constructor stub
//	}
//
//	public MyRect(Dimension arg0) {
//		super(arg0);
//		// TODO Auto-generated constructor stub
//	}
//	
//	public String toString() {
//		StringBuilder buf = new StringBuilder();
//	
//		buf.append(this.x).append(',');
//		buf.append(this.y).append(',');
//		buf.append(this.width).append(',');
//		buf.append(this.height);
//		
//		return buf.toString();
//	}
//}
