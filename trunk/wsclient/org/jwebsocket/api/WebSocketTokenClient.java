//  ---------------------------------------------------------------------------
//  jWebSocket - Copyright (c) 2010 Innotrade GmbH
//  ---------------------------------------------------------------------------
//  This program is free software; you can redistribute it and/or modify it
//  under the terms of the GNU Lesser General Public License as published by the
//  Free Software Foundation; either version 3 of the License, or (at your
//  option) any later version.
//  This program is distributed in the hope that it will be useful, but WITHOUT
//  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//  more details.
//  You should have received a copy of the GNU Lesser General Public License along
//  with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//  ---------------------------------------------------------------------------
package org.jwebsocket.api;

import org.jwebsocket.kit.WebSocketException;

/**
 * Base interface that represents the <tt>Token</tt> based jWebSocket client. This interface
 * defines all the methods that handles the basic jWebSocket specific protocol.
 * @author puran
 * @version $Id: WebSocketTokenClient.java 697 2010-07-17 21:43:50Z mailtopuran@gmail.com $
 */
public interface WebSocketTokenClient extends WebSocketClient {

	/**
	 * @return the loggedIn user name
	 */
	String getUsername();

	/**
	 * Login the client based on given username and password to the jWebSocket server
	 * @param username the user name
	 * @param password the password string
	 * @throws WebSocketException if there's any exception while login
	 */
	void login(String aUsername, String aPassword) throws WebSocketException;

	/**
	 * Logout the user
	 * @throws WebSocketException if exception while logging out
	 */
	void logout() throws WebSocketException;

	/**
	 * Checks if for this client a user already is authenticated.
	 */
	boolean isAuthenticated();

	/**
	 * Broadcast the text to all the connected clients to jWebSocket server
	 * @param text the text value to broadcast
	 * @throws WebSocketException if exception while broadcasting
	 */
	void broadcastText(String aText) throws WebSocketException;

	/**
	 * Ping the jWebSocket server
	 * @param echo flag to enable/disable echo
	 * @throws WebSocketException if exception while doing a ping
	 */
	void ping(boolean aEcho) throws WebSocketException;

	/**
	 * Send the text data
	 * @param target the target
	 * @param message the actual message to send
	 * @throws WebSocketException if exception while sending text
	 */
	void sendText(String aTargetId, String aText) throws WebSocketException;

	/**
	 * Disconnect from the jWebSocket server
	 * @throws WebSocketException if error while disconnecting
	 */
	void disconnect() throws WebSocketException;

	/**
	 * Send the token to get the number of connected clients
	 * @throws WebSocketException
	 */
	void getConnections() throws WebSocketException;

	/**
	 * Add the token client listener which are interested in receiving only
	 * token based data.
	 * @param tokenListener the token listener object
	 */
	void addTokenClientListener(WebSocketClientTokenListener aTokenListener);

	/**
	 * Remove the token client listener
	 * @param tokenListener the token client listener
	 */
	void removeTokenClientListener(WebSocketClientTokenListener aTokenListener);
}
