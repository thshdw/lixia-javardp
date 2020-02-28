package com.lixia.rdp.rdp5.rdpdr;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.lixia.rdp.Options;
import com.lixia.rdp.RdesktopException;
import com.lixia.rdp.RdpPacket;
import com.lixia.rdp.RdpPacket_Localised;
import com.lixia.rdp.crypto.CryptoException;
import com.lixia.rdp.rdp5.VChannel;
import com.lixia.rdp.rdp5.VChannels;

public class Rdpdr extends VChannel {

	private final static int  DEVICE_TYPE_SERIAL        =      0x01;
	private final static int  DEVICE_TYPE_PARALLEL      =      0x02;
	private final static int  DEVICE_TYPE_PRINTER       =      0x04;
	private final static int  DEVICE_TYPE_DISK          =      0x08;
	private final static int  DEVICE_TYPE_SCARD         =      0x20;

	/* NT status codes for RDPDR */
	private final static int RD_STATUS_SUCCESS             =     0x00000000;
	private final static int RD_STATUS_NOT_IMPLEMENTED     =     0x00000001;
	private final static int RD_STATUS_PENDING          =        0x00000103;

	private final static int RD_STATUS_NO_MORE_FILES      =      0x80000006;
	private final static int RD_STATUS_DEVICE_PAPER_EMPTY   =    0x8000000e;
	private final static int RD_STATUS_DEVICE_POWERED_OFF   =    0x8000000f;
	private final static int RD_STATUS_DEVICE_OFF_LINE    =      0x80000010;
	private final static int RD_STATUS_DEVICE_BUSY      =        0x80000011;

	private final static int RD_STATUS_INVALID_HANDLE     =      0xc0000008;
	private final static int RD_STATUS_INVALID_PARAMETER   =     0xc000000d;
	private final static int RD_STATUS_NO_SUCH_FILE      =       0xc000000f;
	private final static int RD_STATUS_INVALID_DEVICE_REQUEST=   0xc0000010;
	private final static int RD_STATUS_ACCESS_DENIED    =        0xc0000022;
	private final static int RD_STATUS_OBJECT_NAME_COLLISION =   0xc0000035;
	private final static int RD_STATUS_DISK_FULL       =         0xc000007f;
	private final static int RD_STATUS_FILE_IS_A_DIRECTORY  =    0xc00000ba;
	private final static int RD_STATUS_NOT_SUPPORTED    =        0xc00000bb;
	private final static int RD_STATUS_TIMEOUT       =           0xc0000102;
	private final static int RD_STATUS_NOTIFY_ENUM_DIR    =      0xc000010c;
	private final static int RD_STATUS_CANCELLED        =        0xc0000120;


	private final static int IRP_MJ_CREATE		=	0x00;
	private final static int IRP_MJ_CLOSE		=	0x02;
	private final static int IRP_MJ_READ		=	0x03;
	private final static int IRP_MJ_WRITE		=	0x04;
	private final static int	IRP_MJ_QUERY_INFORMATION	=0x05;
	private final static int IRP_MJ_SET_INFORMATION		=0x06;
	private final static int IRP_MJ_QUERY_VOLUME_INFORMATION	=0x0a;
	private final static int IRP_MJ_DIRECTORY_CONTROL	=0x0c;
	private final static int IRP_MJ_DEVICE_CONTROL		=0x0e;
	private final static int IRP_MJ_LOCK_CONTROL            = 0x11;

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

	/* CAPABILITY_HEADER.CapabilityType */
	private final static int CAP_GENERAL_TYPE   =  0x0001;
	private final static int CAP_PRINTER_TYPE   =  0x0002;
	private final static int CAP_PORT_TYPE      =  0x0003;
	private final static int CAP_DRIVE_TYPE     =  0x0004;
	private final static int CAP_SMARTCARD_TYPE =  0x0005;

