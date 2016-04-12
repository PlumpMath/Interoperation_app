package ntu.selab.iot.interoperationapp.controller;


public class ScenarioController {
   /*
    private final static String TAG = "ScenarioController";
    private MainActivity main;
    private ScenarioDiscovery discovery;
    private HashMap<String, ScenarioModel> gatewayModels;
    private int ListenToGatewayPort = 17005;
    private int sensorDataUpdatePeriod = 4500;//4500
    public final static int discoverTime = 4000;
    private int deviceDiscoveryPeriod = 7000;
    private Timer sensorDataUpdateTimer;
    private Timer deviceDiscoveryTimer;
    public DiscoveryTask discoveryTask;
    private Reactor reactor;
    private Connector broadcastConnector;
    private GatewayDiscoveryHandler gatewayDiscoveryHandler;
    private Thread reactorThread;
    private SensorDataUpdateTask sensorDataUpdateTask;


    public ScenarioController(MainActivity activity) throws IOException {
        this(activity, null);
    }

    public ScenarioController(MainActivity activity, ScenarioDiscovery discovery) throws IOException {
        this.setMain(activity);
        this.discovery = discovery;
        gatewayModels = new HashMap<String, ScenarioModel>();
        reactor = new Reactor();
        gatewayDiscoveryHandler = new GatewayDiscoveryHandler(this, reactor);
        broadcastConnector = new UDPSocketConnector(new UDPSocketHandle(null, -1, ListenToGatewayPort, "ASYC"));
        broadcastConnector.connect(gatewayDiscoveryHandler, null, -1, "ASYC", reactor);
        reactorThread = new Thread(reactor);
        reactorThread.start();
        Log.d(TAG, "b-ScenarioController");
    }

    public GatewayDiscoveryHandler getGatewayDiscoveryHandler() {
        return gatewayDiscoveryHandler;
    }

    public void discover() {
        Log.d(TAG, "b-discover");
        if (deviceDiscoveryTimer != null) {
            discoveryTask.cancel();
            deviceDiscoveryTimer.cancel();
        }
        discoveryTask = new DiscoveryTask(this, discovery);
        deviceDiscoveryTimer = new Timer(true);
        deviceDiscoveryTimer.schedule(discoveryTask, 0, getDeviceDiscoveryPeriod());
    }


    public void updateSensorData() {
        if (!gatewayModels.isEmpty()) {
            if (sensorDataUpdateTimer != null) {
                sensorDataUpdateTask.cancel();
                sensorDataUpdateTimer.cancel();
            }
            sensorDataUpdateTask = new SensorDataUpdateTask(this, gatewayModels);
            sensorDataUpdateTimer = new Timer(true);
            sensorDataUpdateTimer.schedule(sensorDataUpdateTask, 300, getSensorDataUpdatePeriod());
        }
    }

    public void cancelDiscoveryTask() {
        if (deviceDiscoveryTimer != null) {
            discoveryTask.cancel();
            deviceDiscoveryTimer.cancel();
            deviceDiscoveryTimer = null;
            discoveryTask = null;
        }
    }

    public void cancelSensorDataUpdateTask() {
        if (sensorDataUpdateTimer != null) {
            sensorDataUpdateTask.cancel();
            sensorDataUpdateTimer.cancel();
            sensorDataUpdateTimer = null;
            sensorDataUpdateTask = null;
        }

    }

    // Setters & Getters

    public void setDiscovery(ScenarioDiscovery discovery) {
        this.discovery = discovery;
    }

    public ScenarioDiscovery getDiscovery() {
        return discovery;
    }

    public void setGatewayModel(ScenarioModel model) {


        GatewayModel gatewayModel = (GatewayModel) (model);
        Map map = Collections.synchronizedMap(gatewayModels);
        Set set = map.keySet();
        synchronized (map) {
            if (set.contains(gatewayModel.getHostAddress().getHostAddress())) {
                Log.i(TAG, "gatewayModel is exist");
                return;
            } else {
                map.put(gatewayModel.getHostAddress().getHostAddress(), gatewayModel);
                Log.i(TAG, "Add gatewayModel");
            }
        }

    }

    public HashMap<String, ScenarioModel> getModels() {
        return gatewayModels;
    }

    public void setSensorDataUpdatePeriod(int millis) {
        this.sensorDataUpdatePeriod = millis;
    }

    public int getSensorDataUpdatePeriod() {
        return sensorDataUpdatePeriod;
    }

    public void setDeviceDiscoveryPeriod(int millis) {
        deviceDiscoveryPeriod = millis;
    }

    public int getDeviceDiscoveryPeriod() {
        return deviceDiscoveryPeriod;
    }

    public MainActivity getMain() {
        return main;
    }


    public void setMain(MainActivity main) {
        this.main = main;
    }


    public final static int SCAN_OK = 0x01;
    public final static int UPDATE_OK = 0x02;

    private Handler scanHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ScenarioController.SCAN_OK:
                    getMain().cancelScanningProgressView();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    private Handler SensorDataUpdateHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case ScenarioController.UPDATE_OK:
                    main.update();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Handler mediaHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ScenarioController.UPDATE_OK:
                    getMain().cancelScanningProgressView();
                    break;
            }

            super.handleMessage(msg);
        }
    };


    public void sendSensorDataUpdateMessage(Message msg) {
        SensorDataUpdateHandler.sendMessage(msg);
    }


    public void broadcastIntent(Intent intent){
        Log.d(TAG,"Broadcast Intent");
        main.sendBroadcast(intent);
    }


    public void sendScanMessage(Message msg) {
        scanHandler.sendMessage(msg);

    }

    public GatewayModel getGatewayModel(String ip) {
        Map map = Collections.synchronizedMap(gatewayModels);
        synchronized (map) {
            return (GatewayModel) gatewayModels.get(ip);
        }
    }

//	@Override
//	public void run() {
//		Log.d("Regular Discovery","I am live");
//		discover();
//	}

//	public void setVideoActivity(VideoActivity videoActivity){
//		this.videoActivity=videoActivity;
//	}


    public boolean gatewayIsExist(String ip) {
        Map map = Collections.synchronizedMap(gatewayModels);
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
        Map map = Collections.synchronizedMap(gatewayModels);
        Set set = map.entrySet();
        synchronized (map) {
            if (set.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void closeBroadcastChannel() throws IOException {
        gatewayDiscoveryHandler.close();
    }

    public void close() throws IOException {
        //Close broadcasting
        closeBroadcastChannel();
        //Close ScenarioModel
        Map<String, ScenarioModel> map = Collections.synchronizedMap(gatewayModels);
        synchronized (map) {
            for (ScenarioModel value : map.values()) {
                value.close();
            }
        }
        //Stop Reactor
        reactorThread.interrupt();
    }

*/
}
