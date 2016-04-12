package ntu.selab.iot.interoperationapp.discovery;

import android.app.Service;

import java.io.IOException;
import java.util.ArrayList;

import ntu.selab.iot.interoperationapp.activity.MainActivity;
import ntu.selab.iot.interoperationapp.model.ScenarioModel;

/**
 * Author: Keith Hung (kamael@selab.csie.ncu.edu.tw)
 * Date: 2014.07.16
 * Last Update: 2014.07.24
 */

public abstract class ScenarioDiscovery {
    protected Service mainService;
    protected ScenarioDiscovery next;

    public ScenarioDiscovery(Service activity) {
        this(activity, null);
    }

    public ScenarioDiscovery(Service interoperabilityService, ScenarioDiscovery discovery) {
        mainService = interoperabilityService;
        next = discovery;
    }


    public abstract void discover() throws IOException;

    // Setters & Getters

    public void setNextDiscovery(ScenarioDiscovery discover) {
        next = discover;
    }

    public ScenarioDiscovery getNextDiscovery() {
        return next;
    }

}
