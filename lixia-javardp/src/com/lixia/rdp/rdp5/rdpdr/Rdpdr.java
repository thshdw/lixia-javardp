package com.lixia.rdp.rdp5.rdpdr;

import java.io.IOException;
import java.net.UnknownHostException;

import com.lixia.rdp.Options;
import com.lixia.rdp.RdesktopException;
import com.lixia.rdp.RdpPacket;
import com.lixia.rdp.RdpPacket_Localised;
import com.lixia.rdp.crypto.CryptoException;
import com.lixia.rdp.rdp5.VChannel;
import com.lixia.rdp.rdp5.VChannels;

public class Rdpdr extends VChannel {

	/* RDPDR constants */
	private final static int RDPDR_COMPONENT_TYPE_CORE = 0x4472; // "sD" "Ds" ???
	private final static int RDPDR_COMPONENT_TYPE_PRINTING = 0x5052; // "RP" "PR" (PR)inting

	private final static int PAKID_CORE_SERVER_ANNOUNCE = 0x496E; // "nI" "nI" ???
	private final static int PAKID_CORE_CLIENTID_CONFIRM = 0x4343; // "CC" "CC" (C)lientID (C)onfirm
	private final static int PAKID_CORE_CLIENT_NAME	 = 0x434E; // "NC" "CN" (C)lient (N)ame
	private final static int PAKID_CORE_DEVICELIST_ANNOUNCE = 0x4441; // "AD" "DA" (D)evice (A)nnounce
	private final static int PAKID_CORE_DEVICE_REPLY	 = 0x6472; // "rd" "dr" (d)evice (r)eply
	private final static int PAKID_CORE_DEVICE_IOREQUEST = 0x4952; // "RI" "IR" (I)O (R)equest
	private final static int PAKID_CORE_DEVICE_IOCOMPLETION = 0x4943; // "CI" "IC" (I)O (C)ompletion
	private final static int PAKID_CORE_SERVER_CAPABILITY = 0x5350; // "PS" "SP" (S)erver (C)apability
	private final static int PAKID_CORE_CLIENT_CAPABILITY = 0x4350; // "PC" "CP" (C)lient (C)apability
	private final static int PAKID_CORE_DEVICELIST_REMOVE = 0x444D; // "MD" "DM" (D)evice list (R)emove
	private final static int PAKID_PRN_CACHE_DATA	 = 0x5043; // "CP" "PC" (P)rinter (C)ache data
	private final static int PAKID_CORE_USER_LOGGEDON = 0x554C; // "LU" "UL" (U)ser (L)ogged on
	private final static int PAKID_PRN_USING_XPS	 = 0x5543; // "CU" "UC" (U)sing (?)XPS

	private final static int DR_MINOR_RDP_VERSION_5_0 = 0x0002;
	private final static int DR_MINOR_RDP_VERSION_5_1 = 0x0005;
	private final static int DR_MINOR_RDP_VERSION_5_2 = 0x000A;
	private final static int DR_MINOR_RDP_VERSION_6_X = 0x000C;

	
	public int rdpdr_version_minor = DR_MINOR_RDP_VERSION_5_2;
	public int rdpdr_clientid = 0;
	public String rdpdr_clientname = null;
	
	@Override
	public int flags() {
		return VChannels.CHANNEL_OPTION_INITIALIZED
		| VChannels.CHANNEL_OPTION_COMPRESS_RDP;
	}

	@Override
	public String name() {
		return "rdpdr";
	}

	@Override
	public void process(RdpPacket data) throws RdesktopException, IOException,
			CryptoException {
		int component = data.getLittleEndian16();
		int packetID = data.getLittleEndian16();
		
		if (component == RDPDR_COMPONENT_TYPE_CORE)
		{
			
			switch (packetID)
			{
				case PAKID_CORE_SERVER_ANNOUNCE:
					rdpdr_process_server_announce_request(data);
					rdpdr_send_client_announce_reply();
//					rdpdr_send_client_name_request();
					break;

				case PAKID_CORE_CLIENTID_CONFIRM:
//					rdpdr_send_capabilities();
//					rdpdr_send_device_list();
					break;

				case PAKID_CORE_DEVICE_REPLY:
					/* connect to a specific resource */
//					in_uint32(s, handle);
//					DEBUG_RDP5("RDPDR: Server connected to resource %d\n", handle);
					break;

				case PAKID_CORE_DEVICE_IOREQUEST:
//					rdpdr_process_irp(s);
					break;

				case PAKID_CORE_SERVER_CAPABILITY:
					/* server capabilities */
//					rdpdr_process_capabilities(s);
					break;

				default:
//					ui_unimpl(NULL, "RDPDR core component, packetID: 0x%02X\n", packetID);
					break;

			}
		}
		else if (component == RDPDR_COMPONENT_TYPE_PRINTING)
		{
			switch (packetID)
			{
				case PAKID_PRN_CACHE_DATA:
//					printercache_process(s);
					break;

				default:
//					ui_unimpl(NULL, "RDPDR printer component, packetID: 0x%02X\n", packetID);
					break;
			}
		}
		else
			System.out.printf("RDPDR component: 0x%02X packetID: 0x%02X\n", component, packetID);
	}

