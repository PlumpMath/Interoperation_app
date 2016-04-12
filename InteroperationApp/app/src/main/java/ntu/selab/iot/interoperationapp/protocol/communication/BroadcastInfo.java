package ntu.selab.iot.interoperationapp.protocol.communication;

import java.util.ArrayList;

/**
 * Created by User on 2015/6/1.
 */
public class BroadcastInfo {
    private String gatewayName=null;
    private ArrayList<SensorInfo> sensors = null;

    public BroadcastInfo(){
        sensors = new ArrayList<SensorInfo>(0);
    }
    public void addSensor(SensorInfo sensorInfo){
        sensors.add(sensorInfo);
    }
    public SensorInfo[] getSensors(){
        return sensors.toArray(new SensorInfo[0]);
    }

    public void cleanSensors(){
        sensors = new ArrayList<SensorInfo>(0);
    }


    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getGatewayName() {
        return gatewayName;
    }
}
