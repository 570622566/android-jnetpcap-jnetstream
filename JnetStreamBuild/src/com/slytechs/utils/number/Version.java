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
package com.slytechs.utils.number;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class for Version information.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class Version implements Comparable<Version> {
	private static Log logger = LogFactory.getLog(Version.class);
	
	public enum VersionDetail {
		Major,
		Minor,
		Milli,
		Micro,
		Nano,
	}
	
	private final Set<VersionDetail> detail;
	
	private int major;
	private int minor;
	private int milli;
	private int micro;
	private int nano;

	private final Map<VersionDetail, Integer> map = new EnumMap<VersionDetail, Integer>(VersionDetail.class);
	
	public Version(String expression) {
		if (expression == null) {
			throw new NullPointerException("Expected argument is null");
		}
		if (expression.trim().equals("")) {
			throw new IllegalArgumentException("Expected a version expression, received empty string");
		}
		
		String[] s = expression.trim().split("\\.");
		if (s.length == 0) {
			s = new String[] { expression };
		}
		Integer[] n = new Integer[s.length];
		
		for (int i = 0; i < s.length; i ++) {
			n[i] = Integer.valueOf(s[i]);
		}
		
		detail = EnumSet.of(VersionDetail.Major);
		
		int i = 0;
		if (i < n.length) {
			major = n[i];
			detail.add(VersionDetail.Major);
			map.put(VersionDetail.Major, n[i]);
		}
		
		if (++i < n.length) {
			minor = n[i];
			detail.add(VersionDetail.Minor);
			map.put(VersionDetail.Minor, n[i]);
		}
		
		if (++i < n.length) {
			milli = n[i];
			detail.add(VersionDetail.Milli);
			map.put(VersionDetail.Milli, n[i]);
		}
		
		if (++i < n.length) {
			micro = n[i];
			detail.add(VersionDetail.Micro);
			map.put(VersionDetail.Micro, n[i]);
		}
		
		if (++i < n.length) {
			nano = n[i];
			detail.add(VersionDetail.Nano);
			map.put(VersionDetail.Nano, n[i]);
		}
		
		if (++i < n.length) {
			throw new IllegalArgumentException("Too many version levels. Only supports 5 levels");
		}

	}
	
	/**
	 * Initialized with major version.
	 * 
	 * @param major
	 *   major version
	 */
	public Version(int major) {
		this.major = major;
		
		this.detail = EnumSet.of(VersionDetail.Major);
		
		map.put(VersionDetail.Major, major);
		
	}


	/**
	 * Initialized with major, minor version.
	 * 
	 * @param major
	 *   major version
	 *   
	 * @param minor
	 *   minor version
	 */
	public Version(int major, int minor) {
		this.major = major;
		this.minor = minor;
		
		this.detail = EnumSet.of(VersionDetail.Major, VersionDetail.Minor);
		map.put(VersionDetail.Major, major);
		map.put(VersionDetail.Minor, minor);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(Version o) {
		for (VersionDetail d: VersionDetail.values()) {
			if (detail.contains(d) && o.detail.contains(d)) {
				
				if (map.get(d).equals(o.map.get(d)) == false) {
					return o.map.get(d) - map.get(d);
				} else {
					continue;
				}
			
			} else if (o.detail.contains(d)) {
				return -1;
			} else if (detail.contains(d)) {
				return 1;
			} else {
				break;
			}
		}
		
		return 0;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Version) {
			Version o2 = (Version) o;
			
			return compareTo(o2) == 0;
		}
		
		return false;
	}

	/**
	 * @return Returns the detail.
	 */
	public Set<VersionDetail> getDetail() {
		return detail;
	}

	/**
	 * Getst the major component.
	 * 
	 * @return Returns the major.
	 */
	public int getMajor() {
		return major;
	}

	/**
	 * Sets the major component.
	 * 
	 * @param major The major to set.
	 */
	public void setMajor(int major) {
		this.major = major;
	}

	/**
	 * Getst the micro component.
	 * 
	 * @return Returns the micro.
	 */
	public int getMicro() {
		return micro;
	}

	/**
	 * Sets the micro component.
	 * 
	 * @param micro The micro to set.
	 */
	public void setMicro(int micro) {
		this.micro = micro;
	}

	/**
	 * Gets the milli component.
	 * 
	 * @return Returns the milli.
	 */
	public int getMilli() {
		return milli;
	}

	/**
	 * Sets the milli component.
	 * 
	 * @param milli The milli to set.
	 */
	public void setMilli(int milli) {
		this.milli = milli;
	}

	/**
	 * Gets the minor component.
	 * 
	 * @return Returns the minor.
	 */
	public int getMinor() {
		return minor;
	}

	/**
	 * Sets the minor component.
	 * 
	 * @param minor The minor to set.
	 */
	public void setMinor(int minor) {
		this.minor = minor;
	}

	/**
	 * Sets the nano component.
	 * 
	 * @return Returns the nano.
	 */
	public int getNano() {
		return nano;
	}

	/**
	 * @param nano The nano to set.
	 */
	public void setNano(int nano) {
		this.nano = nano;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		String separator = "";
		for(VersionDetail d: VersionDetail.values()) {
			if (detail.contains(d)) {
				buf.append(separator);
				buf.append(map.get(d));
				
				separator = ".";
			}
		}
		
		return buf.toString();
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			logger.error("Usage: version expression");
			return;
		}
		
		Version version = new Version(args[0]);
		Version v2 = new Version("1.1.5");
		
		System.out.println(version.toString());
		
		System.out.println(version.toString() + " <=> " + v2 + " = " + version.compareTo(v2));
	}

}
