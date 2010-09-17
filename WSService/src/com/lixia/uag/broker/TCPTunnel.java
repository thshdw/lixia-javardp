package com.lixia.uag.broker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.eclipse.jetty.websocket.WebSocket.Outbound;

public class TCPTunnel {

	private static int BUFFER_SIZE = 1024*6;
	
	private Socket rdpsock = null;
	private URI url = null;
	
	private volatile boolean connected = false;
	
	private Outbound outbound;
	
	/** IO streams */
    private DataInputStream input = null;
    private DataOutputStream output = null;
    private TunnelReceiver receiver = null;
    
    TCPTunnel(Outbound outbound){
    	this.outbound = outbound;
    }
    
	public void open(String uriString) throws UAGException{
		
		URI uri = null;
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            throw new UAGException("Error parsing URL:" + uriString, e);
        }
        this.url = uri;
        
        String scheme = url.getScheme();
        String host = url.getHost();
        int port = url.getPort();
        
        if (scheme != null && scheme.equals("rdp")) {
        	try {
        		rdpsock = new Socket(host, port);
        		rdpsock.setTcpNoDelay(true);
				this.input = new DataInputStream(new BufferedInputStream(rdpsock
						.getInputStream()));
				this.output = new DataOutputStream(new BufferedOutputStream(rdpsock
						.getOutputStream()));
	            connected = true;
	            
	            receiver = new TunnelReceiver(input, outbound);
	            receiver.start();
	            System.out.println("tcp connection setup!");
			} catch (UnknownHostException e) {
				throw new UAGException("unknown host:" + uriString, e);
			} catch (IOException e) {
				throw new UAGException("error while connect to:" + uriString, e);
			}
        }
	}
	
	public void send(byte[] data, final int offset, final int length) throws UAGException {
        if (!connected) {
            throw new UAGException("error while sending binary data: not connected");
        }
        try {
			output.write(data, offset, length);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
//			throw new UAGException("error while sending binary data: IOException");
		}                
		
    }
	
	public synchronized void close(){
        if (!connected) {
            return;
        }
        
        connected = false;
        
        if (receiver.isRunning()) {
            receiver.stopit();
        }
        try {
            // input.close();
            // output.close();
        	rdpsock.shutdownInput();
        	rdpsock.shutdownOutput();
        	rdpsock.close();
        } catch (IOException ioe) {
        }
    }
	class TunnelReceiver extends Thread {

		private Outbound outbound;
        private InputStream input = null;
        private volatile boolean stop = false;

        public TunnelReceiver(InputStream input, Outbound outbound) {
            this.input = input;
            this.outbound = outbound;
        }

        @Override
        public void run() {
        	byte[] buffer = new byte[BUFFER_SIZE];
            while (!stop) {
                try {
                	//data bridge between RDP server and websocket client
                    int len = input.read(buffer);
//                    System.out.println("get data from rdp server:"+len);
                    if(len > 0)
                    	outbound.sendMessage((byte)0x80, buffer, 0, len);//0x80 means binary data in websocket
                } catch (IOException ioe) {
                	ioe.printStackTrace();
                    handleError();
                }
            }
            System.out.println("receiver stopped!");
        }

        public void stopit() {
            stop = true;
            
            close();
        }

        public boolean isRunning() {
            return !stop;
        }

        private void handleError() {
            stopit();
        }
    }
}
