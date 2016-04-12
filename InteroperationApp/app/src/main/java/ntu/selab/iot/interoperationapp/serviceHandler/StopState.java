package ntu.selab.iot.interoperationapp.serviceHandler;

import android.util.Log;

import java.io.IOException;

import ntu.selab.iot.interoperationapp.protocol.communication.PacketInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.SensorInfo;

public class StopState extends State{
	public StopState(IPCameraControlHandler handler) {
		super(handler);
        Log.d(TAG,"Stop state is init");
		// TODO Auto-generated constructor stub
	}

	private final static String TAG= "StopState";
	@Override
	public void read() throws IOException {
		String respContent = handler.getMessage();
		Log.d(TAG, "I-Response: " + respContent);
		PacketInfo responce= gson.fromJson(respContent, PacketInfo.class);
		if(responce.getCommand().equals("Stop_Stream_Response")){
            Log.d(TAG,"Receive Stop Stream Response");
			handler.setNextState(new SetupState(this.handler));

			/*
			 * 
			 * 
			 * 
			 * */	
		}
	}

	@Override
	public void write(String cameraName, String uuid) throws IOException{
        if (!handler.isStartSend()) {
            PacketInfo pck = new PacketInfo();
            pck.setCommand("Stop_Stream");
            SensorInfo sensorInfo = new SensorInfo();
            sensorInfo.setName(cameraName);
            sensorInfo.setUuid(uuid);
            pck.addSensor(sensorInfo);
            String request = gson.toJson(pck);
            handler.sendMessage(request);
            Log.d(TAG, "I-Request: " + request);
		/*
		 * close
		 * 
		 */
        }
	}

}
