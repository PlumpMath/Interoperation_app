package org.ice4j.pseudotcp;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.SocketImplFactory;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

public class PseudoTcpSocketFactory
    extends SocketFactory
    implements SocketImplFactory
{
    /**
     * Default conversation ID
     */
    public static final long DEFAULT_CONVERSATION_ID=0;

    /**
     * Default timeout for connect operation
     */
    public static final int DEFAULT_CONNECT_TIMEOUT=5000;

    /**
     * Creates a socket and connects it to the specified 
     * port number at the specified address.
     */
    public Socket createSocket(String host, int port)
        throws IOException,
               UnknownHostException
    {
        Socket socket = createSocket();
        connectSocket(socket, new InetSocketAddress(host, port));
        return socket;
    }

    /**
     * Creates a socket and connect it to the specified remote address 
     * on the specified remote port.
     */
    public Socket createSocket(InetAddress host, int port) throws IOException
    {
        Socket socket = createSocket();
        connectSocket(socket, new InetSocketAddress(host, port));
        return socket;
    }    

    private void connectSocket(Socket socket, InetSocketAddress remoteSockAddr)
        throws IOException
    {
        socket.connect(remoteSockAddr, DEFAULT_CONNECT_TIMEOUT);
    }

    /**
     * Creates socket bound to local <tt>sockAddr</tt>
     * @param sockAddr
     * @return socket bound to local address
     * @throws java.io.IOException
     */
    public Socket createBoundSocket(InetSocketAddress sockAddr) 
        throws IOException
    {
        return new PseudoTcpSocket(
            new PseudoTcpSocketImpl(DEFAULT_CONVERSATION_ID,
                new DatagramSocket(sockAddr)));
    }

    /**
     *  Creates a socket and connects it to the specified remote host at the specified remote port.
     */
    public Socket createSocket(String host, 
                               int port, 
                               InetAddress localHost,
                               int localPort)
        throws IOException, 
               UnknownHostException
    {
        Socket socket = createBoundSocket(
                        new InetSocketAddress(localHost, localPort));
        connectSocket(socket, new InetSocketAddress(host, port));
        return socket;
    }

    /**
     * Creates a socket and connects it to the specified remote host on the specified remote port.
     */
    public Socket createSocket(InetAddress address, int port,
        InetAddress localAddress, int localPort) throws IOException
    {
        Socket socket = createBoundSocket(
            new InetSocketAddress(localAddress, localPort));
        connectSocket(socket, new InetSocketAddress(address, port));
        return socket;
    }

    /**
     * Creates a socket that will run on given <tt>datagramSocket</tt>
     * 
     * @param datagramSocket
     * @return new socket running on given <tt>datagramSocket</tt>
     * @throws java.net.SocketException
     */
    public PseudoTcpSocket createSocket(DatagramSocket datagramSocket)
        throws SocketException
    {        
        return new PseudoTcpSocket(
            new PseudoTcpSocketImpl(DEFAULT_CONVERSATION_ID, datagramSocket));
    }

    /**
     * Creates the PseudoTcp socket and binds it to any available port
     * on the local host machine.  The socket will be bound to the
     * {@link java.net.InetAddress#isAnyLocalAddress wildcard} address,
     * an IP address chosen by the kernel.
     */
    @Override
    public PseudoTcpSocket createSocket()
        throws SocketException
    {        
        return new PseudoTcpSocket(
            new PseudoTcpSocketImpl(DEFAULT_CONVERSATION_ID));
    }

    public SocketImpl createSocketImpl()
    {
        try
        {
            return new PseudoTcpSocketImpl(DEFAULT_CONVERSATION_ID);
        }
        catch (SocketException e)
        {
            throw new RuntimeException(e);
        }        
    }
}
