package ntu.selab.iot.interoperationapp.connection;

import java.io.IOException;
import java.net.InetAddress;

import android.util.Log;

public class TCPSocketConnector extends Connector{

	public TCPSocketConnector(TCPSocketChannelHandle handle) {
		super(handle);
		
	}

	public boolean connect( InetAddress remoteAddr, int remotePort, String mode) throws IOException {
		Log.d("TCPConnector","I-TCPConnector is Connecting");
		this.mode=mode;
		handle.init(remoteAddr, remotePort);
		if(handle.connect()){
			return true;
		}else{
			return false;
		}
	}

	
	
}
