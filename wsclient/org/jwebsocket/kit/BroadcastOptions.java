//	---------------------------------------------------------------------------
//	jWebSocket - Broadcasting options
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
 * This class holds the various options to broadcast a data packet in the
 * WebSocket network. When broadcasting the sender can decide if the packet
 * is supposed to be sent only (one-way packet) or if the server is supposed
 * to sent an acknowledge. The sender also can decide if the sender himself
 * should be a broadcast target or not - which might be required for certain
 * business logic.
 * @author aschulze
 */
public class BroadcastOptions {

	private boolean senderIncluded = false;
	private boolean responseRequested = false;

	/**
	 * Creates a new <tt>BroadcastOptions</tt> instance. The caller can decide
	 * whether or not to include the sender in the broadcast and if the sender
	 * expects a response from the server.
	 * @param aSenderIncluded
	 * @param aResponseRequested
	 */
	public BroadcastOptions(boolean aSenderIncluded, boolean aResponseRequested) {
		senderIncluded = aSenderIncluded;
		responseRequested = aResponseRequested;
	}

	/**
	 * Returns if the sender is supposed to be included in the pending 
	 * broadcast operation.
	 * @return senderIncluded Is the sender included in the pending broadcast?
	 */
	public boolean isSenderIncluded() {
		return senderIncluded;
	}

	/**
	 * Specifies if the sender is supposed to be included in the pending
	 * broadcast operation.
	 * @param aSenderIncluded Sender supposed to be included in the pending broadcast?
	 */
	public void setSenderIncluded(boolean aSenderIncluded) {
		this.senderIncluded = aSenderIncluded;
	}

	/**
	 * Returns if the server is supposed to send a response for the
	 * broadcast operation.
	 * @return responseRequested Server supposed to send a response?
	 */
	public boolean isResponseRequested() {
		return responseRequested;
	}

	/**
	 * Specifies if the server is supposed to send a response for the
	 * broadcast operation.
	 * @param responseRequested Server supposed to send a response?
	 */
	public void setResponseRequested(boolean responseRequested) {
		this.responseRequested = responseRequested;
	}

}
