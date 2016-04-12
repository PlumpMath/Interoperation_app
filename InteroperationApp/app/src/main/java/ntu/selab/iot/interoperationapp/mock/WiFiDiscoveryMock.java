package ntu.selab.iot.interoperationapp.mock;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;

import com.google.common.net.InetAddresses;

import ntu.selab.android.util.NetUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class WiFiDiscoveryMock {
	private static Context context;
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static void mock(Context context) {
		WiFiDiscoveryMock.context = context;
		
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifi = wifiManager.getConnectionInfo();
		DhcpInfo dhcp = wifiManager.getDhcpInfo();
		
		
		toast("Wifi IP Address: "        + NetUtils.IPv4.intToString(wifi.getIpAddress()));
		toast("Wifi IP Address (DHCP): " + NetUtils.IPv4.intToString(dhcp.ipAddress));
		toast("Wifi Gateway Address: "   + NetUtils.IPv4.intToString(dhcp.gateway));
		toast("Wifi Server Address: "    + NetUtils.IPv4.intToString(dhcp.serverAddress));
		toast("Wifi Subnet Mask: "       + NetUtils.IPv4.intToString(dhcp.netmask));
		
		toast("Wifi Subnet Begins from " + NetUtils.IPv4.intToString(dhcp.ipAddress & dhcp.netmask));
		toast("Wifi Subnet Ends at "     + NetUtils.IPv4.intToString(dhcp.ipAddress | ~dhcp.netmask));
		
		
		iterateIpInRange();
		
		
		// Get all local IP addresses from each network interface.
		
		try {
			
			for (NetworkInterface network : Collections.list(NetworkInterface.getNetworkInterfaces())) {
				for (InterfaceAddress interfaceAddr : network.getInterfaceAddresses()) {
					InetAddress ip = interfaceAddr.getAddress();
					
					if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()) {
						
						toast(network.getDisplayName() + ": " + ip.getHostAddress() 
								+ "/" + interfaceAddr.getNetworkPrefixLength());
					}
				}
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void iterateIpInRange() {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifiManager.getDhcpInfo();
		
		
		InetAddress ip = null;
		
		try {
			ip = InetAddress.getByName(NetUtils.IPv4.intToString(dhcp.ipAddress & dhcp.netmask));
		
			int intIP = NetUtils.IPv4.stringToInt(ip.getHostAddress());
			
			while (NetUtils.IPv4.isInSubnet4Int(intIP, dhcp.ipAddress, dhcp.netmask)) {
				
				Log.d("ip", ip.getHostAddress());
				
				ip = InetAddresses.increment(ip);
				intIP = NetUtils.IPv4.stringToInt(ip.getHostAddress());
			}
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
	}
	
	private static void toast(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
