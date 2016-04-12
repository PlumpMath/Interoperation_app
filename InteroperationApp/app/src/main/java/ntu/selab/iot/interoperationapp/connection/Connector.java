package ntu.selab.iot.interoperationapp.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.LinkedList;

import android.util.Log;

import ntu.selab.iot.interoperationapp.reactor.EventHandler;
import ntu.selab.iot.interoperationapp.reactor.Reactor;
import ntu.selab.iot.interoperationapp.serviceHandler.ServiceHandler;

public abstract class Connector extends EventHandler{
	private final static String TAG="Connector"; 
	protected LinkedList<ServiceHandler> pendingHandlerQueue;
	protected String mode;
	
	
	public Connector(Handle handle){
		setHandle(handle);
		pendingHandlerQueue=new LinkedList<ServiceHandler>();
		
	}
	
	public abstract boolean connect(InetAddress remoteAddress, int port, String mode ) throws IOException;
	
	
	public void connect(ServiceHandler sh, InetAddress remoteAddr, int remotePort, String mode, Reactor reactor) throws IOException{
//		Log.d(TAG, "I-connect:"+sh.handlerName+","+remoteAddr.getHostAddress()+","+remotePort);
		this.reactor = reactor;
        connectSvcHandler(sh, remoteAddr, remotePort, mode, reactor);
	}
	
	//For now, there is one ServiceHandler
	public void connectSvcHandler(ServiceHandler sh, InetAddress remoteAddr, int remotePort, String mode, Reactor reactor) throws IOException{
		if(!connect(remoteAddr, remotePort, mode)){//If connection has not been established.
			
			if(mode.equals("ASYC")){		
				Log.d(TAG,"I-register Connector to reactor");
				pendingHandlerQueue.add(sh);//There might be more than one ServiceHandlers will be bind to this Connector
				reactor.registerHandler(this,SelectionKey.OP_CONNECT);
				Log.d(TAG,"I-register Connector to reactor is finish");
				
			}else if (mode.equals("SYC")){
				Log.d(TAG,"I-SYC connection fail");			
			}else{

				Log.d(TAG,"I-connection type is undefined");
			}
		}else{
			Log.d(TAG,"I-Connector is connected");
			activateSvcHandler(sh,handle);
		}
	}
	
	public void activateSvcHandler(ServiceHandler sh, Handle handle)  {
		Log.d(TAG,"I-activateSvcHandler-setHandle");
		sh.setHandle(handle);
        try {
            sh.open();
        } catch (ClosedChannelException e) {
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
        }
    }
	
	public LinkedList<ServiceHandler> getPendingHandlerQueue(){
		return pendingHandlerQueue;
	}

	@Override
	public void handleEvent() throws IOException {
		
			ServiceHandler svcHandelr;
			svcHandelr=pendingHandlerQueue.remove();//There might be more than one ServiceHandlers will be bind to this Connector
			activateSvcHandler(svcHandelr,handle);
	}
}
