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
package com.slytechs.capture.file;

import java.io.IOException;

import org.jnetstream.capture.CaptureDevice;
import org.jnetstream.capture.file.RawIterator;
import org.jnetstream.protocol.ProtocolEntry;
import org.jnetstream.protocol.ProtocolRegistry;

import com.slytechs.capture.file.editor.AbstractIterator;
import com.slytechs.utils.collection.SeekResult;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public abstract class AbstractPacketIterator
    extends AbstractIterator {
	
	private final CaptureDevice captureDevice;
	
	protected final ProtocolEntry dlt;

	/**
	 * @param raw
	 * @param captureDevice TODO
	 * @throws IOException
	 */
	protected AbstractPacketIterator(RawIterator raw, CaptureDevice captureDevice) throws IOException {
		super(raw);
		this.captureDevice = captureDevice;
		this.dlt = ProtocolRegistry.lookup(captureDevice.getLinkType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.capture.file.editor.AbstractIterator#seekFirst()
	 */
	@Override
	public SeekResult seekFirst() throws IOException {
		/*
		 * Packet iterators have to skip over the first record which is a block
		 * record not a packet record
		 */
		raw.seekFirst();

		if (raw.hasNext()) {
			/*
			 * TODO need more RawIterator hasNext() consistency when things are
			 * advanced or not. Filter causes the raw iterator advance to the next
			 * record automatically, unless the recordFilter is not set. Also recordFilter
			 * automatically positions on packets.
			 */
			if (raw.getFilter() == null) {
				raw.skip();
			}

			return SeekResult.Fullfilled;
		} else {
			return SeekResult.NotFullfilled;
		}
	}

	public final CaptureDevice getCaptureDevice() {
  	return this.captureDevice;
  }

}
