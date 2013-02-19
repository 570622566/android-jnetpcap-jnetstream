/**
 * Copyright (C) 2007 Sly Technologies, Inc. This library is free software; you
 * can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version. This
 * library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.jnetstream.capture;

import java.sql.Timestamp;

/**
 * A packet that was sent accross a stream. It was serialized and deserialized
 * and sent using standard java.io Input and Output stream mechanism. The
 * SerialPacket interface adds methods which provide more information about the
 * transit across the stream process. Main methods of interest at the
 * {@link #getSerializedTimestamp} and {@link #getDeserializedTimestamp} which
 * allow one to calculate the transit time of the packet accross the stream.
 * Care must be taken and the clocks of both sender receiver must by
 * synchronized or accounted for. The exact sychronization mechanism is beyond
 * the scope of this API.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface DeserializedPacket extends CapturePacket {

	/**
	 * @return
	 */
	public Timestamp getDeserializedTimestamp();

	/**
	 * @return
	 */
	public Timestamp getSerializedTimestamp();

	/**
	 * Sets the time of exactly when the packet was deserialized from a stream.
	 * 
	 * @param timestamp
	 */
	public void setDeserializedTimestamp(Timestamp timestamp);

	/**
	 * Sets the timestamp of exactly when the packet was serialized.
	 * 
	 * @param timestamp
	 */
	public void setSerializedTimestamp(Timestamp timestamp);

}
