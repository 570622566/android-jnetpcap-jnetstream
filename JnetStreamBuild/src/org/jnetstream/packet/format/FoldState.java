package org.jnetstream.packet.format;

/**
 * Determines an elements folding state. Certain elements with packets,
 * headers and fields may contain sub elements when displayed may either be
 * displayed as Expanded or Collapsed.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * 
 */
public enum FoldState {
	Expanded, Collapsed
}