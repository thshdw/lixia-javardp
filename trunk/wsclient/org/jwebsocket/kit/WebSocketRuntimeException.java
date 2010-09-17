//	---------------------------------------------------------------------------
//	jWebSocket - WebSocket Exception
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
 * Exception class to represent JWebSocketServer related exception
 * @author Puran Singh
 * @version $Id: WebSocketException.java 148 2010-03-07 05:24:10Z mailtopuran $
 *
 */
public class WebSocketRuntimeException extends RuntimeException {

	/**
	 * creates the exception with given message
	 * @param error the error messae
	 */
	public WebSocketRuntimeException(String error) {
		super(error);
	}
	
	/**
	 * creates the exception with given message
	 * @param error the error messae
	 * @param throwable the cause 
	 */
	public WebSocketRuntimeException(String error, Throwable throwable) {
		super(error, throwable);
	}

	private static final long serialVersionUID = 1L;
	
}
