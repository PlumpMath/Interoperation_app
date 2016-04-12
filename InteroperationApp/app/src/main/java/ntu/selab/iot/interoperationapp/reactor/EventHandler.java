package ntu.selab.iot.interoperationapp.reactor;

import java.io.IOException;

import ntu.selab.iot.interoperationapp.connection.Handle;
import android.util.Log;

public abstract class EventHandler {
	public String handlerName;
	protected Handle handle;
    protected Reactor reactor;
	
	
	public abstract void handleEvent() throws IOException;
	
	public Handle getHandle(){
		return handle;
	}
	
	public void setHandle(Handle handle){
		Log.d("EventHandler", "I-setHandle");
		this.handle=handle;
	}
}
