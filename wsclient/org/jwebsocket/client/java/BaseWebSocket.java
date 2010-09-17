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
package org.jwebsocket.client.java;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.jwebsocket.api.WebSocketClient;
import org.jwebsocket.api.WebSocketClientEvent;
import org.jwebsocket.api.WebSocketClientListener;
import org.jwebsocket.api.WebSocketPacket;

import org.jwebsocket.api.WebSocketStatus;
import org.jwebsocket.client.token.WebSocketClientTokenEvent;
import org.jwebsocket.kit.RawPacket;
import org.jwebsocket.kit.WebSocketException;
import org.jwebsocket.kit.WebSocketHandshake;

import com.lixia.uag.websocket.Constents;

/**
 * Base {@code WebSocket} implementation based on
 * http://weberknecht.googlecode.com by Roderick Baier. This uses thread model
 * for handling WebSocket connection which is defined by the <tt>WebSocket</tt>
 * protocol specification. {@linkplain http://www.whatwg.org/specs/web-socket-protocol/} 
 * {@linkplain http://www.w3.org/TR/websockets/}
 * 
 * @author Roderick Baier
 * @author agali
 * @author puran
 * @version $Id:$
 */
public class BaseWebSocket implements WebSocketClient {
	
    /** WebSocket connection url */
    private URI url = null;
    /** list of the listeners registered */
    private List<WebSocketClientListener> listeners = new FastList<WebSocketClientListener>();
    /** flag for connection test */
    protected volatile boolean connected = false;
    private boolean isBinaryData = false;
    /** TCP socket */
    private Socket socket = null;
    /** IO streams */
    protected InputStream input = null;
    protected PrintStream output = null;
    /** Data receiver */
    private WebSocketReceiver receiver = null;
    private WebSocketHandshake handshake = null;
    /** represents the WebSocket status */
    private WebSocketStatus status = WebSocketStatus.CLOSED;

