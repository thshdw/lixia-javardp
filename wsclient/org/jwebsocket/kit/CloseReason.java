//	---------------------------------------------------------------------------
//	jWebSocket - Enumeration with flags why a connection was closed
//	Copyright (c) 2010 Alexander Schulze, Innotrade GmbH
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package org.jwebsocket.kit;

/**
 * Enumeration to handle reasons for connection close.
 * @author aschulze
 */
public enum CloseReason {

	/**
	 * The session timeout exceeded.
	 */
	TIMEOUT,
	/**
	 * The server closed the connection.
	 */
	SERVER,
	/**
	 * The client closed the connection.
	 */
	CLIENT,
	/**
	 * The server was shut down.
	 */
	SHUTDOWN

}
