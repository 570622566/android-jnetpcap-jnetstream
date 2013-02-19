package org.jnetstream.lang;

import java.io.File;

import org.jnetstream.lang.npl.Targets;

/**
 * <p>
 * Interface which setups a code target for the compiler. There are a number
 * of predefined code targets in the {@link Targets} table. Targets are simply
 * different ANTLR string templates which can produce completely different
 * type of output.
 * </p>
 * <p>
 * To provide your own target simply implement this interface and return a set
 * of your own templates. The templates themselves, must implement the
 * IntermediateTemplate StringTemplate interface provided in the
 * <code>org.jnetstream.lang.template</code> package. This is a special type
 * of interface that is not a Java interface but a StringTemplate interface
 * that uses string template syntax. The string template interface ensures
 * that all the elements for which the output emitter utilizing intermediate
 * AST has all the appropriate template elements, all with correct formal
 * parameters and names.
 * </p>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface CodeTarget {

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
	 */
	public File[] getTemplateFiles();
}