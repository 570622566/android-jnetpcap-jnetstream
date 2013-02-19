package org.jnetstream.lang.npl;

import java.io.File;

import org.jnetstream.lang.CodeTarget;

/**
 * Constants define type of output to produce from the compilation
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum Targets implements CodeTarget {
	/**
	 * Compiler will generate BPF output containing BPF byte code compatible
	 * with BPF VM, BPF userland from Tcpdump and most kernels.
	 */
	BPF_FILTER,

	/**
	 * Compiler will generate BPF output containing BPF byte code compatible
	 * with BPF VM, BPF userland from Tcpdump and but not kernels. The code will
	 * return -1 on failure or greater then 0 which is a bitwise index into the
	 * packet buffer of the start of the sink header.
	 */
	BPF_BINDING,

	/**
	 * Compiler will generate BPF output containing BPF byte code which
	 * implements the <code>CodeExpression</code> interface. Allows generic
	 * evaluation of any expression based on a packet byte buffer where integer
	 * result is expected.
	 */
	BPF_INT_EXPRESSION,

	/**
	 * Compiler will generate assembly code suitable for JASMIN assembler. The
	 * code will implement a <code>Filter</code> inteface.
	 */
	JASMIN_FILTER,

	/**
	 * Compiler will generate assembled code suitable for current native 32-bit
	 * X86 processor platform on linux machine.
	 */
	JASMIN_BINDING,

	/**
	 * Compiler will generate assembly code suitable for JASMIN assembler. The
	 * code will implement a <code>CodeExpression</code> inteface.
	 */
	JASMIN_EXPRESSION,

	/**
	 * Compiler will generate JAVA source code suitable for javac compiler. The
	 * output will be a number of interfaces which provide STUB interface for
	 * the source NPL definition for all properties, fields and headers.
	 */
	JAVA_PROTOCOL_STUB,

	/**
	 * Compiler will generate JAVA source code suitable for javac compiler. The
	 * output will be a concrete class that provides implementing classes for
	 * all of the STUB interfaces.
	 */
	JAVA_PROTOCOL_IMPL,

	/**
	 * Compiler will generate JAVA source code suitable for javac compiler. The
	 * output will be a concrete class that implements the
	 * <code>HeaderCodec</code> interface for encoding and decoding of packet
	 * buffers.
	 */
	JAVA_CODEC,

	/**
	 * Compiler will generate JAVA source code suitable for javac compiler. The
	 * output will be a concrete enum class that defines all of the contants
	 * that were defined using NPL <code>table</code> statement. All the
	 * constants will be defined in java's enum table.
	 */
	JAVA_TABLE;

	/**
	 * Gets the list of template files that are grouped into a
	 * StringTemplateGroup by the compiler. Changing targets simply means
	 * providing a different set of templates. All the output language semantics
	 * are specified within the templates.
	 * 
	 * @return array of template files where the order of determines the
	 *         template hierachy with the the template file at index 0 beind the
	 *         lowest super tempalte and the last file within the array being
	 *         the must sub-template.
	 * @see org.jnetstream.lang.npl.NplCompiler.CodeTarget#getTemplateFiles()
	 */
	public File[] getTemplateFiles() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}
}