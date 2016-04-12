package ntu.selab.iot.interoperationapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.ice4j.StackProperties;
import org.ice4j.Transport;
import org.ice4j.TransportAddress;
import org.ice4j.ice.Agent;
import org.ice4j.ice.IceMediaStream;
import org.ice4j.ice.LocalCandidate;
import org.ice4j.ice.NominationStrategy;
import org.ice4j.ice.harvest.StunCandidateHarvester;
import org.ice4j.ice.harvest.TurnCandidateHarvester;
import org.ice4j.security.LongTermCredential;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import ntu.selab.iot.interoperationapp.BroadcastReceiver.BroadcastReceiver;
import ntu.selab.iot.interoperationapp.InteroperabilityServiceInterface;
import ntu.selab.iot.interoperationapp.activity.VideoActivity;
import ntu.selab.iot.interoperationapp.activity.MainActivity;
import ntu.selab.iot.interoperationapp.activity.SmartPlugActivity;
import ntu.selab.iot.interoperationapp.connection.Connector;
import ntu.selab.iot.interoperationapp.connection.ICE_TCPConnector;
import ntu.selab.iot.interoperationapp.connection.PseudoTCPSocketHandle;
import ntu.selab.iot.interoperationapp.connection.TCPSocketConnector;
import ntu.selab.iot.interoperationapp.connection.TCPSocketChannelHandle;
import ntu.selab.iot.interoperationapp.connection.builder.ConnectionInfoDeliveryBuilder;
import ntu.selab.iot.interoperationapp.connection.builder.GatewayDiscoveryBuilder;
import ntu.selab.iot.interoperationapp.connection.builder.P2pSensorOperationBuilder;
import ntu.selab.iot.interoperationapp.discovery.GatewayDiscoveryByServer;
import ntu.selab.iot.interoperationapp.discovery.ScenarioDiscovery;
import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.model.ScenarioModel;
import ntu.selab.iot.interoperationapp.reactor.Reactor;
import ntu.selab.iot.interoperationapp.serviceHandler.GatewayDiscoveryBroadcasting_Handler;
import ntu.selab.iot.interoperationapp.serviceHandler.IPCameraControlHandler;
import ntu.selab.iot.interoperationapp.serviceHandler.ConnectionInfoCommunicationSvcHandler;
import ntu.selab.iot.interoperationapp.serviceHandler.GatewayDiscoverySvcHandler;
import ntu.selab.iot.interoperationapp.serviceHandler.p2p.SensorOperationHandler;
import ntu.selab.iot.interoperationapp.task.GatewayDiscoveryTimerTask;
import ntu.selab.iot.interoperationapp.task.SensorDataUpdateTimerTask;
import ntu.selab.iot.interoperationapp.protocol.communication.CandidateInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.ConnectionInfo;

/**
 * Created by Uiling on 2015/6/1.
 */

public class InteroperabilityService extends Service {

    public static final String TAG = "InteroperabilityService";
    private ScenarioDiscovery discovery;
    private HashMap<String,GatewayModel> discoveredGateways;
    private HashSet<String> connectedGateways;
    private HashMap<String,ICE_TCPConnector> gatewayIceConnectorMap;
    private String rendezvousServerAddress = "140.112.90.147";
    private int rendezvousServerDiscoveryPort = 17773;
    private int rendezvousServerCommunicationPort = 17772;
    public final static int discoverTime = 4000;
    private int deviceDiscoveryPeriod = 2000000;
//    private Timer sensorDataUpdateTimer;
    private Timer deviceDiscoveryTimer;
    public GatewayDiscoveryTimerTask gatewayDiscoveryTimerTask;
    private Reactor reactor;
    private ConnectionInfoCommunicationSvcHandler p2pGatewayConnectionInfoHandler;
    private Handler sensorDataUpdateHandler = null;
    private Thread reactorThread;
//    private SensorDataUpdateTimerTask sensorDataUpdateTimerTask;
    private InteroperabilityService service;
    private BroadcastReceiver mBroadcast;
    public MyBinder mBinder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        service = this;
        Log.e(TAG, "onCreate");
        discoveredGateways = new HashMap<String, GatewayModel>();
        connectedGateways = new HashSet<String>();
        gatewayIceConnectorMap = new HashMap<String,ICE_TCPConnector>();
        System.setProperty(StackProperties.DISABLE_IPv6,"True");
        try {
            reactor = new Reactor();
            reactorThread = new Thread(reactor);
            reactorThread.start();
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                protected Void doInBackground(Void... params) {
                    try {
                        connectToServer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                protected void onPostExecute(Void params) {
                    discover();
                }


            };
            task.execute();




            /*Gateway Discovery by broadcasting
            gatewayDiscoveryBroadcastHandler = new GatewayDiscoveryBroadcasting_Handler(service, reactor);
            broadcastConnector = new UDPSocketConnector(new UDPSocketHandle(null, -1, ListenToGatewayPort, "ASYC"));
            broadcastConnector.connect(gatewayDiscoveryBroadcastHandler, null, -1, "ASYC", reactor);
            discovery = new GatewayDiscoveryByBroadcasting(this, gatewayDiscoveryBroadcastHandler, null);
            */




        } catch (IOException e) {
            e.printStackTrace();
        }
        mBroadcast = new BroadcastReceiver();
        mBroadcast.init(service);
        registerReceiver(mBroadcast, new IntentFilter(BroadcastReceiver.BPELProtocalKey));
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand() executed");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");

