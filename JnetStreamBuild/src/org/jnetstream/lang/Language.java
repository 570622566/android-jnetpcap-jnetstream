package org.jnetstream.lang;

/**
 * Used to specify the language to use with compilers. The language is used to
 * indicate the source and the target of compilers.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum Language {

	/**
	 * Java syntax will be used.
	 */
	JAVA,

	/**
	 * BPF assembly syntax. Needs to be assembled to BPF byte code.
	 */
	BPF,

	/**
	 * Jasmin assembly syntax. Needs to be assembed to Java byte code classfiles.
	 */
	JASIM,
}