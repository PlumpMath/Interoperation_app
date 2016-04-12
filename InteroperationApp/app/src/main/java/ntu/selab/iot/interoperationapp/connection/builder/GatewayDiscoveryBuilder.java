package ntu.selab.iot.interoperationapp.connection.builder;

import ntu.selab.iot.interoperationapp.reactor.Reactor;
import ntu.selab.iot.interoperationapp.service.InteroperabilityService;
import ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.GatewayDiscoveryTask;
import ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.HandlerTask;
import ntu.selab.iot.interoperationapp.serviceHandler.GatewayDiscoverySvcHandler;

/**
 * Created by Uiling on 2015/9/6.
 */
public class GatewayDiscoveryBuilder {
    private GatewayDiscoverySvcHandler p2pGatewayDiscoveryHandler;
    private InteroperabilityService interoperabilityService;

    public GatewayDiscoveryBuilder(InteroperabilityService interoperabilityService){
        this.interoperabilityService = interoperabilityService;
    }


    public void init(Reactor reactor){
        p2pGatewayDiscoveryHandler = new GatewayDiscoverySvcHandler(reactor,2);
    }

    public void buildChain(){
        HandlerTask gatewayDiscoveryTask = new GatewayDiscoveryTask( interoperabilityService,p2pGatewayDiscoveryHandler);
        p2pGatewayDiscoveryHandler.registerTask(gatewayDiscoveryTask);
    }

    public GatewayDiscoverySvcHandler get(){
        return p2pGatewayDiscoveryHandler;
    }
}
