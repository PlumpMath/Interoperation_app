package ntu.selab.iot.interoperationapp.task;

import java.io.IOException;
import java.util.TimerTask;

import ntu.selab.iot.interoperationapp.discovery.ScenarioDiscovery;
import ntu.selab.iot.interoperationapp.service.InteroperabilityService;

import android.util.Log;

public class GatewayDiscoveryTimerTask extends TimerTask {
private final static String TAG = "DiscoveryTask";
	private InteroperabilityService controller;
	private ScenarioDiscovery  discovery;

	
	
	public GatewayDiscoveryTimerTask(InteroperabilityService controller, ScenarioDiscovery discovery) {
		this.controller = controller;
		this.discovery = discovery;
		Log.d(TAG,"b-DiscoveryTask");
	}





    @Override
    public void run() {
        try {
            discovery.discover();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Wait for gateway's reply in fix period.
//        try {
//            Thread.sleep(controller.discoverTime);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        if(!controller.gatewayModelsIsEmpty()){
//			controller.updateSensorData();
//		}else{
//			Log.e("DiscoveryTask","b-There is no new Gateway.");
//		}

    }

//    public void closeDiscoveryChannel(){
//        ((GatewayDiscovery)discovery).closeBroadcastSocket();
//    }
}
