package ntu.selab.iot.interoperationapp.connection.builder;

import ntu.selab.iot.interoperationapp.reactor.Reactor;
import ntu.selab.iot.interoperationapp.service.InteroperabilityService;
import ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.GatewayCommunicationInfoTask;
import ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.HandlerTask;
import ntu.selab.iot.interoperationapp.serviceHandler.ConnectionInfoCommunicationSvcHandler;

/**
 * Created by Uiling on 2015/9/6.
 */
public class ConnectionInfoDeliveryBuilder {
    private ConnectionInfoCommunicationSvcHandler p2pConnectionInfoDeliveryHandler;
    private InteroperabilityService interoperabilityService;


    public ConnectionInfoDeliveryBuilder(InteroperabilityService interoperabilityService){
        this.interoperabilityService = interoperabilityService;

    }


    public void init(Reactor reactor){
        p2pConnectionInfoDeliveryHandler = new ConnectionInfoCommunicationSvcHandler(reactor);
    }

    public void buildChain(){
        HandlerTask gatewayCommunicationInfoTask = new GatewayCommunicationInfoTask(interoperabilityService,p2pConnectionInfoDeliveryHandler);

        p2pConnectionInfoDeliveryHandler.registerTask(gatewayCommunicationInfoTask);
    }

    public ConnectionInfoCommunicationSvcHandler get(){
        return p2pConnectionInfoDeliveryHandler;
    }

}
