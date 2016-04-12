package ntu.selab.iot.interoperationapp.protocol.communication;

import java.util.ArrayList;

public class SensorInfo {
	private String name = null;
	private String uuid = null;
	private ArrayList<DataInfo> data = null;
	
	public SensorInfo(){
		data = new ArrayList<DataInfo>(0);
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
	public void addData(DataInfo dataInfo){
		data.add(dataInfo);
	}
	public DataInfo[] getData(){
		return data.toArray(new DataInfo[0]);
	}
	
	public void cleanData(){
		data = new ArrayList<DataInfo>(0);
	}
}
