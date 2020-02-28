package com.lixia.rdp.rdp5.keys;
import java.io.IOException;
import com.lixia.rdp.RdesktopException;
import com.lixia.rdp.RdpPacket;
import com.lixia.rdp.crypto.CryptoException;
import com.lixia.rdp.rdp5.VChannel;
import com.lixia.rdp.rdp5.VChannels;
public class KeysChannel extends VChannel {

	@Override
	public int flags() {
		// TODO Auto-generated method stub
		return VChannels.CHANNEL_OPTION_INITIALIZED | VChannels.CHANNEL_OPTION_ENCRYPT_RDP;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "LMBLJA";
	}

	@Override
	public void process(RdpPacket data) throws RdesktopException, IOException,
			CryptoException {
		// TODO Auto-generated method stub
//		byte[] content = new byte[data.getEnd() - data.getPosition()];
//        data.copyToByteArray(content, 0, data.getPosition(), content.length);
		System.out.println("11111111111");
		//Position.debugPrintln("seamless message....." + content.length);
	}
	public static void main(String[] args) {
		System.out.println(VChannels.CHANNEL_OPTION_INITIALIZED | VChannels.CHANNEL_OPTION_ENCRYPT_RDP);
	}
}
