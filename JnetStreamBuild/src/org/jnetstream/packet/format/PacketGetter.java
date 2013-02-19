package org.jnetstream.packet.format;

import java.io.IOException;

import org.jnetstream.packet.Packet;

/**
 * Generic value getter from a packet.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * 
 */
public interface PacketGetter {
	public Object get(Packet packet) throws IOException;
}