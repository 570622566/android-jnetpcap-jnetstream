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
package org.jnetstream.filter.bpf;

/**
 * Instruction set for BPF program. Based on "pcap-bpf.h" file.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum BPFCode {
	
	/* Instruction classes */
	  /* CLASS = 0x07 */
	MN_LD(0x00, 0x07),
	MN_LDX(0x01, 0x07),
	MN_ST(0x02, 0x07),
	MN_STX(0x03, 0x07),
	MN_ALU(0x04, 0x07),
	MN_JMP(0x05, 0x07),
	MN_RET(0x06, 0x07),
	MN_MISC(0x07, 0x07),
	
	/* ld/ldx fields */
	  /* SIZE = 0x18 */
	MN_W(0x00, 0x18),
	MN_H(0x08, 0x18),
	MN_B(0x10, 0x18),
	
	  /* MODE = 0xE0 */
	MN_IMM(0x00, 0xE0),
	MN_ABS(0x20, 0xE0),
	MN_IND(0x40, 0xE0),
	MN_MEM(0x60, 0xE0),
	MN_LEN(0x80, 0xE0),
	MN_MSH(0xA0, 0xE0),
	
	/* alu/jmp fields */
	  /* OP = 0xF0 */
	MN_ADD(0x00, 0xF0),
	MN_SUB(0x10, 0xF0),
	MN_MUL(0x20, 0xF0),
	MN_DIV(0x30, 0xF0),
	MN_OR(0x40, 0xF0),
	MN_AND(0x50, 0xF0),
	MN_LSH(0x60, 0xF0),
	MN_RSH(0x70, 0xF0),
	MN_NEG(0x80, 0xF0),
	MN_JA(0x00, 0xF0),
	MN_JEQ(0x10, 0xF0),
	MN_JGT(0x20, 0xF0),
	MN_JGE(0x30, 0xF0),
	MN_JSET(0x40, 0xF0),
	
	  /* SRC = 0x08 */
	MN_K(0x00, 0x08),
	MN_X(0x00, 0x08),
	
	  /* RET K and X also apply */
	MN_A(0x10, 0x18),
	
	/* Misceleneous */
	  /* MISCOP = 0xF8 */
	MN_TAX(0x00, 0xF8),
	MN_TXA(0x80, 0xF8),
	;

	private final int op;
	private final int mask;

	private BPFCode(int opcode, int mask) {
		this.op = opcode;
		this.mask = mask;
	}

	/**
	 * @return Returns the opcode.
	 */
	public final int op() {
		return op;
	}

	/**
	 * @return Returns the mask.
	 */
	public final int getMask() {
		return mask;
	}

	/* Instruction classes */
	  /* CLASS = 0x07 */
	public static final int LD = 0x00;
	public static final int LDX = 0x01;
	public static final int ST = 0x02;
	public static final int STX = 0x03;
	public static final int ALU = 0x04;
	public static final int JMP = 0x05;
	public static final int RET = 0x06;
	public static final int MISC = 0x07;
	
	/* ld/ldx fields */
	  /* SIZE = 0x18 */
	public static final int W = 0x00;
	public static final int H = 0x08;
	public static final int B = 0x10;
	
	  /* MODE = 0xE0 */
	public static final int IMM = 0x00;
	public static final int ABS = 0x20;
	public static final int IND = 0x40;
	public static final int MEM = 0x60;
	public static final int LEN = 0x80;
	public static final int MSH = 0xA0;
	
	/* alu/jmp fields */
	  /* OP = 0xF0 */
	public static final int ADD = 0x00;
	public static final int SUB = 0x10;
	public static final int MUL = 0x20;
	public static final int DIV = 0x30;
	public static final int OR = 0x40;
	public static final int AND = 0x50;
	public static final int LSH = 0x60;
	public static final int RSH = 0x70;
	public static final int NEG = 0x80;
	public static final int JA = 0x00;
	public static final int JEQ = 0x10;
	public static final int JGT = 0x20;
	public static final int JGE = 0x30;
	public static final int JSET = 0x40;
	
	  /* SRC = 0x08 */
	public static final int K = 0x00;
	public static final int X = 0x08;
	
	  /* RET K and X also apply */
	public static final int A = 0x10;
	
	/* Misceleneous */
	  /* MISCOP = 0xF8 */
	public static final int TAX = 0x00;
	public static final int TXA = 0x80;


}
