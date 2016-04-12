package ntu.selab.iot.interoperationapp.reactor;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import ntu.selab.iot.interoperationapp.connection.Handle;
import ntu.selab.iot.interoperationapp.connection.PseudoTCPSocketHandle;
import ntu.selab.iot.interoperationapp.connection.TCPSocketChannelHandle;
import ntu.selab.iot.interoperationapp.connection.UDPSocketChannelHandle;
import ntu.selab.iot.interoperationapp.connection.UDPSocketHandle;
import ntu.selab.iot.interoperationapp.model.ScenarioModel;
import ntu.selab.iot.interoperationapp.service.InteroperabilityService;

import android.util.Log;

public class Reactor implements Runnable {
    private String TAG = "Reactor";
    private Selector selector;
    //	private Queue<Runnable> pendingTasks = new ConcurrentLinkedQueue<Runnable>();
    private HashMap<EventHandler, Integer> ICEHandlerTriggerMap = new HashMap<EventHandler, Integer>();
    private Queue<EventHandler> pendingEventHandler = new ConcurrentLinkedQueue<EventHandler>();
    private Boolean terminating = false;



    public Reactor() throws IOException {
        Log.d(TAG, "I-(Reactor)create");

        selector = Selector.open();
    }

    public void setTerminate(boolean terminate) {
        terminating = terminate;
    }

    public boolean getTerminate() {
        return terminating;
    }

    public SelectionKey

    registerHandler(EventHandler eh, int eventType) throws ClosedChannelException {
        SelectionKey key = null;
        if (eh.getHandle() instanceof TCPSocketChannelHandle) {
            Log.d(TAG, "I-(registerHandler)register TCP to reactor");
            TCPSocketChannelHandle handle = (TCPSocketChannelHandle) eh.getHandle();
//            Log.d(TAG, "I-(registerHandler)get handle");
            key = handle.getSocketChannel().register(selector, eventType, eh);
//            Log.d(TAG, "I-(registerHandler)register TCP to reactor finish?");
            selector.wakeup();
//            Log.d(TAG, "I-(registerHandler)register TCP to reactor finish");
            pendingEventHandler.add(eh);

        } else if (eh.getHandle() instanceof UDPSocketChannelHandle) {//can be merged to above
            Log.d(TAG, "I-(registerHandler)register UDP to reactor");
            UDPSocketChannelHandle handle = (UDPSocketChannelHandle) eh.getHandle();
            key = handle.getSocketChannel().register(selector, eventType, eh);
//            Log.d(TAG, "I-(registerHandler)register UDP to reactor finish?");
            selector.wakeup();
//            Log.d(TAG, "I-(registerHandler)register UDP to reactor finish");
            pendingEventHandler.add(eh);
        } else if (eh.getHandle() instanceof PseudoTCPSocketHandle) {
            Log.d(TAG, "I-(registerHandler)register ICE_TCP_Handle to reactor");
            ICEHandlerTriggerMap.put(eh, eventType);
            pendingEventHandler.add(eh);
        }
        else if(eh.getHandle() instanceof UDPSocketHandle){
            Log.d(TAG, TAG +"I-(registerHandler)register ICE_UDP_Handle to reactor");
            ICEHandlerTriggerMap.put(eh, eventType);
            pendingEventHandler.add(eh);
        }
        return key;
    }


    public void handleEvent() throws IOException {
        if(TAG=="CameraReactor"){
            Log.d(TAG,  TAG+"handle");
        }

        while (!terminating) {
//			Log.d("Reactor","I-Reactor is working");

            /*Selector part*/

//				if(selector.select()==0){
//					continue;
//				}
            if(TAG=="CameraReactor"){
                Log.d(TAG,  TAG+"in while");
            }
            if(pendingEventHandler.size()==0){
                if(TAG=="CameraReactor"){
                    Log.d(TAG,  TAG+"pendingEventHandler.size()==0");
                }
                continue;
            }

            selector.select(100);
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<?> it = readyKeys.iterator();
            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                it.remove();
                if (!key.isValid()) {
                    continue;
                }
                EventHandler eventHandler = (EventHandler) key.attachment();
                if (key.isConnectable()) {
                    if (((SocketChannel) key.channel()).finishConnect()) {
                        Log.d(TAG, "I-(handleEvent)TCP Connection is detect");
                        eventHandler.handleEvent();
                        pendingEventHandler.remove(eventHandler);
                    }
                } else if (key.isReadable()) {
                    eventHandler.handleEvent();
                } else if (key.isWritable()) {
                    eventHandler.handleEvent();
                }

            }
            readyKeys.clear();
//            Log.d(TAG,TAG + "Reactor  handle ICE");

            if(TAG=="CameraReactor"){
                Log.d(TAG,  TAG+"start ICE ");
            }

            /*ICE detection part*/
            Map map = Collections.synchronizedMap(ICEHandlerTriggerMap);
            Set set = map.keySet();
            synchronized (map) {
                Iterator<EventHandler> triggerMapIterator = set.iterator();
                while(triggerMapIterator.hasNext()){
                    if(TAG=="CameraReactor"){
                        Log.d(TAG,TAG + "triggerMapIterator in while ");
                    }


                    EventHandler eventHandler = triggerMapIterator.next();
                    Handle handle = eventHandler.getHandle();
//                    Log.d(TAG,TAG + "Reactor test 2");
                    if(ICEHandlerTriggerMap.get(eventHandler)==SelectionKey.OP_READ) {
                        if(TAG=="CameraReactor"){
                            Log.d(TAG,TAG + "SelectionKey.OP_READ)");
                        }
//
                        if(handle instanceof PseudoTCPSocketHandle){
//                            Log.d(TAG,TAG+"will handle pseudoSocketHandle");
                            if(TAG=="CameraReactor"){
                                Log.d(TAG,TAG + "PseudoTCPSocketHandle");
                            }
                            PseudoTCPSocketHandle pseudoSocketHandle = (PseudoTCPSocketHandle)handle;
                            if (pseudoSocketHandle.getPseudoTcpSocket().getInputStream().available() > 0) {
//                                Log.d(TAG,TAG+"will handle pseudoSocketHandle");


                                eventHandler.handleEvent();
                            }
                        }else if(handle instanceof UDPSocketHandle){
                            if(TAG=="CameraReactor") {
                                Log.d(TAG, TAG + "will handle UDPSocketHandle");
                            }
                            eventHandler.handleEvent();
                        }else{
                            if(TAG=="CameraReactor") {
                                Log.d(TAG, TAG + "Fuck");
                            }
                        }
//                        Log.d(TAG,TAG + "SelectionKey.OP_READ is over");
                    }else if(ICEHandlerTriggerMap.get(eventHandler)==SelectionKey.OP_WRITE){
                        eventHandler.handleEvent();
                    }//Maybe there are some mechanism can be used to detect is writable
                }
            }


        }
        Log.d(TAG, "I-(handleEvent)Reactor thread is End");
    }

    public Selector getSelector() {
        return selector;
    }

    @Override
    public void run() {
        try {
            handleEvent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finalize() throws IOException {
        selector.close();
    }

    public void setTAG(String tag){
        TAG = tag;
    }
}
