package ntu.selab.iot.interoperationapp.serviceHandler;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import ntu.selab.iot.interoperationapp.connection.UDPSocketChannelHandle;
import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.reactor.Reactor;
import ntu.selab.iot.interoperationapp.service.InteroperabilityService;

/**
 * Created by Uiling on 2/2/15.
 */
public class GatewayDiscoveryBroadcasting_Handler extends ServiceHandler {
    private final static String TAG = "GatewayDiscoveryHandler";
    private DatagramChannel datagramChannel = null;
    private int bufferSize = 100;
    private InteroperabilityService controller;
    private int broadCastPort = 17000;
    public GatewayDiscoveryBroadcasting_Handler(InteroperabilityService controller, Reactor reactor) {
        handlerName = TAG;
        this.reactor = reactor;
        this.controller = controller;

        Log.d(TAG, "b-Create");
    }
    @Override
    public void open() {
        UDPSocketChannelHandle socketHandle = (UDPSocketChannelHandle) this.handle;
        datagramChannel = socketHandle.getSocketChannel();
        try {
            reactor.registerHandler(this, SelectionKey.OP_READ);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "b-isOpened");
    }

    @Override
    public void handleEvent() throws IOException {
        read();
    }

    private void read() throws IOException {
        Log.d(TAG,"isRead");
        ByteBuffer buf = ByteBuffer.allocate(bufferSize);
        buf.clear();
        //Get discovered gateway port and IP.
        SocketAddress sending = datagramChannel.receive(buf);
        InetAddress address = ((InetSocketAddress) sending).getAddress();
        String gatewayIP = address.getHostAddress();
        Log.d(TAG,"b-Discovered GatewayIP : "+gatewayIP);


        int packetLength = buf.position();
        // Log.d(TAG,"UDP-Length:"+packetLength);
        buf.rewind();
        byte[] data = new byte[packetLength];
        buf.get(data);
        String receive = new String(data, 0, data.length);
        Log.d(TAG, "b-Broadcast_Received response " + receive);

        String[] s=receive.split(" ");
        String validated = s[0];
        String gatewayName = s[1];

        if(validated.equals("World")){

            Log.d(TAG, "b-broadcast_Received response validated " + validated);

            Log.d(TAG, "b-discovered gateway name." + gatewayName);

            //For sure is not exited.
            if(!controller.gatewayIsExist(gatewayIP)){

                GatewayModel model = new GatewayModel(controller.getReactor(),controller) ;
                model.setHostAddress(InetAddress.getByName(gatewayIP));
//				model.setHostLocation(location);
                model.setHostName(gatewayName);
                Log.d(TAG, "b-Prepare to connect");

//                model.connect();// Should use a builder to create a sensorOperationHandler with TCPConnectionSvcHandler.
                controller.addＤiscoveredGateway(model);
                Log.d(TAG,"b-Add new gateway");
            }
        }

    }

    public void write(InetAddress broadcastAddress) throws IOException{
        Log.d(TAG,"b-write");
        byte[] sendMessage = "Hello".getBytes();
        Log.d(TAG, "b-broadcasting data : " + new String(sendMessage));

        ByteBuffer output = ByteBuffer.allocate(sendMessage.length);
        output.clear();
        output.put(sendMessage);
        output.flip();

        InetSocketAddress inetSocketAddress = new InetSocketAddress(broadcastAddress,broadCastPort);
        datagramChannel.send(output,inetSocketAddress);


        Log.d(TAG, "b-write data:  " + new String(output.array()).trim());
    }


    public void close() throws IOException {
        datagramChannel.close();
    }

}
