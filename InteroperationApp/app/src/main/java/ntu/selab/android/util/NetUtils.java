package ntu.selab.android.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

import android.util.Log;

/**
 * Author: Keith Hung (kamael@selab.csie.ncu.edu.tw) 
 * Date: 2014.07.17
 * Last Update: 2014.07.29
 * */

public class NetUtils {
	
	public static class IPv4 {
		
		public static String intToString(int ip) {
			return String.format("%d.%d.%d.%d", (ip >> 0 & 0xff), (ip >> 8 & 0xff), 
												(ip >> 16 & 0xff), (ip >> 24 & 0xff));
		}
		
		public static int stringToInt(String ip) {
			String parts[] = ip.split("\\.");
			
			int intIP = 0;
			
			for (int i = 0; i < 4; i++) {
				intIP += Integer.parseInt(parts[i]) << (i * 8);
			}
			
			return intIP;
		}
		
		public static int bytesToInt(byte ip[]) {
			// TODO
			return -1;
		}
		
		public static boolean isInSubnet4Int(int ip, int subnet, int mask) {
			return ((subnet & mask) == (ip & mask));
		}
		
		public static boolean ping(String hostAddress, int timeout) {
			try {
				Process proc = Runtime.getRuntime().exec("ping -c 1 -i .3 -W " + timeout + " " + hostAddress);
				int status = proc.waitFor();
				Log.d("scan", "Ping status: " + status);
				
				if (status == 0) {
					return true;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				return false;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
			
			return false;
		}
		
		public static boolean probe(String hostAddress, int port, int timeout) {
			Socket socket = new Socket();
			
			try {
				socket.setReuseAddress(true);

				SocketAddress address = new InetSocketAddress(hostAddress, port);
				socket.connect(address, timeout);
				return true;
				
			} catch (SocketException e) {
				e.printStackTrace();
				return false;
				
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
				}
			}
		}
	}
}
