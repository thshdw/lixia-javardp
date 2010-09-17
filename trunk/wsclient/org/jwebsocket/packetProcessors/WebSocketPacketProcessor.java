// ---------------------------------------------------------------------------
// jWebSocket - Interface for token processors
// Copyright (c) 2010 jWebSocket.org, Alexander Schulze, Innotrade GmbH
// ---------------------------------------------------------------------------
// This program is free software; you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published by the
// Free Software Foundation; either version 3 of the License, or (at your
// option) any later version.
// This program is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
// more details.
// You should have received a copy of the GNU Lesser General Public License along
// with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
// ---------------------------------------------------------------------------
package org.jwebsocket.packetProcessors;

import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.token.Token;

/**
 *
 * @author aschulze
 */
public interface WebSocketPacketProcessor {

	/**
	 * 
	 * @param aDataPacket
	 * @return
	 */
	Token packetToToken(WebSocketPacket aDataPacket);

	/**
	 *
	 * @param aToken
	 * @return
	 */
	WebSocketPacket tokenToPacket(Token aToken);

}
