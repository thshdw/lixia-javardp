//	---------------------------------------------------------------------------
//	jWebSocket - Token Interface
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
package org.jwebsocket.api;

import java.util.List;
import java.util.Map;

/**
 *
 * @author aschulze
 */
public interface WebSocketToken {


	/**
	 *
	 * @param aKey
	 * @param aDefault
	 * @return
	 */
	String getString(String aKey, String aDefault);

	/**
	 *
	 * @param aKey
	 * @return
	 */
	String getString(String aKey);

	/**
	 *
	 * @param aKey
	 * @param aValue 
	 */
	void setString(String aKey, String aValue);

	/**
	 *
	 * @param aKey
	 * @param aDefault
	 * @return
	 */
	Integer getInteger(String aKey, Integer aDefault);

	/**
	 *
	 * @param aKey
	 * @return
	 */
	Integer getInteger(String aKey);

	/**
	 *
	 * @param aKey
	 * @param aValue
	 */
	void setInteger(String aKey, Integer aValue);

	/**
	 *
	 * @param aKey
	 * @param aDefault
	 * @return
	 */
	Boolean getBoolean(String aKey, Boolean aDefault);

	/**
	 *
	 * @param aKey
	 * @return
	 */
	Boolean getBoolean(String aKey);

	/**
	 *
	 * @param aKey
	 * @param aValue
	 */
	void setBoolean(String aKey, Boolean aValue);

	/**
	 *
	 * @param aKey
	 * @param aDefault
	 * @return
	 */
	List getList(String aKey, Boolean aDefault);

	/**
	 *
	 * @param aKey
	 * @return
	 */
	List getList(String aKey);

	// TODO: Add list access methods

	/**
	 *
	 * @param aKey
	 * @param aDefault
	 * @return
	 */
	Map getMap(String aKey, Boolean aDefault);

	/**
	 *
	 * @param aKey
	 * @return
	 */
	Map getMap(String aKey);

	// TODO: Add map access methods

	// TODO: Add date/time fields


	/**
	 *
	 * @param aKey
	 * @return
	 */
	void remove(String aKey);

}
