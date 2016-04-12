package ntu.selab.iot.interoperationapp.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.channels.spi.AbstractSelectableChannel;

public abstract class Handle {
	protected	String serviceName;
	protected int remotePort;
	protected InetAddress ip;
	protected int timeout;
	protected String mode;
	
	public void init(InetAddress ip, int port) {
		setIp(ip);
		setPort(port);
	}
	
	public void setServiceName(String serviceName){
		this.serviceName=serviceName;	
	}

	public void setPort(int port){
		remotePort=port;
	}
	
	public void setIp(InetAddress ip){
		this.ip=ip;
	}
	
	public void setTimeout(int timeOut){
		this.timeout=timeOut;
	} 
	
	public String getServiceName(){
		return serviceName;	
	}

	public int getPort(){
		return remotePort;
	}
	
	public InetAddress getIp(){
		return ip;
	}
	
	public int getTimeout(){
		return timeout;
	} 
	
	public abstract boolean isConnected() throws SocketException;
	public abstract boolean connect(InetAddress ip, int port) throws IOException;
	public abstract boolean connect() throws IOException;
	public abstract void close()  throws IOException;
	
}

