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
package org.jwebsocket.client.token;

import org.jwebsocket.api.WebSocketTokenClient;
import org.jwebsocket.api.WebSocketClientEvent;
import org.jwebsocket.api.WebSocketClientListener;
import org.jwebsocket.api.WebSocketClientTokenListener;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.client.java.BaseWebSocket;
import org.jwebsocket.config.JWebSocketCommonConstants;
import org.jwebsocket.kit.WebSocketException;
import org.jwebsocket.packetProcessors.CSVProcessor;
import org.jwebsocket.packetProcessors.JSONProcessor;
import org.jwebsocket.packetProcessors.XMLProcessor;
import org.jwebsocket.token.Token;
import org.apache.commons.codec.binary.Base64;

/**
 * Token based implementation of {@code JWebSocketClient}
 * @author aschulze
 * @author puran
 * @version $Id:$
 */
public class BaseTokenClient extends BaseWebSocket implements WebSocketTokenClient {

	/** base namespace for jWebSocket */
	private final static String NS_BASE = "org.jWebSocket";
	/** constants for connection status */
	public final static int DISCONNECTED = 0;
	public final static int CONNECTED = 1;
	public final static int AUTHENTICATED = 2;
	/** token client protocols */
	private final static String WELCOME = "welcome";
	private final static String LOGIN = "login";
	private final static String GOODBYE = "goodBye";
	private final static String LOGOUT = "logout";
	/** token id */
	private int CUR_TOKEN_ID = 0;
	/** subprotocol value */
	private String lSubProt = JWebSocketCommonConstants.SUB_PROT_DEFAULT;
	private String fUsername = null;
	private String fClientId = null;
	private String fSessionId = null;

	/**
	 * Default constructor
	 */
	public BaseTokenClient() {
		addListener(new TokenClientListener());
	}

	/**
	 * WebSocketClient listener implementation that recieves the data packet and
	 * creates <tt>token</tt> objects
	 *
	 * @author aschulze
	 */
	class TokenClientListener implements WebSocketClientListener {

		/**
		 * {@inheritDoc} Initialize all the variables when the process starts
		 */
		@Override
		public void processOpened(WebSocketClientEvent aEvent) {
			fUsername = null;
			fClientId = null;
			fSessionId = null;
		}