	private void rdpdr_process_server_announce_request(RdpPacket data)
	{
		int versionMajor = data.getLittleEndian16();// versionMajor, must be 1
		int versionMinor = data.getLittleEndian16(); // versionMinor
		rdpdr_clientid = data.getLittleEndian32(); // clientID

		if(versionMinor < rdpdr_version_minor)
			rdpdr_version_minor = versionMinor;
	}
	
	private void
	rdpdr_send_client_announce_reply()
	{
		RdpPacket_Localised s;

		s = new RdpPacket_Localised(12);
		s.setLittleEndian16(RDPDR_COMPONENT_TYPE_CORE);
		s.setLittleEndian16(PAKID_CORE_CLIENTID_CONFIRM);
		s.setLittleEndian16(1);// versionMajor, must be set to 1
		s.setLittleEndian16(rdpdr_version_minor);// versionMinor
		s.setLittleEndian32(rdpdr_clientid); // clientID, given by the server in a Server Announce Request
		s.markEnd();
		
		try {
			this.send_packet(s);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private void
	rdpdr_send_client_name_request()
	{
		RdpPacket_Localised s;
		int hostlen;

		if (null == rdpdr_clientname)
		{
			try {
				rdpdr_clientname = java.net.InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				rdpdr_clientname = "127.0.0.1";
				e.printStackTrace();
			}
		}
		hostlen = rdpdr_clientname.length() * 2;

		s = new RdpPacket_Localised(16 + hostlen);
		s.setLittleEndian16(RDPDR_COMPONENT_TYPE_CORE);
		s.setLittleEndian16(PAKID_CORE_CLIENT_NAME);
		s.setLittleEndian32(1);// unicodeFlag, 0 for ASCII and 1 for Unicode
		s.setLittleEndian32(0);// codePage, must be set to zero
		s.setLittleEndian32(hostlen); // clientID, given by the server in a Server Announce Request
		if(hostlen > 0){
			s.copyFromByteArray(rdpdr_clientname.getBytes(),0,s.getPosition(),hostlen);
			s.incrementPosition(hostlen);
		}
		s.markEnd();
		
		try {
			this.send_packet(s);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private void
	rdpdr_send_capabilities()
	{
		RdpPacket_Localised s;

		s = new RdpPacket_Localised(0x50);
		s.setLittleEndian16(RDPDR_COMPONENT_TYPE_CORE);
		s.setLittleEndian16(PAKID_CORE_CLIENT_CAPABILITY);
		s.setLittleEndian16(2);//(5);// numCapabilities
		s.setLittleEndian16(0);// pad
		rdp_out_dr_general_capset(s);
		rdp_out_dr_printer_capset(s);
//		rdp_out_dr_port_capset(s);
//		rdp_out_dr_drive_capset(s);
//		rdp_out_dr_smartcard_capset(s);
		s.markEnd();
		
		try {
			this.send_packet(s);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/* Output device direction general capability set */
	private void
	rdp_out_dr_general_capset(RdpPacket_Localised s)
	{
		int cap_len = 4*2+2*2+4*5;
		
		s.setLittleEndian16(DR_CAPSET_TYPE_GENERAL);
		s.setLittleEndian16(cap_len);
		s.setLittleEndian32(DR_GENERAL_CAPABILITY_VERSION_01);
		s.setLittleEndian32(0);// osType, ignored on receipt
		s.setLittleEndian32(0);// osVersion, unused and must be set to zero
		s.setLittleEndian16(1); // protocolMajorVersion, must be set to 1
		s.setLittleEndian16(DR_MINOR_RDP_VERSION_5_2);// protocolMinorVersion
		out_uint16_le(s, DR_MINOR_RDP_VERSION_5_2); 
		out_uint32_le(s, 0x0000FFFF); // ioCode1
		out_uint32_le(s, 0); // ioCode2, must be set to zero, reserved for future use
		out_uint32_le(s, DR_DEVICE_REMOVE_PDUS | DR_CLIENT_DISPLAY_NAME_PDU); // extendedPDU
		out_uint32_le(s, DR_ENABLE_ASYNCIO); // extraFlags1
		out_uint32_le(s, 0); // extraFlags2, must be set to zero, reserved for future use5f7pre
	}
}
