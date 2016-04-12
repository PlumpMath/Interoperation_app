package ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask;

import ntu.selab.iot.interoperationapp.model.ScenarioModel;
import ntu.selab.iot.interoperationapp.serviceHandler.ServiceHandler;

/**
 * Created by User on 2015/5/15.
 */
public abstract class HandlerTask {

    protected ServiceHandler belongedSvcHandler;
    protected HandlerTask nextTask;


    public HandlerTask(ServiceHandler svcHandler){
        setBelongedSvcHandler(svcHandler);
    }


    public void setBelongedSvcHandler(ServiceHandler svcHandler){ belongedSvcHandler= svcHandler;}

    public void setNextTask(HandlerTask nextTask){
        this.nextTask = nextTask;
    }

    public abstract void handlePacketInfo(String packetInfo);

}
