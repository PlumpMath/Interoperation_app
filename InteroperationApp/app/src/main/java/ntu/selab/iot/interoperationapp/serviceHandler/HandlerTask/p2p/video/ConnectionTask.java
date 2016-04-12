package ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.p2p.video;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.ice4j.Transport;
import org.ice4j.TransportAddress;
import org.ice4j.ice.Agent;
import org.ice4j.ice.CandidateType;
import org.ice4j.ice.Component;
import org.ice4j.ice.IceMediaStream;
import org.ice4j.ice.RemoteCandidate;

import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.model.ScenarioModel;
import ntu.selab.iot.interoperationapp.protocol.communication.CandidateInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.ConnectionInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.DataInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.ExpressionInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.PacketInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.SensorInfo;
import ntu.selab.iot.interoperationapp.serviceHandler.HandlerTask.HandlerTask;
import ntu.selab.iot.interoperationapp.serviceHandler.ServiceHandler;
import ntu.selab.iot.interoperationapp.serviceHandler.p2p.SensorOperationHandler;

/**
 * Created by Uiling on 2015/10/16.
 */
public class ConnectionTask extends HandlerTask {
    private final static String TAG = "ConnectionTask";
    private final static String CONNECTION_RESPONSE = "Connection_Establishment_Response";
    protected ScenarioModel belongedModel;
    Gson gson = new GsonBuilder().create();
    private SensorOperationHandler sensorHandler;
    public ConnectionTask(ScenarioModel belongedModel, ServiceHandler svcHandler) {
        super(svcHandler);
        this.belongedModel = belongedModel;
        sensorHandler = (SensorOperationHandler) belongedSvcHandler;
    }

    @Override
    public void handlePacketInfo(String packetInfo) {
        Log.d(TAG, "handleConnectionInfo: " + packetInfo);

        PacketInfo response = gson.fromJson(packetInfo, PacketInfo.class);
        GatewayModel gatewayModel = (GatewayModel) belongedModel;
        if(response.getCommand().equals(CONNECTION_RESPONSE)){
            Log.d(TAG,"fuck1");
            SensorInfo info = response.getSensors()[0];
            String name = info.getName();
            String uuid = info.getUuid();
            DataInfo data = info.getData()[0];
            ExpressionInfo expressionInfo = data.getExpressions()[0];
            ConnectionInfo remoteConnectionInfo = gson.fromJson((String)expressionInfo.getValue(), ConnectionInfo.class);
            Agent videoAgent = gatewayModel.getVideoAgent(uuid);
            loadConnection(remoteConnectionInfo,videoAgent);
            videoAgent.startConnectivityEstablishment();
            gatewayModel.setLocalVideoConnectionState(uuid, 0);
            Log.d(TAG,"fuck2");

            //Enter to next state
            PacketInfo pck = new PacketInfo();
            pck.setCommand("Describe_Stream");
            SensorInfo sensorInfo = new SensorInfo();
            sensorInfo.setName(name);
            sensorInfo.setUuid(uuid);
            pck.addSensor(sensorInfo);
            String request = gson.toJson(pck);
            sensorHandler.write(request);
            Log.d(TAG,"Describe Request:"+request);

        }else{
            if (nextTask != null) {
                nextTask.handlePacketInfo(packetInfo);
            }
        }
    }

    public void loadConnection(ConnectionInfo connectionInfo, Agent agent) {
        IceMediaStream stream = agent.getStream("data");
        stream.setRemoteUfrag(connectionInfo.getUfrag());
        stream.setRemotePassword(connectionInfo.getPassword());
        Component component = stream.getComponents().get(0);

        for (CandidateInfo candidateInfo : connectionInfo.getCandidates()) {
            TransportAddress address = new TransportAddress(
                    candidateInfo.getIp(),
                    candidateInfo.getPort(),
                    Transport.parse(candidateInfo.getProtocol().toLowerCase()));
            RemoteCandidate remoteCandidate = new RemoteCandidate(
                    address,
                    component,
                    CandidateType.parse(candidateInfo.getType()),
                    candidateInfo.getFoundation(),
                    candidateInfo.getPiority(), null);
            component.addRemoteCandidate(remoteCandidate);
        }
    }
}
