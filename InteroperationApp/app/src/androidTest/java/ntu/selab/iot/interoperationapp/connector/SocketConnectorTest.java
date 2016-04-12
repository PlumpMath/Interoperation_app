package ntu.selab.iot.interoperationapp.connector;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import ntu.selab.iot.interoperationapp.connection.TCPSocketConnector;
import ntu.selab.iot.interoperationapp.connection.TCPSocketChannelHandle;
import ntu.selab.iot.interoperationapp.connection.UDPSocketChannelHandle;

import junit.framework.TestCase;


public class SocketConnectorTest extends TestCase {
	
	TCPSocketChannelHandle ASYC_TCP_handle;
	TCPSocketChannelHandle SYC_TCP_handle;
	UDPSocketChannelHandle ASYC_UDP_handle;
	UDPSocketChannelHandle SYC_UDP_handle;
	TCPSocketConnector ASYC_Connector;
	TCPSocketConnector SYC_Connector;
	TCPSocketConnector Non_Handle_Connector;
	
	public SocketConnectorTest(String name) {
		super(name);
		try {
			ASYC_TCP_handle = new TCPSocketChannelHandle("ASYC");
			SYC_TCP_handle = new TCPSocketChannelHandle("SYC");
			ASYC_Connector = new TCPSocketConnector(ASYC_TCP_handle);
			SYC_Connector = new TCPSocketConnector(SYC_TCP_handle);
			Non_Handle_Connector = new TCPSocketConnector(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void setUp() throws Exception {
		
	}
	
	
	public void tearDown() throws Exception {
		
	}

	public void testConnectionWithNoHandle() {
		try {
			assertFalse(Non_Handle_Connector.connect(InetAddress.getByName("192.168.1.135"), 17001, "ASYC"));
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public void testConnectionWithASYC_TCP_handle() {
		try {

			assertFalse(ASYC_Connector.connect(InetAddress.getByName("192.168.1.135"), 17001, "ASYC"));
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//If Server is on
	public void testConnectionWithSYC_TCP_handle() {
		try {

			assertTrue(SYC_Connector.connect(InetAddress.getByName("192.168.1.135"), 17001, "SYC"));
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			assertEquals("Connection timed out: connect" , e.getMessage());
		}
	}

}
