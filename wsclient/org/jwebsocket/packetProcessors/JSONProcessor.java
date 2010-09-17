// ---------------------------------------------------------------------------
// jWebSocket - JSON Token Processor
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

import java.io.UnsupportedEncodingException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.kit.RawPacket;
import org.jwebsocket.token.Token;

/**
 * converts JSON formatted data packets into tokens and vice versa.
 * @author Alexander Schulze, Roderick Baier (improvements regarding JSON array).
 */
public class JSONProcessor {

	// TODO: Logging cannot be used in common module because not supported on all clients
	// private static Logger log = Logging.getLogger(JSONProcessor.class);
	/**
	 * converts a JSON formatted data packet into a token.
	 * @param aDataPacket
	 * @return
	 */
	public static Token packetToToken(WebSocketPacket aDataPacket) {
		Token lToken = new Token();
		try {
			String lStr = aDataPacket.getString("UTF-8");
			JSONTokener lJSONTokener = new JSONTokener(lStr);
			lToken.setJSONObject(new JSONObject(lJSONTokener));
		} catch (UnsupportedEncodingException ex) {
			// TODO: handle exception
			// log.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
		} catch (JSONException ex) {
			// // TODO: handle exception
			// log.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
		}
		return lToken;
	}

	public static WebSocketPacket tokenToPacket(Token aToken) {
		WebSocketPacket lPacket = null;
		try {
			JSONObject lJSON = aToken.getJSONObject();
			String lData = lJSON.toString();
			lPacket = new RawPacket(lData, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			// TODO: process exception
			// log.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
		}
		return lPacket;
	}


}
