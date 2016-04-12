package ntu.selab.iot.interoperationapp.connection;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by Uiling on 2015/9/14.
 */
public class UDPSocketHandle extends Handle {
    private DatagramSocket datagramSocket;

    @Override
    public boolean isConnected() throws SocketException {
        return false;
    }

    @Override
    public boolean connect(InetAddress ip, int port) throws IOException {
        return false;
    }

    @Override
    public boolean connect() throws IOException {
        return false;
    }

    @Override
    public void close() throws IOException {

    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public void setDatagramSocket(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }
}
