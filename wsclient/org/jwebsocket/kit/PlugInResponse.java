//	---------------------------------------------------------------------------
//	jWebSocket - Result from a PlugIn in the PlugInChain
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
 * Implements the response class to return results from the plug-in chain to
 * the server. The server can forward data packets to a chain of plug-ins.
 * Each plug-in can either process or ignore the packet. If the packet was
 * successfully processed the plug-in can abort the chain.
 * @author aschulze
 */
public class PlugInResponse {

	private boolean chainAborted = false;
	private boolean tokenProcessed = false;

	/**
	 * Returns if the plug-in chain has to be aborted after a plug-in has
	 * finished its work.
	 * @return the chainAborted
	 */
	public Boolean isChainAborted() {
		return chainAborted;
	}

	/**
	 * Signals that the plug-in chain has to be be aborted. The token has not
	 * been processed.
	 */
	public void abortChain() {
		this.chainAborted = true;
		this.tokenProcessed = false;
	}

	/**
	 * Signals that the plug-in chain has to be be aborted. The token has been
	 * processed.
	 */
	public void breakChain() {
		this.chainAborted = true;
		this.tokenProcessed = true;
	}

	/**
	 * Signals that the plug-in chain has to be be continued.
	 */
	public void continueChain() {
		this.chainAborted = false;
	}

}
