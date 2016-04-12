package ntu.selab.iot.interoperationapp.serviceHandler;

import java.io.IOException;

import ntu.selab.iot.interoperationapp.protocol.communication.DataInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.ExpressionInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.PacketInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.SensorInfo;
import android.util.Log;

public class StartState extends State {

	public StartState(IPCameraControlHandler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}

	private final static String TAG = "StartState";

	@Override
	public void read() throws IOException{
		String respContent = handler.getMessage();
		Log.d(TAG, "I-Response: " + respContent);
		PacketInfo responce = gson.fromJson(respContent, PacketInfo.class);
		if (responce.getCommand().equals("Start_Stream_Response")) {
			handler.setNextState(new StopState(handler));
		}
	}

	@Override
	public void write(String cameraName, String uuid) throws IOException{
		if (handler.isStartSend()) {// Let user decide when to start.
			try {
				handler.createIPCameraQosHandler();
			} catch (IOException e) {
//				TODO Auto-generated catch block
				e.printStackTrace();
			}
			PacketInfo pck = new PacketInfo();
			pck.setCommand("Start_Stream");
			SensorInfo sensorInfo = new SensorInfo();
			DataInfo dataInfo = new DataInfo();
			dataInfo.setType("Streaming Data");
			ExpressionInfo expressionInfo = new ExpressionInfo();
			expressionInfo.setUnit("Track id");
			expressionInfo.setValue(handler.getMediaInfo().getMediaControl());
			expressionInfo.setClassName("java.lang.String");
			dataInfo.addExpression(expressionInfo);
			sensorInfo.addData(dataInfo);
			sensorInfo.setName(cameraName);
			sensorInfo.setUuid(uuid);
			pck.addSensor(sensorInfo);
			String request = gson.toJson(pck);
			handler.sendMessage(request);
			Log.d(TAG, "I-Request: " + request);
		}

	}

}
