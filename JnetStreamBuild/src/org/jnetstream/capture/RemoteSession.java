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
package org.jnetstream.capture;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

import org.jnetstream.capture.Captures.RemoteFactory;


/**
 * <P>A remote session which allows remote capture of live packets or capture file manipulation. 
 * Use a CaptureServer to create a RemoteSession instance which is connected to the requested
 * remote system. The RemoteSession object implements the Captures which is an 
 * abstract factory method for establishing live or file captures and performing operations
 * on files. All these operations are preformed on the remote system transparentely. Notice
 * that Captures is the same interface used to create local captures and manipulate
 * local files, so this is a very powerful abstract which allows exact same functionality
 * but accross the network.</P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface RemoteSession  extends RemoteFactory, Closeable {
	
//	public enum Option {
//		Compression,
//		SSL,
//		RateLimit,
//	}
	
	/**
	 * <P>A list of all the avaiable remote services that can
	 * be opened, changed, enabled and disabled.</P>
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
//	public enum RemoteService {
//
//		/**
//		 * Allows clients to open live captures in none promiscous mode
//		 * only.
//		 */
//		OpenNonPromiscMode,
//		
//		/**
//		 * Allows clients to open live captures in promiscous mode.
//		 */
//		OpenPromiscMode,
//		
//		/**
//		 * Allows clients to open file for read only. No write is allowed.
//		 */
//		OpenFileReadOnly,
//		
//		/**
//		 * Allows clients to open file for read and write.
//		 */
//		OpenFileReadWrite,
//		
//		/**
//		 * Allows clients to list network interfaces this system has.
//		 */
//		ListNetworkInterfaces,
//		
//		/**
//		 * Allows clients to list files and directories.
//		 */
//		ListFiles,
//		
//		/**
//		 * Allows clients to enable and disable services on this server.
//		 */
//		EnableServices,
//		
//		/**
//		 * Allows clients to retrieve server's internal statistics.
//		 */
//		GetServerStatistics,
//	}

	/**
	 * Remote session handler is an object that is called by the CaptureServer to
	 * handle an incomming RemoteSession connection. Once the incoming RemoteSession
	 * is detected the method {@link #createSession} is called and from
	 * then on it is upto the handler to implement the proper protocol, authenticate
	 * the connection and verify and validate which services are available to the
	 * client.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface RemoteSessionFactory {

		public RemoteSession createSession(RemoteServer parent,
				Socket socket) throws IOException,
				RemoteAuthenticationException;

	}
	
	/**
	 * Gets a list of capture devices on the remote system. The list will
	 * contain all available capture devices or network interfaces available
	 * on the remote system.
	 * 
	 * @return
	 *   List of devices, if no devices are available the list will be empty. 
	 *   This method never returns null.
	 */
	public LiveCaptureDevice[] listCaptureDevices() throws IOException;
	
	/**
	 * Closes the remote session to the remote server. The session object
	 * is no longer valid. Any method invocation on this remote session
	 * after the close will throw IllegalStateException. All open capture
	 * sessions through this remote session are closed as well.
	 * 
	 * @throws
	 *   IOException any IO errors
	 */
	public void close() throws IOException;

}