	/* CAPABILITY_HEADER.Version */
	private final static int GENERAL_CAPABILITY_VERSION_01 =  0x00000001;
	private final static int GENERAL_CAPABILITY_VERSION_02  = 0x00000002;
	private final static int PRINT_CAPABILITY_VERSION_01   =  0x00000001;
	private final static int PORT_CAPABILITY_VERSION_01    =  0x00000001;
	private final static int DRIVE_CAPABILITY_VERSION_01   =  0x00000001;
	private final static int DRIVE_CAPABILITY_VERSION_02   =  0x00000002;
	private final static int SMARTCARD_CAPABILITY_VERSION_01 = 0x00000001;

	private final static int DR_MINOR_RDP_VERSION_5_0 = 0x0002;
	private final static int DR_MINOR_RDP_VERSION_5_1 = 0x0005;
	private final static int DR_MINOR_RDP_VERSION_5_2 = 0x000A;
	private final static int DR_MINOR_RDP_VERSION_6_X = 0x000C;

	/* GENERAL_CAPS_SET.extendedPDU */
	private final static int RDPDR_DEVICE_REMOVE_PDUS    =    0x00000001;
	private final static int RDPDR_CLIENT_DISPLAY_NAME_PDU  = 0x00000002;
	private final static int RDPDR_USER_LOGGEDON_PDU      =   0x00000004;

	/* GENERAL_CAPS_SET.extraFlags1 */
	private final static int ENABLE_ASYNCIO       =           0x00000001;

	/* DEVICE_ANNOUNCE.DeviceType */
	public final static int RDPDR_DTYP_SERIAL        =       0x00000001;
	public final static int RDPDR_DTYP_PARALLEL      =       0x00000002;
	public final static int RDPDR_DTYP_PRINT          =      0x00000004;
	public final static int RDPDR_DTYP_FILESYSTEM      =     0x00000008;
	public final static int RDPDR_DTYP_SMARTCARD       =     0x00000020;

	public final static int RDPDR_PRINTER_ANNOUNCE_FLAG_ASCII	=	0x00000001;
	public final static int RDPDR_PRINTER_ANNOUNCE_FLAG_DEFAULTPRINTER	=0x00000002;
	public final static int RDPDR_PRINTER_ANNOUNCE_FLAG_NETWORKPRINTER	=0x00000004;
	public final static int RDPDR_PRINTER_ANNOUNCE_FLAG_TSPRINTER		=0x00000008;
	public final static int RDPDR_PRINTER_ANNOUNCE_FLAG_XPSFORMAT		=0x00000010;

	public int rdpdr_version_minor = DR_MINOR_RDP_VERSION_5_2;
	public int rdpdr_clientid = 0;
	public String rdpdr_clientname = null;
	
	public ArrayList<RdpdrDevice> devices = new ArrayList<RdpdrDevice>();
	
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
					rdpdr_send_client_name_request();
					break;
				case PAKID_CORE_SERVER_CAPABILITY:
					/* server capabilities */
					rdpdr_process_capabilities(data);
					rdpdr_send_capabilities();
					break;

				case PAKID_CORE_CLIENTID_CONFIRM:
					rdpdr_send_capabilities();
					
					/* versionMinor 0x0005 doesn't send PAKID_CORE_USER_LOGGEDON,
					so we have to send it here */
					if (rdpdr_version_minor == 0x0005)
						rdpdr_send_device_list_announce_request();
					break;
					
				case PAKID_CORE_USER_LOGGEDON:
					rdpdr_send_device_list_announce_request();
					break;

				case PAKID_CORE_DEVICE_REPLY:
					/* connect to a specific resource */
					int deviceID = data.getLittleEndian32(); 
					int status = data.getLittleEndian32();
					break;

