package org.jnetstream.packet.format;

/**
 * Default supplied format types for formatting packet contents for textual
 * output.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * 
 */
public enum FormatterType {
	/**
	 * Textual formatter that formats packet content for plain output. The full
	 * verbose output displays:
	 * <UL>
	 * <LI> Packet summary line
	 * <LI> First column of output contains the current header name
	 * <LI> Second column contains nicely aligned field names and values of all
	 * the fields within a header
	 * <LI> If a field has a subfield, that subfields are output to a 3rd column
	 * that aligns the subfield output below the summary line of the parent
	 * field.
	 * <LI> ... and so on for each header, field and subfield
	 * </UL>
	 */
	Plain,

	/**
	 * Textual formatter that formats packet content for Html output. The full
	 * verbose output displays:
	 * <UL>
	 * <LI> Packet summary line
	 * <LI> First column of output contains the current header name
	 * <LI> Second column contains nicely aligned field names and values of all
	 * the fields within a header
	 * <LI> If a field has a subfield, that subfields are output to a 3rd column
	 * that aligns the subfield output below the summary line of the parent
	 * field.
	 * <LI> ... and so on for each header, field and subfield
	 * </UL>
	 * 
	 * In addition, each column, names and values are HTML tagged along with
	 * additional CSS classes and IDs.
	 */
	// Html,
	/**
	 * Textual formatter that formats the packet content for Xml output. The XML
	 * output contains XML elements that describe the complete structure of the
	 * packet, headers and fields within it. The output also contains any
	 * defined properties that packet main contain.
	 */
	// Xml,
	/**
	 * Textual formatter that formats the packet content for hierarchal tree
	 * based output. The output of the packet is less aligned then Plain format
	 * in coluns, but each level of the packet hierarchy is displayed as a
	 * collapsable tree.
	 * 
	 * <PRE> + Packet summary line + Header1 summary line + Field1 of header1
	 * summary or name = value pair + Field2 of header1 summary or name = value
	 * pair + Field3 of header1 summary or name = value pair + SubField1 of
	 * field3 summary or name = value pair + SubField2 of field3 summary or name =
	 * value pair + Header2 summary line + Field1 of header2 summary or name =
	 * value pair + Property1 of header2 name = value pair + Property1 of packet
	 * name = value pair + Property2 of packet name = value pair
	 * 
	 * </PRE>
	 * 
	 * 
	 */
	// Tree("com.slytechs.packet.TreePacketFormat"),
	/*
	 * Done
	 */
	;



}