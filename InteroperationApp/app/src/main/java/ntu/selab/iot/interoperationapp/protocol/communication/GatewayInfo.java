package ntu.selab.iot.interoperationapp.protocol.communication;

import java.util.ArrayList;

/**
 * Created by Uiling on 2015/9/7.
 */
public class GatewayInfo {
    private String name = null;
    private String uuid = null;
    private String location = null;
    private ArrayList<SensorInfo> sensors = null;

    public GatewayInfo(){
        sensors = new ArrayList<SensorInfo>(0);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public void addSensor(SensorInfo sensorInfo){
        sensors.add(sensorInfo);
    }
    public SensorInfo[] getSensors(){
        return sensors.toArray(new SensorInfo[0]);
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }


    public void cleanData(){
        sensors = new ArrayList<SensorInfo>(0);
    }
}
