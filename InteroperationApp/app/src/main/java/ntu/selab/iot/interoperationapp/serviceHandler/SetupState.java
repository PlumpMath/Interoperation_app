package ntu.selab.iot.interoperationapp.serviceHandler;

import java.io.IOException;

import ntu.selab.iot.interoperationapp.protocol.communication.DataInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.ExpressionInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.PacketInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.SensorInfo;
import ntu.selab.iot.interoperationapp.serviceHandler.p2p.IPCameraStreamingHandler;

import android.util.Log;

public class SetupState extends State{
	
	int test=0;
	protected SetupState(IPCameraControlHandler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}

	private final static String TAG= "SetupState";
	@Override
	public void read() throws IOException{
		String respContent = handler.getMessage();
		Log.d(TAG, "I-Response: " + respContent);
		PacketInfo responce= gson.fromJson(respContent, PacketInfo.class);
		if(responce.getCommand().equals("Setup_Stream_Response")){
			if(responce.getSensors()[0].getData()[0].getExpressions()[0].getUnit().equals("Port")){
				double port=(Double) responce.getSensors()[0].getData()[0].getExpressions()[0].getValue();
				int remotePort =(int) port;
				handler.setRemotePort(remotePort);
			}
			handler.setNextState(new StartState(handler));
		}
	}

	@Override
	public void write(String cameraName, String uuid) throws IOException{
			Log.d(TAG, "I-write"+test);
			test++;
			int localPort =0;
			IPCameraStreamingHandler ipCameraStreamingHandler;
			
			try {
				ipCameraStreamingHandler = handler.createIPCameraStreamingHandler();
				localPort = ipCameraStreamingHandler.getLocalPort();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			PacketInfo pck = new PacketInfo();
			pck.setCommand("Setup_Stream");
			SensorInfo sensorInfo = new SensorInfo();
			sensorInfo.setName(cameraName);
			sensorInfo.setUuid(uuid);
			
			DataInfo clientPort = new DataInfo();
			clientPort.setType("Client port");
			ExpressionInfo expressionInfo = new ExpressionInfo();
			expressionInfo.setUnit("Port");
			expressionInfo.setValue(localPort);
			Log.d(TAG, "I-(write)port:"+localPort);
			expressionInfo.setClassName("java.lang.Integer");
			clientPort.addExpression(expressionInfo);
			sensorInfo.addData(clientPort);
			 
			DataInfo StreamingData = new DataInfo();
			StreamingData.setType("Streaming Data");
			ExpressionInfo track = new ExpressionInfo();
			track.setUnit("Track id");
			track.setValue(handler.getMediaInfo().getMediaControl());
			track.setClassName("java.lang.String");
			StreamingData.addExpression(track);
			sensorInfo.addData(StreamingData);
			
			pck.addSensor(sensorInfo);
			String request = gson.toJson(pck);
			handler.sendMessage(request);
			Log.d(TAG, "I-Request: " + request);
	}

}
