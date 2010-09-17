package com.lixia.uag.websocket;

import org.jwebsocket.client.java.BaseWebSocket;
import org.jwebsocket.kit.WebSocketException;

public class EnhancedWebSocket extends BaseWebSocket {

    public void sendBinary(byte[] data, int length) throws WebSocketException {
        if (!connected) {
            throw new WebSocketException("error while sending binary data: not connected");
        }
        
     // maximum of 3 byte length == 21 bits
//        if (length>2097152)
//        	throw new IllegalArgumentException("too big");
        int length_bytes=(length>16384)?3:(length>128)?2:1;
        
        output.write(0x80);
        switch (length_bytes)
        {
	         case 3:
	        	 output.write((byte)(0x80|(length>>14)));
	         case 2:
	        	 output.write((byte)(0x80|(0x7f&(length>>7))));
	         case 1:
	        	 output.write((byte)(0x7f&length));
	     }
//		output.write(length);
		output.write(data, 0, length);                
		output.flush();
    }

}
