package com.lixia.uag.websocket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Random;

import org.jwebsocket.api.WebSocketClientEvent;
import org.jwebsocket.api.WebSocketClientListener;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.kit.WebSocketException;


public class LocalHttpTunnel {

	private String rdpAddr;
	private int rdpPort;
	private String http_server = null;
	
	private ServerSocket listener;
	private EnhancedWebSocket wsSocket;
	
	Socket server = null;
	private DataInputStream in=null;
	private PrintStream out = null;
	
	private boolean running=false;
	private int wsStatus = 0;//-1 - error, 0-close, 1-open, 2-rdp tunnel opened.
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String http_server = "localhost:8080/WSService/CounterSocketServlet";
		LocalHttpTunnel tunnel = new LocalHttpTunnel(http_server, "192.168.1.8", 3389);
		SocketAddress endpoint = tunnel.createTunnel();
		if(endpoint != null){
			//for test
//			Socket client = new Socket();
//			try {
//				client.connect(endpoint);
//				DataInputStream in = new DataInputStream (client.getInputStream());
//				while(true){
//					in.read();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//				tunnel.closeTunnel();
//			}
			
			while(true){
				if(tunnel.listener == null)
					break;
			}
			
		}
	}

	/**
	 * Checks to see if a specific port is available.
	 *
	 * @param port the port to check for availability
	 */
	public static ServerSocket createSocketServer(){
		Random rd = new Random();
		for(int port=Constents.MIN_PORT_NUMBER+rd.nextInt()%10; port<Constents.MIN_PORT_NUMBER+100; port++ ){
			ServerSocket ss = null;
		    try {
		        ss = new ServerSocket(port);
		        ss.setReuseAddress(true);
		        return ss;
		    } catch (IOException e) {
		        if (ss != null) {
		            try {
		                ss.close();
		            } catch (IOException ee) {
		                /* should not be thrown */
		            }
		        }
		    }
		}
	    return null;
	}

	public LocalHttpTunnel(String http_server, String rdpAddr, int rdpPort) {
		super();
		this.rdpAddr = rdpAddr;
		this.rdpPort = rdpPort;
		this.http_server = http_server;
	}
	
	public LocalHttpTunnel(String http_server,
			InetSocketAddress inetSocketAddress) {
		super();
		this.rdpAddr = inetSocketAddress.getAddress().getHostAddress();
		this.rdpPort = inetSocketAddress.getPort();
		this.http_server = http_server;
	}

	private boolean openWebSocket(){
		for(int i=0;i<3;i++){
			try {
				wsSocket.open("ws://"+http_server);
				return true;
			} catch (Exception e) {
				try {
					wsSocket.close();
				} catch (WebSocketException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
		return false;
	}
	public SocketAddress createTunnel(){
		
		listener = createSocketServer();
		if(listener == null){
			return null;
		}
		//create websocket connection here
		try{
			wsSocket = new EnhancedWebSocket();
			WebSocketClientListener wslistener = new WebSocketClient();
			wsSocket.addListener(wslistener);
				
			if(!openWebSocket()){
				throw new Exception("failed to create web socket");
			}
			
			for(int i=0; i < 100; i++){
				if(wsStatus == 2){
					System.out.println(listener.getLocalSocketAddress());
					TunnelTask task = new TunnelTask();
					running = true;
					task.start();
					return listener==null?null:listener.getLocalSocketAddress();
				}else if(wsStatus == -1){
					//error
					throw new Exception();
				}
				Thread.sleep(200);
			}
		}catch(Exception e){
		}
		closeTunnel();
		return null;
	}
	
	public void closeTunnel(){
		running = false;
		if (listener != null) {
            try {
            	listener.close();
            	listener = null;
            } catch (IOException ee) {
                /* should not be thrown */
            }
        }
		if(server != null){
			 try {
				 server.close();
				 server = null;
            } catch (IOException ee) {
                /* should not be thrown */
            }
		}
		if(wsSocket!=null){
			try {
				wsSocket.close();
			} catch (Exception e1) {
			}
		}
	}
	
	class TunnelTask extends Thread{
		public void run(){
			byte[] buffer = new byte[Constents.SOCKET_BUFFER_SIZE];
			try {
				server = listener.accept();
				in = new DataInputStream (server.getInputStream());
				out = new PrintStream(server.getOutputStream());
				
				//while to transfer data
				while(running){
					
					int len = in.read(buffer);
//					System.out.println("send len:"+len);
					if(len>0){
						try {
							wsSocket.sendBinary(buffer,len);
						} catch (WebSocketException e) {
							e.printStackTrace();
						}
					}else{
						running = false;
					}
				}
				System.out.println("stopped.");
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				closeTunnel();
			}
		}
	}
	
	class WebSocketClient implements WebSocketClientListener{
		@Override
		public void processOpened(WebSocketClientEvent aEvent) {
			if(wsStatus == 0){
				try {
					wsSocket.send(("rdp://"+rdpAddr+":"+rdpPort).getBytes());
				} catch (Exception e) {
					wsStatus = -1;
					return;
				}
				wsStatus = 1;
			  }
			
		}

		@Override
		public void processPacket(WebSocketClientEvent aEvent,
				WebSocketPacket aPacket) {
			
			if(aPacket.getFrameType() > 0){//binary
				//error, binary is processed in processStream();
				System.out.println("frametype:"+aPacket.getFrameType());
			}else{
//				System.out.println("string:"+aPacket.getString());
				if(aPacket.getString().equalsIgnoreCase("success")){
			    	wsStatus = 2;
			    }else if(aPacket.getString().equalsIgnoreCase("failed")){
			    	wsStatus = -1;
			    }else if(aPacket.getString().equalsIgnoreCase("rdpclosed")){
			    	wsStatus = -1;
			    	closeTunnel();
			    }
			}
			
		}

		@Override
		public void processClosed(WebSocketClientEvent aEvent) {
			wsStatus = -1;
		}

		@Override
		public void processStream(byte[] data, int offset, int length) {
//			System.out.println("processStream len:"+length);
			if(out != null){
				out.write(data, offset, length);
				out.flush();
			}
		}

	}
}
