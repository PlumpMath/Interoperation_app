package ntu.selab.iot.interoperationapp.serviceHandler.p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import ntu.selab.iot.interoperationapp.connection.UDPSocketChannelHandle;
import ntu.selab.iot.interoperationapp.connection.UDPSocketHandle;
import ntu.selab.iot.interoperationapp.depacketizer.Depacketizer;
import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.protocol.rtp.RtcpSession;
import ntu.selab.iot.interoperationapp.protocol.rtp.RtpPacket;
import ntu.selab.iot.interoperationapp.protocol.rtp.RtpPacketReceiver;
import ntu.selab.iot.interoperationapp.reactor.MediaReactor;
import ntu.selab.iot.interoperationapp.reactor.Reactor;
import ntu.selab.iot.interoperationapp.serviceHandler.ServiceHandler;
import ntu.selab.iot.interoperationapp.serviceHandler.media.VideoOutput;

import ntu.selab.iot.interoperationapp.utils.Tuple;

import android.util.Log;

public class IPCameraStreamingHandler extends ServiceHandler {
    private final static String TAG = "IPCameraStreamingHandler";
//    private DatagramChannel datagramChannel = null;
private DatagramSocket datagramSocket = null;
    private int localPort;
    private VideoOutput videoOutput;
    private int bufferSize = 64000;
    private RtpPacketReceiver rtpPacketReceiver;
    //	private Format inputFormat = null;
    private boolean stopDecode = true;
    private RtcpSession rtcpSession;
    private Depacketizer depacketizer;
    private GatewayModel gatewayModel;
    private String uuid;
    MediaReactor mediaReactor;
    // Test
    int i = 0;

    public IPCameraStreamingHandler(MediaReactor reactor, VideoOutput videoOutput,
                                    RtcpSession rtcpSession, GatewayModel gateway, String uuid) {
        handlerName = TAG;
//        this.reactor = reactor;
        mediaReactor = reactor;
        this.videoOutput = videoOutput;
        this.rtcpSession = rtcpSession;
        depacketizer = new Depacketizer();
        depacketizer.nonInterleavedModeBuild();
        gatewayModel = gateway;
        this.uuid= uuid;
        Log.d(TAG, "I-create");
    }

    @Override
    public void open() {
        Log.d(TAG,"IPCameraStreamingHandler is Open");
//        UDPSocketChannelHandle socketHandle = (UDPSocketChannelHandle) this.handle;
        UDPSocketHandle socketHandle = (UDPSocketHandle)this.handle;
        datagramSocket = socketHandle.getDatagramSocket();
//        Log.d(TAG,"IPCameraStreamingHandler:"+ datagramSocket.getClass());
//        try {
//            DatagramSocket testD = new DatagramSocket(2000);
//            testD.setSoTimeout(20);
//            Log.d(TAG,"IPCameraStreamingHandler set TEst timeOut");
//        } catch (SocketException e) {
//            e.printStackTrace();
//            Log.d(TAG,"IPCameraStreamingHandler set TEst timeOut failed");
//        }

//
//        Log.d(TAG,"IPCameraStreamingHandler will set timeout");
//        try {
//            datagramSocket.setSoTimeout(1);
//            Log.d(TAG,"IPCameraStreamingHandler will set timeout complete");
//        } catch (SocketException e) {
//            Log.e(TAG,"IPCameraStreamingHandler setTimoutFuck");
//            e.printStackTrace();
//        }
//        datagramChannel = socketHandle.getSocketChannel();
//        Log.d(TAG,"IPCameraStreamingHandler finish timeout");
        try {
            rtpPacketReceiver = new RtpPacketReceiver(rtcpSession);
            mediaReactor.registerHandler(this, SelectionKey.OP_READ);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // codecChain =
        // MediaRegistry.generateDecodingCodecChain(inputFormat.getCodec());
        // processor = new Processor(inputBuffer, new
        // MediaRendererStream(videoOutput),codecChain);

        gatewayModel.setLocalVideoConnectionState(uuid, 1);
        Log.d(TAG, " IPCameraStreamingHandler is Opened");
    }


    public void stopRead() {

        stopDecode = true;
        Log.e(TAG, "StopRead");
    }

    public void startRead() {

        stopDecode = false;
        Log.e(TAG, "StartRead");
    }

    @Override
    public void handleEvent() throws IOException {
        Log.e(TAG,"read");

        read();
    }

    void read() throws IOException {
        Log.d(TAG,"I-read");
/*      For DatagramChannel:
//        ByteBuffer buf = ByteBuffer.allocate(bufferSize);
//        buf.clear();
//        datagramChannel.receive(buf);
//        int packetLength = buf.position();
//        Log.d(TAG,"UDP-Length:"+packetLength);
//        buf.rewind();
//        byte[] data = new byte[packetLength];
//        buf.get(data);
        */
        byte[] buf = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(buf,buf.length);
        try {
            datagramSocket.receive(packet);
        }catch(SocketTimeoutException e){
            Log.d(TAG,"video is not sent.");
        }
        int length = packet.getLength();


        byte[] data  = java.util.Arrays.copyOf(packet.getData(),length);
        Log.d(TAG,"receive length:"+length);
        RtpPacket rtpPacket = rtpPacketReceiver.readRtpPacket(data);

        // Log.e(TAG, "f-" +": " + bytesToHex(data));
        // Log.d(TAG, "RTPpacket-length" + rtpPacket.length);
        // Log.d(TAG, "RTPpacket-payloadlength" + rtpPacket.payloadlength);
//		Log.d(TAG, "f-RTPpacket-timestamp" + i + ": "
//				+ (int) rtpPacket.timestamp);
//		Log.e(TAG, "f-RTPpacket-timestamp(byte): " + i + ": "
//				+ bytesToHex(new byte[] { data[4] }) + " "
//				+ bytesToHex(new byte[] { data[5] }) + " "
//				+ bytesToHex(new byte[] { data[6] }) + " "
//				+ bytesToHex(new byte[] { data[7] }));

//		if(i<100) {
//            Log.d(TAG, "f-RTPpacket-seqnum" + i + ": " + rtpPacket.seqnum);
//            Log.e(TAG, "f-RTPpacket-seqmum(byte): " + i + ": "
//                    + bytesToHex(new byte[]{data[2]}) + " "
//                    + bytesToHex(new byte[]{data[3]}));
//        }

        // Log.e(TAG, "f-f-" + ": " + bytesToHex(rtpPacket.data));
        // Log.d(TAG,"f-"+i);
        i++;

        depacketizer.depacketize(rtpPacket);
        if (depacketizer.bufferIsEnough()) {
            Tuple<Byte[], Long, Integer> nalu = depacketizer.getNalu();
            if (nalu != null && nalu._1() != null) {
                if (!stopDecode) {
                    videoOutput.setNalu(nalu);
                }
            }
            // Test
            // videoOutput.run();
            Log.d(TAG, "nalu-seqNum: " + nalu._3());
            Log.d(TAG, "nalu-timestamp: " + nalu._2());
        }

        Log.d(TAG, "depacketizer's buffer size:"
                + depacketizer.getBuffer().size());
    }


    public Depacketizer getDepacketizer() {
        return depacketizer;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int port) {
        localPort = port;
        ;
    }

    public VideoOutput getVideoOutput() {
        return videoOutput;
    }


    //For testing
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String bytesToHex(Byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public void close() throws IOException {
//        datagramChannel.close();
        datagramSocket.close();
    }

    public void flushDepacketizer() {
        depacketizer.flush();
    }

}
