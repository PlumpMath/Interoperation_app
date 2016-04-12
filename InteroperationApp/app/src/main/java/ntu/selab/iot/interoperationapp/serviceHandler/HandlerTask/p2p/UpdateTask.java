package ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.p2p;

import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;

import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.model.ScenarioModel;
import ntu.selab.iot.interoperationapp.protocol.communication.ExpressionInfo;
import ntu.selab.iot.interoperationapp.service.InteroperabilityService;
import ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.HandlerTask;
import ntu.selab.iot.interoperationapp.serviceHandler.IPCameraControlHandler;
import ntu.selab.iot.interoperationapp.serviceHandler.ServiceHandler;
import ntu.selab.iot.interoperationapp.serviceHandler.p2p.SensorOperationHandler;
import ntu.selab.iot.interoperationapp.protocol.communication.DataInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.PacketInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.SensorInfo;

/**
 * Created by User on 2015/5/15.
 */
public class UpdateTask extends HandlerTask {
    private final static String GET_LIST_RESPONSE = "Get_List_Response";
    private final static String TAG = "UpdateTask";
//    private final static String IntentBroadcastKey = "ntu.selab.iot.interoperability";
//    private RegularTCPConnectionSvcHandler sensorHandler;
    private SensorOperationHandler sensorHandler;
    protected ScenarioModel belongedModel;

    public UpdateTask(ScenarioModel belongedModel, ServiceHandler svcHandler) {
        super(svcHandler);
        this.belongedModel = belongedModel;
    }


    @Override
    public void handlePacketInfo(String packetInfo) {
        Log.d(TAG, "handlePacketInfo: " + packetInfo);
        Gson gson = new GsonBuilder().create();
        PacketInfo response = gson.fromJson(packetInfo, PacketInfo.class);
        GatewayModel gatewayModel = (GatewayModel) belongedModel;
        if (response.getCommand().equals(GET_LIST_RESPONSE)) {
            Log.d(TAG, GET_LIST_RESPONSE);
            for (SensorInfo info : response.getSensors()) {
                String uuid = info.getUuid();
                String deviceName = info.getName();
                if (deviceName.contains("Plug")) {
                    gatewayModel.addSpecificDevice(info.getUuid(), info);

                }

                else if (info.getData().length != 0 && info.getData()[0].getType() != null & info.getData()[0].getType().contains("Video")) {
                    try {
                        if(gatewayModel.getRemoteVideoConnectionState(uuid)==0) {
                            PacketInfo pck = new PacketInfo();
                            pck.setCommand("Setup_Stream");
                            SensorInfo sensorInfo = new SensorInfo();
                            sensorInfo.setName(deviceName);
                            sensorInfo.setUuid(uuid);

                            DataInfo StreamingData = new DataInfo();
                            StreamingData.setType("Streaming Data");
                            ExpressionInfo track = new ExpressionInfo();
                            track.setUnit("Track id");
                            track.setValue(gatewayModel.getMediaInfo(uuid).getMediaControl());
                            track.setClassName("java.lang.String");
                            StreamingData.addExpression(track);
                            sensorInfo.addData(StreamingData);

                            pck.addSensor(sensorInfo);
                            String request = gson.toJson(pck);
                            sensorHandler.write(request);
                            Log.d(TAG,"Setup Request:"+request);
                        }else if(gatewayModel.getRemoteVideoConnectionState(uuid)==-1||gatewayModel.getLocalVideoConnectionState(uuid)==-1){
                            gatewayModel.initIPCameraControlHandler(info.getName(), info.getUuid());
                        }
                        //Make sure the IP camera button on the App is ready for displaying
//                        while (!(handler.getState() instanceof StartState)) {
//                            Thread.sleep(20);
//                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String cameraUuid = info.getUuid();
                    gatewayModel.getCameraUuid().add(cameraUuid);
                    HashMap<String, DataInfo> dataInfos = new HashMap<String, DataInfo>();
                    HashMap<String, DataInfo> dataInfo_Storage = new HashMap<String, DataInfo>();
                    gatewayModel.getSensorData().put(cameraUuid, dataInfos);
                    gatewayModel.getDataInfo().put(cameraUuid, dataInfo_Storage);
                    String type = info.getData()[0].getType();
                    gatewayModel.getSensorData().get(info.getUuid()).put(type, null);
                    gatewayModel.getDataInfo().get(info.getUuid()).put(type, null);
                }
            }
            //Reasd Sensor Info
            Log.d(TAG, "Read_Sensors");
            response.setCommand("Read_Sensors");
            String request = gson.toJson(response);
            sensorHandler = (SensorOperationHandler) belongedSvcHandler;
            //request sensor_data
            sensorHandler.write(request);
            Log.d(TAG, "Read_Sensors_Request: " + request);

        /* Broadcast intent for Composite App
            Intent it = new Intent();
            it.setAction(IntentBroadcastKey);
//            it.putExtra("gatewayIPAddress", gatewayModel.getHostAddress().getHostAddress());
            it.putExtra("SensorList", request);
            it.putExtra("gatewayName", gatewayModel.getHostName());
            gatewayModel.getController().broadcastIntent(it);
        */
        } else if (response.getCommand().equals("Read_Sensors_Response")) {//!!! We ignore deviceName !!
            Log.d(TAG, "readData_the number of sensors: " + response.getSensors().length);

            for (SensorInfo info : response.getSensors()) {
                String uuid = info.getUuid();

                HashMap<String, DataInfo> dataInfos = new HashMap<String, DataInfo>();
                HashMap<String, DataInfo> dataInfo_Storage = new HashMap<String, DataInfo>();
                if (!gatewayModel.getCameraUuid().contains(uuid)) {
                    gatewayModel.getSensorData().put(uuid, dataInfos);
                    gatewayModel.getDataInfo().put(uuid, dataInfo_Storage);

                    for (DataInfo data : info.getData()) {
                        String type = data.getType();
                        gatewayModel.getSensorData().get(uuid).put(type, data);
                        gatewayModel.getDataInfo().get(uuid).put(type, data);
                    }
                }
            }

            Log.i(TAG, "MT-Prepare to update");
            Message msg = new Message();
            msg.what = InteroperabilityService.UPDATE_OK;
            gatewayModel.getInteroperabilityService().sendSensorDataUpdateMessage(msg);
        } else {
            Log.d(TAG, "will send to next task: " + packetInfo);

            if (nextTask != null) {
                Log.d(TAG, "send to next task: " + packetInfo);
                nextTask.handlePacketInfo(packetInfo);
            }else{
                Log.d(TAG,"nextTask==null");
            }
        }
    }
}
