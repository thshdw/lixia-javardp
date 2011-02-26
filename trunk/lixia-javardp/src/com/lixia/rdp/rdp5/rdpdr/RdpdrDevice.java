package com.lixia.rdp.rdp5.rdpdr;

import com.lixia.rdp.RdpPacket;
import com.lixia.rdp.RdpPacket_Localised;

public abstract class RdpdrDevice {

	public String name;
	public int type;
	RdpPacket_Localised deviceData;
	
	public RdpdrDevice(int type) {
		super();
		this.type = type;
	}
	
	abstract public void register(String optarg, int port);
	abstract public void create(String filename);
	abstract public void write(RdpPacket data, int length, int offset);
	abstract public void close();
}
