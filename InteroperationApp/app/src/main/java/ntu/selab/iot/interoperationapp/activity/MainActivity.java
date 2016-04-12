package ntu.selab.iot.interoperationapp.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import ntu.selab.iot.interoperationapp.R;
import ntu.selab.iot.interoperationapp.convertor.GatewayDataViewsBuilder;
import ntu.selab.iot.interoperationapp.gatewayBar.SensorGallery;
import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.model.ScenarioModel;

import ntu.selab.iot.interoperationapp.service.InteroperabilityService;
import ntu.selab.iot.interoperationapp.serviceHandler.IPCameraControlHandler;
import ntu.selab.iot.interoperationapp.tile.TileView;
import ntu.selab.iot.interoperationapp.protocol.communication.DataInfo;


public class MainActivity extends ActionBarActivity {
    private final static String TAG = "MainActivity";
    public static LinearLayout gatewayArea;
    public static LinearLayout scanningProgressView;


    private int updatePeriod = 4500;
    private static MainActivity mainActivity;

    public final static boolean FAKE_MODE = false;
    public final static boolean TEST_MODE = false;


    private static GatewayDataViewsBuilder gatewayDataViewsBuilder;
    private static HashMap<String, Integer> sensorGalleryPosition = new HashMap<String, Integer>();


    public final static String EXTRA_MESSAGE = "ntu.selab.iot.interoperation.MESSAGE";
    public static String image_cache_dir = "/sdcard/Interoperationapp/img/";
    public static InteroperabilityService.MyBinder myBinder;

