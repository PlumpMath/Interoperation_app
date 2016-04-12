package ntu.selab.iot.interoperationapp.connection;

import java.io.IOException;
import java.net.InetAddress;

import android.util.Log;

public class UDPSocketConnector extends Connector{

	public UDPSocketConnector(UDPSocketChannelHandle handle) {
		super(handle);
	}

	@Override
	public boolean connect(InetAddress remoteAddress, int remotePort, String mode)
			throws IOException {
		this.mode=mode;
		Log.d("UDPSocketConnector","I-UDPConnector is connecting");
			
	
		handle.init(remoteAddress, remotePort);
		if(handle.connect()){
			return true;
		}else{
			return false;
		}
	}

}
