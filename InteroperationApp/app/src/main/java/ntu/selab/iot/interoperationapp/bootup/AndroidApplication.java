package ntu.selab.iot.interoperationapp.bootup;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import ntu.selab.iot.interoperationapp.service.InteroperabilityService;

/**
 * Created by User on 2015/6/1.
 */
public class AndroidApplication extends Application {
    public final static String TAG = "AndroidApplication";
    @Override
    public void onCreate(){
        Log.d(TAG, "onCreate");
        super.onCreate();

    }
}
