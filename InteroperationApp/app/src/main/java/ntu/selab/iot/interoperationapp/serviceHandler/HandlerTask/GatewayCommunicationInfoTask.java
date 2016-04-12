package ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ntu.selab.iot.interoperationapp.service.InteroperabilityService;
import ntu.selab.iot.interoperationapp.serviceHandler.ServiceHandler;
import ntu.selab.iot.interoperationapp.protocol.communication.ConnectionInfo;

/**
 * Created by Uiling on 2015/9/8.
 */
public class GatewayCommunicationInfoTask extends HandlerTask {
    private final static String TAG = "GatewayCommunicationInfoTask";
    private InteroperabilityService interoperabilityService;

    public GatewayCommunicationInfoTask(InteroperabilityService interoperabilityService, ServiceHandler svcHandler) {
        super(svcHandler);
        this.interoperabilityService = interoperabilityService;
    }

    @Override
    public void handlePacketInfo(String packetInfo) {
        Log.d(TAG, "handlePacketInfo: " + packetInfo);
        Gson gson = new GsonBuilder().create();
        ConnectionInfo response = gson.fromJson(packetInfo, ConnectionInfo.class);

        //prevent discovery gateway again before the connection establish
//        interoperabilityService.startActiveGateway(response.getUuid());
        interoperabilityService.getGatewayIceConnector(response.getUuid()).setActiving(true);
        //build ICE Connection
        interoperabilityService.getGatewayModel(response.getUuid()).connect(response);

    }
}
