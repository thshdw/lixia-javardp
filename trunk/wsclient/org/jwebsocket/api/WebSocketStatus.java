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

/**
 * WebSocket connection status. These status values are based on 
 * HTML5 WebSocket API specification
 * {@linkplain http://dev.w3.org/html5/websockets/#websocket}
 * 
 * @author puran
 * @version $Id: WebSocketStatus.java 683 2010-07-17 04:35:39Z mailtopuran@gmail.com $
 */
public enum WebSocketStatus {
    
    CONNECTING(0), OPEN(1), CLOSING(2), CLOSED(3);

    private int status;

    WebSocketStatus(int theStatus) {
        this.status = theStatus;
    }

    /**
     * @return the status int value
     */
    public int getStatus() {
        return status;
    }
}
