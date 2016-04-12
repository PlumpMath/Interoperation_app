package ntu.selab.iot.interoperationapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import ntu.selab.iot.interoperationapp.R;
import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.protocol.communication.DataInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.ExpressionInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.SensorInfo;

public class SmartPlugActivity extends Activity {
    public final static String TAG = "SmartPlugActivity";
    private GatewayModel gatewayModel;
    private String uuid;
    private String deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_plug);
        //Get intent from TileView
        Intent intent = getIntent();
        String[] intents = intent.getStringExtra(MainActivity.EXTRA_MESSAGE).split("[ ]");
        String ip = intents[0];
        Log.d(TAG, "I-onCreate-IP:" + ip);
        uuid = intents[1];
        Log.d(TAG, "I-onCreate-uuid:" + uuid);
        gatewayModel = MainActivity.myBinder.getGatewayModel(ip);
        SensorInfo sensorInfo = gatewayModel.getSpecificDevices().get(uuid);
        deviceName = sensorInfo.getName();
        Log.d(TAG,"Smart Plug Name: "+deviceName);

        if(sensorInfo.getData().length==0){
            fillRowEditText(findViewById(R.id.Voltage),"Miss", "Miss", false, false);
            fillRowEditText(findViewById(R.id.Current), "Miss", "Miss" , false, false);
            fillRowEditText(findViewById(R.id.Frequency),"Miss", "Miss" , false, false);
            fillRowEditText(findViewById(R.id.PowerConsumption),"Miss", "Miss", false, false);
            fillRowEditText(findViewById(R.id.AccumulateEnergy),"Miss", "Miss" , false, false);
            fillRowSwitch(findViewById(R.id.Switch), "Switch", true);
        }else {
            Log.d(TAG,"Type: "+sensorInfo.getData()[0].getType());
            Log.d(TAG,"Value: "+sensorInfo.getData()[0].getExpressions()[0].getValue());
            Log.d(TAG,"Unit: "+sensorInfo.getData()[0].getExpressions()[0].getUnit());
            fillRowEditText(findViewById(R.id.Voltage), sensorInfo.getData()[0].getType(), sensorInfo.getData()[0].getExpressions()[0].getValue() + " " + sensorInfo.getData()[0].getExpressions()[0].getUnit(), false, false);
            fillRowEditText(findViewById(R.id.Current), sensorInfo.getData()[2].getType(), sensorInfo.getData()[2].getExpressions()[0].getValue() + " " + sensorInfo.getData()[2].getExpressions()[0].getUnit(), false, false);
            fillRowEditText(findViewById(R.id.Frequency), sensorInfo.getData()[1].getType(), sensorInfo.getData()[1].getExpressions()[0].getValue() + " " + sensorInfo.getData()[1].getExpressions()[0].getUnit(), false, false);
            fillRowEditText(findViewById(R.id.PowerConsumption), sensorInfo.getData()[4].getType(), sensorInfo.getData()[4].getExpressions()[0].getValue() + " " + sensorInfo.getData()[4].getExpressions()[0].getUnit(), false, false);
            fillRowEditText(findViewById(R.id.AccumulateEnergy), sensorInfo.getData()[3].getType(), sensorInfo.getData()[3].getExpressions()[0].getValue() + " " + sensorInfo.getData()[3].getExpressions()[0].getUnit(), false, false);
            Double currentState = Double.valueOf(String.valueOf(sensorInfo.getData()[2].getExpressions()[0].getValue()));
            Log.e(TAG, "State: " + currentState);
            if (currentState > 0) {
                fillRowSwitch(findViewById(R.id.Switch), "Switch", true);
            } else {
                fillRowSwitch(findViewById(R.id.Switch), "Switch", false);
            }
        }

    }

    private void fillRowEditText(View view, String label, Object value, boolean enable, boolean singleLine) {
        TextView labelView = (TextView) view.findViewById(R.id.label);
        labelView.setText(label);
        EditText valueView = (EditText) view.findViewById(R.id.value);
        valueView.setText(String.valueOf(value));
        valueView.setSingleLine(singleLine);
        valueView.setEnabled(enable);
    }

    private void fillRowSwitch(View view, String label, boolean switch1) {
        TextView labelView = (TextView) view.findViewById(R.id.label);
        labelView.setText(label);
        Switch switch1View = (Switch) view.findViewById(R.id.switch1);
        switch1View.setChecked(switch1);
        switch1View.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {//On
                    SensorInfo sensorInfo = new SensorInfo();
                    sensorInfo.setUuid(uuid);
                    DataInfo dataInfo = new DataInfo();
                    ExpressionInfo expressionInfo = new ExpressionInfo();
                    expressionInfo.setClassName("java.lang.String");
                    expressionInfo.setUnit("");
                    expressionInfo.setValue("On");
                    dataInfo.addExpression(expressionInfo);
                    dataInfo.setType("Switch");
                    sensorInfo.addData(dataInfo);
                    sensorInfo.setName(deviceName);
                    gatewayModel.sendData(sensorInfo);
                } else {//Off
                    SensorInfo sensorInfo = new SensorInfo();
                    sensorInfo.setUuid(uuid);
                    DataInfo dataInfo = new DataInfo();
                    ExpressionInfo expressionInfo = new ExpressionInfo();
                    expressionInfo.setClassName("java.lang.String");
                    expressionInfo.setUnit("");
                    expressionInfo.setValue("Off");
                    dataInfo.addExpression(expressionInfo);
                    dataInfo.setType("Switch");
                    sensorInfo.addData(dataInfo);
                    sensorInfo.setName(deviceName);
                    gatewayModel.sendData(sensorInfo);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_smart_plug, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
