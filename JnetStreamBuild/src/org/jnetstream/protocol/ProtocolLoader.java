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
package org.jnetstream.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * <P>A protocol loader is an object that is responsible for loading protocols. The class
 * ProtocolLoader is an interface. Given binary or source name of a protocol, a protocol loader
 * will attempt to load the binary file or compile the corresponding source file into binary
 * form and load it into the runtime environment.</P> 
 * 
 * <P>Every protocol object contains a reference to the ProtocolLoader that loaded it. Although 
 * more convenient methods are provided through the ProtocolRegistry methods to find and load
 * classes.</P>
 * 
 * <P>Care must be taken to link a loaded protocol definition at an appropriate time. This is
 * typically done by the runtime environment when all the protocol's dependencies have been loaded
 * first. Normally protocols are linked ondemand when they are needed by decoders and dissectors.</P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface ProtocolLoader {
	
	public static final String PROPERTY_NPL_PROTOCOL_PATH = "npl.protocol.path";
	public static final String PROPERTY_JAVA_CLASS_PATH = "java.class.path";
	
	/**
	 * <P>Will search only in the list of protocols that have been already defined
	 * in the ProtocolRegistry. Will not perform an extensive search for the
	 * protocol.</P>
	 * 
	 * @param name
	 *   name of the protocol to locate
	 *   
	 * @return
	 *   protocol if found, otherwise null
	 */
	public ProtocolInfo findLoadedProtocol(String name);
	
	/**
	 * Loads either the binary protocol file or invokes the compiler to compile the 
	 * source protocol definition to binary form and loads it. Does not perform link
	 * on the protocol.
	 *
	 * @param name
	 *   name of the protocol to load
	 *   
	 * @return
	 *   protocol that has been fully loaded. This method never returns null.
	 *   
	 * @throws ProtocolDeclarationException
	 *   if the protocol binary or source files contain some kind of
	 *   delaration or syntax errors
	 *   
	 * @throws ProtocolNotFoundException
	 *   if the protocol binary and source files can not be found
	 *   
	 * @throws IOException
	 *   any problems with physically accessing the files
	 */
	public ProtocolInfo loadProtocol(String name) throws ProtocolDeclarationException, ProtocolNotFoundException, IOException;
	
	/**
	 * Links the named protocol into the runtime environment. This should only be done by the runtime system 
	 * at an appropriate time. If done by the user too early, unresolved link exceptions may be thrown.
	 * 
	 * @param name
	 *   name of the protocol to link
	 *   
	 * @return
	 *   protocol that has been fully linked. This method never returns null.

	 * @throws ProtocolBindingException
	 *   Any problems during the linking process. Uresolved references, etc..
	 *   
	 * @throws ProtocolDeclarationException
	 *   if the protocol binary or source files contain some kind of
	 *   delaration or syntax errors
	 *   
	 * @throws ProtocolNotFoundException
	 *   if the protocol binary and source files can not be found
	 *   
	 * @throws IOException
	 *   any problems with physically accessing the files
	 */
	public ProtocolInfo linkProtocol(String name) throws ProtocolNotFoundException, ProtocolDeclarationException, IOException, ProtocolBindingException;
	
	/**
	 * Returns the parent ProtocolLoader.
	 * 
	 * @return
	 *   Returns the parent loader or null if this is the root system loader.
	 */
	public ProtocolLoader getParent();
	
	/**
	 * Method that returns a URL for the requested named resource.
	 * 
	 * @param name
	 *   name of the resource (protocol declaration, image, or anything else to search for.)
	 *   
	 * @return
	 *   url to the resource or null if not found
	 */
	public URL getResource(String name);
	
	/**
	 * Opens up an InputStream to the resource if found. 
	 * 
	 * @param name
	 *   name of the resource to open
	 *   
	 * @return
	 *   InputStream bound to the named resource
	 */
	public InputStream getResourceAsStream(String name);
	
	/**
	 * Loads a protocol definition from a user supplied stream. The URL is required
	 * to help identify the protocol and report errors. The protocol is not
	 * linked into the environment until it is needed by the runtime environment.
	 * 
	 * @param in
	 *   Input stream to read from
	 *   
	 * @param url
	 *   url of the source of this stream
	 *   
	 * @return
	 *   protocol that has been loaded with the definition from the stream. This
	 *   method never returns null.
	 *   
	 * @throws ProtocolDeclarationException
	 *   any syntax or declaration errors found in the definition
	 */
	public ProtocolInfo loadProtocol(InputStream in, URL url) throws ProtocolDeclarationException;

	/**
	 * <P>Will search in the list of protocols that have been already defined
	 * in the ProtocolRegistry and in other places as defined by the implementation 
	 * of this interface.</P>
	 * 
	 * @param name
	 *   name of the protocol to locate
	 *   
	 * @return
	 *   protocol if found, otherwise null
	 */
	public ProtocolInfo findProtocol(String name) throws ProtocolNotFoundException;
}
