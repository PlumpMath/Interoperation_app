package ntu.selab.iot.interoperationapp.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.service.InteroperabilityService;
import ntu.selab.iot.interoperationapp.serviceHandler.media.VideoOutput;
import ntu.selab.iot.interoperationapp.protocol.communication.DataInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.ExpressionInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.SensorInfo;

public class ZyXELCamActivity extends Activity implements SurfaceHolder.Callback {
    private final static String TAG = "ZyXELCamActivity";
    private VideoOutput mediaPlayer = null;
    public static InteroperabilityService.MyBinder myBinder;
    private String ip;
    private String uuid;
    private GatewayModel belongedGateway;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            myBinder = (InteroperabilityService.MyBinder) service;
            if (myBinder == null) {
                Log.e(TAG, "myBinder==null");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FrameLayout fl = new FrameLayout(this);
        LinearLayout ll = new LinearLayout(this);
        SurfaceView sv = new SurfaceView(this);
        sv.getHolder().addCallback(this);


        //Get intent from TileView
        Intent intent = getIntent();
        String[] intents = intent.getStringExtra(MainActivity.EXTRA_MESSAGE).split("[ ]");
        ip = intents[0];
        Log.d(TAG, "I-onCreate-IP:" + ip);
        uuid = intents[1];
        Log.d(TAG, "I-onCreate-uuid:" + uuid);

        Intent bindIntent = new Intent(this, InteroperabilityService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
        belongedGateway = MainActivity.myBinder.getGatewayModel(ip);
        final GatewayModel gatewayModel = MainActivity.myBinder.getGatewayModel(ip);
        mediaPlayer = gatewayModel.getVideoOutput(uuid);

        Button buttonLeft = new Button(this);
        buttonLeft.setText("Left");
        buttonLeft.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Left");
                SensorInfo sensorInfo = new SensorInfo();
                sensorInfo.setUuid(uuid);
                DataInfo dataInfo = new DataInfo();
                ExpressionInfo expressionInfo = new ExpressionInfo();
                expressionInfo.setClassName("java.lang.String");
                expressionInfo.setUnit("left");
                dataInfo.addExpression(expressionInfo);
                dataInfo.setType("Move");
                sensorInfo.addData(dataInfo);
                gatewayModel.sendData(sensorInfo);
            }
        });
        ll.addView(buttonLeft);

        Button buttonUp = new Button(this);
        buttonUp.setText("Up");
        buttonUp.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensorInfo sensorInfo = new SensorInfo();
                sensorInfo.setUuid(uuid);
                DataInfo dataInfo = new DataInfo();
                ExpressionInfo expressionInfo = new ExpressionInfo();
                expressionInfo.setClassName("java.lang.String");
                expressionInfo.setUnit("up");
                dataInfo.addExpression(expressionInfo);
                dataInfo.setType("Move");
                sensorInfo.addData(dataInfo);
                gatewayModel.sendData(sensorInfo);
            }
        });
        ll.addView(buttonUp);

        Button buttonDown = new Button(this);
        buttonDown.setText("Down");
        buttonDown.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensorInfo sensorInfo = new SensorInfo();
                sensorInfo.setUuid(uuid);
                DataInfo dataInfo = new DataInfo();
                ExpressionInfo expressionInfo = new ExpressionInfo();
                expressionInfo.setClassName("java.lang.String");
                expressionInfo.setUnit("down");
                dataInfo.addExpression(expressionInfo);
                dataInfo.setType("Move");
                sensorInfo.addData(dataInfo);
                gatewayModel.sendData(sensorInfo);
            }
        });
        ll.addView(buttonDown);

        Button buttonRight = new Button(this);
        buttonRight.setText("Right");
        buttonRight.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensorInfo sensorInfo = new SensorInfo();
                sensorInfo.setUuid(uuid);
                DataInfo dataInfo = new DataInfo();
                ExpressionInfo expressionInfo = new ExpressionInfo();
                expressionInfo.setClassName("java.lang.String");
                expressionInfo.setUnit("right");
                dataInfo.addExpression(expressionInfo);
                dataInfo.setType("Move");
                sensorInfo.addData(dataInfo);
                gatewayModel.sendData(sensorInfo);
            }
        });

        ll.addView(buttonRight);
        fl.addView(sv);
        fl.addView(ll);

        setContentView(fl);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        belongedGateway.sendStopVideoCommand(uuid);
        mediaPlayer.flushDepacketizer();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        belongedGateway.sendStartVideoCommand(uuid,null);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged");
        if (mediaPlayer != null) {
            mediaPlayer.setSurface(holder.getSurface());
            mediaPlayer.init();
            mediaPlayer.startDecode();
            //mediaPlayer.startSendRTCP();
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mediaPlayer != null) {
            mediaPlayer.stopDecode();
            //mediaPlayer.stopSendRTCP();
            while (mediaPlayer.decoding) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mediaPlayer.shutDown();
        }
    }
}
