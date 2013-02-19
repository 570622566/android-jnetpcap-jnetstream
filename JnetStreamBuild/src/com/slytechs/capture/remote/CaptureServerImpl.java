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
package com.slytechs.capture.remote;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jnetstream.capture.RemoteAuthenticationException;
import org.jnetstream.capture.RemoteServer;
import org.jnetstream.capture.RemoteSession;
import org.jnetstream.capture.RemoteSession.RemoteSessionFactory;


import com.slytechs.utils.net.IpAddress;

/**
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class CaptureServerImpl extends RemoteServer {
	
	protected RemoteSessionFactory handler;
	
	private List<RemoteSession> sessions = new LinkedList<RemoteSession>();
	
	private Thread listenerThread = null;

	public CaptureServerImpl(byte[] authentication) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		handler = (RemoteSessionFactory) Class.forName("").newInstance();
	}


	public void listen() throws IOException {
		start();
	}
	
	public void close() throws IOException {
		for (RemoteSession s: sessions) {
			s.close();
		}
		
		stop();
	}
	
	private void start() {
		
		listenerThread = new Thread(new Runnable() {

			public void run() {
				while (listenerThread != null) {
					
				}
			}
			
		}, "CaptureServerListener");
		
	}
	
	private void stop() {
		
		if (listenerThread != null) {
			Thread t = listenerThread;
			listenerThread = null;
			t.interrupt();
		}
		
	}

	/**
	 * Creates an empty, but connected session to the remote server. At that
	 * point any of the RemoteSession commands can be invoked and new remote
	 * captures started.
	 * 
	 * @param server
	 *            server to connect to
	 * 
	 * @param authentication
	 *            authentication parameter used to authenticate the remote
	 *            session
	 * 
	 * @return a connected remote session to the remote server
	 */
	public static RemoteSession openSession(IpAddress server,
			byte[] authentication) throws IOException,
			RemoteAuthenticationException {
		return null;
	}

	/**
	 * Creates an empty, but connected session to the remote server. At that
	 * point any of the RemoteSession commands can be invoked and new remote
	 * captures started.
	 * 
	 * @param server
	 *            server to connect to
	 * 
	 * @param port
	 *            use the specified port instead of the default remote session
	 *            port
	 * 
	 * @param authentication
	 *            authentication parameter used to authenticate the remote
	 *            session
	 * 
	 * @return a connected remote session to the remote server
	 */

	public static RemoteSession openSession(IpAddress server, int port,
			byte[] authentication) throws IOException, RemoteAuthenticationException {
		return null;
	}
}
