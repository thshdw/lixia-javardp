//	---------------------------------------------------------------------------
//	jWebSocket - Raw Data Packet Implementation
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

import java.io.UnsupportedEncodingException;
import org.jwebsocket.api.WebSocketPacket;

/**
 * Implements the low level data packets which are interchanged between
 * client and server. Data packets do not have a special format at this
 * communication level.
 * @author aschulze
 */
public class RawPacket implements WebSocketPacket {

	public static final int FRAMETYPE_UTF8 = 0;
	public static final int FRAMETYPE_BINARY = 1;

	private byte[] data = null;
	private int frameType = FRAMETYPE_UTF8;

	/**
	 * Instantiates a new data packet and initializes its value to the passed
	 * array of bytes.
	 * @param aByteArray byte array to be used as value for the data packet.
	 */
	public RawPacket(byte[] aByteArray) {
		setByteArray(aByteArray);
	}

	/**
	 * Instantiates a new data packet and initializes its value to the passed
	 * string using the default encoding.
	 * @param aString string to be used as value for the data packet.
	 */
	public RawPacket(String aString) {
		setString(aString);
	}

	/**
	 * Instantiates a new data packet and initializes its value to the passed
	 * string using the passed encoding (should always be "UTF-8").
	 * @param aString string to be used as value for the data packet.
	 * @param aEncoding should always be "UTF-8"
	 * @throws UnsupportedEncodingException
	 */
	public RawPacket(String aString, String aEncoding)
		throws UnsupportedEncodingException {
		setString(aString, aEncoding);
	}

	@Override
	public void setByteArray(byte[] aByteArray) {
		data = aByteArray;
	}

	@Override
	public void setString(String aString) {
		data = aString.getBytes();
	}

	@Override
	public void setString(String aString, String aEncoding)
		throws UnsupportedEncodingException {
		data = aString.getBytes(aEncoding);
	}

	@Override
	public void setUTF8(String aString) {
		try {
			data = aString.getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {
			// ignore exception here
		}
	}

	@Override
	public void setASCII(String aString) {
		try {
			data = aString.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException ex) {
			// ignore exception here
		}
	}

	@Override
	public byte[] getByteArray() {
		return data;
	}

	@Override
	public String getString() {
		return new String(data);
	}

	@Override
	public String getString(String aEncoding)
		throws UnsupportedEncodingException {
		return new String(data, aEncoding);
	}

	@Override
	public String getUTF8() {
		try {
			return new String(data, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
	}

	@Override
	public String getASCII() {
		try {
			return new String(data, "US-ASCII");
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
	}

	/**
	 * @return the frameType
	 */
	@Override
	public int getFrameType() {
		return frameType;
	}

	/**
	 * @param frameType the frameType to set
	 */
	@Override
	public void setFrameType(int aFrameType) {
		this.frameType = aFrameType;
	}
}
