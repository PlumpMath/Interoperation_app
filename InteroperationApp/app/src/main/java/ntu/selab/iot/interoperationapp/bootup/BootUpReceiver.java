package ntu.selab.iot.interoperationapp.bootup;

/**
 * Created by User on 2015/6/1.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ntu.selab.iot.interoperationapp.service.InteroperabilityService;

public class BootUpReceiver extends BroadcastReceiver {
    public final static String TAG = "BootUpReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG,"onReceive");
        /* 同一個接收者可以收多個不同行為的廣播
           所以可以判斷收進來的行為為何，再做不同的動作 */
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            /* 收到廣播後開啟目的Service */
            Intent startServiceIntent = new Intent(context, InteroperabilityService.class);
            context.startService(startServiceIntent);
        }
    }
}
