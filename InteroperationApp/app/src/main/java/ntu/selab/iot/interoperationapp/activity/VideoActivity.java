package ntu.selab.iot.interoperationapp.activity;

import java.io.IOException;

import ntu.selab.iot.interoperationapp.R;
import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.service.InteroperabilityService;
import ntu.selab.iot.interoperationapp.serviceHandler.media.VideoOutput;
import ntu.selab.iot.interoperationapp.serviceHandler.media.VideoTestOutput;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.media.MediaExtractor;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoActivity extends Activity implements SurfaceHolder.Callback {
    private final static String TAG = "VideoActivity";
    private VideoOutput mediaPlayer = null;
    private VideoTestOutput mediaTestPlayer = null;
    public static InteroperabilityService.MyBinder myBinder;
    private String gatewayUuid;
    private String uuid;
    private GatewayModel gatewayModel;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Fuck");
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
        Log.e(TAG, "onCreate");
        SurfaceView sv = new SurfaceView(this);
        sv.getHolder().addCallback(this);
        setContentView(sv);
        //Get intent from TileView
        Intent intent = getIntent();
        String[] intents = intent.getStringExtra(MainActivity.EXTRA_MESSAGE).split("[ ]");
        gatewayUuid = intents[0];
        Log.d(TAG, "I-onCreate-GatewayUuid:" + gatewayUuid);
        uuid = intents[1];
        Log.d(TAG, "I-onCreate-CameraUuid:" + uuid);

        Intent bindIntent = new Intent(this, InteroperabilityService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        gatewayModel = MainActivity.myBinder.getGatewayModel(gatewayUuid);
        mediaPlayer = gatewayModel.getVideoOutput(uuid);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        gatewayModel.sendStopVideoCommand(uuid);
        mediaPlayer.flushDepacketizer();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart");
        gatewayModel.sendStartVideoCommand(uuid,null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged");
        if (mediaPlayer != null) {
//            if(mediaPlayer.getSurface()==null) {
            mediaPlayer.setSurface(holder.getSurface());
//            }
            mediaPlayer.init();
            mediaPlayer.startDecode();
            //mediaPlayer.startSendRTCP();
        }
        if (mediaTestPlayer != null) {
            Log.d(TAG, "surfaceChanged");
            mediaTestPlayer.setSurface(holder.getSurface());
            try {
                mediaTestPlayer.setExtrator(createExtractor(R.raw.h264_test3_1080p));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mediaTestPlayer.start();
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed");
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
        if (mediaTestPlayer != null) {
            mediaTestPlayer.interrupt();
        }
    }


    private MediaExtractor createExtractor(int mSourceResId) throws IOException {
        MediaExtractor extractor;
        AssetFileDescriptor srcFd = this.getResources().openRawResourceFd(mSourceResId);
        extractor = new MediaExtractor();
        extractor.setDataSource(srcFd.getFileDescriptor(), srcFd.getStartOffset(), srcFd.getLength());
        return extractor;
    }
}
