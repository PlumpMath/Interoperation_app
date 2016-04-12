package ntu.selab.iot.interoperationapp.discovery;

import android.app.Service;

import java.io.IOException;

import ntu.selab.iot.interoperationapp.serviceHandler.GatewayDiscoverySvcHandler;

/**
 * Created by Uiling on 2015/9/7.
 */
public class GatewayDiscoveryByServer extends ScenarioDiscovery{
    private final static String TAG = "GatewayDiscovery";
    private GatewayDiscoverySvcHandler p2pGatewayDiscoverySvcHandler;

    public GatewayDiscoveryByServer(Service interoperabilityService, GatewayDiscoverySvcHandler p2pGatewayDiscoverySvcHandler){
        super(interoperabilityService);
        this.p2pGatewayDiscoverySvcHandler = p2pGatewayDiscoverySvcHandler;
    }


    @Override
    public void  discover() throws IOException {
        if(p2pGatewayDiscoverySvcHandler.isActive()) {
            p2pGatewayDiscoverySvcHandler.sendMessage("Gateway_List");
        }
    }
}
