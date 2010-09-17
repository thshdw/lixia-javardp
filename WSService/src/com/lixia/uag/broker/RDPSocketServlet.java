package com.lixia.uag.broker;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;


public class RDPSocketServlet extends WebSocketServlet {

	TCPTunnel tunnel = null;
	private int connection_counter = 0;
	
	@Override
	protected WebSocket doWebSocketConnect(HttpServletRequest arg0, String arg1) {
    	if(TCPTunnelManager.getInstance()!=null){
    		TCPTunnelManager.getInstance().register(this);
    	}
		return new CounterSocket();
	}
	@Override
	public void destroy(){
		if(TCPTunnelManager.getInstance()!=null){
    		TCPTunnelManager.getInstance().deRegister(this);
    	}
	}
	
	final class CounterSocket implements WebSocket {

        private Outbound outbound;

        public void onConnect(final Outbound outbound) {
        	connection_counter++;
            System.out.println("onConnect:"+connection_counter);
            this.outbound = outbound;
        }
        
//        http://www.eb163.com/club/thread-6565-1-1.html
        
        public void onMessage(final byte frame, final String data) {
            System.out.println("onMessage:"+data+"frame:" + frame);

            
            if (data.startsWith("rdp")) {
            	tunnel = new TCPTunnel(outbound);
            	try {
					tunnel.open(data);
					try {
						outbound.sendMessage(frame, "success");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (UAGException e) {
					try {
						outbound.sendMessage(frame, "failed");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					tunnel.close();
					tunnel = null;
					e.printStackTrace();
				}
                
            }
        }

        public void onMessage(final byte frame, final byte[] data,
                              final int offset, final int length) {
			//any binary data from client will go to rdp server
            if(tunnel != null){
            	if(Constents.debug_mode)
            		System.out.println("frame:"+frame+"send to rdp server - len:"+length+"offset:"+offset + "data[] len:"+data.length);
            	try {
//            		outbound.sendMessage(frame, data, offset, length);
            		outbound.sendMessage((byte)0x00, "echo");//have to send back echo to clean outbound buffer, jetty websocket issue???
					tunnel.send(data, offset, length);
				} catch (Exception e) {
					e.printStackTrace();
					try {
						outbound.sendMessage((byte)0x00, "rdpclosed");
					} catch (IOException e1) {
					}
				}
//				System.out.println("send to rdp server done");
            }else{
            	System.out.println("error: tunnel is stopped!!!!");
            }
        }

        public void onDisconnect() {
        	connection_counter--;
            System.out.println("onDisconnect:"+connection_counter);
            if(connection_counter<=0 && tunnel != null)
            	tunnel.close();
        }
    }
}
