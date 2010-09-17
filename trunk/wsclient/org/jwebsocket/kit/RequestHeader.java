//	---------------------------------------------------------------------------
//	jWebSocket - RequestHeader Object
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

import java.util.Map;
import javolution.util.FastMap;

/**
 * Holds the header of the initial WebSocket request from the client
 * to the server. The RequestHeader internally maintains a FastMap to store
 * key/values pairs.
 * @author aschulze
 * @version $Id: RequestHeader.java 596 2010-06-22 17:09:54Z fivefeetfurther $
 */
public final class RequestHeader {

    private Map<String, Object> args = new FastMap<String, Object>();
    private static final String ARGS = "args";
    private static final String PROT = "prot";
    private static final String TIMEOUT = "timeout";

    /**
     * Puts a new object value to the request header.
     * @param aKey
     * @param aValue
     */
    public void put(String aKey, Object aValue) {
        args.put(aKey, aValue);
    }

    /**
     * Returns the object value for the given key or {@code null} if the
     * key does not exist in the header.
     * @param aKey
     * @return object value for the given key or {@code null}.
     */
    public Object get(String aKey) {
        return args.get(aKey);
    }

    /**
     * Returns the string value for the given key or {@code null} if the
     * key does not exist in the header.
     * @param aKey
     * @return String value for the given key or {@code null}.
     */
    public String getString(String aKey) {
        return (String) args.get(aKey);
    }

    /**
     * Returns a FastMap of the optional URL arguments passed by the client.
     * @return FastMap of the optional URL arguments.
     */
    public Map getArgs() {
        return (Map) args.get(ARGS);
    }

    /**
     * Returns the sub protocol passed by the client or a default value
     * if no sub protocol has been passed either in the header or in the
     * URL arguments.
     * @param aDefault
     * @return Sub protocol passed by the client or default value.
     */
    public String getSubProtocol(String aDefault) {
        Map lArgs = getArgs();
        String lSubProt = null;
        if (lArgs != null) {
            lSubProt = (String) lArgs.get(PROT);
        }
        return (lSubProt != null ? lSubProt : aDefault);
    }

    /**
     * Returns the session timeout passed by the client or a default value
     * if no session timeout has been passed either in the header or in the
     * URL arguments.
     * @param aDefault
     * @return Session timeout passed by the client or default value.
     */
    public Integer getTimeout(Integer aDefault) {
        Map lArgs = getArgs();
        Integer lTimeout = null;
        if (lArgs != null) {
            try {
                lTimeout = Integer.parseInt((String) (lArgs.get(TIMEOUT)));
            } catch (Exception ex) {
            }
        }
        return (lTimeout != null ? lTimeout : aDefault);
    }
}
