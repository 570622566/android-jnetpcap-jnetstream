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
package com.slytechs.file.pcap;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.jnetstream.capture.FileCapture;
import org.jnetstream.capture.file.RawIterator;
import org.jnetstream.capture.file.pcap.PcapBlockRecord;
import org.jnetstream.capture.file.pcap.PcapPacketRecord;


import com.slytechs.capture.file.editor.FileEditor;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class PcapRecordIteratorPacketOnly
    extends PcapRecordIterator<PcapPacketRecord> {

	/**
	 * @param file
	 * @param editor
	 * @param block
	 * @param raw
	 * @throws IOException
	 */
	public PcapRecordIteratorPacketOnly(FileCapture file, FileEditor editor,
	    PcapBlockRecord block, RawIterator raw) throws IOException {
		super(file, editor, block, raw);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.file.pcap.PcapRecordIterator#add(java.util.List)
	 */
	@Override
	public void addAll(List<PcapPacketRecord> elements) throws IOException {
		super.addAll(elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.file.pcap.PcapRecordIterator#add(T[])
	 */
	@Override
	public void addAll(PcapPacketRecord... elements) throws IOException {
		super.addAll(elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.file.pcap.PcapRecordIterator#add(org.jnetstream.capture.file.pcap.PcapRecord)
	 */
	@Override
	public void add(PcapPacketRecord element) throws IOException {
		super.add(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.file.pcap.PcapRecordIterator#next()
	 */
	@Override
	public PcapPacketRecord next() throws IOException {
		return super.next();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.file.pcap.PcapRecordIterator#remove(java.util.Collection)
	 */
	@Override
	public void removeAll(Collection<PcapPacketRecord> elements)
	    throws IOException {
		super.removeAll(elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.file.pcap.PcapRecordIterator#remove(T[])
	 */
	@Override
	public void removeAll(PcapPacketRecord... elements) throws IOException {
		super.removeAll(elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.file.pcap.PcapRecordIterator#replace(org.jnetstream.capture.file.pcap.PcapRecord)
	 */
	@Override
	public void replace(PcapPacketRecord element) throws IOException {
		super.replace(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.file.pcap.PcapRecordIterator#retain(java.util.List)
	 */
	@Override
	public void retainAll(List<PcapPacketRecord> elements) throws IOException {
		super.retainAll(elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.file.pcap.PcapRecordIterator#retain(T[])
	 */
	@Override
	public void retainAll(PcapPacketRecord... elements) throws IOException {
		super.retainAll(elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slytechs.file.pcap.PcapRecordIterator#swap(org.jnetstream.capture.file.pcap.PcapRecord,
	 *      org.jnetstream.capture.file.pcap.PcapRecord)
	 */
	@Override
	public void swap(PcapPacketRecord dst, PcapPacketRecord src)
	    throws IOException {
		super.swap(dst, src);
	}

}
