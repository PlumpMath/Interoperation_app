package ntu.selab.iot.interoperationapp.reactor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;

import ntu.selab.iot.interoperationapp.connection.Connector;
import ntu.selab.iot.interoperationapp.serviceHandler.GatewayDiscoverySvcHandler;

import junit.framework.TestCase;

public class ReactorTest extends TestCase {
	static Reactor reactor;
	static Thread thread;
	
	EventHandler connector;
	Connector Socketconnector_ASYC_TCP;
	GatewayDiscoverySvcHandler TCPConnetionHandler;
	
	public ReactorTest(String name) {
		super(name);
		
	}



	protected void setUp() throws Exception {
		super.setUp();
//		reactor = new Reactor();
//		Socketconnector_ASYC_TCP = new Connector(reactor, new TCPSocketConnector(new TCPSocketHandle("ASYC")));
//		sensorOperationHandler= new SensorOperationHandler(reactor);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		Socketconnector_ASYC_TCP.getHandle().close();
		
	}

	public void testRegisterHandler() {
	
			try {
				Socketconnector_ASYC_TCP.getHandle().connect(InetAddress.getByName("192.168.1.135"), 17001);
				reactor.registerHandler(Socketconnector_ASYC_TCP, SelectionKey.OP_CONNECT);
			} catch (ClosedChannelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			assertNotNull(reactor.getPendingHandler(Socketconnector_ASYC_TCP.getHandle()));

	}


	public void testRemoveHandler() {
		try {
			Socketconnector_ASYC_TCP.getHandle().connect(InetAddress.getByName("192.168.1.135"), 17001);
			reactor.registerHandler(Socketconnector_ASYC_TCP, SelectionKey.OP_CONNECT);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertNotNull(reactor.getPendingHandler(Socketconnector_ASYC_TCP.getHandle()));
		reactor.removeHandler(Socketconnector_ASYC_TCP);
		assertNull(reactor.getPendingHandler(Socketconnector_ASYC_TCP.getHandle()));
	}

	public void testHandleEventWithConnector() {
//		Thread thread = new Thread(reactor);
//		try {
//			Socketconnector_ASYC_TCP.connect(sensorOperationHandler, InetAddress.getByName("192.168.1.135"), 17001, "ASYC");
//			thread.start();
//			assertNotNull("Connector is registed successedly",reactor.getPendingHandler(Socketconnector_ASYC_TCP.getHandle()));
//			assertSame("Server is not on/Connector is not ready to finish",0,sensorOperationHandler.getState());
//			
//			
//			TCPSocketHandle handle = (TCPSocketHandle)Socketconnector_ASYC_TCP.getHandle();
//			
//			if(handle.isConnected()){
//				assertNotNull(sensorOperationHandler.getHandle());
//				assertSame(2,sensorOperationHandler.getState());
//			}
//			
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}


}
