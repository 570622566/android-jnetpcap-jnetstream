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
package com.slytechs.utils;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 *
 */
public enum Size {
	Nible(4, true),
	Byte(1),
	Octet(1),
	Word(2),
	Long(8),
	Integer(4),
	
	One(1),
	Two(2),
	Three(3),
	Four(4),
	Five(5),
	Six(6),
	Seven(7),
	Eight(8),
	Nine(9),
	Ten(10),
	Dozen(12),
	TwoDozen(24),
	
	Kilo(1024),
	OneKilo(Kilo.bytes()),
	TwoKilo(2 * OneKilo.bytes()),
	ThreeKilo(3 * OneKilo.bytes()),
	FourKilo(4 * OneKilo.bytes()),
	EightKilo(8 * OneKilo.bytes()),
	SixteenKilo(16 * OneKilo.bytes()),
	TwentyKilo(20 * OneKilo.bytes()),
	SixyFourKilo(64 * OneKilo.bytes()),
	ThirtyKilo(30 * OneKilo.bytes()),
	HundredKilo(100 * OneKilo.bytes()),
	
	HalfMeg(512 * OneKilo.bytes()),
	Meg(OneKilo.bytes() * OneKilo.bytes()),
	OneMeg(Meg.bytes()),
	FiftyMeg(50 * OneMeg.bytes()),
	
	HalfGig(512 * OneMeg.bytes()),
	Gig(1024 * OneMeg.bytes()),
	OneGig(Gig.bytes()),
	TwoGig(2 * OneGig.bytes()),
	
	Tera(1024 * OneGig.bytes()),
	OneTera(Tera.bytes()), 
	FiveMeg(5 * OneMeg.bytes()),
	
	;
	
	private final long size;

	private Size(long size) {
		this.size = size * 8;
	}
	
	private Size(long size, boolean state) {
		this.size = size;
		
	}
	
	public long bytes() {
		return size / 8;
	}
	
	public long bits() {
		return size;
	}

}
