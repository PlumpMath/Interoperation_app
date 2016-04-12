package ntu.selab.iot.interoperationapp.connector;



import java.io.IOException;

import junit.framework.TestCase;

import ntu.selab.iot.interoperationapp.connection.Connector;
import ntu.selab.iot.interoperationapp.reactor.Reactor;
import ntu.selab.iot.interoperationapp.serviceHandler.GatewayDiscoverySvcHandler;


public class ConnectorTest extends TestCase{
	
	Connector Socketconnector_ASYC_TCP;
	Connector Socketconnector_SYC_TCP;
	Reactor reactor;
	Thread thread;
	GatewayDiscoverySvcHandler sh;
	
	public ConnectorTest(String name){
		super(name);
	}


	public void setUp() throws Exception {
		reactor =new Reactor();
//		Socketconnector_ASYC_TCP= new Connector(reactor, new TCPSocketConnector(new TCPSocketHandle("ASYC")));
//		Socketconnector_SYC_TCP= new Connector(reactor, new TCPSocketConnector(new TCPSocketHandle("SYC")));
		sh = new GatewayDiscoverySvcHandler(reactor);
	}


	public void tearDown() throws Exception {
		reactor =null;
		Socketconnector_ASYC_TCP.getHandle().close();
		Socketconnector_SYC_TCP.getHandle().close();
		Socketconnector_ASYC_TCP= null;
		Socketconnector_SYC_TCP= null;
		sh = null;
	}


	public void testHandleEvent() {
		Socketconnector_ASYC_TCP.getPendingHandlerQueue().add(sh);
		try {
			Socketconnector_ASYC_TCP.handleEvent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(sh.getHandle());
		assertSame(2,sh.getState());
	}

	public void testActivateSvcHandler(){
		Socketconnector_ASYC_TCP.activateSvcHandler(sh,Socketconnector_ASYC_TCP.getHandle());
		assertSame(2,sh.getState());
	}


	public void testConnectSvcHandlerWith_ASYC_TCP() {
//		try {
//			Socketconnector_ASYC_TCP.connectSvcHandler(sh, InetAddress.getByName("192.168.1.135"), 17001, "ASYC");
//		}catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		assertNotNull(reactor.getPendingHandler(Socketconnector_ASYC_TCP.getHandle()));
		
	}


	public void testConnectSvcHandlerWith_SYC_TCP() {
//		try {
//			Socketconnector_SYC_TCP.connectSvcHandler(sh, InetAddress.getByName("192.168.1.135"), 17001, "SYC");
//			
//		} catch (IOException e) {
////			e.printStackTrace();
//			// TODO Auto-generated catch block
//			assertEquals("Connection timed out: connect" , e.getMessage());
//			assertSame(0,sh.getState());
//			return;
//		}
//		Set<SelectionKey> readyKeys = reactor.getSelector().selectedKeys();
//		assertSame("ServiceHandler is ready",1,readyKeys.size());
//		assertSame(2,sh.getState());
//		assertSame("ServiceHandler is not ready",0,readyKeys.size());
	}
	
}
