package ntu.selab.iot.interoperationapp.serviceHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

public abstract class State {
	
	protected IPCameraControlHandler handler;
	protected Gson gson = new GsonBuilder().create();
	
	
	public abstract void read()throws IOException;
	public abstract void write(String cameraName, String uuid)throws IOException;
	
	protected State(IPCameraControlHandler handler){
		this.handler=handler;
	}

	
}