				case PAKID_CORE_DEVICE_IOREQUEST:
					rdpdr_process_irp(data);
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
	rdpdr_process_server_clientid_confirm(RdpPacket data)
	{
		int versionMinor = data.getLittleEndian16(); // versionMinor
		int clientID = data.getLittleEndian32(); // clientID

		if (rdpdr_clientid != clientID)
			rdpdr_clientid = clientID;

		if (versionMinor != rdpdr_version_minor)
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
		if(rdpdr_clientid > 0){
			s.setLittleEndian32(rdpdr_clientid); // clientID, given by the server in a Server Announce Request
		}else{
			s.setLittleEndian32(0x815ed39d);///* IP address (use 127.0.0.1) 0x815ed39d */
		}
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
		hostlen = rdpdr_clientname.length() * 2 ;

		s = new RdpPacket_Localised(16 + hostlen+ 2);
		s.setLittleEndian16(RDPDR_COMPONENT_TYPE_CORE);
		s.setLittleEndian16(PAKID_CORE_CLIENT_NAME);
		s.setLittleEndian32(1);// unicodeFlag, 0 for ASCII and 1 for Unicode
		s.setLittleEndian32(0);// codePage, must be set to zero
		s.setLittleEndian32(hostlen+ 2); // clientID, given by the server in a Server Announce Request
		if(hostlen > 0){
			try {
				s.copyFromByteArray(rdpdr_clientname.getBytes("UTF-16LE"),0,s.getPosition(),hostlen);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s.incrementPosition(hostlen);
		}
		s.setLittleEndian16(0);
		s.markEnd();
		
		try {
			this.send_packet(s);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	private void
	rdpdr_process_capabilities(RdpPacket data)
	{
		int i;
		int numCapabilities = data.getLittleEndian16();
		/* pad (2 bytes) */
		data.incrementPosition(2);

		for(i = 0; i < numCapabilities; i++)
		{
			int capabilityType = data.getLittleEndian16();

			switch (capabilityType)
			{
				case CAP_GENERAL_TYPE:
					rdpdr_process_general_capset(data);
					break;

				case CAP_PRINTER_TYPE:
					rdpdr_process_printer_capset(data);
					break;

				case CAP_PORT_TYPE:
					rdpdr_process_port_capset(data);
					break;

				case CAP_DRIVE_TYPE:
					rdpdr_process_drive_capset(data);
					break;

				case CAP_SMARTCARD_TYPE:
					rdpdr_process_smartcard_capset(data);
					break;

				default:
					//fprintf(stderr, "unimpl: Device redirection capability set type %d\n", capabilityType);
					break;
			}
		}
	}

	/* Process device direction general capability set */
	private void
	rdpdr_process_general_capset(RdpPacket data)
	{
		int capabilityLength = data.getLittleEndian16();/* capabilityLength */
		int version = data.getLittleEndian32();/* version */
		data.incrementPosition(4);/* osType, ignored on receipt (4 bytes) */
		data.incrementPosition(4);/* osVersion, unused and must be set to zero (4 bytes) */
		data.incrementPosition(2);/* protocolMajorVersion, must be set to 1 (2 bytes) */
		int protocolMinorVersion = data.getLittleEndian16();
		int ioCode1 = data.getLittleEndian32();
		data.incrementPosition(4);/* ioCode2, must be set to zero, reserved for future use (4 bytes) */
		int extendedPDU = data.getLittleEndian32();
		int extraFlags1 = data.getLittleEndian32();
		data.incrementPosition(4);/* extraFlags2, must be set to zero, reserved for future use (4 bytes) */

		/*
		 * SpecialTypeDeviceCap (4 bytes):
		 * present when GENERAL_CAPABILITY_VERSION_02 is used
		 */

		if (version == GENERAL_CAPABILITY_VERSION_02)
		{
			int specialTypeDeviceCap = data.getLittleEndian32();
		}

		return;
	}
	/* Process printer direction capability set */
	private void
	rdpdr_process_printer_capset(RdpPacket data)
	{
		int capabilityLength = data.getLittleEndian16();
		int version = data.getLittleEndian32();
	}
	/* Process port redirection capability set */
	private void
	rdpdr_process_port_capset(RdpPacket data)
	{
		int capabilityLength = data.getLittleEndian16();
		int version = data.getLittleEndian32();
	}
	private void
	rdpdr_process_drive_capset(RdpPacket data)
	{
		int capabilityLength = data.getLittleEndian16();
		int version = data.getLittleEndian32();
	}
	private void
	rdpdr_process_smartcard_capset(RdpPacket data)
	{
		int capabilityLength = data.getLittleEndian16();
		int version = data.getLittleEndian32();
	}
	private void
	rdpdr_send_capabilities()
	{
		RdpPacket_Localised s;

		s = new RdpPacket_Localised(0x54);
		s.setLittleEndian16(RDPDR_COMPONENT_TYPE_CORE);
		s.setLittleEndian16(PAKID_CORE_CLIENT_CAPABILITY);
		s.setLittleEndian16(5);// numCapabilities
		s.setLittleEndian16(0);// pad
		
		/* Output device direction general capability set */
//		s.setLittleEndian16(1);	/* first */
//		s.setLittleEndian16(0x28);	/* length */
//		s.setLittleEndian32(1);
//		s.setLittleEndian32(2);
//		s.setLittleEndian16(2);
//		s.setLittleEndian16(5);
//		s.setLittleEndian16(1);
//		s.setLittleEndian16(5);
//		s.setLittleEndian16(0xFFFF);
//		s.setLittleEndian16(0);
//		s.setLittleEndian32(0);
//		s.setLittleEndian32(3);
//		s.setLittleEndian32(0);
//		s.setLittleEndian32(0);

		s.setLittleEndian16(CAP_GENERAL_TYPE);
		s.setLittleEndian16(44);
		s.setLittleEndian32(GENERAL_CAPABILITY_VERSION_01);
		s.setLittleEndian32(0);// osType, ignored on receipt
		s.setLittleEndian32(0);// osVersion, unused and must be set to zero
		s.setLittleEndian16(1); // protocolMajorVersion, must be set to 1
		s.setLittleEndian16(DR_MINOR_RDP_VERSION_5_2);// protocolMinorVersion
		s.setLittleEndian32(0x0000FFFF); // ioCode1
		s.setLittleEndian32(0); // ioCode2, must be set to zero, reserved for future use
		s.setLittleEndian32(RDPDR_DEVICE_REMOVE_PDUS | RDPDR_CLIENT_DISPLAY_NAME_PDU | RDPDR_USER_LOGGEDON_PDU); // extendedPDU
		s.setLittleEndian32(ENABLE_ASYNCIO); // extraFlags1
		s.setLittleEndian32(0); // extraFlags2, must be set to zero, reserved for future use5f7pre
		s.setLittleEndian32(0); /* SpecialTypeDeviceCap, number of special devices to be redirected before logon */

		s.setLittleEndian16(CAP_PRINTER_TYPE);
		s.setLittleEndian16(8);
		s.setLittleEndian32(PRINT_CAPABILITY_VERSION_01);

		s.setLittleEndian16(CAP_PORT_TYPE);	/* third */
		s.setLittleEndian16(8);	/* length */
		s.setLittleEndian32(PORT_CAPABILITY_VERSION_01);
		
		s.setLittleEndian16(CAP_DRIVE_TYPE);	/* fourth */
		s.setLittleEndian16(8);	/* length */
		s.setLittleEndian32(DRIVE_CAPABILITY_VERSION_01);
		
		s.setLittleEndian16(CAP_SMARTCARD_TYPE);	/* fifth */
		s.setLittleEndian16(8);	/* length */
		s.setLittleEndian32(SMARTCARD_CAPABILITY_VERSION_01);

		s.markEnd();
		
		try {
			this.send_packet(s);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
		
	private int
	announcedata_size()
	{
		int size;
		size = 8;		/* static announce size */
		size += devices.size() * 0x14;

		for (RdpdrDevice dev : devices)
		{
			if (dev.type == RDPDR_DTYP_PRINT)
			{
				size += dev.deviceData.size();
			}
		}

		return size;
	}

	private  int
	 rdpdr_send_device_list_announce_request()
	 {
		RdpPacket_Localised s;

		s = new RdpPacket_Localised(announcedata_size());
	 	s.setLittleEndian16(RDPDR_COMPONENT_TYPE_CORE);
	 	s.setLittleEndian16(PAKID_CORE_DEVICELIST_ANNOUNCE);
	 	s.setLittleEndian32(devices.size()); /* deviceCount */

	 	for (RdpdrDevice dev : devices){
	 		s.setLittleEndian32(dev.type); /* deviceType */
	 		s.setLittleEndian32(devices.indexOf(dev)+1); /* deviceID */
	 		/* preferredDosName, Max 8 characters, may not be null terminated */
	 		String name = dev.name.replace(" ", "_").substring(0, dev.name.length()>8?8:dev.name.length());
	 		s.copyFromByteArray(name.getBytes(), 0, s.getPosition(), name.length());
		 	s.incrementPosition(8);
		 	
		 	s.setLittleEndian32(dev.deviceData.size());
		 	if(dev.deviceData.size()>0){
		 		s.copyFromPacket(dev.deviceData, 0, s.getPosition(), dev.deviceData.size());
		 		s.incrementPosition(dev.deviceData.size());
		 	}
	 	}
	 	s.markEnd();
		byte[] outputbyte = new byte[s.size()];
		s.copyToByteArray(outputbyte, 0, 0, s.size());
		try {
			this.send_packet(s);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	 	return 0;
	 }

	
	//devices list
	public void deviceRegister(RdpdrDevice newDevice){
		int port = 1;
		if(newDevice.type == RDPDR_DTYP_PRINT){
			for(RdpdrDevice dev : devices){
				if(dev.type == RDPDR_DTYP_PRINT){
					port++;
				}
			}
		}
		newDevice.register("", port);
		devices.add(newDevice);
	}
	
	private void rdpdr_process_irp(RdpPacket data){
		
		byte[] buffer=null;
		int buffer_len = 0;
		
		int deviceid = data.getLittleEndian32();
		int file = data.getLittleEndian32();
		int id = data.getLittleEndian32();
		int major = data.getLittleEndian32();
		int minor = data.getLittleEndian32();
		
		RdpdrDevice device =  devices.get(deviceid-1);
		switch(device.type){
		case DEVICE_TYPE_SERIAL:

			break;

		case DEVICE_TYPE_PARALLEL:

			break;

		case DEVICE_TYPE_PRINTER:

			break;

		case DEVICE_TYPE_DISK:

			break;

		case DEVICE_TYPE_SCARD:
		default:
			return;
	
		}
		
		switch (major)
		{
			case IRP_MJ_CREATE:
				device.create("PDFCreator");
				break;

			case IRP_MJ_CLOSE:
				device.close();
				break;

			case IRP_MJ_READ:
				break;
			case IRP_MJ_WRITE:
				buffer_len = 1;
				buffer = new byte[buffer_len];
				
				int length = data.getLittleEndian32();
				int offset = data.getLittleEndian32();
				data.incrementPosition(0x18);
				device.write(data, length, offset);
				break;

			case IRP_MJ_QUERY_INFORMATION:
				break;

			case IRP_MJ_SET_INFORMATION:
				break;

			case IRP_MJ_QUERY_VOLUME_INFORMATION:
				break;

			case IRP_MJ_DIRECTORY_CONTROL:
				break;

			case IRP_MJ_DEVICE_CONTROL:
				break;


			case IRP_MJ_LOCK_CONTROL:
				break;

			default:
				break;
		}
		int status = RD_STATUS_SUCCESS;
		int result = 0;
		rdpdr_send_completion(deviceid, id, status,result ,buffer, buffer_len);
	}
	
	void
	rdpdr_send_completion(int device, int id, int status, int result, byte[] buffer,
			      int length)
	{
		RdpPacket_Localised s;

		s = new RdpPacket_Localised(20+length);
		s.setLittleEndian16(RDPDR_COMPONENT_TYPE_CORE);//PAKID_CORE_DEVICE_REPLY?
		s.setLittleEndian16(PAKID_CORE_DEVICE_IOCOMPLETION);
		s.setLittleEndian32(device);
		s.setLittleEndian32(id);
		s.setLittleEndian32(status);
		s.setLittleEndian32(result);
		if(length > 0){
			s.copyFromByteArray(buffer, 0, s.getPosition(), length);
		}
		s.markEnd();
		
		try {
			this.send_packet(s);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}