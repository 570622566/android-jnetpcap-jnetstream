/**
 * $Id$
 *
 * Copyright (C) 2006 Mark Bednarczyk, Sly Technologies, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place,
 * Suite 330, Boston,
 * MA 02111-1307 USA
 * 
 */
package com.slytechs.utils.net;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <P>IpNetwork specific implementation of a SortedMap. All the normal SortedMap and Map 
 * methods are implemented with the addition of new method cidrMap(). Unlike its cusins 
 * headMap(), subMap(), tailMap(), cidrMap() is not a view or set backed by the original map
 * but its a complete copy. Any changes to the set returned from cidrMap() are not reflected in the
 * original map.</P>
 * 
 * <P>The main feature of this collection class is that it adds the cidrMap(IpNetwork toElement) method. This method allows
 * a user to get a CIDR map of networks found in the set that the toElement belongs to. That is you can check to see
 * if specified IpAddress (a IpNetwork object with netmask of 32 bits) has any super class networks within the map.
 * All the entries upto 0.0.0.0/0 if present are returned in the resultant set.</P>
 *
 * @param <K> Any IpNetwork subclasses to be used as a key into hierachal map.
 * @param <V> Value type to be stored in the map.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class NetworkMap<K extends IpNetwork, V> extends TreeMap<K, V>  {

	
	private static final long serialVersionUID = 1759638196866569749L;

	/**
	 * 
	 */
	public NetworkMap() {
		super();
	}

	/**
	 * @param c
	 */
	public NetworkMap(Comparator<? super K> c) {
		super(c);
	}

	/**
	 * @param m
	 */
	public NetworkMap(Map<? extends K, ? extends V> m) {
		super(m);
	}

	/**
	 * @param m
	 */
	public NetworkMap(SortedMap<K, ? extends V> m) {
		super(m);
	}

	/**
	 * <P>Pulls out all the CIDR supernets of the supplied network. The Map returned
	 * is a copy not linked to the original Map of elements that are
	 * hierachally organized upto the root of the CIDR tree of addresses.<P>
	 * 
	 * <P>Example:
	 * <PRE>
	 * NetworkMap%lt;IpNetwork&gt; netMap = new NetworkMap&lt;IpNetwork&gt;();
	 * netMap.add(new IpNetwork("192.168.1.1", 32));
	 * netMap.add(new IpNetwork("192.168.2.1", 32));
	 * netMap.add(new IpNetwork("0.0.0.0", 0));
	 * netMap.add(new IpNetwork(192.168.2.0", 24));
	 * 
	 * System.out.println(netMap.rootMap(new IpNetwork("192.168.2.1", 32)));
	 * </PRE>
	 * Will produce output:
	 * <PRE>
	 * {0.0.0.0/0=global, 192.168.2.0/24=engineering, 192.168.2.1/32=private}
	 * </PRE>
	 * 
	 * @param toElement network element to start from.
	 * @return A Map of networks that are VLSM compatible or part of the the
	 * user requested toElement.
	 */

	public SortedMap<K, V> cidrMap(K toKey) {
		SortedMap<K, V> inplace = super.headMap(toKey);
		SortedMap<K, V> copy = new TreeMap<K, V>();
		
		for(Map.Entry<K, V> e: inplace.entrySet()) {
			if (e.getKey().isPartOf(toKey)) {
				copy.put(e.getKey(), e.getValue());
			}
		}
		
		if (containsKey(toKey)) {
			copy.put(toKey, get(toKey));
		}
		
		return copy;
	}
}
