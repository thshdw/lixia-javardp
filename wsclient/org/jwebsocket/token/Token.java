//	---------------------------------------------------------------------------
//	jWebSocket - Token Implementation
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
package org.jwebsocket.token;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jwebsocket.api.WebSocketToken;

/**
 * A token is ...
 * @author aschulze
 */
public class Token extends BaseToken implements WebSocketToken {

	private JSONObject mData = new JSONObject();

	/**
	 * Creates a new empty instance of a token.
	 * The token does not contain any items.
	 */
	public Token() {
	}

	/**
	 *
	 * @param aType
	 */
	public Token(String aType) {
		setType(aType);
	}

	/**
	 *
	 * @param aJSON
	 */
	public Token(JSONObject aJSON) {
		mData = aJSON;
	}

	/**
	 *
	 * @param aNS
	 * @param aType
	 */
	public Token(String aNS, String aType) {
		setNS(aNS);
		setType(aType);
	}

	/**
	 *
	 * @param aJSON
	 */
	public void setJSONObject(JSONObject aJSON) {
		mData = aJSON;
	}

	/**
	 *
	 *
	 * @return
	 */
	public JSONObject getJSONObject() {
		return mData;
	}

	private Object getValue(Object aValue) {
		if (aValue instanceof Token) {
			aValue = ((Token) aValue).getJSONObject();
		} else if (aValue instanceof Collection) {
			JSONArray lJA = new JSONArray();
			for (Object lItem : (Collection) aValue) {
				lJA.put(getValue(lItem));
			}
			aValue = lJA;
		} else if (aValue instanceof Map) {
			JSONObject lJO = new JSONObject();
			for (Entry<Object, Object> lItem : ((Map<Object, Object>) aValue).entrySet()) {
				try {
					lJO.put(lItem.getKey().toString(), getValue(lItem.getValue()));
				} catch (JSONException ex) {
				}
			}
			aValue = lJO;
		} else if (aValue instanceof Object[]) {
			JSONArray lJA = new JSONArray();
			Object[] lOA = (Object[]) aValue;
			for (int i = 0; i < lOA.length; i++) {
				lJA.put(getValue(lOA[i]));
			}
			aValue = lJA;
		}
		return aValue;
	}

	/**
	 * puts a new key/value pair into the token, in other words it adds a
	 * new item to the token.
	 * @param aKey key of the the token item.
	 * @param aValue value of the token item.
	 */
	public void put(String aKey, Object aValue) {
		try {
			mData.put(aKey, getValue(aValue));
		} catch (JSONException ex) {
			// TODO: handle exception
		}
	}

	/**
	 *
	 * @param aKey
	 * @return
	 */
	public Object get(String aKey) {
		try {
			return mData.get(aKey);
		} catch (JSONException ex) {
			return null;
		}
	}

	/**
	 *
	 * @param aKey
	 * @return
	 */
	@Override
	public void remove(String aKey) {
		mData.remove(aKey);
	}

	/**
	 *
	 * @return
	 */
	public Iterator<String> getKeys() {
		return mData.keys();
	}

	/**
	 *
	 * @param aKey
	 * @param aDefault
	 * @return
	 */
	@Override
	public String getString(String aKey, String aDefault) {
		String lResult;
		try {
			lResult = mData.getString(aKey);
		} catch (JSONException ex) {
			lResult = aDefault;
		}
		return lResult;
	}

	/**
	 *
	 * @param aKey
	 */
	@Override
	public void setString(String aKey, String aValue) {
		try {
			mData.put(aKey, aValue);
		} catch (JSONException ex) {
			// TODO: handle exception
		}
	}

	/**
	 *
	 * @param aKey
	 * @return
	 */
	@Override
	public String getString(String aKey) {
		return getString(aKey, null);
	}

	/**
	 *
	 * @param aKey
	 * @param aDefault
	 * @return
	 */
	@Override
	public Integer getInteger(String aKey, Integer aDefault) {
		Integer lResult;
		try {
			lResult = mData.getInt(aKey);
		} catch (JSONException ex) {
			lResult = aDefault;
		}
		return lResult;
	}

	/**
	 *
	 * @param aKey
	 * @return
	 */
	@Override
	public Integer getInteger(String aKey) {
		return getInteger(aKey, null);
	}

	@Override
	public void setInteger(String aKey, Integer aValue) {
		try {
			mData.put(aKey, aValue);
		} catch (JSONException ex) {
			// TODO: handle exception
		}
	}

	/**
	 *
	 * @param aKey
	 * @param aDefault
	 * @return
	 */
	@Override
	public Boolean getBoolean(String aKey, Boolean aDefault) {
		Boolean lResult;
		try {
			lResult = mData.getBoolean(aKey);
		} catch (JSONException ex) {
			lResult = aDefault;
		}
		return lResult;
	}

	/**
	 *
	 * @param aArg
	 * @return
	 */
	@Override
	public Boolean getBoolean(String aArg) {
		return getBoolean(aArg, null);
	}

	/**
	 *
	 * @param aKey
	 */
	@Override
	public void setBoolean(String aKey, Boolean aValue) {
		try {
			mData.put(aKey, aValue);
		} catch (JSONException ex) {
			// TODO: handle exception
		}
	}

	/**
	 *
	 * @param aKey
	 * @param aDefault
	 * @return
	 */
	@Override
	public List getList(String aKey, Boolean aDefault) {
		// TODO: Implement this
		return null;
	}

	/**
	 *
	 * @param aKey
	 * @return
	 */
	@Override
	public List getList(String aKey) {
		// TODO: Implement this
		return null;
	}

	/**
	 *
	 * @param aKey
	 * @param aDefault
	 * @return
	 */
	@Override
	public Map getMap(String aKey, Boolean aDefault) {
		// TODO: Implement this
		return null;
	}

	/**
	 *
	 * @param aKey
	 * @return
	 */
	@Override
	public Map getMap(String aKey) {
		// TODO: Implement this
		return null;
	}

	/**
	 *
	 * @return
	 */
	public final String getType() {
		return getString("type");
	}

	/**
	 *
	 * @param aType
	 */
	public final void setType(String aType) {
		put("type", aType);
	}

	/**
	 * Returns the name space of the token. If you have the same token type
	 * interpreted by multiple different plug-ins the name space allows to
	 * uniquely address a certain plug-in. Each plug-in has its own name space.
	 * @return the name space.
	 */
	public final String getNS() {
		return getString("ns");
	}

	/**
	 * Sets the name space of the token. If you have the same token type
	 * interpreted by multiple different plug-ins the namespace allows to
	 * uniquely address a certain plug-in. Each plug-in has its own namespace.
	 * @param aNS the namespace to be set for the token.
	 */
	public final void setNS(String aNS) {
		put("ns", aNS);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return mData.toString();
	}
}
