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

import java.io.IOException;
import java.net.URI;

import org.jnetstream.capture.RemoteSession.RemoteSessionFactory;



/**
 * Allows remote capture sessions to be started from non-local system.
 * The remote server listens for incomming client network connections
 * on a network socket and after proper authentication opens up remote
 * sessions with the clients. The RemoteServer also provides several
 * methods that can be used on the client side for establishing a 
 * remote session with a running server on a remote system. Once a 
 * RemoteSession is created it can be used to do nearly everything that
 * is possible as it all operations were local. The speed of the operations
 * has to be taken into account as all operations are transmitted accross
 * the network, in a client/server environment, including packet buffer data,
 * which may take considerably longer then if the same operation was done
 * using a local session created using Captures methods.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class RemoteServer {

	private RemoteServer delagate;

	protected RemoteServer() {
		delagate = null;
	}

	/**
	 * Creates a remote server that listens on the default port for 
	 * incomming connections and establishes RemoteSessions. The server
	 * is initialized with the specified authentication byte pattern
	 * that must be supplied by the remote end inorder to authenticate
	 * its connection.
	 * 
	 * @param authentication
	 *   authentication pattern that all remote clients will have to authenticate
	 *   against 
	 */
	public RemoteServer(byte[] authentication) throws IOException {
		try {
			delagate = (RemoteServer) Class.forName("").newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Creates a remote server that listens on the default port for 
	 * incomming connections and establishes RemoteSessions. The server
	 * is initialized with the specified authentication byte pattern
	 * that must be supplied by the remote end inorder to authenticate
	 * its connection.
	 * 
	 * @param authentication
	 *   authentication pattern that all remote clients will have to authenticate
	 *   against 

	 * @param handler
	 *   a custom handler that is registered to handle the work
	 *   
	 * @throws IOException
	 *   any IO errors
	 */
	public RemoteServer(byte[] authentication, RemoteSessionFactory handler)
			throws IOException {
		try {
			delagate = (RemoteServer) Class.forName("").newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		delagate.setHandler(handler);
	}

	/**
	 * Changes the default handler which handles the incomming connection
	 * requests.
	 * 
	 * @param handler
	 *   user supplied handler to handling incomming connections
	 */
	protected void setHandler(RemoteSessionFactory handler) {
		throw new IllegalStateException("Unhandled implementation method");
	}

	/**
	 * Listens on a network server socket for incomming connections. All connections
	 * are handed off to the handler for processing and authentication.
	 * 
	 * @throws IOException
	 *   any IO errors
	 */
	public void listen() throws IOException {
		delagate.listen();
	}

	/**
	 * Closes this remote server down and will stop listenting for any more
	 * incomming connections. Any existing connections are also shutdown.
	 * 
	 * @throws IOException
	 *   any IO errors
	 */
	public void close() throws IOException {
		delagate.close();
	}

	/**
	 * Creates an unauthenticated, but connected session to the remote server. At that
	 * point any of the RemoteSession commands can be invoked and new remote
	 * captures started. If the server requires authentication the connection will
	 * fail and RemoteAuthenticationException will be thrown.
	 * 
	 * @param server
	 *            server to connect to
	 * 
	 * @param authentication
	 *            authentication parameter used to authenticate the remote
	 *            session
	 * 
	 * @return a connected remote session to the remote server
	 * 
	 * @exception RemoteAuthenticationException
	 *   if the server requires remote authentication this method will always
	 *   throw this exception
	 */
	public static RemoteSession openSession(URI uri) throws IOException,
			RemoteAuthenticationException {
		return null;
	}

	/**
	 * <P>Creates an empty, but connected session to the remote server. At that
	 * point any of the RemoteSession commands can be invoked and new remote
	 * captures started. Here is a short example:
	 * 
	 * <PRE>
	 * RemoteSession session = RemoteServer.openSession(new URI("192.168.1.100"));
	 * LiveCapture capture = session.openLive();
	 * 
	 * while (capture.hasNext()) {
	 *   System.out.println("Got a packet=" + capture.next().toString());
	 * }
	 * 
	 * session.close(); // Closes down everything
	 * </PRE>
	 * </P>
	 * 
	 * <P>If the supplied authentication byte pattern does not authenticate
	 * the remote session correctly, RemoteAuthenticationException will be thrown.</P>
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
	 * 
	 * @exception RemoteAuthenticationException
	 *   if the authentication fails with the server
	 */
	public static RemoteSession openSession(URI uri, byte[] authentication)
			throws IOException, RemoteAuthenticationException {
		return null;
	}

}
