package ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.IOException;

import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.service.InteroperabilityService;
import ntu.selab.iot.interoperationapp.serviceHandler.ServiceHandler;
import ntu.selab.iot.interoperationapp.protocol.communication.GatewayInfo;


/**
 * Created by Uiling on 2015/9/7.
 */
public class GatewayDiscoveryTask extends HandlerTask {
    private final static String TAG = "GatewayDiscovertTask";
    private final static String GATEWAY_LIST_RESPONSE = "Gateway_List_Response";
    private InteroperabilityService interoperabilityService;

    public GatewayDiscoveryTask( InteroperabilityService interoperabilityService, ServiceHandler svcHandler) {
        super(svcHandler);
        this.interoperabilityService = interoperabilityService;
    }

    @Override
    public void handlePacketInfo(String packetInfo) {
        Log.d(TAG, "handlePacketInfo: " + packetInfo);
        Gson gson = new GsonBuilder().create();
        GatewayInfo[] response = gson.fromJson(packetInfo, GatewayInfo[].class);
            Log.d(TAG, GATEWAY_LIST_RESPONSE);
            for(GatewayInfo gatewayInfo:response){
                if(!interoperabilityService.gatewayIsExist(gatewayInfo.getUuid())){

                    GatewayModel model = null;
                    try {
                        model = new GatewayModel(interoperabilityService.getReactor(),interoperabilityService);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    model.setUuid(gatewayInfo.getUuid());
                    model.setHostName(gatewayInfo.getName());
                    Log.d(TAG, "b-Prepare to connect");

                    interoperabilityService.addＤiscoveredGateway(model);

                    //Can be choose by client, automatically connect all discovered gateway by default.
                    interoperabilityService.connectToGateway(null);
                    Log.d(TAG,"b-Add new gateway");
                }
            }
            if(nextTask!=null) {
                nextTask.handlePacketInfo(packetInfo);
            }
    }
}
