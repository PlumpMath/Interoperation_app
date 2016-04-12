package ntu.selab.iot.interoperationapp.discovery;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import ntu.selab.iot.interoperationapp.activity.MainActivity;
import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.model.ScenarioModel;
import ntu.selab.iot.interoperationapp.serviceHandler.GatewayDiscoveryBroadcasting_Handler;


@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class GatewayDiscoveryByBroadcasting extends ScenarioDiscovery  {
    private final static String TAG = "GatewayDiscoveryByBroadcasting";
    private GatewayDiscoveryBroadcasting_Handler gatewayDiscoveryBroadcastHandler;
	
	public GatewayDiscoveryByBroadcasting(Service activity, GatewayDiscoveryBroadcasting_Handler gatewayDiscoveryBroadcastHandler, ScenarioDiscovery successor) throws IOException {
        this(activity, successor);
        this.gatewayDiscoveryBroadcastHandler = gatewayDiscoveryBroadcastHandler;
        Log.d(TAG, "b-isStart");
	}
	
	public GatewayDiscoveryByBroadcasting(Service activity, ScenarioDiscovery successor) throws IOException {
		super(activity, successor);
	}
	
	
	@Override
	public void discover() throws IOException {
		Log.d(TAG,"b-discover");

        broadcastGateway();

        if (next != null) {
            next.discover();
        }
	}
	
	


    private void broadcastGateway() throws IOException {
        gatewayDiscoveryBroadcastHandler.write(getBroadcastAddress());
    }
	
	private InetAddress getBroadcastAddress() throws IOException {
		WifiManager wifiManager = (WifiManager) this.mainService.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifiManager.getDhcpInfo();
	    if (dhcp == null) {
	      Log.d("GetBroadcastAddress", "Could not get dhcp info");
	      return null;
	    }

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    Log.d("GetBroadcastAddress", "I-BroadCast_Address"+InetAddress.getByAddress(quads));
	    return InetAddress.getByAddress(quads);
	 }

}
