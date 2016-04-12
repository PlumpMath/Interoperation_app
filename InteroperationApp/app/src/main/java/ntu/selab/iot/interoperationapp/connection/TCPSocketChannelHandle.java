package ntu.selab.iot.interoperationapp.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

import android.util.Log;

public class TCPSocketChannelHandle extends Handle{
	

	SocketChannel sockChannel;//Can be non-block
	
	public TCPSocketChannelHandle(String mode) throws IOException{
		this.mode=mode;
		sockChannel=SocketChannel.open();
		if(mode.equals("ASYC")){
			sockChannel.configureBlocking(false);
		}else if(mode.equals("SYC")){
			sockChannel.configureBlocking(true);
		}else{
			Log.d("TCPSockeHamdle","mode is fail");
		}
	}
	
	public TCPSocketChannelHandle(InetAddress ip, int port) throws IOException{
		init(ip,port);
		sockChannel=SocketChannel.open();
		sockChannel.configureBlocking(false);
	}
	
	
//	public TCPSocketHandle(InetAddress ip, int port, int timeout) throws IOException{
//		init(ip,port,timeout);
//		sockChannel=SocketChannel.open();
//		sockChannel.configureBlocking(false);
//	}
	


//	public void init(InetAddress ip, int port, int timeout) throws IOException {
//		setTimeout(150);
//		init(ip,port);
//	}
//	
	public SocketChannel getSocketChannel(){
		return sockChannel;
	}
	
	public boolean isConnected() throws SocketException{
		return sockChannel.isConnected();
	}
	
	
	public boolean connect() throws IOException{
		return connect(ip,remotePort);
	}
	
	public boolean connect(InetAddress ip, int port) throws IOException{
		return sockChannel.connect(new InetSocketAddress(ip, port));
	}


	@Override
	public void close() throws IOException {
		 sockChannel.close();
	}

}
