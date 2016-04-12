package ntu.selab.iot.interoperationapp.task;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.TimerTask;

import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.model.ScenarioModel;
import ntu.selab.iot.interoperationapp.serviceHandler.media.VideoTestOutput;
import ntu.selab.iot.interoperationapp.protocol.communication.DataInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.ExpressionInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.SensorInfo;


public class SensorDataUpdateTimerTask extends TimerTask {
    private final static String TAG = "SensorDataUpdateTask";
    private HashMap<String, GatewayModel> models;
    private GatewayModel model;
//    	private LinkedList<ScenarioModel>      models;
    private boolean isReady = false;

    public SensorDataUpdateTimerTask(HashMap<String, GatewayModel> models) {
        this.models = models;
    }

    public SensorDataUpdateTimerTask(GatewayModel model) {
        this.model = model;
    }
    @Override
    public void run() {
        Log.i(TAG, "SensorDataUpdateTask is running");
//        Map map = Collections.synchronizedMap(models);
//        Set modelSet = map.entrySet();
//        Log.i(TAG, "models' size: " + models.size());
//        int i = 0;
//        synchronized (map) {
//            Log.i(TAG, "map's size: " + map.size());
//            Iterator<Map.Entry<String, ScenarioModel>> it = modelSet.iterator();
//            while (it.hasNext()) {
//                Log.i(TAG, "map's iterate number: " + (++i));
//                Map.Entry<String, ScenarioModel> set = it.next();
//                GatewayModel gatewayModel = (GatewayModel) set.getValue();
//                Log.i(TAG, "ReadyToGetSensors");
//                if (!MainActivity.TEST_MODE) {
//                    try {
//                        gatewayModel.getSensors();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
        try {
            model.getSensors();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public HashMap<String, SensorInfo> setTestData(ScenarioModel model) {
        GatewayModel gatewayModel = (GatewayModel) model;
        HashMap<String, SensorInfo> sensorData = new HashMap<String, SensorInfo>();

        SensorInfo sensorInfo1 = new SensorInfo();
        DataInfo dataInfo1 = new DataInfo();
        ExpressionInfo expressionInfo1 = new ExpressionInfo();
        expressionInfo1.setClassName("java.lang.String");
        expressionInfo1.setUnit("w");
        expressionInfo1.setValue("25.456");
        dataInfo1.setType("Voltage");

        dataInfo1 = new DataInfo();
        expressionInfo1 = new ExpressionInfo();
        expressionInfo1.setClassName("java.lang.String");
        expressionInfo1.setUnit("");
        expressionInfo1.setValue("0");
        dataInfo1.setType("Current");
        sensorInfo1.addData(dataInfo1);

        dataInfo1 = new DataInfo();
        expressionInfo1 = new ExpressionInfo();
        expressionInfo1.setClassName("java.lang.String");
        expressionInfo1.setUnit("");
        expressionInfo1.setValue("0");
        dataInfo1.setType("Frequency");
        sensorInfo1.addData(dataInfo1);

        dataInfo1 = new DataInfo();
        expressionInfo1 = new ExpressionInfo();
        expressionInfo1.setClassName("java.lang.String");
        expressionInfo1.setUnit("");
        expressionInfo1.setValue("0");
        dataInfo1.setType("PowerConsumption");
        sensorInfo1.addData(dataInfo1);

        dataInfo1 = new DataInfo();
        expressionInfo1 = new ExpressionInfo();
        expressionInfo1.setClassName("java.lang.String");
        expressionInfo1.setUnit("");
        expressionInfo1.setValue("0");
        dataInfo1.setType("AccumulateEnergy");
        sensorInfo1.addData(dataInfo1);


        sensorInfo1.setName("SmartPlug");
        sensorInfo1.setUuid("123456700");


        SensorInfo sensorInfo2 = new SensorInfo();
        DataInfo dataInfo2 = new DataInfo();
        ExpressionInfo expressionInfo2 = new ExpressionInfo();
        expressionInfo2.setClassName("java.lang.String");
        expressionInfo2.setUnit("Pa");
        expressionInfo2.setValue("76.34");
        dataInfo2.addExpression(expressionInfo2);
        dataInfo2.setType("Pressure");
        sensorInfo2.addData(dataInfo2);
        sensorInfo2.setName("Hi");
        sensorInfo2.setUuid("123456700");

        SensorInfo sensorInfo3 = new SensorInfo();
        DataInfo dataInfo3 = new DataInfo();
        ExpressionInfo expressionInfo3 = new ExpressionInfo();
        expressionInfo3.setClassName("java.lang.String");
        expressionInfo3.setUnit("m/s^2");
        expressionInfo3.setValue("15.57");
        dataInfo3.addExpression(expressionInfo3);
        dataInfo3.setType("Accelerator");
        sensorInfo3.addData(dataInfo3);
        sensorInfo3.setName("Hi");
        sensorInfo3.setUuid("123456700");


        SensorInfo sensorInfo4 = new SensorInfo();
        DataInfo dataInfo4 = new DataInfo();
        ExpressionInfo expressionInfo4 = new ExpressionInfo();
        expressionInfo4.setClassName("java.lang.String");
        expressionInfo4.setUnit("%");
        expressionInfo4.setValue("50.5");
        dataInfo4.addExpression(expressionInfo4);
        dataInfo4.setType("Battery");
        sensorInfo4.addData(dataInfo4);
        sensorInfo4.setName("Hi");
        sensorInfo4.setUuid("123456700");


        SensorInfo video = new SensorInfo();
        DataInfo videoData = new DataInfo();
        videoData.setType("Video");
        video.addData(videoData);
        video.setName("Test");
        video.setUuid("Test");


        gatewayModel.setTestVideoOutput("Test", new VideoTestOutput());


//		SensorInfo sensorInfo5 = new SensorInfo();
//		DataInfo dataInfo5 = new DataInfo();
//		ExpressionInfo expressionInfo5 = new ExpressionInfo();
//		expressionInfo5.setClassName("java.lang.String");
//		expressionInfo5.setUnit("fps");
//		expressionInfo5.setValue("rtsp://root:root@192.168.1.216/axis-media/media.amp");
//		dataInfo5.addExpression(expressionInfo5);
//		dataInfo5.setType("Camera");
//		sensorInfo5.addData(dataInfo5);
//		sensorInfo5.setName("IPCamera");
//		sensorInfo5.setUuid("123456700");


        sensorData.put("123456700", sensorInfo1);
        sensorData.put("123456700", sensorInfo2);
        sensorData.put("123456700", sensorInfo3);
        sensorData.put("123456700", sensorInfo4);
//		sensorData.add(sensorInfo5);
        sensorData.put("123456700", video);

        Log.d("Init", "I-init Fake data");
        return sensorData;
    }
}
