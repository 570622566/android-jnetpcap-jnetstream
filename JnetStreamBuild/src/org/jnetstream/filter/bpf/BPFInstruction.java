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
package org.jnetstream.filter.bpf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.EnumSet;
import java.util.Set;

import com.slytechs.utils.number.IntegerUtils;

/**
 * A single instruction of a BPF binary program. This is the equivelement to an
 * assembly instruction. The representation here is more java friendly.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class BPFInstruction {

	public enum Symbol {
		/**
		 * 
		 */
		ALUADDK(EnumSet.of(BPFCode.MN_ALU, BPFCode.MN_ADD, BPFCode.MN_K)),

		/**
		 * 
		 */
		ALUADDX(EnumSet.of(BPFCode.MN_ALU, BPFCode.MN_ADD, BPFCode.MN_X)),

		/**
		 * 
		 */
		ALUANDK(EnumSet.of(BPFCode.MN_ALU, BPFCode.MN_AND, BPFCode.MN_K)),

		/**
		 * 
		 */
		ALUANDX(EnumSet.of(BPFCode.MN_ALU, BPFCode.MN_AND, BPFCode.MN_X)),

		/**
		 * 
		 */
		ALUDIVK(EnumSet.of(BPFCode.MN_ALU, BPFCode.MN_DIV, BPFCode.MN_K)),

		/**
		 * 
		 */
		ALUDIVX(EnumSet.of(BPFCode.MN_ALU, BPFCode.MN_DIV, BPFCode.MN_X)),

		/**
		 * 
		 */
		ALULSHK(EnumSet.of(BPFCode.MN_ALU, BPFCode.MN_LSH, BPFCode.MN_K)),

		/**
		 * 
		 */
		ALULSHX(EnumSet.of(BPFCode.MN_ALU, BPFCode.MN_LSH, BPFCode.MN_X)),

		/**
		 * 
		 */
		ALUMULK(EnumSet.of(BPFCode.MN_ALU, BPFCode.MN_MUL, BPFCode.MN_K)),

		/**
		 * 
		 */
		ALUMULX(EnumSet.of(BPFCode.MN_ALU, BPFCode.MN_MUL, BPFCode.MN_X)),

		/**
		 * 
		 */
		ALUORK(EnumSet.of(BPFCode.MN_ALU, BPFCode.MN_OR, BPFCode.MN_K)),

		/**
		 * 
		 */
		ALUORX(EnumSet.of(BPFCode.MN_ALU, BPFCode.MN_OR, BPFCode.MN_X)),

		/**
		 * 
		 */
		ALURSHK(EnumSet.of(BPFCode.MN_ALU, BPFCode.MN_RSH, BPFCode.MN_K)),

		/**
		 * 
		 */
		ALURSHX(EnumSet.of(BPFCode.MN_ALU, BPFCode.MN_RSH, BPFCode.MN_X)),

		/**
		 * 
		 */
		ALUSUBK(EnumSet.of(BPFCode.MN_ALU, BPFCode.MN_SUB, BPFCode.MN_K)),

		/**
		 * 
		 */
		ALUSUBX(EnumSet.of(BPFCode.MN_ALU, BPFCode.MN_SUB, BPFCode.MN_X)),

		/**
		 * 
		 */
		JMPJA(EnumSet.of(BPFCode.MN_JMP, BPFCode.MN_JA)),

		/**
		 * 
		 */
		JMPJEQK(EnumSet.of(BPFCode.MN_JMP, BPFCode.MN_JEQ, BPFCode.MN_K)),

		/**
		 * 
		 */
		JMPJEQX(EnumSet.of(BPFCode.MN_JMP, BPFCode.MN_JEQ, BPFCode.MN_X)),

		/**
		 * 
		 */
		JMPJGEK(EnumSet.of(BPFCode.MN_JMP, BPFCode.MN_JGE, BPFCode.MN_K)),

		/**
		 * 
		 */
		JMPJGEX(EnumSet.of(BPFCode.MN_JMP, BPFCode.MN_JGE, BPFCode.MN_X)),

		/**
		 * 
		 */
		JMPJGTK(EnumSet.of(BPFCode.MN_JMP, BPFCode.MN_JGT, BPFCode.MN_K)),

		/**
		 * 
		 */
		JMPJGTX(EnumSet.of(BPFCode.MN_JMP, BPFCode.MN_JGT, BPFCode.MN_X)),

		/**
		 * 
		 */
		JMPJSETK(EnumSet.of(BPFCode.MN_JMP, BPFCode.MN_JSET, BPFCode.MN_K)),

		/**
		 * 
		 */
		JMPJSETX(EnumSet.of(BPFCode.MN_JMP, BPFCode.MN_JSET, BPFCode.MN_X)),

		/**
		 * 
		 */
		LDBABS(EnumSet.of(BPFCode.MN_LD, BPFCode.MN_B, BPFCode.MN_ABS)),

		/**
		 * 
		 */
		LDBIND(EnumSet.of(BPFCode.MN_LD, BPFCode.MN_B, BPFCode.MN_IND)),

		/**
		 * 
		 */
		LDHABS(EnumSet.of(BPFCode.MN_LD, BPFCode.MN_H, BPFCode.MN_ABS)),

		/**
		 * 
		 */
		LDHIND(EnumSet.of(BPFCode.MN_LD, BPFCode.MN_H, BPFCode.MN_IND)),

		/**
		 * 
		 */
		LDIMM(EnumSet.of(BPFCode.MN_LD, BPFCode.MN_IMM)),

		/**
		 */
		LDMEM(EnumSet.of(BPFCode.MN_LD, BPFCode.MN_MEM)),

		/**
		 * 
		 */
		LDWABS(EnumSet.of(BPFCode.MN_LD, BPFCode.MN_W, BPFCode.MN_ABS)),

		/**
		 * 
		 */
		LDWIND(EnumSet.of(BPFCode.MN_LD, BPFCode.MN_W, BPFCode.MN_IND)),

		/**
		 * 
		 */
		LDWLEN(EnumSet.of(BPFCode.MN_LD, BPFCode.MN_W, BPFCode.MN_LEN)),

		/**
		 * 
		 */
		LDXIMM(EnumSet.of(BPFCode.MN_LDX, BPFCode.MN_IMM)),

		/**
		 * 
		 */
		LDXMEM(EnumSet.of(BPFCode.MN_LDX, BPFCode.MN_MEM)),

		/**
		 * 
		 */
		LDXMSHB(EnumSet.of(BPFCode.MN_LDX, BPFCode.MN_MSH, BPFCode.MN_B)),

		/**
		 * 
		 */
		LDXWLEN(EnumSet.of(BPFCode.MN_LDX, BPFCode.MN_W, BPFCode.MN_LEN)),

		/**
		 * 
		 */
		MISCTAX(EnumSet.of(BPFCode.MN_MISC, BPFCode.MN_TAX)),

		/**
		 * 
		 */
		MISCTXA(EnumSet.of(BPFCode.MN_MISC, BPFCode.MN_TXA)),

		/**
		 * 
		 */
		RETA(EnumSet.of(BPFCode.MN_RET, BPFCode.MN_A)),

		/**
		 * 
		 */
		RETK(EnumSet.of(BPFCode.MN_RET, BPFCode.MN_K)),

		/**
		 * 
		 */
		ST(EnumSet.of(BPFCode.MN_ST)),

		/**
		 * 
		 */
		STX(EnumSet.of(BPFCode.MN_STX)),

		;

		/**
		 * @param code
		 * @return
		 */
		public static Symbol valueOf(final int code) {
			for (final Symbol s : Symbol.values()) {
				if (code == s.full) {
					return s;
				}
			}

			return null;
		}

		private final int full;

		private final Set<BPFCode> symbol;

		private Symbol(final Set<BPFCode> symbol) {
			this.symbol = symbol;

			int total = 0;
			for (final BPFCode p : symbol) {
				total |= p.op();
			}

			this.full = total;
		}

		/**
		 * @return Returns the full.
		 */
		public final int getFull() {
			return this.full;
		}

		/**
		 * @return Returns the symbol.
		 */
		public final Set<BPFCode> getSymbol() {
			return this.symbol;
		}
	}

	static StringBuilder buf = new StringBuilder(1024);

	static int count = 0;

	/**
	 * Converts an array of BPF instructions to a binary program stored in a
	 * buffer.
	 * 
	 * @param inst
	 *          instruction array to do the conversion of
	 * @return buffer containing the converted binary bpf program
	 */
	public static byte[] encodeToBinary(final BPFInstruction[] inst) {

		final ByteBuffer b = ByteBuffer.wrap(new byte[8 * inst.length]);

		for (BPFInstruction element : inst) {
			b.put(element.encodeToBinary());
		}

		b.clear();

		return b.array();
	}

	/**
	 * 
	 */
	public final int code;

	/**
	 * 
	 */
	public final int jf;

	/**
	 * 
	 */
	public final int jt;

	/**
	 * 
	 */
	public final long k;

	/**
	 * 
	 */
	public final Symbol symbol;

	/**
	 * Initializes the instruction from raw buffer data. Each instruction is
	 * 64-bits wide and has the following structure:
	 * 
	 * <PRE>
	 * 
	 * struct bpf_insn { u_short code; u_char jt; u_char jf; bpf_int32 k; };
	 * 
	 * </PRE>
	 * 
	 * @param buf
	 *          buffer to extract the structure from
	 * @param offset
	 *          offset within the buffer to read the structure
	 * @throws IllegalInstructionException
	 */
	public BPFInstruction(final byte[] buf, final int offset,
	    final ByteOrder encoding) throws IllegalInstructionException {

		this.code = IntegerUtils.readUShort(buf, offset, encoding);

		this.jt = IntegerUtils.readUByte(buf, offset + 2);
		this.jf = IntegerUtils.readUByte(buf, offset + 3);
		this.k = IntegerUtils.readUInt(buf, offset + 4, encoding);
		this.symbol = Symbol.valueOf(this.code);

		if (this.symbol == null) {
			throw new IllegalInstructionException("Unknown OP code=" + this.toString()
			    + " at offset=0x" + Integer.toHexString(offset) + " encoding=LE");
		}

		// System.out.println("#" + (count++) + " " + toString());
		// System.out.flush();
	}

	/**
	 * Initializes the instruction from raw buffer data. Each instruction is
	 * 64-bits wide and has the following structure:
	 * 
	 * <PRE>
	 * 
	 * struct bpf_insn { u_short code; u_char jt; u_char jf; bpf_int32 k; };
	 * 
	 * </PRE>
	 * 
	 * @param buf
	 *          buffer to extract the structure from
	 * @param offset
	 *          offset within the buffer to read the structure
	 */
	public BPFInstruction(final ByteBuffer buf, final int offset) {
		this.code = buf.get(offset + 1) << 0x8 | buf.get(offset + 0);
		this.jt = buf.get(offset + 2);
		this.jf = buf.get(offset + 3);
		this.k = buf.get(offset + 7) << 0x24 | buf.get(offset + 6) << 0x16
		    | buf.get(offset + 5) << 0x8 | buf.get(offset + 4);
		this.symbol = Symbol.valueOf(this.code);
	}

	public BPFInstruction(final int code, final int jt, final int jf, final int k)
	    throws IllegalInstructionException {
		this.code = code;
		this.jt = jt;
		this.jf = jf;
		this.k = k;

		this.symbol = Symbol.valueOf(code);

		if (this.symbol == null) {
			throw new IllegalInstructionException("Unknown OP code=" + code + " jt=" + jt
			    + " jf=" + jf + " k=" + k);
		}

	}

	/**
	 * @param code
	 *          BPF op code
	 * @param jt
	 *          jump address
	 * @param jf
	 *          jump address
	 * @param k
	 *          base offset
	 */
	public BPFInstruction(final int code, final int jt, final int jf, final long k) {
		this.code = code;
		this.jt = jt;
		this.jf = jf;
		this.k = k;
		this.symbol = Symbol.valueOf(code);
	}

	/**
	 * @return
	 */
	public byte[] encodeToBinary() {
		final byte[] b = new byte[8];
		b[0] = (byte) (this.code & 0xFF);
		b[1] = (byte) (this.code & 0xFF00 >> 8);
		b[2] = (byte) (this.jt & 0xFF);
		b[3] = (byte) (this.jf & 0xFF);
		b[4] = (byte) (this.k & 0xFF);
		b[5] = (byte) (this.k & 0xFF00 >> 8);
		b[6] = (byte) (this.k & 0xFF0000 >> 16);
		b[7] = (byte) (this.k & 0xFF000000 >> 24);

		return b;
	}

	/**
	 * @return Returns the code.
	 */
	public final int getCode() {
		return this.code;
	}

	/**
	 * @return Returns the jf.
	 */
	public final int getJf() {
		return this.jf;
	}

	/**
	 * @return Returns the jt.
	 */
	public final int getJt() {
		return this.jt;
	}

	/**
	 * @return Returns the k.
	 */
	public final long getK() {
		return this.k;
	}

	@Override
	public String toString() {
		BPFInstruction.buf.setLength(0);

		BPFInstruction.buf.append("").append(this.symbol).append("(0x").append(
		    Integer.toHexString(this.code)).append(")\t");
		BPFInstruction.buf.append("jt(0x").append(Integer.toHexString(this.jt))
		    .append(",").append(this.jt).append(")\t");
		BPFInstruction.buf.append("jf(0x").append(Integer.toHexString(this.jf))
		    .append(",").append(this.jf).append(")\t");
		BPFInstruction.buf.append("k(0x").append(Long.toHexString(this.k)).append(
		    ",").append(this.k).append(")");

		return BPFInstruction.buf.toString();
	}

}
