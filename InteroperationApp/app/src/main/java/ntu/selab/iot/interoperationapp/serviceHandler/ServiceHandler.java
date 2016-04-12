package ntu.selab.iot.interoperationapp.serviceHandler;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;

import ntu.selab.iot.interoperationapp.connection.Handle;
import ntu.selab.iot.interoperationapp.reactor.EventHandler;
import ntu.selab.iot.interoperationapp.reactor.Reactor;
import ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.HandlerTask;

public abstract class ServiceHandler extends EventHandler{
    protected HandlerTask taskChain;
    private boolean isActive=false;

	public abstract void open() throws ClosedChannelException;

    public abstract void close() throws IOException;

    public void registerTask(HandlerTask task) {
        taskChain = task;
    }

    public void active(){
        isActive=true;
    }
    public void disActive(){
        isActive=false;
    }
    public boolean isActive(){
        return isActive;
    }

}
