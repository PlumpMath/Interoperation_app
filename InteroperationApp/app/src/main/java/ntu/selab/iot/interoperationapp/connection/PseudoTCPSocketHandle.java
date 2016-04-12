package ntu.selab.iot.interoperationapp.connection;

import org.ice4j.pseudotcp.PseudoTcpSocket;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by Uiling on 2015/9/1.
 */
public class PseudoTCPSocketHandle extends Handle{
    private PseudoTcpSocket pseudoTcpSocket;


    public void setPseudoTcpSocket(PseudoTcpSocket pseudoTcpSocket){
        this.pseudoTcpSocket = pseudoTcpSocket;
    }

    @Override
    public boolean isConnected() throws SocketException {
        return pseudoTcpSocket.isConnected();
    }

    @Override
    public boolean connect(InetAddress ip, int port) throws IOException {
        return true;

    }

    @Override
    public boolean connect() throws IOException {
        return connect(ip, remotePort);
    }

    @Override
    public void close() throws IOException {
        pseudoTcpSocket.close();
    }

    public PseudoTcpSocket getPseudoTcpSocket(){
        return pseudoTcpSocket;
    }
}
