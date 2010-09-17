//	---------------------------------------------------------------------------
//	jWebSocket - WebSocket CGI Token Client
//	Copyright (c) 2010 jWebSocket.org, Alexander Schulze, Innotrade GmbH
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
package org.jwebsocket.client.cgi;

import java.io.InputStream;
import java.io.OutputStream;

import org.jwebsocket.client.token.BaseTokenClient;
import org.jwebsocket.config.JWebSocketCommonConstants;
import org.jwebsocket.kit.WebSocketException;

/**
 *
 * @author aschulze
 */
public class CGITokenClient extends BaseTokenClient {

    private final static char START_FRAME = 0x02; // ASCII STX
    private final static char END_FRAME = 0x03; // ASCII ETX
    // used from JWebSocketCommonConstants from v0.10
    // private final static int MAX_FRAMESIZE = 16384;
    private boolean isRunning = false;
    private Thread inboundThread;
    private InboundProcess inboundProcess;
    private InputStream is = null;
    private OutputStream os = null;
    private OutputStream es = null;

    /**
     *
     * @param aListener
     */
    public CGITokenClient() {
    }

    @Override
    public void open(String aURL) throws WebSocketException {
        // establish connection to WebSocket Network
        super.open(aURL);

        // assign streams to CGI channels
        is = System.in;
        os = System.out;
        es = System.err;

        // instantiate thread to process messages coming from stdIn
        inboundProcess = new InboundProcess();
        inboundThread = new Thread(inboundProcess);
        inboundThread.start();
    }

    @Override
    public void close() throws WebSocketException {
        // stop CGI listener
        isRunning = false;
        // and close WebSocket connection
        super.close();
    }

    private class InboundProcess implements Runnable {

        @Override
        public void run() {
            isRunning = true;
            byte[] lBuff = new byte[JWebSocketCommonConstants.DEFAULT_MAX_FRAME_SIZE];
            int lIdx = -1;
            int lStart = -1;

            while (isRunning) {
                try {
                    int b = is.read();
                    // start of frame
                    if (b == START_FRAME) {
                        lIdx = 0;
                        lStart = 0;
                        // end of frame
                    } else if (b == END_FRAME) {
                        if (lStart >= 0) {
                            byte[] lBA = new byte[lIdx];
                            System.arraycopy(lBuff, 0, lBA, 0, lIdx);
                            // Arrays class is not supported in Android
                            // byte[] lBA = Arrays.copyOf(lBuff, pos);
                            send(lBA);
                        }
                        lStart = -1;
                        // end of stream
                    } else if (b < 0) {
                        isRunning = false;
                        // any other byte within or outside a frame
                    } else {
                        if (lStart >= 0) {
                            lBuff[lIdx] = (byte) b;
                        }
                        lIdx++;
                    }
                } catch (Exception ex) {
                    isRunning = false;
                    // throw new WebSocketException(ex.getClass().getSimpleName() + ": " + ex.getMessage());
                    // System.out.println(ex.getClass().getSimpleName() + ": " + ex.getMessage());
                }
            }

        }
    }
}