    /**
     * Base constructor
     */
    public BaseWebSocket() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open(String uriString) throws WebSocketException {
        URI uri = null;
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            throw new WebSocketException("Error parsing WebSocket URL:" + uriString, e);
        }
        this.url = uri;
        handshake = new WebSocketHandshake(url);
        try {
            socket = createSocket();
            input = socket.getInputStream();
            output = new PrintStream(socket.getOutputStream());

            output.write(handshake.getHandshake());

            boolean handshakeComplete = false;
            boolean header = true;
            int len = 1000;
            byte[] buffer = new byte[len];
            int pos = 0;
            ArrayList<String> handshakeLines = new ArrayList<String>();

            byte[] serverResponse = new byte[16];

            while (!handshakeComplete) {
                status = WebSocketStatus.CONNECTING;
                int b = input.read();
                buffer[pos] = (byte) b;
                pos += 1;

                if (!header) {
                    serverResponse[pos - 1] = (byte) b;
                    if (pos == 16) {
                        handshakeComplete = true;
                    }
                } else if (buffer[pos - 1] == 0x0A && buffer[pos - 2] == 0x0D) {
                    String line = new String(buffer, "UTF-8");
                    if (line.trim().equals("")) {
                        header = false;
                    } else {
                        handshakeLines.add(line.trim());
                    }

                    buffer = new byte[len];
                    pos = 0;
                }
            }

//System.out.printf("handshakeLines-len:%d,%s\n",handshakeLines.size(), handshakeLines.toString());
            handshake.verifyServerStatusLine(handshakeLines.get(0));
            handshake.verifyServerResponse(serverResponse);

            handshakeLines.remove(0);

            Map<String, String> headers = new FastMap<String, String>();
            for (String line : handshakeLines) {
                String[] keyValue = line.split(": ", 2);
                headers.put(keyValue[0], keyValue[1]);
            }
            handshake.verifyServerHandshakeHeaders(headers);

            receiver = new WebSocketReceiver(input);

            // TODO: Add event parameter
            // notifyOpened(null);

            receiver.start();
            connected = true;
            status = WebSocketStatus.OPEN;
        } catch (WebSocketException wse) {
            throw wse;
        } catch (IOException ioe) {
            throw new WebSocketException("error while connecting: " + ioe.getMessage(), ioe);
        }
    }

    @Override
    public void send(byte[] data) throws WebSocketException {
        if (!connected) {
            throw new WebSocketException("error while sending binary data: not connected");
        }
        try {
            if (isBinaryData) {
                output.write(0x80);
                // TODO: what if frame is longer than 255 characters (8bit?) Refer to IETF spec!
                output.write(data.length);
                output.write(data);                
            } else {
                output.write(0x00);
                output.write(data);
                output.write(0xff);
            }
            output.flush();
        } catch (IOException ioe) {
            throw new WebSocketException("error while sending binary data: ", ioe);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(String aData, String aEncoding) throws WebSocketException {
        byte[] data;
        try {
            data = aData.getBytes(aEncoding);
            send(data);
        } catch (UnsupportedEncodingException e) {
            throw new WebSocketException("Encoding exception while sending the data:" + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(WebSocketPacket dataPacket) throws WebSocketException {
        send(dataPacket.getByteArray());
    }

    public void handleReceiverError() {
        try {
            if (connected) {
                status = WebSocketStatus.CLOSING;
                close();
            }
        } catch (WebSocketException wse) {
            // TODO: don't use printStackTrace
            wse.printStackTrace();
        }
    }

    @Override
    public synchronized void close() throws WebSocketException {
        if (!connected) {
            return;
        }
        sendCloseHandshake();
        if (receiver.isRunning()) {
            receiver.stopit();
        }
        try {
            // input.close();
            // output.close();
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
            status = WebSocketStatus.CLOSED;
        } catch (IOException ioe) {
            throw new WebSocketException("error while closing websocket connection: ", ioe);
        }
        // TODO: add event
        notifyClosed(null);
    }

    private void sendCloseHandshake() throws WebSocketException {
        if (!connected) {
            throw new WebSocketException("error while sending close handshake: not connected");
        }
        try {
            output.write(0xff00);
            // TODO: check if final CR/LF is required/valid!
            output.write("\r\n".getBytes());
            // TODO: shouldn't we put a flush here?
        } catch (IOException ioe) {
            throw new WebSocketException("error while sending close handshake", ioe);
        }
        connected = false;
    }

    private Socket createSocket() throws WebSocketException {
        String scheme = url.getScheme();
        String host = url.getHost();
        int port = url.getPort();

        socket = null;

        if (scheme != null && scheme.equals("ws")) {
            if (port == -1) {
                port = 80;
            }
            try {
                socket = new Socket(host, port);
            } catch (UnknownHostException uhe) {
                throw new WebSocketException("unknown host: " + host, uhe);
            } catch (IOException ioe) {
                throw new WebSocketException("error while creating socket to " + url, ioe);
            }
        } else if (scheme != null && scheme.equals("wss")) {
            if (port == -1) {
                port = 443;
            }
            try {
                SocketFactory factory = SSLSocketFactory.getDefault();
                socket = factory.createSocket(host, port);
            } catch (UnknownHostException uhe) {
                throw new WebSocketException("unknown host: " + host, uhe);
            } catch (IOException ioe) {
                throw new WebSocketException("error while creating secure socket to " + url, ioe);
            }
        } else {
            throw new WebSocketException("unsupported protocol: " + scheme);
        }

        return socket;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isConnected() {
        if (connected && status.equals(WebSocketStatus.OPEN)) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    public WebSocketStatus getConnectionStatus() {
        return status;
    }

    /**
     * @return the client socket
     */
    public Socket getConnectionSocket() {
        return socket;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(WebSocketClientListener aListener) {
        listeners.add(aListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(WebSocketClientListener aListener) {
        listeners.remove(aListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WebSocketClientListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyOpened(WebSocketClientEvent aEvent) {
        for (WebSocketClientListener lListener : getListeners()) {
            lListener.processOpened(aEvent);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyPacket(WebSocketClientEvent aEvent, WebSocketPacket aPacket) {
        for (WebSocketClientListener lListener : getListeners()) {
            lListener.processPacket(aEvent, aPacket);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyClosed(WebSocketClientEvent aEvent) {
        for (WebSocketClientListener lListener : getListeners()) {
            lListener.processClosed(aEvent);
        }
    }

    class WebSocketReceiver extends Thread {

        private InputStream input = null;
        private volatile boolean stop = false;

        public WebSocketReceiver(InputStream input) {
            this.input = input;
        }

        @Override
        public void run() {
            boolean frameStart = false;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[Constents.WS_BUFFER_SIZE];
            notifyOpened(null);
            while (!stop) {
                try {
                    int b = input.read();
                    // TODO support binary frames
                    if (b == 0x00) {
                        frameStart = true;
                    } else if (b == 0xff && frameStart == true) {
                        frameStart = false;

                        WebSocketClientEvent lWSCE = new WebSocketClientTokenEvent();
                        RawPacket lPacket = new RawPacket(baos.toByteArray());

                        baos.reset();
                        notifyPacket(lWSCE, lPacket);
                    } else if (frameStart == true) {
                        // messageBytes.add((byte) b);
                        baos.write(b);
                    } 
                    else if((b&0x80)==0x80){
                    	int data = input.read();//read length
                    	int datalen = 0;
                    	datalen=datalen<<7 | (0x7f&data);
                    	if((data&0x80)==0x80){
                    		data = input.read();
                    		datalen=datalen<<7 | (0x7f&data);
                    		if((data&0x80)==0x80){
                    			data = input.read();
                        		datalen=datalen<<7 | (0x7f&data);
                    		}
                    	}
//                    	System.out.println("datalen:"+datalen);
                    	int len = 0;
                    	for(int i=0; i<datalen; ){//read content
                    		len = input.read(buffer, 0, (datalen-len)>buffer.length?buffer.length:(datalen-len));
                    		i+= len;
                    		for(WebSocketClientListener listener: listeners){
                        		listener.processStream(buffer, 0, len);
                        	}
                    	}
                    }
                    else if (b == -1) {
                        handleError();
                    }
                } catch (IOException ioe) {
                    handleError();
                }
            }
        }

        public void stopit() {
            stop = true;
        }

        public boolean isRunning() {
            return !stop;
        }

        private void handleError() {
        	System.out.println("handleError------>");
            stopit();
        }
    }
}
