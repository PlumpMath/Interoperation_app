package ntu.selab.iot.interoperationapp.serviceHandler;

import java.io.IOException;
import java.nio.charset.Charset;

import ntu.selab.iot.interoperationapp.protocol.sdp.MediaAttribute;
import ntu.selab.iot.interoperationapp.protocol.sdp.MediaDescription;
import ntu.selab.iot.interoperationapp.protocol.sdp.SdpParser;
import ntu.selab.iot.interoperationapp.protocol.communication.PacketInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.SensorInfo;
import android.util.Base64;
import android.util.Log;

public class DescribeState extends State{

	protected DescribeState(IPCameraControlHandler handler) {
		super(handler);
		
	}

	private final static String TAG= "DescribeState";
	@Override
	public void read() throws IOException{
		String respContent = handler.getMessage();
		Log.d(TAG, "I-Response: " + respContent);
		PacketInfo responce= gson.fromJson(respContent, PacketInfo.class);
		if(responce.getCommand().equals("Describe_Stream_Response")){
			if(responce.getSensors()[0].getData()[0].getExpressions()[0].getUnit().equals("SDP")){
				String SDP=(String) responce.getSensors()[0].getData()[0].getExpressions()[0].getValue();
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
                    handler.getMediaInfo().setMediaType(codecName);
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

	                    		
	                    		handler.getMediaInfo().setSPS(spsDecoded);
	                    		handler.getMediaInfo().setPPS(ppsDecoded);
	                    	}
	                    }
	                }
                }
                MediaAttribute control = mediaVideo.getMediaAttribute("control");
                String value = control.getValue();
                handler.getMediaInfo().setMediaControl(value);
        		Log.d(TAG, "I-SDP: control: " + value);
			}
			handler.setNextState(new SetupState(this.handler));
		}
	}

	@Override
	public void write(String cameraName, String uuid) throws IOException{
		Log.d(TAG, "write");
		PacketInfo pck = new PacketInfo();
		pck.setCommand("Describe_Stream");
		SensorInfo sensorInfo = new SensorInfo();
		sensorInfo.setName(cameraName);
		sensorInfo.setUuid(uuid);
		pck.addSensor(sensorInfo);
		String request = gson.toJson(pck);
		handler.sendMessage(request);
		Log.d(TAG, "I-Request: " + request);
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
