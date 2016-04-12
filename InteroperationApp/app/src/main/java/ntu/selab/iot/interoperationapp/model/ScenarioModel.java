package ntu.selab.iot.interoperationapp.model;


import java.io.IOException;
import java.util.HashMap;

import ntu.selab.iot.interoperationapp.protocol.communication.DataInfo;

/**
 * Author: Keith Hung (kamael@selab.csie.ncu.edu.tw)
 * Date: 2014.07.16
 * Last Update: 2014.07.29
 */

public abstract class ScenarioModel {
    //	protected List<Display>               observers;
    protected HashMap<String, HashMap<String,DataInfo>> sensorData;//HashMap<uuid, HashMap<type,DataInfo>> cache for display
    protected HashMap<String, HashMap<String,DataInfo>> dataInfo;//HashMap<uuid, HashMap<type,DataInfo>> For  Storage
    protected boolean TEST_MODE;


//	public abstract boolean                  probe(InetAddress ip);


	public ScenarioModel() {
        dataInfo= new HashMap<String, HashMap<String,DataInfo>>();
        sensorData= new HashMap<String, HashMap<String,DataInfo>>();
	}
//	
//	public void notifyObservers() {
//		for (Display observer : observers) {
////			Log.d("scan", "Notify Observer: " + observer);
//			observer.update();
//		}
//	}

//	public void addObserver(Display observer) {
//		observers.add(observer);
//	}

//	public boolean removeObserver(Display observer) {
//		return observers.remove(observer);
//	}

    // Getters & Setters

    public void resetSensorData(){
        sensorData= new HashMap<String, HashMap<String,DataInfo>>();
    }

    public HashMap<String, HashMap<String,DataInfo>> getSensorData() {
        return sensorData;
    }


    public void setSensorData(HashMap<String, HashMap<String,DataInfo>>  sensorData) {
        this.sensorData = sensorData;
    }

    public void setTestMode(boolean test) {
        TEST_MODE = test;
    }

    public boolean getTestMode() {
        return TEST_MODE;
    }

    public abstract void close() throws IOException;

    public HashMap<String, HashMap<String,DataInfo>> getDataInfo() {
        return dataInfo;
    }

}
