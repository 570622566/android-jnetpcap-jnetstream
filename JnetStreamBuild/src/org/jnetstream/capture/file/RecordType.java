package org.jnetstream.capture.file;

/**
 * Generic record types found within capture files.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum RecordType {
	/**
	 * Defines global properties for entire block of records. Also known as
	 * file header in single block record formats such as Pcap and Snoop. Other
	 * formats may contain more than one single block record within a file and
	 * thus it is not appropriate to call this type of record a file header its
	 * not strictly that alone.
	 */
	BlockRecord, 
	
	/**
	 * A data record within a block record that contains packet data. 
	 */
	PacketRecord,
	
	/**
	 * A data record within a block record that contain meta information about
	 * the block and about packet records. Certain file formats use additional
	 * records within a file to store additional or meta information besides the
	 * basic packet data.
	 */
	MetaRecord, ;
}