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

package org.jnetstream.capture.file.pcap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Pcap file DLT value map. The values are mapped to jNetStream's ProtocolConst
 * enum constants that have jNetStream's definitions for the specified headers.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public class PcapDataEncapsulation {
	
	private interface DataEncapsulation {
		
	}
    
    private static Map<Integer, DataEncapsulation> map;

    static {
	Map<Integer, DataEncapsulation> map = new HashMap<Integer, DataEncapsulation>();
	
//	map.put(0, DataEncapsulation.NULL_BSD);
//	map.put(1, DataEncapsulation.EN10MB);
//	map.put(2, DataEncapsulation.EN3MB);
//	map.put(3, DataEncapsulation.AX25);
//	map.put(4, DataEncapsulation.PRONET);
//	map.put(5, DataEncapsulation.CHAOS);
//	map.put(6, DataEncapsulation.IEEE802);
//	map.put(7, DataEncapsulation.ARCNET);
//	map.put(8, DataEncapsulation.SLIP);
//	map.put(9, DataEncapsulation.PPP);
//	
//	map.put(10, DataEncapsulation.FDDI);
//	map.put(11, DataEncapsulation.ATM_RFC1483);
//	map.put(12, DataEncapsulation.RAW);
//	map.put(13, DataEncapsulation.SLIP);
//	map.put(14, DataEncapsulation.PPP);
////	map.put(15, ProtocolConst.CHAOS);
////	map.put(16, ProtocolConst.IEEE802);
////	map.put(17, ProtocolConst.ARCNET);
////	map.put(18, ProtocolConst.SLIP);
//	map.put(19, DataEncapsulation.ATM_RFC1483);
//
//	map.put(104, DataEncapsulation.HDLC);
//	map.put(105, DataEncapsulation.IEEE802_11);
//	map.put(106, DataEncapsulation.RAW);
//	map.put(107, DataEncapsulation.FRELAY);
//	map.put(108, DataEncapsulation.LOOP);
//	map.put(109, DataEncapsulation.ENC);
//	
//	map.put(113, DataEncapsulation.LINUX_SLL);
//	map.put(114, DataEncapsulation.LTALK);
//	map.put(115, DataEncapsulation.ECONET);
//	map.put(116, DataEncapsulation.IPFILTER);
//	map.put(117, DataEncapsulation.PFLOG);
//	map.put(118, DataEncapsulation.CISCO_IOS);
//	map.put(119, DataEncapsulation.PRISM_HEADER);
//	
//	map.put(120, DataEncapsulation.AIRONET_HEADER);
//	map.put(121, DataEncapsulation.SIEMENS_HIPATH_HDLC);
//	map.put(122, DataEncapsulation.IP_OVER_FIBERCHANNEL);
//	map.put(123, DataEncapsulation.SUN_ATM);
//	map.put(124, DataEncapsulation.RAPID_IO);
//	map.put(125, DataEncapsulation.PCI_EXPRESS);
//	map.put(126, DataEncapsulation.XILINX_AURORA_LINK);
//	map.put(127, DataEncapsulation.IEEE802_11_RADIO);
//	map.put(128, DataEncapsulation.TAZMEN_SNIFFER_PROTOCOL);
//	map.put(129, DataEncapsulation.ARCNET);
//
//	map.put(130, DataEncapsulation.JUNIPER_MLPPP);
//	map.put(131, DataEncapsulation.JUNIPER_MLFR);
//	map.put(132, DataEncapsulation.JUNIPER_ES);
//	map.put(133, DataEncapsulation.JUNIPER_GGSN);
//	map.put(134, DataEncapsulation.JUNIPER_MFR);
//	map.put(135, DataEncapsulation.JUNIPER_ATM2);
//	map.put(136, DataEncapsulation.JUNIPER_SERVICES);
//	map.put(137, DataEncapsulation.JUNIPER_ATM1);
//	map.put(138, DataEncapsulation.APPLE_IP_OVER_IEEE1394);
//	map.put(139, DataEncapsulation.AIRONET_HEADER);
//	
////	map.put(140, ProtocolConst.SIEMENS_HIPATH_HDLC);
////	map.put(141, ProtocolConst.IP_OVER_FIBERCHANNEL);
////	map.put(142, ProtocolConst.AIRONET_HEADER);
//	map.put(143, DataEncapsulation.DOCSIS);
//	map.put(144, DataEncapsulation.LINUX_IRDA);
//	map.put(145, DataEncapsulation.IBM_SP);
//	map.put(146, DataEncapsulation.IBM_SN);
//	
//	map.put(163, DataEncapsulation.IEEE802_11_RADIO_AVS);
//	map.put(164, DataEncapsulation.JUNIPER_MONITOR);
	
	PcapDataEncapsulation.map = Collections.unmodifiableMap(map);
    }

    /**
     * Returns the mappings between PCAP DLT values and jNetStream ProtocolConst
     * cosntants. The map is immutable and none of the mutable methods will work. The
     * mappings are standard and can not be modified by the user.
     * 
     * @return the map
     *   map containing DLT keys to constant values
     * 
     */
    public static Map<Integer, DataEncapsulation> getMap() {
        return map;
    }

}
