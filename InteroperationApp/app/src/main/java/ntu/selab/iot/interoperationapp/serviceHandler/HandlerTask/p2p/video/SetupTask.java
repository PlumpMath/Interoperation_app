package ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.p2p.video;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.Charset;

import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.model.ScenarioModel;
import ntu.selab.iot.interoperationapp.protocol.communication.DataInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.ExpressionInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.PacketInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.SensorInfo;
import ntu.selab.iot.interoperationapp.protocol.sdp.MediaAttribute;
import ntu.selab.iot.interoperationapp.protocol.sdp.MediaDescription;
import ntu.selab.iot.interoperationapp.protocol.sdp.SdpParser;
import ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.HandlerTask;
import ntu.selab.iot.interoperationapp.serviceHandler.ServiceHandler;
import ntu.selab.iot.interoperationapp.serviceHandler.p2p.SensorOperationHandler;

/**
 * Created by Uiling on 2015/10/16.
 */
public class SetupTask extends HandlerTask{
    private final static String TAG = "SetupTask";
    private final static String SETUP_RESPONSE = "Setup_Stream_Response";
    protected ScenarioModel belongedModel;
    Gson gson = new GsonBuilder().create();
    private SensorOperationHandler sensorHandler;
    public SetupTask(ScenarioModel belongedModel,ServiceHandler svcHandler) {
        super(svcHandler);
        this.belongedModel = belongedModel;
        sensorHandler = (SensorOperationHandler) belongedSvcHandler;
    }

    @Override
    public void handlePacketInfo(String packetInfo) {
        Log.d(TAG, "handleSetupInfo: " + packetInfo);
        PacketInfo response = gson.fromJson(packetInfo, PacketInfo.class);
        GatewayModel gatewayModel = (GatewayModel) belongedModel;
        SensorInfo info = response.getSensors()[0];
        String name = info.getName();
        String uuid = info.getUuid();
        if(response.getCommand().equals(SETUP_RESPONSE)){

            if(info.getData()[0].getExpressions()[0].getUnit().equals("Ready")){
                int ready = ((Double)(info.getData()[0].getExpressions()[0].getValue())).intValue();
                Log.d(TAG,SETUP_RESPONSE+", Video Ready Sate:"+ready);
                if(ready==1){
                    gatewayModel.setRemoteVideoConnectionState(uuid,1);
                }else if(ready==0){
                    gatewayModel.setRemoteVideoConnectionState(uuid,0);
                }else{// failed
                    gatewayModel.setRemoteVideoConnectionState(uuid,-1);
                    gatewayModel.getVideos().remove(uuid);
                }
            }


        }else{
            if (nextTask != null) {
                nextTask.handlePacketInfo(packetInfo);
            }
        }
    }
}
