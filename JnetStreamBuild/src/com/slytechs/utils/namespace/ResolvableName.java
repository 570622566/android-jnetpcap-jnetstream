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
package com.slytechs.utils.namespace;


/**
 * <P>Interface that allows query about name resolving services and the
 * state of the object's resolution.</P>
 * 
 * <P>Further more the interface extends NamedObject interface which allows the
 * generic getName() call to be made.
 * 
 * @author Mark Bednarczyk
 *
 */
public interface ResolvableName extends NamedObject {
	
	/**
	 * Checks to see if a resolving service is avaiable, bound and
	 * ready to resolve this objects value to a name.
	 * @return True if service is bound and ready for resolving.
	 */
	public boolean hasNameResolvingService();
		
	/**
	 * Checks to see if object has been resolved to a name. If service is unavailable or
	 * object does not have a mapping it will return false.
	 * 
	 * @return true if object has been resovled to a nem.
	 */
	public boolean isNameResolved();
	
	/**
	 * Tells the object to perform the object value to name resolution.
	 * @return True if resolution of the object to a name succeeded otherwise
	 * it returns false if either the service was unavailable or unable to resolve the
	 * object to a name. 
	 * @throws NameResolutionException 
	 */
	public boolean resolveName() throws NameResolutionException;
}
