package ntu.selab.iot.interoperationapp.BroadcastReceiver;

import android.app.Service;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;

import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.service.InteroperabilityService;
import ntu.selab.iot.interoperationapp.protocol.communication.DataInfo;
import ntu.selab.iot.interoperationapp.protocol.communication.ExpressionInfo;

/**
 * Created by User on 2015/6/3.
 */
public class BPELCommandHandler extends CommandHandler{
    public final static String TAG = "BPELCommandHandler";
    public final static String BPELreply = "ntu.selab.iot.interoperability.bpel.respond";
    private Service service;
    public BPELCommandHandler(Service service){
        super();
        this.service = service;
    }
    private double roundDataValue(String dataVal, int decimal) {
        return Math.round(Double.parseDouble(dataVal) * Math.pow(10, decimal)) / Math.pow(10, decimal);
    }
    public String convertData(DataInfo dataInfo){
        String dataVal = "";
        String unit = "";

		/* For now, there is only one expression.*/
        for (int j = 0; j < dataInfo.getExpressions().length; j++) {
            ExpressionInfo dataExpression = dataInfo.getExpressions()[j];
            unit = dataExpression.getUnit();
            try {
                Class c = Class.forName(dataExpression.getClassName());
                String s = (String) c.cast(dataExpression.getValue());
                dataVal += roundDataValue(s, 3);
                if (j != dataInfo.getExpressions().length - 1) {
                    dataVal += ", ";
                }
            } catch (ClassNotFoundException e) {
                Log.d(TAG, "Convert error" + e.getMessage());
            }

        }
        return dataVal + unit;
    }
    @Override
    public void handle(Intent intent) {
        if(BroadcastReceiver.BPELProtocalKey.equals(intent.getAction())){
            String command = intent.getStringExtra("command");
            String gatewayIPAddress = intent.getStringExtra("gatewayIPAddress");
            String uuid = intent.getStringExtra("uuid");
            String type = intent.getStringExtra("type");


            Log.e(TAG, command);
            Log.e(TAG,gatewayIPAddress);
            Log.e(TAG,uuid);
            Log.e(TAG,type);


            if(command.equals("GET")){
                GatewayModel gatewayModel = ((InteroperabilityService) service).getGatewayModel(gatewayIPAddress);
                HashMap<String, HashMap<String,DataInfo>> data = gatewayModel.getDataInfo();
                data.get(uuid).get(type);
//                if(data.get(uuid).get(type)==null){
//                    Log.e(TAG,"")
//                }
                data.get(uuid).get(type).getExpressions();
                data.get(uuid).get(type).getExpressions()[0].getValue();
                String SensordataValue = convertData(data.get(uuid).get(type));
                Log.e(TAG,"SensordataValue:"+SensordataValue);
//                String SensordataValue = (String)((InteroperabilityService)service).getGatewayModel(gatewayIPAddress).getDataInfo().get(uuid).get(type).getExpressions()[0].getValue();
                Intent it = new Intent();
                it.setAction(BPELreply);
                it.putExtra("command", "GET_RESPONSE");
                it.putExtra("gatewayIPAddress", gatewayIPAddress);
                it.putExtra("uuid", uuid);
                it.putExtra("type", type);
                it.putExtra("value", SensordataValue);
                ((InteroperabilityService)service).broadcastIntent(it);
            }else if (command.equals("SET")){
                Log.e(TAG,"in SET");
                ((InteroperabilityService.MyBinder) ((InteroperabilityService)service).getBinder()).startVideo(gatewayIPAddress,uuid);
            }




        }else{
            next.handle(intent);
        }

    }
}