            mBinder.cancelSensorDataUpdateTask();
            mBinder.cancelDiscoveryTask();
            try {
                mBinder.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


    }


    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        Log.e(TAG,"onUnbind() executed");

        return super.onUnbind(intent);
        //return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind() executed");
        return mBinder;
        //mayBe otherBinder
    }

    public void connectToServer() throws IOException {
        Log.d(TAG,"Establish discovery connection with server\n");
        //Establish discovery connection
        TCPSocketConnector gatewayDiscoveryConnector = new TCPSocketConnector(new TCPSocketChannelHandle("ASYC"));
        GatewayDiscoverySvcHandler p2pGatewayDiscoveryHandler = buildGatewayDiscoveryHandler();
        discovery = new GatewayDiscoveryByServer(this, p2pGatewayDiscoveryHandler);
        gatewayDiscoveryConnector.connect(p2pGatewayDiscoveryHandler, InetAddress.getByName(rendezvousServerAddress), rendezvousServerDiscoveryPort, "ASYC", reactor);

        Log.d(TAG,"Establish communication connection with server\n");
        //Establish Info communication connection
        TCPSocketConnector gatewayConnectionInfoConnector = new TCPSocketConnector(new TCPSocketChannelHandle("ASYC"));
        ConnectionInfoDeliveryBuilder connectionInfoDeliveryBuilder = new ConnectionInfoDeliveryBuilder(this);
        connectionInfoDeliveryBuilder.init(reactor);
        connectionInfoDeliveryBuilder.buildChain();
        p2pGatewayConnectionInfoHandler = connectionInfoDeliveryBuilder.get();
        gatewayConnectionInfoConnector.connect(p2pGatewayConnectionInfoHandler, InetAddress.getByName(rendezvousServerAddress), rendezvousServerCommunicationPort, "ASYC", reactor);
    }

    private GatewayDiscoverySvcHandler buildGatewayDiscoveryHandler(){
        GatewayDiscoveryBuilder gatewayDiscoveryBuilder = new GatewayDiscoveryBuilder(this);
        gatewayDiscoveryBuilder.init(reactor);
        gatewayDiscoveryBuilder.buildChain();
        return gatewayDiscoveryBuilder.get();
    }


    public void discover() {
        Log.d(TAG, "b-discover");

        if (deviceDiscoveryTimer != null) {
            gatewayDiscoveryTimerTask.cancel();
                deviceDiscoveryTimer.cancel();
        }
        gatewayDiscoveryTimerTask = new GatewayDiscoveryTimerTask(service, discovery);
        deviceDiscoveryTimer = new Timer(true);
        deviceDiscoveryTimer.schedule(gatewayDiscoveryTimerTask, 0, deviceDiscoveryPeriod);

    }

    public IBinder getBinder(){
        return mBinder;
    }

    public class MyBinder extends Binder {

        public void cancelSensorDataUpdateTask() {
            for(String uuid : connectedGateways){
                discoveredGateways.get(uuid).cancelSensorDataUpdateTask();
            }
        }

        public void cancelDiscoveryTask() {
            if (deviceDiscoveryTimer != null) {
                gatewayDiscoveryTimerTask.cancel();
                deviceDiscoveryTimer.cancel();
                deviceDiscoveryTimer = null;
                gatewayDiscoveryTimerTask = null;
            }
        }

        public void close() throws IOException {
        /*Close ScenarioModel*/
            Map<String, GatewayModel> map = Collections.synchronizedMap(discoveredGateways);
            synchronized (map) {
                for (ScenarioModel value : map.values()) {
                    value.close();
                }
            }
        /*Stop Reactor*/
            reactorThread.interrupt();
        }
//
        public void discover() {
            service.discover();
        }

        public HashMap<String, GatewayModel> getModels() {
            return discoveredGateways;
        }

        public boolean isConnectedGatewayEmpty(){
            return connectedGateways.isEmpty();
        }


        public GatewayModel getGatewayModel(String ip) {
            Collections.synchronizedMap(discoveredGateways);
            return discoveredGateways.get(ip);
        }


        public void setSensorDataUpdateHandler(Handler SensorDataUpdateHandler) {
            sensorDataUpdateHandler = SensorDataUpdateHandler;
        }

        public String getSpecificSensorData(String gatewayIPAddress,String uuid, String type){
             return (String)discoveredGateways.get(gatewayIPAddress).getSensorData().get(uuid).get(type).getExpressions()[0].getValue();
        }

        public void setSpecificSensorData(String gatewayIPAddress,String uuid){
            String deviceName=(discoveredGateways.get(gatewayIPAddress)).getSpecificDevices().get(uuid).getName();
            Log.d(TAG, "I-activateSpecialDevice");
            if (deviceName.equals("SmartPlug")) {
                Intent intent = new Intent(service, SmartPlugActivity.class);
                intent.putExtra(MainActivity.EXTRA_MESSAGE, gatewayIPAddress + " " + uuid);
                startActivity(intent);
            }//Hummm....Have a design issue!!
        }

        public void startVideo(String ip, String uuid){
            Log.d(TAG, "I-sendMessageToVideoOutput");
            Log.d(TAG, "d-ip: " + ip);
            Log.d(TAG, "d-Amount of gatewaymodels: " + mBinder.getModels().size());
            GatewayModel gatewayModel = mBinder.getGatewayModel(ip);

            if (gatewayModel == null) {
                Log.e(TAG, "d-gatewayModel == null");
            }
            Log.d(TAG, "d-uuid: " + uuid);
            if (gatewayModel.getVideos(uuid) == null) {
                Log.e(TAG, "d-gatewayModel.getVideos(uuid)==null");
            }
            Log.d(TAG, "d-gatewayModel.getVideos().size(): " + gatewayModel.getVideos().size());
            Iterator i = gatewayModel.getVideos().keySet().iterator();
            while (i.hasNext()) {
                String s = (String) i.next();
                Log.d(TAG, "d-uuid is " + s);
            }
            ((IPCameraControlHandler) (gatewayModel.getVideos(uuid)[0])).startSend();
            Intent intent = new Intent(service, VideoActivity.class);
            intent.putExtra(MainActivity.EXTRA_MESSAGE, ip + " " + uuid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    //Now it's not use
    public final InteroperabilityServiceInterface.Stub otherBinder = new InteroperabilityServiceInterface.Stub(){
        @Override
        public void cancelSensorDataUpdateTask() throws RemoteException {
            mBinder.cancelSensorDataUpdateTask();
        }

        @Override
        public void cancelDiscoveryTask() throws RemoteException {
            mBinder.cancelDiscoveryTask();
        }

        @Override
        public void close() throws RemoteException {
            try {
                mBinder.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void discover() throws RemoteException {
            mBinder.discover();;
        }

        @Override
        public String getSpecificSensorData(String gatewayIPAddress, String uuid, String type) throws RemoteException {
            return mBinder.getSpecificSensorData(gatewayIPAddress,uuid,type);
        }

        @Override
        public void setSpecificSensorData(String gatewayIPAddress, String uuid) throws RemoteException {
            mBinder.setSpecificSensorData(gatewayIPAddress,uuid);
        }

        @Override
        public void startVideo(String ip, String uuid) throws RemoteException {
            mBinder.startVideo(ip,uuid);
        }
    };
    public GatewayModel getGatewayModel(String ip) {
            return  discoveredGateways.get(ip);
    }

    public void addＤiscoveredGateway(ScenarioModel model) {

        GatewayModel gatewayModel = (GatewayModel) (model);
        Map map = Collections.synchronizedMap(discoveredGateways);
        Set set = map.keySet();
        synchronized (map) {
        /* Broadcasting
            if (set.contains(gatewayModel.getHostAddress().getHostAddress())) {
                Log.i(TAG, "gatewayModel is exist");
                return;
            } else {
                map.put(gatewayModel.getHostAddress().getHostAddress(), gatewayModel);
                Log.i(TAG, "Add gatewayModel");
            }
        */
            if (set.contains(gatewayModel.getUuid())) {
                Log.i(TAG, "gatewayModel is exist");
                return;
            } else {
                map.put(gatewayModel.getUuid(), gatewayModel);
                Log.i(TAG, "Add gatewayModel");
            }
        }
    }

    public void connectToGateway(String gatewayUuid){
        Log.d(TAG,"connect to gateway");
        if(gatewayUuid==null){
            Map map = Collections.synchronizedMap(discoveredGateways);
            Set<String> set = map.keySet();
            synchronized (map) {
                Iterator<String> iterator = set.iterator();
                while(iterator.hasNext()){
                    String uuid = iterator.next();
                    if(!isConnected(uuid)) {//尚未處理突然斷掉的情況
                        if(getGatewayIceConnector(uuid)==null||!getGatewayIceConnector(uuid).isActiving()) {
                            ConnectionInfo connectionInfo = new ConnectionInfo();
                            connectionInfo.setUuid(uuid);
                            Agent agent = initAgent(connectionInfo);
                            //ask myself communication info
                            getCommunicationCandidates(agent, connectionInfo);
                            discoveredGateways.get(uuid).setSensorDataConnectionAgent(agent);
                            Gson gson = new GsonBuilder().create();
                            String jsonConnectionInfo = gson.toJson(connectionInfo);
                            if (p2pGatewayConnectionInfoHandler.isActive()) {
                                p2pGatewayConnectionInfoHandler.write(jsonConnectionInfo);
                            }
                        }

                    }
                }
            }
        }else{
            if(!isConnected(gatewayUuid)) {//尚未處理突然斷掉的情況
                ConnectionInfo connectionInfo = new ConnectionInfo();
                connectionInfo.setUuid(gatewayUuid);
                Agent agent = initAgent(connectionInfo);
                //ask myself communication info
                getCommunicationCandidates(agent,connectionInfo);
                discoveredGateways.get(gatewayUuid).setSensorDataConnectionAgent(agent);
                Gson gson = new GsonBuilder().create();
                String jsonConnectionInfo = gson.toJson(connectionInfo);
                p2pGatewayConnectionInfoHandler.write(jsonConnectionInfo);
            }
        }
    }


    private boolean isConnected(String uuid){
        return connectedGateways.contains(uuid);
    }

    public void tagToConnected(String uuid){
        connectedGateways.add(uuid);
    }
    public void disconnectGateway(String uuid){
        connectedGateways.remove(uuid);
    }




    public void startUpdateSensorData(String uuid){
        getGatewayModel(uuid).updateSensorData();
    }


    private Agent initAgent(ConnectionInfo connectionInfo){//For gateway sensor data communication
        Agent agent = new Agent();

        P2pSensorOperationBuilder p2pSensorOperationBuilder = new P2pSensorOperationBuilder(this, getGatewayModel(connectionInfo.getUuid()));
        p2pSensorOperationBuilder.init(reactor);
        p2pSensorOperationBuilder.buildChain();
        SensorOperationHandler sensorOperationHandler = p2pSensorOperationBuilder.get();
        ICE_TCPConnector listener = new ICE_TCPConnector(agent,reactor,new PseudoTCPSocketHandle(),sensorOperationHandler);
        getGatewayModel(connectionInfo.getUuid()).setSensorHandler(sensorOperationHandler);
        gatewayIceConnectorMap.put(connectionInfo.getUuid(),listener);
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
                "140.112.90.147",
                "140.112.90.152",
                "140.112.90.154"

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



    public final static int UPDATE_OK = 0x02;



    public void sendSensorDataUpdateMessage(Message msg) {
        if (sensorDataUpdateHandler != null) {
            sensorDataUpdateHandler.sendMessage(msg);
        }
    }


    public void broadcastIntent(Intent intent) {
        Log.d(TAG, "Broadcast Intent");
        sendBroadcast(intent);
    }


    public boolean gatewayIsExist(String ip) {
        Map map = Collections.synchronizedMap(discoveredGateways);
        Set set = map.keySet();
        synchronized (map) {
            if (set.contains(ip)) {
                Log.d(TAG, "b-gateway is exist");
                return true;
            } else {
                return false;
            }
        }
    }


    public Reactor getReactor() {
        return reactor;
    }

    public boolean gatewayModelsIsEmpty() {
        Map map = Collections.synchronizedMap(discoveredGateways);
        Set set = map.entrySet();
        synchronized (map) {
            if (set.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void setGatewayIceConnectorMap(String uuid, ICE_TCPConnector ice_tcpConnector){
        gatewayIceConnectorMap.put(uuid,ice_tcpConnector);
    }
    public ICE_TCPConnector getGatewayIceConnector(String uuid){
        return gatewayIceConnectorMap.get(uuid);
    }

}