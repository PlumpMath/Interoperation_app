package ntu.selab.iot.interoperationapp.protocol.communication;

import java.util.ArrayList;

public class PacketInfo {

	private String command = null;
	private ArrayList<SensorInfo> sensors = null;


	public PacketInfo(){
		sensors = new ArrayList<SensorInfo>(0);
	}
	public String getCommand(){
		return command;
	}
	public void setCommand(String _command){
		command = _command;
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

}
