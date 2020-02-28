/* UnicodeHandler.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.1.1.1 $
 * Author: $Author: suvarov $
 * Date: $Date: 2007/03/08 00:26:40 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: 
 */
package com.lixia.rdp.rdp5.cliprdr;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.UnsupportedEncodingException;

import com.lixia.rdp.Common;
import com.lixia.rdp.RdpPacket;
import com.lixia.rdp.RdpPacket_Localised;
import com.lixia.rdp.Utilities_Localised;

public class UnicodeHandler extends TypeHandler {

	public boolean formatValid(int format) {
		return (format == CF_UNICODETEXT);
	}

	public boolean mimeTypeValid(String mimeType) {
		return mimeType.equals("text");
	}

	public int preferredFormat() {
		return CF_UNICODETEXT;
	}

	public void handleData(RdpPacket data, int length, ClipInterface c) {
//		String thingy = "";
		byte[] sBytes = new byte[length];
		data.copyToByteArray(sBytes, 0, data.getPosition(), length);
		data.incrementPosition(length);
//		for(int i = 0; i < length; i+=2){
//			int aByte = data.getLittleEndian16();
//			if(aByte != 0) thingy += (char) (aByte);
//		}
		try {
			c.copyToClipboard(new StringSelection(new String(sBytes, "UTF-16LE")));
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
		}
//		c.copyToClipboard(new StringSelection(thingy));
		//return(new StringSelection(thingy));
	}

	public String name() {
		return "CF_UNICODETEXT";
	}

	public byte[] fromTransferable(Transferable in) {
		String s;
		if (in != null)
		{
			try {
				s = (String)(in.getTransferData(DataFlavor.stringFlavor));
			} 
			catch (Exception e) {
				e.printStackTrace();
//				s = e.toString();
				return null;
			}
			
			// TODO: think of a better way of fixing this
			s = s.replace('\n',(char) 0x0a);
			//s = s.replaceAll("" + (char) 0x0a, "" + (char) 0x0d + (char) 0x0a);
//			s = Utilities_Localised.strReplaceAll(s, "" + (char) 0x0a, "" + (char) 0x0d + (char) 0x0a);
			byte[] sBytes = null;
			try {
				sBytes = s.getBytes("UTF-16LE");
			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
			}
//			int length = sBytes.length;
//			int lengthBy2 = length*2;
//			RdpPacket p = new RdpPacket_Localised(lengthBy2);
//			for(int i = 0; i < sBytes.length; i++){
//				p.setLittleEndian16(sBytes[i]);
//			}
//			sBytes = new byte[length*2];
//			p.copyToByteArray(sBytes,0,0,lengthBy2);
			
			return sBytes;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.lixia.rdp.rdp5.cliprdr.TypeHandler#send_data(java.awt.datatransfer.Transferable)
	 */
	public void send_data(Transferable in, ClipInterface c) {
		byte[] data = fromTransferable(in);
		c.send_data(data,data.length);
	}

}
