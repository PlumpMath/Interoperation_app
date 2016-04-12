package ntu.selab.iot.interoperationapp.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.ice4j.StackProperties;
import org.ice4j.Transport;
import org.ice4j.TransportAddress;
import org.ice4j.ice.Agent;
import org.ice4j.ice.CandidateType;
import org.ice4j.ice.Component;
import org.ice4j.ice.IceMediaStream;
import org.ice4j.ice.LocalCandidate;
import org.ice4j.ice.NominationStrategy;
import org.ice4j.ice.RemoteCandidate;
import org.ice4j.ice.harvest.StunCandidateHarvester;
import org.ice4j.ice.harvest.TurnCandidateHarvester;
import org.ice4j.security.LongTermCredential;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import ntu.selab.iot.interoperationapp.connection.Connector;
import ntu.selab.iot.interoperationapp.connection.ICE_UDPConnector;
import ntu.selab.iot.interoperationapp.connection.PseudoTCPSocketHandle;
import ntu.selab.iot.interoperationapp.connection.UDPSocketConnector;
import ntu.selab.iot.interoperationapp.connection.UDPSocketChannelHandle;
import ntu.selab.iot.interoperationapp.connection.UDPSocketHandle;
import ntu.selab.iot.interoperationapp.protocol.communication.DataInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.ExpressionInfo;
import ntu.selab.iot.interoperationapp.protocol.rtp.RtcpSession;
import ntu.selab.iot.interoperationapp.reactor.MediaReactor;
import ntu.selab.iot.interoperationapp.reactor.Reactor;
import ntu.selab.iot.interoperationapp.service.InteroperabilityService;
import ntu.selab.iot.interoperationapp.serviceHandler.IPCameraQosHandler;
import ntu.selab.iot.interoperationapp.serviceHandler.p2p.IPCameraStreamingHandler;
import ntu.selab.iot.interoperationapp.serviceHandler.MediaInfo;
import ntu.selab.iot.interoperationapp.serviceHandler.ServiceHandler;
import ntu.selab.iot.interoperationapp.serviceHandler.media.VideoOutput;
import ntu.selab.iot.interoperationapp.serviceHandler.media.VideoTestOutput;
import ntu.selab.iot.interoperationapp.serviceHandler.p2p.SensorOperationHandler;
import ntu.selab.iot.interoperationapp.task.SensorDataUpdateTimerTask;
import ntu.selab.iot.interoperationapp.protocol.communication.CandidateInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.ConnectionInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.PacketInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.SensorInfo;

public class GatewayModel extends ScenarioModel {

    private final static String TAG = "GatewayModel";
    private final static String GET_SENSOR_LIST = "Get_List";

    private String gatewayUuid;
    private InetAddress hostAddress;
    private String location;
    private String name;
    private final static int TCP_CONNECTION_PORT = 17001;
    private static int IPCamera_START_PORT = 17634;
    private Reactor reactor;
    private HashMap<String,Thread> cameraThreads;

    //Maybe a pool;
    private Connector UDPConnector;
    //    private RegularTCPConnectionSvcHandler sensorHandler;
    private SensorOperationHandler sensorHandler;

    private HashMap<String, SensorInfo> specificDevices;//uuid:SensorInfo
    private HashSet<String> CameraUuid;
    private Agent sensorDataConnectionAgent;
    private Timer sensorDataUpdateTimer;
    private SensorDataUpdateTimerTask sensorDataUpdateTimerTask;
    private int sensorDataUpdatePeriod = 4500;//4500
    private InteroperabilityService interoperabilityService;
    private HashMap<String, ServiceHandler[]> videos;
    private HashMap<String, Agent> videoAgentMap;
    private HashMap<String, Integer> localVideoConnectionState;
    private HashMap<String, Integer> remoteVideoConnectionState;
    /*
     *
     * ServiceHandler[0]=IPCameraStreamingHandler;
     * ServiceHandler[1]=IPCameraQosHandler;
     *
     * */
    private HashMap<String, VideoTestOutput> videoTestOutput;
    private ConnectionInfo remoteConnectionInfo;
    private Gson gson = new GsonBuilder().create();
    private HashMap<String,MediaInfo> mediaInfoHashMap;
    private HashMap<String,RtcpSession> rtcpSessionHashMap;


