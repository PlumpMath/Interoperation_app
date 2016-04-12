package ntu.selab.iot.interoperationapp.convertor;

import java.util.ArrayList;

import ntu.selab.iot.interoperationapp.R;
import ntu.selab.iot.interoperationapp.activity.MainActivity;
import ntu.selab.iot.interoperationapp.gatewayBar.SensorGallery;
import ntu.selab.iot.interoperationapp.tile.TileView;
import ntu.selab.iot.interoperationapp.protocol.communication.DataInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.ExpressionInfo;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class GatewayDataViewsBuilder extends DataViewBuilder {
    private final static String TAG = "GatewayDataViewsConvertor";


    public GatewayDataViewsBuilder(Context context) {
        this.context = context;
    }

    @Override
    public void convertStringDataView(DataInfo dataInfo, String uuid) {

        TileView view = TileView.obtain(context);
        view.setVisibility(View.VISIBLE);
        view.visible();
        String dataType = dataInfo.getType();
        view.setType(dataType);
        view.setUuid(uuid);

        view.setContent(findSensorIcon(dataType));

        Log.d("UI-Update", "I-dataType: " + dataType);
        if (dataInfo.getType().equals("Camera")) {
            view.setTitle(dataInfo.getType());
        } else {
            view.setTitle(convertData(dataInfo));
        }
        dataViewsOutput.add(view);
    }

    public String convertData(DataInfo dataInfo){
        String dataVal = "";
        String unit = "";
		/* For now, there is only one expression.*/
        for (int j = 0; j < dataInfo.getExpressions().length; j++) {
            ExpressionInfo dataExpression = dataInfo.getExpressions()[j];
            unit = dataExpression.getUnit();
            String s=null;
                if(dataExpression.getClassName().equals("String")){
                    s = (java.lang.String) dataExpression.getValue();
                }else{
                    double d = (Double) dataExpression.getValue();
                    s=Double.toString(d);
                }

//                else if(dataExpression.getClassName().equals("double")){
//                    double d = (Double) dataExpression.getValue();
//                    s=Double.toString(d);
//                }else if(dataExpression.getClassName().equals("float")){
//                    float f = (Float) dataExpression.getValue();
//                    s=Double.toString(f);
//                }else if(dataExpression.getClassName().equals("int")){
//                    int i = (Integer) dataExpression.getValue();
//                    s= Integer.toString(i);
//                }else{
//                    s= null;
//                    Log.e(TAG,"Error Primary Type from gateway");
//                }
//                Class c = Class.forName(dataExpression.getClassName());
//                String s = (String) c.cast(dataExpression.getValue());

                dataVal += roundDataValue(s, 3);
                if (j != dataInfo.getExpressions().length - 1) {
                    dataVal += ", ";
                }

        }
        return dataVal + unit;
    }

    @Override
    public void convertSpecificSensorTileView(final String uuid, final String deviceName, final String ip) {
        Log.d(TAG, "I-convertSpecificSensorTileView");
        Log.d(TAG, "I-deviceName: " + deviceName);
        
        TileView view = TileView.obtain(context);
        view.setVisibility(View.VISIBLE);
        view.visible();
        view.setTitle(deviceName);
        view.setType(deviceName);
        view.setContent(findSensorIcon(deviceName));

        view.setUuid(uuid);


        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) context).activateSpecialDevice(view, ip, uuid, deviceName);
            }
        });
        dataViewsOutput.add(view);
    }
/*
    @Override
    public void convertIntegerDataView(int integer) {
        // TODO Auto-generated method stub
    }

    @Override
    public void convertImageDataView(Bitmap image) {
        // TODO Auto-generated method stub
    }

    @Override
    public void convertContextDataView(Bitmap image) {
        // TODO Auto-generated method stub

    }

*/
    @Override
    public TileView convertMediaDataView(DataInfo dataInfo, final String uuid, final String ip, final String deviceName) {
        Log.d(TAG, "I-convertMediaDataView");
//        String dataType = dataInfo.getType();
        String dataType = "Video";
        TileView view = TileView.obtain(context);
        view.setVisibility(View.VISIBLE);
        view.visible();
        view.setTitle(deviceName);
        view.setType(deviceName);
        view.setUuid(uuid);
        view.setContent(findSensorIcon(dataType));
        Log.d(TAG, "I-dataType: " + dataType);
        if (!MainActivity.TEST_MODE) {

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)context).sendMessageToVideoOutput(view, ip, uuid, deviceName);
                }
            });
        } else {
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)context).sendMessageToVideoTestOutput(view, ip, uuid);
                }
            });
        }

        dataViewsOutput.add(view);
        return view;
    }


    public int findSensorIcon(String dataType) {
        if (dataType.contains("Temperature")) {
            return R.drawable.temperature;

        } else if (dataType.contains("Humidity")) {
            return R.drawable.humidity;
        } else if (dataType.contains("Pressure")) {
            return R.drawable.pressure;
        } else if (dataType.contains("Accelarator")) {
            return R.drawable.acceleration;
        } else if (dataType.contains("Battery")) {
            return R.drawable.battery;
        } else if (dataType.contains("Video")) {
            return R.drawable.ipcamera;// will change
        } else if (dataType.contains("SmartPlug")) {
            return R.drawable.smartplug;//Will Change
        } else if(dataType.contains("Motion Sensor")){
            return R.drawable.motion;//Will Change
        } else if(dataType.contains("Sound Sensor")){
            return R.drawable.volume;
        } else if(dataType.contains("Compass")){
            return R.drawable.compass;
        }
        return R.drawable.sensor;
    }

    private String roundDataValue(String dataVal, int decimal) {
        try {
            return Double.toString(Math.round(Double.parseDouble(dataVal) * Math.pow(10, decimal)) / Math.pow(10, decimal));
        }
        catch(Exception e){
            return dataVal;
        }
    }

    @Override
    public ArrayList<RelativeLayout> getDataViews() {
        return dataViewsOutput;
    }

    public void flushDataViews() {
        dataViewsOutput.clear();
    }



    @Override
    public ArrayList<RelativeLayout> getDataViews_OldUI() {
        // TODO Auto-generated method stub
        return null;
    }

    public SensorGallery getGateway() {
        return new SensorGallery(context, null);
    }
}
