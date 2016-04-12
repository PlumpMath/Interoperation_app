package ntu.selab.iot.interoperationapp.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;

import android.util.Log;


public class UDPSocketChannelHandle extends Handle {
    private final static String TAG = "UDPSocketHandle";
    InetSocketAddress destinationAddress;
    DatagramChannel channel;
    int bindPort;


    public UDPSocketChannelHandle(String mode) throws IOException {
        this.mode = mode;
        channel = DatagramChannel.open();
        if (mode.equals("ASYC")) {
            channel.configureBlocking(false);
        } else if (mode.equals("SYC")) {
            channel.configureBlocking(true);
        } else {
            Log.d(TAG, "mode is fail");
        }
    }

    public UDPSocketChannelHandle() throws IOException {
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
    }

    public UDPSocketChannelHandle(InetAddress remoteIP, int remotePort, int listenedPort, String mode) throws IOException {
        this(mode);
//		Log.d(TAG,"I-create");
        bind(listenedPort);
        if (remotePort > 0 && remoteIP != null) {
            ip = remoteIP;
            this.remotePort = remotePort;
            destinationAddress = new InetSocketAddress(ip, remotePort);
        } else {
            destinationAddress = null;
        }
        Log.d(TAG, "I-create");
    }

    public void bind(int bindPort) throws SocketException {
        Log.d(TAG, "bind");
        channel.socket().bind(new InetSocketAddress(bindPort));
    }

    public void setBindPort(int bindPort) {
        this.bindPort = bindPort;
    }

    public DatagramChannel getSocketChannel() {
        return channel;
    }


    public boolean connect() throws IOException {
        if (destinationAddress != null) {
            channel.connect(destinationAddress);
        }
        return isConnected();
    }

    public boolean connect(InetAddress ip, int remotePort) throws IOException {
        if (remotePort > 0 && ip != null) {
            channel.connect(new InetSocketAddress(ip, remotePort));
        }
        return isConnected();
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

    @Override
    public boolean isConnected() throws SocketException {
        return true;
    }
}
