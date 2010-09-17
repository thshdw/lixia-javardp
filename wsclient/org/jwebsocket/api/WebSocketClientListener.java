//	---------------------------------------------------------------------------
//	jWebSocket - Copyright (c) 2010 Innotrade GmbH
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
package org.jwebsocket.api;

/**
 * Interface for the jWebSocket client listeners
 * @author aschulze
 */
public interface WebSocketClientListener {
   /**
    * This method is invoked when a new client connects to the Client.
    * @param aEvent
    */
   void processOpened(WebSocketClientEvent aEvent);

   /**
    * This method is invoked when a data packet from a client is received.
    * The event provides getter for the Client and the connector to send
    * responses to back the client.
    * @param aEvent
    * @param aPacket
    */
   void processPacket(WebSocketClientEvent aEvent, WebSocketPacket aPacket);

   /**
    * This method is invoked when a client was disconnted from the Client.
    * @param aEvent
    */
   void processClosed(WebSocketClientEvent aEvent);
   
   void processStream(byte[] data, final int offset, final int length);
}