    public final static int SCAN_OK = 0x01;
    public final static int UPDATE_OK = 0x02;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            myBinder = (InteroperabilityService.MyBinder) service;
            myBinder.setSensorDataUpdateHandler(SensorDataUpdateHandler);

        }
    };

    private Handler SensorDataUpdateHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case UPDATE_OK:
                    update();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        initLayout();
        mainActivity = this;
        Intent bindIntent = new Intent(mainActivity, InteroperabilityService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        gatewayDataViewsBuilder = new GatewayDataViewsBuilder(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause~~~");
        myBinder.cancelSensorDataUpdateTask();
        myBinder.cancelDiscoveryTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy~~~");
        unbindService(connection);
        System.exit(0);
        myBinder.cancelDiscoveryTask();
        myBinder.cancelSensorDataUpdateTask();
        try {
            myBinder.close();

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart~~~");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume~~~");


        if (myBinder != null) {
            myBinder.discover();

            if (!myBinder.isConnectedGatewayEmpty()) {
                Log.e(TAG, "b-Hide progressView");
                hideProgressView();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    public static MainActivity getInstance() {
        return mainActivity;
    }

    public void initLayout() {
        setContentView(R.layout.activity_main);
//		mainView = (LinearLayout) findViewById(R.id.main_content); 
        gatewayArea = (LinearLayout) findViewById(R.id.gateway_area);
        scanningProgressView = (LinearLayout) findViewById(R.id.main_progress);

        Log.d(TAG, "I-initLayout");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            Log.d("exit", id + ": " + "REFRESH");
//			controller.cancelDiscoveryTask();
            myBinder.discover();
            return true;

        } else if (id == R.id.action_exit) {
            Log.d("exit", id + ": " + "EXIT");
            System.exit(0);
            myBinder.cancelDiscoveryTask();
            myBinder.cancelSensorDataUpdateTask();
            try {
                myBinder.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;

        }

        return super.onOptionsItemSelected(item);
    }


    public synchronized void update() {
        HashMap<String, GatewayModel> scenarioModels = myBinder.getModels();
        Map map = Collections.synchronizedMap(scenarioModels);
        Set set = map.entrySet();
        synchronized (map) {
            Iterator<Map.Entry<String, ScenarioModel>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry<String, ScenarioModel> scenarioModelSet = it.next();
                //If is gatewayModel
                GatewayModel gatewayModel = (GatewayModel) scenarioModelSet.getValue();
                final String ip = scenarioModelSet.getKey();
                Integer position = sensorGalleryPosition.get(ip);
                HashMap<String, HashMap<String, DataInfo>> deviceInfo = gatewayModel.getSensorData();
                if (position != null) {//If the gateway bar is exist.(Gateway is detected before)
                    SensorGallery gateway = (SensorGallery) gatewayArea.getChildAt(position.intValue());
                    //Whether the last gotten data are still there.
                    for (int i = 0; i < gateway.getSize(); i++) {
                        TileView tileView = gateway.getTileViewByIndex(i);
                        String uuid = tileView.getUuid();
                        String type = tileView.getType();
                        Log.e(TAG, "uuid=" + uuid);
                        Log.e(TAG, "type=" + type);


                        if (deviceInfo.get(uuid) != null) {//gateway has this deviceInfo
                            if (gatewayModel.containSpecificDevice(uuid)) {
                                tileView.visible();
                                deviceInfo.remove(uuid);
                            } else if (type.contains("Video")) {
                                if((gatewayModel.getLocalVideoConnectionState(uuid)&gatewayModel.getRemoteVideoConnectionState(uuid))==1) {
                                    tileView.visible();

                                }
                                deviceInfo.remove(uuid);
                            } else if (deviceInfo.get(uuid).get(type) != null) {
                                DataInfo dataInfo = deviceInfo.get(uuid).get(type);
                                tileView.setTitle(gatewayDataViewsBuilder.convertData(dataInfo));
                                tileView.visible();
                                deviceInfo.get(uuid).remove(type);
                            } else {//Can't find dataInfo in gateway
                                Toast.makeText(this, tileView.getType()+" is detached", Toast.LENGTH_SHORT).show();
                                gateway.hideSpecificView(i);
                            }
                        } else {//gateway has no this deviceInfo
                            Toast.makeText(this, tileView.getType()+" is detached", Toast.LENGTH_SHORT).show();
                            gateway.hideSpecificView(i);
                        }
                    }
                    //Add the new gotten data
                    Iterator<Map.Entry<String, HashMap<String, DataInfo>>> infoIterator = deviceInfo.entrySet().iterator();
                    while (infoIterator.hasNext()) {
                        Map.Entry<String, HashMap<String, DataInfo>> pair = infoIterator.next();
                        String uuid = pair.getKey();
                        HashMap<String, DataInfo> sensorInfoMap = pair.getValue();
                        if (sensorInfoMap == null) {
                            continue;
                        }

                        if (gatewayModel.containSpecificDevice(uuid)&&!gatewayModel.getCameraUuid().contains(uuid)) {//
                            gatewayDataViewsBuilder.convertSpecificSensorTileView(uuid, gatewayModel.getSpecificDevices().get(uuid).getName(), ip);
                        } else {
                            Log.i(TAG, "MT-sensorInfos size: " + sensorInfoMap.size());
                            Iterator<Map.Entry<String, DataInfo>> sensorInfoIterator = sensorInfoMap.entrySet().iterator();
                            while (sensorInfoIterator.hasNext()) {
                                Map.Entry<String, DataInfo> sensorInfoPair = sensorInfoIterator.next();
                                String sensorType = sensorInfoPair.getKey();
                                DataInfo sensorData = sensorInfoPair.getValue();
                                if (sensorType.contains("Video")) {
                                    if((gatewayModel.getLocalVideoConnectionState(uuid)&gatewayModel.getRemoteVideoConnectionState(uuid))==1) {
                                        gatewayModel.getCameraThreads(uuid).start();
                                        Log.d(TAG, " IPCameraStreamingHandler CameraThread is start");
                                        gatewayDataViewsBuilder.convertMediaDataView(sensorData, uuid, ip, "Video");
                                    }
                                } else {
                                    gatewayDataViewsBuilder.convertStringDataView(sensorData, uuid);
                                }//...

                            }
                        }
                        ArrayList<RelativeLayout> sensorDataViews = gatewayDataViewsBuilder.getDataViews();
                        for (RelativeLayout view : sensorDataViews) {
                            TileView tileView = (TileView)view;
                            Toast.makeText(this, tileView.getType()+" is attached", Toast.LENGTH_SHORT).show();
                            gateway.addContent(view);
                        }
                        gatewayDataViewsBuilder.flushDataViews();
                    }
                } else {//Gateway bar is not exist. (Gateway is not detected before.)
                    SensorGallery gateway = gatewayDataViewsBuilder.getGateway();
                    gateway.setTitle(gatewayModel.getHostName(), true);
                    Iterator<Map.Entry<String, HashMap<String, DataInfo>>> infoIterator = deviceInfo.entrySet().iterator();
                    while (infoIterator.hasNext()) {
                        Map.Entry<String, HashMap<String, DataInfo>> pair = infoIterator.next();
                        String uuid = pair.getKey();
                        HashMap<String, DataInfo> sensorInfoMap = pair.getValue();
                        if (sensorInfoMap == null) {
                            continue;
                        }
                        if (gatewayModel.containSpecificDevice(uuid)&&!gatewayModel.getCameraUuid().contains(uuid)) {//

                            gatewayDataViewsBuilder.convertSpecificSensorTileView(uuid, gatewayModel.getSpecificDevices().get(uuid).getName(), ip);
                        } else {
                            Log.i(TAG, "MT-sensorInfos size: " + sensorInfoMap.size());
                            Iterator<Map.Entry<String, DataInfo>> sensorInfoIterator = sensorInfoMap.entrySet().iterator();
                            while (sensorInfoIterator.hasNext()) {
                                Map.Entry<String, DataInfo> sensorInfoPair = sensorInfoIterator.next();
                                String sensorType = sensorInfoPair.getKey();
                                DataInfo sensorData = sensorInfoPair.getValue();
                                if (sensorType.contains("Video")) {
                                    if((gatewayModel.getLocalVideoConnectionState(uuid)&gatewayModel.getRemoteVideoConnectionState(uuid))==1) {
                                        gatewayModel.getCameraThreads(uuid).start();
                                        Log.d(TAG, " IPCameraStreamingHandler CameraThread is start");
                                        gatewayDataViewsBuilder.convertMediaDataView(sensorData, uuid, ip, "Video");
                                    }
                                } else {
                                    gatewayDataViewsBuilder.convertStringDataView(sensorData, uuid);
                                }//...
                            }
                        }
                        ArrayList<RelativeLayout> sensorDataViews = gatewayDataViewsBuilder.getDataViews();
                        for (RelativeLayout view : sensorDataViews) {

                            gateway.addContent(view);
                            TileView tileView = (TileView)view;
                            Toast.makeText(this, tileView.getType()+" is attached", Toast.LENGTH_SHORT).show();
                        }
                        gatewayDataViewsBuilder.flushDataViews();
                    }
                    gatewayArea.addView(gateway);
                    sensorGalleryPosition.put(ip, gateway.getContentIndex(gateway));
                }
            }
        }
        hideProgressView();
    }

    private static void recycleAllTileViewinGatewayBar() {
        int childcount = gatewayArea.getChildCount();
        for (int i = 0; i < childcount; i++) {
            SensorGallery g = (SensorGallery) gatewayArea.getChildAt(i);
            g.recycleAllTileView();
        }
    }


    public void sendMessageToVideoOutput(View view, String ip, String uuid, String name) {
        Log.d(TAG, "I-sendMessageToVideoOutput");
        Log.d(TAG, "d-ip: " + ip);
        Log.d(TAG, "d-Amount of gatewaymodels: " + myBinder.getModels().size());
        GatewayModel gatewayModel = myBinder.getGatewayModel(ip);

        if (gatewayModel == null) {
            Log.e(TAG, "d-gatewayModel == null");
        }
        Log.d(TAG, "d-uuid: " + uuid);
        if (gatewayModel.getVideos(uuid) == null) {
            Log.e(TAG, "d-gatewayModel.getVideos(uuid)==null");
        }
//        Log.d(TAG, "d-gatewayModel.getVideos().size(): " + gatewayModel.getVideos().size());
        Iterator i = gatewayModel.getVideos().keySet().iterator();
        while (i.hasNext()) {
            String s = (String) i.next();
            Log.d(TAG, "d-uuid is " + s);
        }


         gatewayModel.sendStartVideoCommand(uuid, name);
        if(name.equals("ZyXEL_CAM2115")){
            gatewayModel.sendStartVideoCommand(uuid,name);
            Intent intent = new Intent(this, ZyXELCamActivity.class);
            intent.putExtra(MainActivity.EXTRA_MESSAGE, ip + " " + uuid);
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, VideoActivity.class);
            intent.putExtra(MainActivity.EXTRA_MESSAGE, ip + " " + uuid);
            startActivity(intent);
        }
    }

    public void sendMessageToVideoTestOutput(View view, String ip, String uuid) {
        Log.d(TAG, "I-sendMessageToVideoOutput");
//		GatewayModel gatewayModel = controller.getGatewayModel(ip);
//		((IPCameraControlHandler)(gatewayModel.getVideos(uuid)[0])).SetStart(true);
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE, ip + " " + uuid);
        startActivity(intent);
    }

    public void activateSpecialDevice(View view, String ip, String uuid, String deviceName) {
        Log.d(TAG, "I-activateSpecialDevice");
        if (deviceName.equals("SmartPlug")) {
            Intent intent = new Intent(this, SmartPlugActivity.class);
            intent.putExtra(MainActivity.EXTRA_MESSAGE, ip + " " + uuid);
            startActivity(intent);
        }


        //Hummm....Have a design issue!!
    }

    public void hideProgressView() {
        if (FAKE_MODE) {
            scanningProgressView.setVisibility(View.GONE);
        } else {
            Message msg = new Message();
            msg.what = SCAN_OK;
            scanHandler.sendMessage(msg);

        }
    }

    private Handler scanHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCAN_OK:
                    cancelScanningProgressView();
                    break;
            }

            super.handleMessage(msg);
        }
    };



    public void cancelScanningProgressView() {
        Log.d("UI_cancelScanningView", "I-hide scan");
        scanningProgressView.setVisibility(View.GONE);
        scanningProgressView.invalidate();
    }

    public int getUpdatePeriod() {
        return updatePeriod;
    }

}
