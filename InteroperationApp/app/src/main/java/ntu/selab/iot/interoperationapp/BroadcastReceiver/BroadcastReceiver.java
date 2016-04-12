package ntu.selab.iot.interoperationapp.BroadcastReceiver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Created by User on 2015/6/2.
 */
public  class BroadcastReceiver extends android.content.BroadcastReceiver {
    public final static String TAG = "BroadcastReceiver";
    public final static String BPELProtocalKey = "ntu.selab.iot.interoperability.bpel.request";
    private CommandHandler handlerChain;
    private Service service;

    public BroadcastReceiver(){

    }


    public void init(Service service){

        this.service=service;
        BPELCommandHandler bpelCommandHandler = new BPELCommandHandler(service);
        handlerChain = bpelCommandHandler;
    }
//    public BroadcastReceiver(Service service){
//
//    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        handlerChain.handle(intent);

    }
}

