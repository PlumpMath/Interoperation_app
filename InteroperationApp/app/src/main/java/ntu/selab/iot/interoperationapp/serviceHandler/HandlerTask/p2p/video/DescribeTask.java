package ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.p2p.video;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
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
import ntu.selab.iot.interoperationapp.serviceHandler.p2p.IPCameraStreamingHandler;
import ntu.selab.iot.interoperationapp.serviceHandler.p2p.SensorOperationHandler;

/**
 * Created by Uiling on 2015/10/16.
 */
public class DescribeTask extends HandlerTask {
    private final static String TAG = "DescribeTask";
    private final static String DESCRIPTION_RESPONSE = "Describe_Stream_Response";
    protected ScenarioModel belongedModel;
    Gson gson = new GsonBuilder().create();
    private SensorOperationHandler sensorHandler;
    public DescribeTask(ScenarioModel belongedModel,ServiceHandler svcHandler) {
        super(svcHandler);
        this.belongedModel = belongedModel;
        sensorHandler = (SensorOperationHandler) belongedSvcHandler;
    }

    @Override
    public void handlePacketInfo(String packetInfo) {
        Log.d(TAG, "handleConnectionInfo: " + packetInfo);
        PacketInfo response = gson.fromJson(packetInfo, PacketInfo.class);
        GatewayModel gatewayModel = (GatewayModel) belongedModel;
        SensorInfo info = response.getSensors()[0];
        String name = info.getName();
        String uuid = info.getUuid();
        if(response.getCommand().equals(DESCRIPTION_RESPONSE)){
            if(info.getData()[0].getExpressions()[0].getUnit().equals("SDP")){
                String SDP=(String) response.getSensors()[0].getData()[0].getExpressions()[0].getValue();
                byte[] sdp =SDP.getBytes(Charset.forName("UTF-8"));
                SdpParser parser = new SdpParser(sdp);
                MediaDescription mediaVideo = parser.getMediaDescription("video");
                String rtpmap =  mediaVideo.getMediaAttribute("rtpmap").getValue();

                String encoding = rtpmap.substring(rtpmap.indexOf( mediaVideo.payload)
                        +  mediaVideo.payload.length() + 1).trim();
                // Extract clock rate
                int clockRate = 0;
                int index = encoding.indexOf("/");
                if (index != -1) {
                    String codecName = encoding.substring(0, index);
                    clockRate = Integer.parseInt(encoding.substring(index + 1));
                    gatewayModel.getMediaInfo(uuid).setMediaType(codecName);
                }
                //codecParameters
                MediaAttribute fmtp = mediaVideo.getMediaAttribute("fmtp");
                String codecParameters = "";
                if (fmtp != null) {
                    String value = fmtp.getValue();
                    index = 0; // value.indexOf(media.payload);
                    if ((index != -1) && (value.length() > mediaVideo.payload.length())) {
                        codecParameters = value.substring(index + mediaVideo.payload.length() + 1);
                        String[] p = codecParameters.split(";");
                        for(int i =0;i<p.length;i++){
                            if(p[i].contains("sprop-parameter-sets")){
                                String sps=null;
                                String pps=null;
                                String parameters = p[i].trim();
                                index=parameters.indexOf('=');
                                if(index!=-1){
                                    parameters=parameters.substring(index+1);
                                    String sps_pps[]=parameters.split(",");
                                    sps=sps_pps[0];
                                    pps=sps_pps[1];
//	                    			index=pps.indexOf("=");
//	                    			if(index!=-1){
//	                    				pps=pps.substring(0, index);
//	                    			}
                                }
                                Log.d(TAG,"I-Before Decode:sps="+sps);
                                Log.d(TAG,"I-Before Decode:pps="+pps);

                                byte[] spsDecoded= Base64.decode(sps, Base64.DEFAULT);
                                byte[] ppsDecoded= Base64.decode(pps, Base64.DEFAULT);
                                Log.d(TAG,"I-After Decode:sps="+bytesToHex(spsDecoded));
                                Log.d(TAG,"I-After Decode:pps="+bytesToHex(ppsDecoded));


                                gatewayModel.getMediaInfo(uuid).setSPS(spsDecoded);
                                gatewayModel.getMediaInfo(uuid).setPPS(ppsDecoded);
                            }
                        }
                    }
                }
                MediaAttribute control = mediaVideo.getMediaAttribute("control");
                String value = control.getValue();
                gatewayModel.getMediaInfo(uuid).setMediaControl(value);
                Log.d(TAG, "I-SDP: control: " + value);
            }

            //Enter to next state
            PacketInfo pck = new PacketInfo();
            pck.setCommand("Setup_Stream");
            SensorInfo sensorInfo = new SensorInfo();
            sensorInfo.setName(name);
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

        }else{
            if (nextTask != null) {
                nextTask.handlePacketInfo(packetInfo);
            }
        }
    }
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
