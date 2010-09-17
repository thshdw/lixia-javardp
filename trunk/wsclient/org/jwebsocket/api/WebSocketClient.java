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

import java.util.List;

import org.jwebsocket.kit.WebSocketException;
/**
 * Base interface that represents the <tt>jWebSocket</tt> java client and it defines
 * all the methods and operations to allow the implementation of jWebSocket specific 
 * client protocols and allows to register/deregister different types of listeners 
 * for different types of communication and data format.The implementation of this
 * interface handles all the data formats and client protocols and delegates the events
 * and the data to different listeners for further processing.
 * @author aschulze
 * @author puran
 * @version $Id: WebSocketClient.java 701 2010-07-18 17:53:06Z mailtopuran@gmail.com $
 */
public interface WebSocketClient {
    /**
     * Opens the jWebSocket connection 
     * @param aURL the websocket connection url
     * @throws WebSocketException if therre's an 
     */
    void open(String aURL) throws WebSocketException;
    /**
     * Send the given byte data to the server
     * @param aData the byte data
     * @throws WebSocketException if exception occurs while sending the data
     */
    void send(byte[] aData) throws WebSocketException;
    
    /**
     * Sends the data to the jWebSocket server, data has to be UTF-8 encoded.
     * @param aData the data to send
     * @param aEncoding the encoding type
     * @throws WebSocketException if there's any exception while sending the data
     */
    void send(String aData, String aEncoding) throws WebSocketException;
    /**
     * Sends the websocket data packet to the <tt>WebSocket</tt> client
     * @param aPacket the data packet to send
     * @throws WebSocketException if there's any exception while sending
     */
    void send(WebSocketPacket aPacket) throws WebSocketException;
    /**
     * Close the jWebSocket connection. This method should perform all the cleanup
     * operation to release the jWebSocket resources 
     * @throws WebSocketException if exception while close operation
     */
    void close() throws WebSocketException;
    /**
     * Method to check if the jWebSocketClient is still connected to the jWebSocketServer
     * @return {@code true} if there's a persistent connection {@code false} otherwise
     */
    boolean isConnected();
    /**
     * Notifies the <tt>jWebSocket</tT> client implementation about the connection being opened
     * to the jWebSocket server via <tt>WebSocket</tt> 
     * @param aEvent the websocket client event object
     */
    void notifyOpened(WebSocketClientEvent aEvent);
    /**
     * Notifies the <tt>jWebSocket</tT> client implementation about the packet being received
     * from the <tt>WebSocket</tt> client. 
     * @param aEvent the websocket client event object
     * @param aPacket the data packet received
     */
    void notifyPacket(WebSocketClientEvent aEvent, WebSocketPacket aPacket);
    /**
     * Notifies the <tt>jWebSocket</tT> client implementation about the connection being closed
     * @param aEvent the websocket client event object
     */
    void notifyClosed(WebSocketClientEvent aEvent);
    /**
     * Adds the client listener to the lists of listener which are interested in receiving the 
     * <tt>jWebSocket</tt> connection and data events. Listeners are good way to handle all the
     * <tt>jWebSocket</tt> specific protocol and data format 
     * @param aListener the event listener object
     */
    void addListener(WebSocketClientListener aListener);

    /**
     * Remove the listener from the list of listeners, once the listener is
     * removed it won't be notified of any <tt>jWebSocket</tt> events.
     * 
     * @param aListener the listener object to remove
     */
    void removeListener(WebSocketClientListener aListener);

    /**
     * Returns the list of listeners registered.
     * @return the list of all listeners.
     */
    List<WebSocketClientListener> getListeners();
}
