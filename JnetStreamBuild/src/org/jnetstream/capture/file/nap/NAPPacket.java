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
package org.jnetstream.capture.file.nap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Set;

import org.jnetstream.capture.FilePacket;



/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface NAPPacket extends FilePacket {

	public long getCaptureSeconds() throws IOException;
	public void setCaptureSeconds() throws IOException;
	
	public long getCaptureNanos() throws IOException;
	public void setCaptureNanos() throws IOException;
		
	public Timestamp getCaptureTimestamp() throws IOException;
	public void setCaptureTimestamp(Timestamp captureTimestamp) throws IOException;
	
	public int getCaptureSystemId() throws IOException;
	public void setCaptureSystemId(int id) throws IOException;
	
	public NAPCaptureInfo getCaptureSystem() throws IOException;
	public void setCaptureSystem(NAPCaptureInfo captureSystem) throws IOException;
	
	public long getOriginalLength() throws IOException;
	public void setOriginalLength() throws IOException;
	
	public long getIncludedLength() throws IOException;
	/*
	 * No setter as included length is determined by the buffer length
	 * when the buffer is set.
	 */
	
	public ByteBuffer getPacketBuffer() throws IOException;
	public void setPacketBuffer(ByteBuffer buffer) throws IOException;
	
	public Set<NAPFlag> getFlags() throws IOException; 
	public void setFlags(Set<NAPFlag> flags) throws IOException;
	
}
