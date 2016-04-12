package ntu.selab.iot.interoperationapp.connection.builder;

import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.reactor.Reactor;
import ntu.selab.iot.interoperationapp.service.InteroperabilityService;
import ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.HandlerTask;
import ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.p2p.UpdateTask;
import ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.p2p.video.ConnectionTask;
import ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.p2p.video.DescribeTask;
import ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.p2p.video.SetupTask;
import ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.p2p.video.StartTask;
import ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.p2p.video.StopTask;
import ntu.selab.iot.interoperationapp.serviceHandler.p2p.SensorOperationHandler;

/**
 * Created by Uiling on 2015/9/6.
 */
public class P2pSensorOperationBuilder {
    private SensorOperationHandler sensorOperationHandler;
    private GatewayModel gateway;
    private InteroperabilityService interoperabilityService;

    public P2pSensorOperationBuilder(InteroperabilityService interoperabilityService , GatewayModel gatewayModel) {
        gateway = gatewayModel;
        this.interoperabilityService = interoperabilityService;
    }

    public void init(Reactor reactor) {
        sensorOperationHandler = new SensorOperationHandler(interoperabilityService,reactor,gateway);
    }

    public void buildChain() {
        HandlerTask updateTask = new UpdateTask(gateway, sensorOperationHandler);
        HandlerTask connectionTask = new ConnectionTask(gateway, sensorOperationHandler);
        HandlerTask describeTask = new DescribeTask(gateway, sensorOperationHandler);
        HandlerTask setupTask = new SetupTask(gateway, sensorOperationHandler);
        updateTask.setNextTask(connectionTask);
        connectionTask.setNextTask(describeTask);
        describeTask.setNextTask(setupTask);


        sensorOperationHandler.registerTask(updateTask);
    }

    public SensorOperationHandler get() {
        return sensorOperationHandler;
    }

}
