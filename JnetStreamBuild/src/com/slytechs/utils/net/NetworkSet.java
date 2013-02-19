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

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * <P>IpNetwork specific implementation of a SortedSet. All the normal SortedSet and Set 
 * methods are implemented with the addition of new method cidrSet(). Unlike its cusins 
 * headSet(), subSet(), tailSet(), cidrSet() is not a view or set backed by the original set
 * but its a complete copy. Any changes to the set returned from cidrSet() are not reflected in the
 * original set.</P>
 * 
 * <P>The main feature of this collection class is that it adds the cidrSet(IpNetwork toElement) method. This method allows
 * a user to get a CIDR collection of networks found in the set that the toElement belongs to. That is you can check to see
 * if specified IpAddress (a IpNetwork object with netmask of 32 bits) has any super class networks within the collection.
 * All the entries upto 0.0.0.0/0 if present are returned in the resultant set.</P>
 * 
 * @param <K> IpNetwork subclass to be used as the key for this set.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class NetworkSet<K extends IpNetwork> extends TreeSet<K> {

	private static final long serialVersionUID = 8264277535529036452L;

	

	/**
	 * 
	 */
	public NetworkSet() {
		super();
	}



	/**
	 * @param c
	 */
	public NetworkSet(Collection<? extends K> c) {
		super(c);
	}



	/**
	 * @param c
	 */
	public NetworkSet(Comparator<? super K> c) {
		super(c);
	}



	/**
	 * @param s
	 */
	public NetworkSet(SortedSet<K> s) {
		super(s);
	}



	/**
	 * <P>Pulls out all the CIDR supernets of the supplied network. The set returned
	 * is a copy not linked to the original set of elements that are
	 * hierachally organized upto the root of the CIDR tree of addresses.<P>
	 * 
	 * <P>Example:
	 * <PRE>
	 * NetworkSet&lt;IpNetwork&gt; netSet = new NetworkSet&lt;IpNetwork&gt;();
	 * netSet.add(new IpNetwork("192.168.1.1", 32));
	 * netSet.add(new IpNetwork("192.168.2.1", 32));
	 * netSet.add(new IpNetwork("0.0.0.0", 0));
	 * netSet.add(new IpNetwork(192.168.2.0", 24));
	 * 
	 * System.out.println(netSet.rootSet(new IpNetwork("192.168.2.1", 32)));
	 * </PRE>
	 * Will produce output:
	 * <PRE>
	 * [0.0.0.0/0, 192.168.2.0/24, 192.168.2.1/32]
	 * </PRE>
	 * 
	 * @param toElement network element to start from.
	 * @return A set of networks that are VLSM compatible or part of the the
	 * user requested toElement.
	 */
	public SortedSet<K> cidrSet(K toElement) {
		SortedSet<K> inplace = super.headSet(toElement);
		
		SortedSet<K> copy = new TreeSet<K>();
		
		for(K net: inplace) {
			if (net.isPartOf(toElement)) {
				copy.add(net);
			}
		}
		
		if (contains(toElement)) {
			copy.add(toElement);
		}
		
		return copy;
	}

}
