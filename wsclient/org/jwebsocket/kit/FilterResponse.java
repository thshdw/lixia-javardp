//	---------------------------------------------------------------------------
//	jWebSocket - Result from a filter in the filter chain
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
 * Implements the response class to return results from the filter chain to
 * the server.
 * @author aschulze
 */
public class FilterResponse {

	private boolean isRejected = false;

	/**
	 * Returns if a filter in the filter chain has rejected a message.
	 * @return the chainAborted
	 */
	public Boolean isRejected() {
		return isRejected;
	}

	/**
	 * Signals that a message has to be rejected and that the filter chain
	 * was aborted.
	 */
	public void rejectMessage() {
		this.isRejected = true;
	}

	/**
	 * Signals that a message may be relayed to further filters, the server or
	 * clients, depending on its direction.
	 */
	public void relayMessage() {
		this.isRejected = false;
	}


}
