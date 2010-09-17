package com.lixia.uag.broker;

import java.util.ArrayList;

public class TCPTunnelManager {
	private static TCPTunnelManager instance;
	
	private ArrayList<RDPSocketServlet> servlets = new ArrayList<RDPSocketServlet>();
	
	public static TCPTunnelManager getInstance(){
		if(instance == null){
			instance =  new TCPTunnelManager();
		}
		return instance;
	}
	
	synchronized public void register(RDPSocketServlet tunnel){
		servlets.add(tunnel);
	}
	
	synchronized public void deRegister(RDPSocketServlet tunnel){
		servlets.remove(tunnel);
	}
}
