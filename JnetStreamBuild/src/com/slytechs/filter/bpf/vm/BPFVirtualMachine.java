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
package com.slytechs.filter.bpf.vm;

import java.nio.ByteBuffer;

import org.jnetstream.filter.bpf.BPFCode;
import org.jnetstream.filter.bpf.BPFInstruction;
import org.jnetstream.filter.bpf.BPFProgram;
import org.jnetstream.filter.bpf.BpfVM;
import org.jnetstream.filter.bpf.IllegalInstructionException;

import com.slytechs.utils.number.IntegerUtils;

/**
 * Virtual State Machine that interprets and executes a BPF program.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class BPFVirtualMachine implements BpfVM {

	public static final int BPF_MEMWORDS = 16;

	public BPFVirtualMachine() {
		// Empty
	}

	public long execute(final BPFProgram program, final byte[] data,
	    final int wirelen, final int buflen) throws IllegalInstructionException {

		long A = 0; // A register
		long X = 0; // X register
		long k = 0; // Memory pointer
		int pi = 0; // Program index into the PC array
		final long[] mem = new long[BPFVirtualMachine.BPF_MEMWORDS]; // Memory
																																	// stack

		BPFInstruction i = null;
		pi--;
		while (true) {
			pi++;
			i = program.getCode()[pi];

			switch (i.getCode()) {
				default:
					throw new IllegalInstructionException(
					    "Illegal BPF instruction encountered: opcode=" + i.getCode()
					        + " at OP index=" + pi);

				case BPFCode.RET | BPFCode.K:
					return i.k;

				case BPFCode.RET | BPFCode.A:
					return A;

				case BPFCode.LD | BPFCode.W | BPFCode.ABS:
					k = i.k;
					if (k + 4 > buflen) {
						return 0;
					}
					A = IntegerUtils.readUIntBE(data, (int) k);
					continue;

				case BPFCode.LD | BPFCode.H | BPFCode.ABS:
					k = i.k;
					if (k + 2 > buflen) {
						return 0;
					}
					A = IntegerUtils.readUShortBE(data, (int) k);
					continue;

				case BPFCode.LD | BPFCode.B | BPFCode.ABS:
					k = i.k;
					if (k + 1 > buflen) {
						return 0;
					}
					A = IntegerUtils.readUByte(data, (int) k);
					continue;

				case BPFCode.LD | BPFCode.W | BPFCode.LEN:
					A = wirelen;
					continue;

				case BPFCode.LDX | BPFCode.W | BPFCode.LEN:
					X = wirelen;
					continue;

				case BPFCode.LD | BPFCode.W | BPFCode.IND:
					k = X + i.k;
					if (k + 4 > buflen) {
						return 0;
					}
					A = IntegerUtils.readUIntBE(data, (int) k);
					continue;

				case BPFCode.LD | BPFCode.H | BPFCode.IND:
					k = X + i.k;
					if (k + 2 > buflen) {
						return 0;
					}
					A = IntegerUtils.readUShortBE(data, (int) k);
					continue;

				case BPFCode.LD | BPFCode.B | BPFCode.IND:
					k = X + i.k;
					if (k + 1 > buflen) {
						return 0;
					}
					A = IntegerUtils.readUByte(data, (int) k);
					continue;

				case BPFCode.LDX | BPFCode.MSH | BPFCode.B:
					k = i.k;
					X = (IntegerUtils.readUByte(data, (int) k) & 0xf) << 2;
					continue;

				case BPFCode.LDX | BPFCode.IMM:
					X = i.k;
					continue;

				case BPFCode.LD | BPFCode.IMM:
					A = i.k;
					continue;

				case BPFCode.LD | BPFCode.MEM:
					A = mem[(int) i.k];
					continue;

				case BPFCode.LDX | BPFCode.MEM:
					X = mem[(int) i.k];
					continue;

				case BPFCode.ST:
					mem[(int) i.k] = A;
					continue;

				case BPFCode.STX:
					mem[(int) i.k] = X;
					continue;

				case BPFCode.JMP | BPFCode.JA:
					pi = (int) i.k;
					continue;

				case BPFCode.JMP | BPFCode.JGT | BPFCode.K:
					pi += (A > i.k) ? i.jt : i.jf;
					continue;

				case BPFCode.JMP | BPFCode.JGE | BPFCode.K:
					pi += (A >= i.k) ? i.jt : i.jf;
					continue;

				case BPFCode.JMP | BPFCode.JEQ | BPFCode.K:
					pi += (A == i.k) ? i.jt : i.jf;
					continue;

				case BPFCode.JMP | BPFCode.JSET | BPFCode.K:
					pi += ((A & i.k) != 0) ? i.jt : i.jf;
					continue;

				case BPFCode.JMP | BPFCode.JGT | BPFCode.X:
					pi += (A > X) ? i.jt : i.jf;
					continue;

				case BPFCode.JMP | BPFCode.JGE | BPFCode.X:
					pi += (A >= X) ? i.jt : i.jf;
					continue;

				case BPFCode.JMP | BPFCode.JEQ | BPFCode.X:
					pi += (A == X) ? i.jt : i.jf;
					continue;

				case BPFCode.JMP | BPFCode.JSET | BPFCode.X:
					pi += ((A & X) != 0) ? i.jt : i.jf;
					continue;

					/* ALUs on X */

				case BPFCode.ALU | BPFCode.ADD | BPFCode.X:
					A += X;
					continue;

				case BPFCode.ALU | BPFCode.SUB | BPFCode.X:
					A -= X;
					continue;

				case BPFCode.ALU | BPFCode.MUL | BPFCode.X:
					A *= X;
					continue;

				case BPFCode.ALU | BPFCode.DIV | BPFCode.X:
					if (X == 0) {
						return 0;
					}
					A /= X;
					continue;

				case BPFCode.ALU | BPFCode.AND | BPFCode.X:
					A &= X;
					continue;

				case BPFCode.ALU | BPFCode.OR | BPFCode.X:
					A |= X;
					continue;

				case BPFCode.ALU | BPFCode.LSH | BPFCode.X:
					A <<= X;
					continue;

				case BPFCode.ALU | BPFCode.RSH | BPFCode.X:
					A >>>= X;
					continue;

					/* ALUs on K */

				case BPFCode.ALU | BPFCode.ADD | BPFCode.K:
					A += i.k;
					continue;

				case BPFCode.ALU | BPFCode.SUB | BPFCode.K:
					A -= i.k;
					continue;

				case BPFCode.ALU | BPFCode.MUL | BPFCode.K:
					A *= i.k;
					continue;

				case BPFCode.ALU | BPFCode.DIV | BPFCode.K:
					if (X == 0) {
						return 0;
					}
					A /= i.k;
					continue;

				case BPFCode.ALU | BPFCode.AND | BPFCode.K:
					A &= i.k;
					continue;

				case BPFCode.ALU | BPFCode.OR | BPFCode.K:
					A |= i.k;
					continue;

				case BPFCode.ALU | BPFCode.LSH | BPFCode.K:
					A <<= i.k;
					continue;

				case BPFCode.ALU | BPFCode.RSH | BPFCode.K:
					A >>>= i.k;
					continue;

				case BPFCode.ALU | BPFCode.NEG:
					A = -A;
					continue;

				case BPFCode.MISC | BPFCode.TAX:
					X = A;
					continue;

				case BPFCode.MISC | BPFCode.TXA:
					A = X;
					continue;

			}
		}
	}

	public long execute(final BPFProgram program, final ByteBuffer data,
	    final int wirelen, final int buflen) throws IllegalInstructionException {

		long A = 0; // A register
		long X = 0; // X register
		long k = 0; // Memory pointer
		int pi = 0; // Program index into the PC array
		final long[] mem = new long[BPFVirtualMachine.BPF_MEMWORDS]; // Memory
																																	// stack

		// data.order(ByteOrder.BIG_ENDIAN);
		final int offset = data.position();

		BPFInstruction i = null;
		pi--;
		while (true) {
			pi++;
			i = program.getCode()[pi];

			switch (i.getCode()) {
				default:
					throw new IllegalInstructionException(
					    "Illegal BPF instruction encountered: opcode=" + i.getCode()
					        + " at OP index=" + pi);

				case BPFCode.RET | BPFCode.K:
					return i.k;

				case BPFCode.RET | BPFCode.A:
					return A;

				case BPFCode.LD | BPFCode.W | BPFCode.ABS:
					k = i.k;
					if (k + 4 > buflen) {
						return 0;
					}
					A = IntegerUtils.readUInt(data, offset + (int) k);
					continue;

				case BPFCode.LD | BPFCode.H | BPFCode.ABS:
					k = i.k;
					if (k + 2 > buflen) {
						return 0;
					}
					A = IntegerUtils.readUShort(data, offset + (int) k);
					continue;

				case BPFCode.LD | BPFCode.B | BPFCode.ABS:
					k = i.k;
					if (k + 1 > buflen) {
						return 0;
					}
					A = IntegerUtils.readUByte(data, offset + (int) k);
					continue;

				case BPFCode.LD | BPFCode.W | BPFCode.LEN:
					A = wirelen;
					continue;

				case BPFCode.LDX | BPFCode.W | BPFCode.LEN:
					X = wirelen;
					continue;

				case BPFCode.LD | BPFCode.W | BPFCode.IND:
					k = X + i.k;
					if (k + 4 > buflen) {
						return 0;
					}
					A = IntegerUtils.readUInt(data, offset + (int) k);
					continue;

				case BPFCode.LD | BPFCode.H | BPFCode.IND:
					k = X + i.k;
					if (k + 2 > buflen) {
						return 0;
					}
					A = IntegerUtils.readUShort(data, offset + (int) k);
					continue;

				case BPFCode.LD | BPFCode.B | BPFCode.IND:
					k = X + i.k;
					if (k + 1 > buflen) {
						return 0;
					}
					A = IntegerUtils.readUByte(data, offset + (int) k);
					continue;

				case BPFCode.LDX | BPFCode.MSH | BPFCode.B:
					k = i.k;
					X = (IntegerUtils.readUByte(data, offset + (int) k) & 0xf) << 2;
					continue;

				case BPFCode.LDX | BPFCode.IMM:
					X = i.k;
					continue;

				case BPFCode.LD | BPFCode.IMM:
					A = i.k;
					continue;

				case BPFCode.LD | BPFCode.MEM:
					A = mem[(int) i.k];
					continue;

				case BPFCode.LDX | BPFCode.MEM:
					X = mem[(int) i.k];
					continue;

				case BPFCode.ST:
					mem[(int) i.k] = A;
					continue;

				case BPFCode.STX:
					mem[(int) i.k] = X;
					continue;

				case BPFCode.JMP | BPFCode.JA:
					pi = (int) i.k;
					continue;

				case BPFCode.JMP | BPFCode.JGT | BPFCode.K:
					pi += (A > i.k) ? i.jt : i.jf;
					continue;

				case BPFCode.JMP | BPFCode.JGE | BPFCode.K:
					pi += (A >= i.k) ? i.jt : i.jf;
					continue;

				case BPFCode.JMP | BPFCode.JEQ | BPFCode.K:
					pi += (A == i.k) ? i.jt : i.jf;
					continue;

				case BPFCode.JMP | BPFCode.JSET | BPFCode.K:
					pi += ((A & i.k) != 0) ? i.jt : i.jf;
					continue;

				case BPFCode.JMP | BPFCode.JGT | BPFCode.X:
					pi += (A > X) ? i.jt : i.jf;
					continue;

				case BPFCode.JMP | BPFCode.JGE | BPFCode.X:
					pi += (A >= X) ? i.jt : i.jf;
					continue;

				case BPFCode.JMP | BPFCode.JEQ | BPFCode.X:
					pi += (A == X) ? i.jt : i.jf;
					continue;

				case BPFCode.JMP | BPFCode.JSET | BPFCode.X:
					pi += ((A & X) != 0) ? i.jt : i.jf;
					continue;

					/* ALUs on X */

				case BPFCode.ALU | BPFCode.ADD | BPFCode.X:
					A += X;
					continue;

				case BPFCode.ALU | BPFCode.SUB | BPFCode.X:
					A -= X;
					continue;

				case BPFCode.ALU | BPFCode.MUL | BPFCode.X:
					A *= X;
					continue;

				case BPFCode.ALU | BPFCode.DIV | BPFCode.X:
					if (X == 0) {
						return 0;
					}
					A /= X;
					continue;

				case BPFCode.ALU | BPFCode.AND | BPFCode.X:
					A &= X;
					continue;

				case BPFCode.ALU | BPFCode.OR | BPFCode.X:
					A |= X;
					continue;

				case BPFCode.ALU | BPFCode.LSH | BPFCode.X:
					A <<= X;
					continue;

				case BPFCode.ALU | BPFCode.RSH | BPFCode.X:
					A >>>= X;
					continue;

					/* ALUs on K */

				case BPFCode.ALU | BPFCode.ADD | BPFCode.K:
					A += i.k;
					continue;

				case BPFCode.ALU | BPFCode.SUB | BPFCode.K:
					A -= i.k;
					continue;

				case BPFCode.ALU | BPFCode.MUL | BPFCode.K:
					A *= i.k;
					continue;

				case BPFCode.ALU | BPFCode.DIV | BPFCode.K:
					if (X == 0) {
						return 0;
					}
					A /= i.k;
					continue;

				case BPFCode.ALU | BPFCode.AND | BPFCode.K:
					A &= i.k;
					continue;

				case BPFCode.ALU | BPFCode.OR | BPFCode.K:
					A |= i.k;
					continue;

				case BPFCode.ALU | BPFCode.LSH | BPFCode.K:
					A <<= i.k;
					continue;

				case BPFCode.ALU | BPFCode.RSH | BPFCode.K:
					A >>>= i.k;
					continue;

				case BPFCode.ALU | BPFCode.NEG:
					A = -A;
					continue;

				case BPFCode.MISC | BPFCode.TAX:
					X = A;
					continue;

				case BPFCode.MISC | BPFCode.TXA:
					A = X;
					continue;

			}
		}
	}
}
