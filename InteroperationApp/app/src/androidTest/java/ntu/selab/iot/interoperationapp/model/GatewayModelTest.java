package ntu.selab.iot.interoperationapp.model;

import java.io.IOException;

import ntu.selab.iot.interoperationapp.protocol.communication.SensorInfo;
import ntu.selab.iot.interoperationapp.reactor.Reactor;

import junit.framework.TestCase;

public class GatewayModelTest extends TestCase {
	
	GatewayModel gatewayModel;
	Reactor reactor;
	
	public GatewayModelTest(String name) {
		super(name);
	}


	protected void setUp() throws Exception {
//		gatewayModel = new GatewayModel();
	
	}

	protected void tearDown() throws Exception {
		reactor = null;
		gatewayModel = null;
	}	
	
	public void testConnect(){
//		try {
//			gatewayModel.connect(InetAddress.getByName("192.168.1.135"));
//			assertSame(1,gatewayModel.getConnector().getPendingHandlerQueue().size());
//			gatewayModel.getConnector().getHandle().close();
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	
	}
	
	//If server is on
	public void testGetSensors() throws InterruptedException {
		
//		try {
//			gatewayModel.connect(InetAddress.getByName("192.168.1.135"));
//			gatewayModel.startReactor();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
////			e.printStackTrace();
//			assertEquals("Connection timed out: connect" , e.getMessage());
//			return;
//		}
//		List<String[]> sensors = gatewayModel.getSensors();
//		System.out.print(sensors.get(0)[0]);
//		assertNotNull(sensors.get(0)[0]);
	}
	//If server is on
	public void testReadData() {
		
		try {
			
			gatewayModel.connect();
			gatewayModel.startReactor();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			assertEquals("Connection timed out: connect" , e.getMessage());
			return;
		}
		
		SensorInfo info =gatewayModel.readData(null);
		System.out.print(info);
		assertNotNull(info);
	}

}
