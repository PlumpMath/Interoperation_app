package ntu.selab.iot.interoperationapp.discovery;

import android.app.Service;

import java.io.IOException;
import java.util.ArrayList;

import ntu.selab.iot.interoperationapp.activity.MainActivity;
import ntu.selab.iot.interoperationapp.model.MobileAPModel;
import ntu.selab.iot.interoperationapp.model.ScenarioModel;
import ntu.selab.iot.interoperationapp.reactor.Reactor;

/**
 * Author: Keith Hung (kamael@selab.csie.ncu.edu.tw) 
 * Date: 2014.07.16
 * Last Update: 2014.07.25
 * */

public class MobileAPDiscovery extends ScenarioDiscovery {

	
	public MobileAPDiscovery(Service activity) {
		this(activity, null);
	}
	
	public MobileAPDiscovery(Service activity, ScenarioDiscovery successor) {
		super(activity, successor);

	}
	
	@Override
	public void discover() throws IOException {
		// TODO
	}

	private boolean isMobileAPScenario() {
		// TODO Auto-generated method stub
		return false;
	}


}