		/**
		 * {@inheritDoc} This callback method is invoked by jWebSocket client
		 * after the data is received from low-level <tt>WebSocket</tt>
		 * connection. This method then generates the <tt>token</tt> objects
		 * using the data packets.
		 */
		@Override
		public void processPacket(WebSocketClientEvent aEvent, WebSocketPacket aPacket) {
			for (WebSocketClientListener lListener : getListeners()) {
				if (lListener instanceof WebSocketClientTokenListener) {
					Token lToken = packetToToken(aPacket);

					String lType = lToken.getType();
					String lReqType = lToken.getString("reqType");

					if (lType != null) {
						if (WELCOME.equals(lType)) {
							fClientId = lToken.getString("sourceId");
							fSessionId = lToken.getString("usid");
						} else if (GOODBYE.equals(lType)) {
							fUsername = null;
						}
					}
					if (lReqType != null) {
						if (LOGIN.equals(lReqType)) {
							fUsername = lToken.getString("username");
						} else if (LOGOUT.equals(lReqType)) {
							fUsername = null;
						}
					}
					((WebSocketClientTokenListener) lListener).processToken(aEvent, lToken);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void processClosed(WebSocketClientEvent aEvent) {
			// clean up resources
			fUsername = null;
			fClientId = null;
			fSessionId = null;
		}

		@Override
		public void processStream(byte[] data, int offset, int length) {
			// TODO Auto-generated method stub
			
		}
	}

	/**
	 * {@
	 */
	@Override
	public void close() throws WebSocketException {
		super.close();
		fUsername = null;
		fClientId = null;
		fSessionId = null;
	}

	/**
	 * @return the fUsername
	 */
	@Override
	public String getUsername() {
		return fUsername;
	}

	@Override
	public boolean isAuthenticated() {
		return (fUsername != null);
	}

	/**
	 * @return the fClientId
	 */
	public String getClientId() {
		return fClientId;
	}

	/**
	 * @return the fSessionId
	 */
	public String getfSessionId() {
		return fSessionId;
	}

	/**
	 *
	 * @param aConnector
	 * @param aPacket
	 * @return
	 */
	public Token packetToToken(WebSocketPacket aPacket) {
		Token lToken = null;
		if (lSubProt.equals(JWebSocketCommonConstants.SUB_PROT_JSON)) {
			lToken = JSONProcessor.packetToToken(aPacket);
		} else if (lSubProt.equals(JWebSocketCommonConstants.SUB_PROT_CSV)) {
			lToken = CSVProcessor.packetToToken(aPacket);
		} else if (lSubProt.equals(JWebSocketCommonConstants.SUB_PROT_XML)) {
			lToken = XMLProcessor.packetToToken(aPacket);
		}
		return lToken;
	}

	/**
	 *
	 * @param aConnector
	 * @param aToken
	 * @return
	 */
	public WebSocketPacket tokenToPacket(Token aToken) {
		WebSocketPacket lPacket = null;

		if (lSubProt.equals(JWebSocketCommonConstants.SUB_PROT_JSON)) {
			lPacket = JSONProcessor.tokenToPacket(aToken);
		} else if (lSubProt.equals(JWebSocketCommonConstants.SUB_PROT_CSV)) {
			lPacket = CSVProcessor.tokenToPacket(aToken);
		} else if (lSubProt.equals(JWebSocketCommonConstants.SUB_PROT_XML)) {
			lPacket = XMLProcessor.tokenToPacket(aToken);
		}
		return lPacket;
	}

	public void sendToken(Token aToken) throws WebSocketException {
		CUR_TOKEN_ID++;
		aToken.put("utid", CUR_TOKEN_ID);
		super.send(tokenToPacket(aToken));
	}
	private final static String NS_SYSTEM_PLUGIN = NS_BASE + ".plugins.system";

	@Override
	public void login(String aUsername, String aPassword) throws WebSocketException {
		Token lToken = new Token();
		lToken.put("type", "login");
		lToken.put("ns", NS_SYSTEM_PLUGIN);
		lToken.put("username", aUsername);
		lToken.put("password", aPassword);
		sendToken(lToken);
	}

	@Override
	public void logout() throws WebSocketException {
		Token lToken = new Token();
		lToken.put("type", "logout");
		lToken.put("ns", NS_SYSTEM_PLUGIN);
		sendToken(lToken);
	}

	@Override
	public void ping(boolean aEcho) throws WebSocketException {
		Token lToken = new Token();
		lToken.put("ns", NS_SYSTEM_PLUGIN);
		lToken.put("type", "ping");
		lToken.put("echo", aEcho);
		sendToken(lToken);
	}

	@Override
	public void sendText(String aTarget, String aData) throws WebSocketException {
		Token lToken = new Token();
		lToken.put("ns", NS_SYSTEM_PLUGIN);
		lToken.put("type", "send");
		lToken.put("targetId", aTarget);
		lToken.put("sourceId", getClientId());
		lToken.put("sender", getUsername());
		lToken.put("data", aData);
		sendToken(lToken);
	}

	@Override
	public void broadcastText(String aData) throws WebSocketException {
		Token lToken = new Token();
		lToken.put("ns", NS_SYSTEM_PLUGIN);
		lToken.put("type", "broadcast");
		lToken.put("sourceId", getClientId());
		lToken.put("sender", getUsername());
		lToken.put("data", aData);
		lToken.put("senderIncluded", false);
		lToken.put("responseRequested", true);
		sendToken(lToken);
	}
	private final static String NS_FILESYSTEM_PLUGIN = NS_BASE + ".plugins.filesystem";

	// @Override
	public void saveFile(byte[] aData, String aFilename, String aScope, Boolean aNotify) throws WebSocketException {
		Token lToken = new Token();
		lToken.put("ns", NS_FILESYSTEM_PLUGIN);
		lToken.put("type", "save");
		lToken.put("sourceId", getClientId());
		lToken.put("sender", getUsername());
		lToken.put("filename", aFilename);
		// TODO: set mimetype correctly according to file extension based on configuration in jWebSocket.xml
		lToken.put("mimetype", "image/jpeg");
		lToken.put("scope", aScope);
		lToken.put("notify", aNotify);

		lToken.put("data", new String(Base64.encodeBase64(aData)));
		sendToken(lToken);
	}

	/* functions of the Admin Plug-in */
	private final static String NS_ADMIN_PLUGIN = NS_BASE + ".plugins.admin";

	@Override
	public void disconnect() throws WebSocketException {
	}

	public void shutdown() throws WebSocketException {
		Token lToken = new Token();
		lToken.put("type", "shutdown");
		lToken.put("ns", NS_ADMIN_PLUGIN);
		sendToken(lToken);
	}

	@Override
	public void getConnections() throws WebSocketException {
		Token lToken = new Token();
		lToken.put("type", "getConnections");
		lToken.put("ns", NS_ADMIN_PLUGIN);
		sendToken(lToken);
	}

	@Override
	public void addTokenClientListener(WebSocketClientTokenListener tokenListener) {
		super.addListener(tokenListener);
	}

	@Override
	public void removeTokenClientListener(WebSocketClientTokenListener tokenListener) {
		super.removeListener(tokenListener);
	}
}
