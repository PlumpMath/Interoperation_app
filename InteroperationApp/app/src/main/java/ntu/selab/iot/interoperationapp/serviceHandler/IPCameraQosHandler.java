package ntu.selab.iot.interoperationapp.serviceHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import ntu.selab.iot.interoperationapp.connection.UDPSocketChannelHandle;
import ntu.selab.iot.interoperationapp.protocol.rtp.RtcpPacketReceiver;
import ntu.selab.iot.interoperationapp.protocol.rtp.RtcpPacketTransmitter;
import ntu.selab.iot.interoperationapp.protocol.rtp.RtcpSession;
import ntu.selab.iot.interoperationapp.protocol.rtp.util.Packet;
import ntu.selab.iot.interoperationapp.reactor.Reactor;
import android.util.Log;

public class IPCameraQosHandler extends ServiceHandler{
	private final static String TAG = "IPCameraQosHandler";
	private DatagramChannel datagramChannel;
	public static int DEFAULT_DATAGRAM_SIZE = 4096 * 8;
	private byte[] buf = new byte[DEFAULT_DATAGRAM_SIZE];
	RtcpPacketReceiver rtcpReceiver;
	RtcpPacketTransmitter rtcpTransmitter;
	RtcpSession rtcpSession = null;
	private InetAddress  remoteAddress;
	private int remotePort;
	private int localPort;
	
	
	public IPCameraQosHandler(Reactor reactor,InetAddress address, int remotePort, RtcpSession rtcpSession){
		handlerName=TAG;
		remoteAddress=address;
		this.remotePort=remotePort;
		this.rtcpSession=rtcpSession;
		this.reactor=reactor;
	}
	
	@Override
	public void open() {
		UDPSocketChannelHandle socketHandle= (UDPSocketChannelHandle)this.handle;
		datagramChannel = socketHandle.getSocketChannel();
		try {
			rtcpReceiver = new RtcpPacketReceiver(rtcpSession,datagramChannel);
			rtcpTransmitter = new RtcpPacketTransmitter(remoteAddress,
	                remotePort,
	                rtcpSession,
	                datagramChannel);
            startTrasmite();
			
			reactor.registerHandler(this,  SelectionKey.OP_READ);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        Log.e(TAG,"QosLocalPort: "+ localPort);
        Log.e(TAG,"QosRemotePort: "+ remotePort);
        Log.d(TAG,"I-isOpened");
	}

	@Override
	public void handleEvent() throws IOException {
		Log.d(TAG,"is handling");

         read();
	}
	
	private  Packet read() throws IOException{
		Log.d(TAG,"I-read");
//        DatagramPacket dataPacket = new DatagramPacket(buf, DEFAULT_DATAGRAM_SIZE);
//        datagramChannel.socket().receive(dataPacket); 
		ByteBuffer buf = ByteBuffer.allocate(DEFAULT_DATAGRAM_SIZE);
		buf.clear();
		datagramChannel.receive(buf);
		int packetLength=buf.position();
		Log.d(TAG,"(read)packetLength:"+packetLength);
		buf.rewind();
        byte[] data = new byte[packetLength];
        buf.get(data);

        Packet packet = new Packet();
        packet.data = data;
        packet.length = data.length;
        packet.offset = 0;
        packet.receivedAt = System.currentTimeMillis();
        rtcpReceiver.handlePacket(packet);
        return packet;
	}

    public void startTrasmite(){
        rtcpTransmitter.start();
    }

    public void stopTrasmite(){
        rtcpTransmitter.stop();
    }

	public void setLocalPort(int port){
		localPort = port;
	}
	public int getLocalPort(){
		return localPort;
	}
	public int getRemotePort(){
		return remotePort;
	}


    public void close() throws IOException {
        datagramChannel.close();
    }
}
