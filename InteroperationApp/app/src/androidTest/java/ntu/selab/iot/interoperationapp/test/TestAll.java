package ntu.selab.iot.interoperationapp.test;

import ntu.selab.iot.interoperationapp.connection.TCPSocketConnector;
import ntu.selab.iot.interoperationapp.connector.ConnectorTest;
import ntu.selab.iot.interoperationapp.connector.SocketConnectorTest;
import ntu.selab.iot.interoperationapp.connector.TCPSocketHandleTest;
import ntu.selab.iot.interoperationapp.model.GatewayModelTest;
import ntu.selab.iot.interoperationapp.reactor.ReactorTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAll extends TestCase {

	public TestAll(String name) {
		super(name);
	}

	public static Test suite(){
		TestSuite suite = new TestSuite();
    	suite.addTestSuite(ConnectorTest.class);
    	suite.addTestSuite(SocketConnectorTest.class);
    	suite.addTestSuite(TCPSocketHandleTest.class);
    	suite.addTestSuite(GatewayModelTest.class);
    	suite.addTestSuite(ReactorTest.class);
 
    	return suite;
	}

}