    public GatewayModel(Reactor reactor, InteroperabilityService controller) throws IOException {
        super();
        this.reactor = reactor;
        videos = new HashMap<String, ServiceHandler[]>();
        videoTestOutput = new HashMap<String, VideoTestOutput>();
        specificDevices = new HashMap<String, SensorInfo>();
        CameraUuid = new HashSet<String>();
        videoAgentMap = new HashMap<String,Agent>();
        interoperabilityService = controller;
        mediaInfoHashMap = new HashMap<String,MediaInfo>();
        rtcpSessionHashMap = new HashMap<String,RtcpSession>();
        localVideoConnectionState = new HashMap<String,Integer>();
        remoteVideoConnectionState = new HashMap<String,Integer>();
        cameraThreads = new HashMap<String,Thread>();
    }


    public void connect(ConnectionInfo remoteConnectionInfo) {
        //Use ICEConnectionBuilder to build ICEConnector
        this.remoteConnectionInfo = remoteConnectionInfo;
        loadConnection(remoteConnectionInfo, sensorDataConnectionAgent);
        sensorDataConnectionAgent.startConnectivityEstablishment();
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

    public Reactor getReactor() {
        return reactor;
    }

    public void updateSensorData() {
        if (sensorDataUpdateTimer != null) {
            sensorDataUpdateTimerTask.cancel();
            sensorDataUpdateTimer.cancel();
        }
        sensorDataUpdateTimerTask = new SensorDataUpdateTimerTask(this);
        sensorDataUpdateTimer = new Timer(true);
        sensorDataUpdateTimer.schedule(sensorDataUpdateTimerTask, 300, sensorDataUpdatePeriod);
    }


    public void cancelSensorDataUpdateTask() {
        if (sensorDataUpdateTimer != null) {
            sensorDataUpdateTimerTask.cancel();
            sensorDataUpdateTimer.cancel();
            sensorDataUpdateTimer = null;
            sensorDataUpdateTimerTask = null;
        }

    }
    public synchronized void getSensors() throws IOException {
        Log.d(TAG, GET_SENSOR_LIST);

        PacketInfo pck = new PacketInfo();
        pck.setCommand(GET_SENSOR_LIST);

        String request = gson.toJson(pck);

        Log.d(TAG, GET_SENSOR_LIST + request);

        sensorHandler.write(request);
    }


    public synchronized void sendData(SensorInfo sensorInfo)  {
        Log.e(TAG, "d-sendData");


        PacketInfo pck = new PacketInfo();
        pck.setCommand("Change_State");

        pck.addSensor(sensorInfo);
        String request = gson.toJson(pck);
        sensorHandler.write(request);

    }

    public void sendStartVideoCommand(String uuid, String name){
        PacketInfo pck = new PacketInfo();
        pck.setCommand("Start_Stream");
        SensorInfo sensorInfo = new SensorInfo();
        DataInfo dataInfo = new DataInfo();
        dataInfo.setType("Streaming Data");
        ExpressionInfo expressionInfo = new ExpressionInfo();
        expressionInfo.setUnit("Track id");
        expressionInfo.setValue(getMediaInfo(uuid).getMediaControl());
        expressionInfo.setClassName("java.lang.String");
        dataInfo.addExpression(expressionInfo);
        sensorInfo.addData(dataInfo);
        sensorInfo.setName(name);
        sensorInfo.setUuid(uuid);
        pck.addSensor(sensorInfo);
        String request = gson.toJson(pck);
        sensorHandler.write(request);
    }

    public void sendStopVideoCommand(String uuid){
        PacketInfo pck = new PacketInfo();
        pck.setCommand("Stop_Stream");
        SensorInfo sensorInfo = new SensorInfo();
        sensorInfo.setName(name);
        sensorInfo.setUuid(uuid);
        pck.addSensor(sensorInfo);
        String request = gson.toJson(pck);
        sensorHandler.write(request);
    }

    // Setters & Getters

    public InetAddress getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(InetAddress hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getUuid() {
        return gatewayUuid;
    }

    public void setUuid(String uuid) {
       gatewayUuid = uuid;
    }

    public void setHostLocation(String location) {
        this.location = location;
    }

    public String getHostLocation() {
        return location;
    }

    public void setHostName(String name) {
        this.name = name;
    }

    public String getHostName() {
        return name;
    }


    public VideoOutput getVideoOutput(String uuid) {
        return ((IPCameraStreamingHandler) (videos.get(uuid)[0])).getVideoOutput();
    }

    public ServiceHandler[] getVideos(String uuid) {
     /*
	 * ServiceHandler[0]=IPCameraStreamingHandler;
	 * ServiceHandler[1]=IPCameraQosHandler;
	 *
	 * */
        return videos.get(uuid);
    }

    public HashMap<String, ServiceHandler[]> getVideos() {

        return videos;
    }

    public VideoTestOutput getTestVideoOutput(String uuid) {
        return videoTestOutput.get(uuid);
    }

    public void setTestVideoOutput(String uuid, VideoTestOutput video) {
        videoTestOutput.put(uuid, video);
    }


    private Agent initAgentForCamera(IPCameraStreamingHandler ipCameraStreamingHandler, MediaReactor cameraReactor){//For gateway sensor data communication
        Agent agent = new Agent();
        ICE_UDPConnector listener = new ICE_UDPConnector(agent,null,new UDPSocketHandle(), ipCameraStreamingHandler, interoperabilityService);
        agent.addStateChangeListener(listener);
        agent.setControlling(true);
        return agent;
    }

    public ConnectionInfo getCommunicationCandidates(Agent agent,ConnectionInfo connectionInfo ){

        // STUN
        String[] stunHostnames = new String[]{
                "stun.l.google.com:19302",
                "stun1.l.google.com:19302",
                "stun2.l.google.com:19302",
                "stun3.l.google.com:19302",
                "stun4.l.google.com:19302",
                "stun.ekiga.net",
                "stun.ideasip.com",
                "stun.rixtelecom.se",
                "stun.schlund.de",
                "stun.stunprotocol.org:3478",
                "stun.voiparound.com",
                "stun.voipbuster.com",
                "stun.voipstunt.com",
                "stun.voxgratia.org"
        };
        for(String host:stunHostnames){
            String[] pair = host.split(":");
            StunCandidateHarvester stunHarv;
            if(pair.length < 2){
                stunHarv = new StunCandidateHarvester(new TransportAddress(pair[0],3478, Transport.UDP));
            }
            else{
                stunHarv = new StunCandidateHarvester(new TransportAddress(pair[0],Integer.parseInt(pair[1]),Transport.UDP));
            }
            agent.addCandidateHarvester(stunHarv);
        }
        // TURN
        String[] turnHostnames = new String[]{
                "140.112.90.147"
        };
        int port = 3478;
        LongTermCredential longTermCredential = new LongTermCredential("ray", "ray");

        for (String hostname : turnHostnames){
            agent.addCandidateHarvester(new TurnCandidateHarvester(
                    new TransportAddress(hostname, port,
                            Transport.UDP), longTermCredential));
        }

        IceMediaStream stream = agent.createMediaStream("data");
        try {
            agent.createComponent(stream, Transport.UDP, 27777, 27777, 27877);
        } catch (BindException e) {
            Log.d(TAG,"BindException");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            Log.d(TAG,"IllegalArgumentException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG,"IOException");
            e.printStackTrace();
        }
        agent.setNominationStrategy(NominationStrategy.NOMINATE_FIRST_VALID);

//        connectionInfo.id = id;
        connectionInfo.setUfrag(agent.getLocalUfrag());
        connectionInfo.setPassword(agent.getLocalPassword());
        for(LocalCandidate c:agent.getStream("data").getComponents().get(0).getLocalCandidates()){
            CandidateInfo candidateInfo = new CandidateInfo();
            candidateInfo.setIp(c.getTransportAddress().getHostAddress());
            candidateInfo.setPort(c.getTransportAddress().getPort());
            candidateInfo.setProtocol(c.getTransportAddress().getTransport().toString());
            candidateInfo.setType(c.getType().toString());
            candidateInfo.setFoundation(c.getFoundation());
            candidateInfo.setPiority(c.getPriority());
            connectionInfo.getCandidates().add(candidateInfo);
        }
        return connectionInfo;
    }


    public void initIPCameraControlHandler(String name, String uuid) throws IOException {
//        if (videos.get(uuid) == null) {
//            Log.d(TAG, "I-createIPCameraControlHandler:" + "create");
//            IPCameraControlHandler ipCameraControlHandler = new IPCameraControlHandler(reactor, this, name, uuid);
//
//            Agent agent = new Agent();
//            ConnectionInfo connectionInfo = new ConnectionInfo();
//            connectionInfo.setUuid(this.getUuid());
//            ICE_TCPConnector sconnector = new ICE_TCPConnector(controller,agent,reactor,new PseudoTCPSocketHandle(),connectionInfo, ipCameraControlHandler);
//            agent.addStateChangeListener(connector);
//            agent.setControlling(true);
//            controller.getCommunicationCandidates(agent,connectionInfo);
//            loadConnection(this.remoteConnectionInfo,agent);
//            agent.startConnectivityEstablishment();
//            /* For SocketChannel
////            TCPconnector.connect(ipCameraControlHandler, hostAddress, TCP_CONNECTION_PORT, "ASYC", reactor);
//            */
//            //
//            ServiceHandler[] videoHandlers = new ServiceHandler[3];
//            videoHandlers[0] = ipCameraControlHandler;
//            videos.put(uuid, videoHandlers);
//            return ipCameraControlHandler;
//        } else {
//            return (IPCameraControlHandler) videos.get(uuid)[0];
//        }
        Log.d(TAG,"initIPCameraControlHandler");
            //Record all video info
            MediaInfo mediaInfo = new MediaInfo();
            mediaInfoHashMap.put(uuid, mediaInfo);
            RtcpSession rtcpSession = new RtcpSession(false, 16000);
            rtcpSessionHashMap.put(uuid, rtcpSession);
            //init CameraReactor

            MediaReactor cameraReactor = new MediaReactor();
        cameraReactor.setTAG("CameraReactor");
            Thread cameraThread = new Thread(cameraReactor);
            cameraThreads.put(uuid,cameraThread);

            IPCameraStreamingHandler ipCameraStreamingHandler = new IPCameraStreamingHandler(cameraReactor, new VideoOutput(this, uuid, mediaInfo), rtcpSession, this,uuid);
            //QosHandler later
            ServiceHandler[] videoHandlers = new ServiceHandler[2];
            videoHandlers[0] = ipCameraStreamingHandler;
            videos.put(uuid, videoHandlers);

            ConnectionInfo candidateInfo = new ConnectionInfo();
            candidateInfo.setUuid(uuid);
            Agent agent = initAgentForCamera(ipCameraStreamingHandler,cameraReactor);
            addVideoAgent(uuid,agent);
            //ask myself communication info
            getCommunicationCandidates(agent, candidateInfo);
            String candidateInfo_gson = gson.toJson(candidateInfo);

            //
            PacketInfo pck = new PacketInfo();
            pck.setCommand("Connection_Establishment");
            SensorInfo sensorInfo = new SensorInfo();
            sensorInfo.setName(name);
            sensorInfo.setUuid(uuid);

            DataInfo connectionInfo = new DataInfo();
            connectionInfo.setType("ConnectionInfo");
            ExpressionInfo candidates = new ExpressionInfo();
            candidates.setUnit("Candidates");


            candidates.setValue(candidateInfo_gson);
            candidates.setClassName("java.lang.String");
            connectionInfo.addExpression(candidates);
            sensorInfo.addData(connectionInfo);

            pck.addSensor(sensorInfo);
            String request = gson.toJson(pck);
//            String request = "fuck";
            sensorHandler.write(request);
            Log.d(TAG,"ConnectionInfo Request:"+request);



    }

    public IPCameraStreamingHandler createIPCameraStreamingHandler(String uuid, RtcpSession rtcpSession, MediaInfo mediaInfo) throws IOException {
//        if (videos.get(uuid) != null) {
//            if (videos.get(uuid)[1] == null) {
//                Log.d(TAG, "I-createIPCameraStreamingHandler:" + "create");
//                IPCameraStreamingHandler ipCameraStreamingHandler = new IPCameraStreamingHandler(reactor, new VideoOutput(this, uuid, mediaInfo), rtcpSession, this,uuid);
//                int port = IPCamera_START_PORT;
//                ipCameraStreamingHandler.setLocalPort(port);
//                UDPConnector = new UDPSocketConnector(new UDPSocketChannelHandle(hostAddress, -1, IPCamera_START_PORT, "ASYC"));
//                UDPConnector.connect(ipCameraStreamingHandler, hostAddress, -1, "ASYC", reactor);
//                //
//                ServiceHandler[] videoHandlers = videos.get(uuid);
//                videoHandlers[1] = ipCameraStreamingHandler;
//                videos.put(uuid, videoHandlers);
//                //
//                IPCamera_START_PORT += 2;
//                return ipCameraStreamingHandler;
//            } else {
//                return (IPCameraStreamingHandler) videos.get(uuid)[1];
//            }
//        } else {
            return null;
//        }
    }

    public IPCameraQosHandler createIPCameraQosHandler(String uuid, int reomtePort, RtcpSession rtcpSession) throws IOException {
        if (videos.get(uuid) != null) {
            if (videos.get(uuid)[2] == null) {
                Log.d(TAG, "I-createIPCameraQosHandler:" + "create");
                IPCameraQosHandler ipCameraQosHandler = new IPCameraQosHandler(reactor, hostAddress, reomtePort, rtcpSession);
                int port = ((IPCameraStreamingHandler) videos.get(uuid)[1]).getLocalPort() + 1;
                ipCameraQosHandler.setLocalPort(port);
                UDPConnector = new UDPSocketConnector(new UDPSocketChannelHandle(hostAddress, -1, port, "ASYC"));
                UDPConnector.connect(ipCameraQosHandler, hostAddress, -1, "ASYC", reactor);
                //hw1_15_train.get(i)
                ServiceHandler[] videoHandlers = videos.get(uuid);
                videoHandlers[2] = ipCameraQosHandler;
                videos.put(uuid, videoHandlers);
                //
                return ipCameraQosHandler;
            } else {
                return (IPCameraQosHandler) videos.get(uuid)[2];
            }
        } else {
            return null;
        }
    }

    public void addSpecificDevice(String uuid, SensorInfo info) {
        specificDevices.put(uuid, info);
    }

    public HashMap<String, SensorInfo> getSpecificDevices() {
        return specificDevices;
    }

    public boolean containSpecificDevice(String uuid) {
        Set<String> keySet = specificDevices.keySet();
        return keySet.contains(uuid);
    }

    public HashSet getCameraUuid() {
        return CameraUuid;
    }

    public void close() throws IOException {
        /*Close SensoerOperationHandler*/
        sensorHandler.close();
        /*Close Video Handlers*/
        Map<String, ServiceHandler[]> map = Collections.synchronizedMap(videos);
        synchronized (map) {
            for (ServiceHandler[] value : map.values()) {
                if (value[0] != null) {
                    value[0].close();
                }
                if (value[1] != null) {
                    value[1].close();
                }
                if (value[2] != null) {
                    value[2].close();
                }
            }
        }
    }


    public Agent getSensorDataConnectionAgent() {
        return sensorDataConnectionAgent;
    }

    public void setSensorDataConnectionAgent(Agent connectionAgent) {
        this.sensorDataConnectionAgent = connectionAgent;
    }

    public void setSensorHandler(SensorOperationHandler sensorHandler) {
        this.sensorHandler = sensorHandler;
    }

    public InteroperabilityService getInteroperabilityService() {
        return interoperabilityService;
    }

    public void setInteroperabilityService(InteroperabilityService controller) {
        interoperabilityService = controller;
    }

    public MediaInfo getMediaInfo(String uuid){
        return mediaInfoHashMap.get(uuid);
    }

    public void addVideoAgent(String uuid, Agent agent){
        videoAgentMap.put(uuid,agent);
    }

    public Agent getVideoAgent(String uuid){
           return videoAgentMap.get(uuid);
    }

    public void setLocalVideoConnectionState(String uuid, int state){
        Log.d(TAG,"setLocalVideoConnectionState: uuid:"+uuid+" state:"+state);
        localVideoConnectionState.put(uuid,state);
    }

    public int getLocalVideoConnectionState(String uuid){//0: inactive 1: active
        if(localVideoConnectionState.get(uuid)!=null) {
            return localVideoConnectionState.get(uuid);
        }
        return -1;
    }

    public void setRemoteVideoConnectionState(String uuid, int state){
        remoteVideoConnectionState.put(uuid,state);
    }

    public int getRemoteVideoConnectionState(String uuid){//0: inactive 1: active
        if(remoteVideoConnectionState.get(uuid)!=null) {
            return remoteVideoConnectionState.get(uuid);
        }
        return -1;
    }

    public Thread getCameraThreads(String uuid){
        return cameraThreads.get(uuid);
    }


}