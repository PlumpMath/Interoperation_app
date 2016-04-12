package ntu.selab.iot.interoperationapp.connector;



import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import ntu.selab.iot.interoperationapp.connection.TCPSocketChannelHandle;

import junit.framework.TestCase;



public class TCPSocketHandleTest extends TestCase{
	TCPSocketChannelHandle ASYC_handle;
	TCPSocketChannelHandle SYC_handle;
	
	public TCPSocketHandleTest(String name){
		super(name);
	}



	public void setUp() throws Exception {
		ASYC_handle = new TCPSocketChannelHandle("ASYC");
		SYC_handle = new TCPSocketChannelHandle("SYC");
	}


	public void tearDown() throws Exception {
		ASYC_handle.close();
		SYC_handle .close();
	}


	public void testConnectInetAddressInt_ASYC() {
		try {
			assertFalse(ASYC_handle.connect(InetAddress.getByName("192.168.1.135"), 17001));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void testConnectInetAddressInt_SYC() {
		boolean isConnected;
		try {
			isConnected=SYC_handle.connect(InetAddress.getByName("192.168.1.135"), 17001);
			//If Server is on
//			assertTrue(SYC_handle.connect(InetAddress.getByName("192.168.1.135"), 17001));
		}catch (IOException e) {
			e.printStackTrace();
			assertEquals("Connection timed out: connect",e.getMessage());
			return;
		}
		assertTrue(isConnected);
	}
	

}
