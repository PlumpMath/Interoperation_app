package ntu.selab.iot.interoperationapp.serviceHandler.p2p;

import android.os.AsyncTask;
import android.util.Log;

import org.ice4j.pseudotcp.PseudoTcpSocket;
import org.ice4j.pseudotcp.util.ByteFifoBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;

import ntu.selab.iot.interoperationapp.connection.PseudoTCPSocketHandle;
import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.reactor.Reactor;
import ntu.selab.iot.interoperationapp.service.InteroperabilityService;
import ntu.selab.iot.interoperationapp.serviceHandler.ServiceHandler;

/**
 * Created by Uiling on 2015/9/9.
 */
public class SensorOperationHandler extends ServiceHandler{
    private final static String TAG = "SensorOperationHandler";
    private PseudoTcpSocket pseudoSocket;
    private GatewayModel belongedGatewayModel;
    private InteroperabilityService interoperabilityService;

    public SensorOperationHandler(InteroperabilityService interoperabilityService, Reactor reactor, GatewayModel gatewayModel) {
        handlerName = TAG;
        this.reactor = reactor;
        belongedGatewayModel = gatewayModel;
        this.interoperabilityService = interoperabilityService;
    }

    @Override
    public void open() throws ClosedChannelException {
        PseudoTCPSocketHandle socketHandle = (PseudoTCPSocketHandle) this.handle;
        pseudoSocket = socketHandle.getPseudoTcpSocket();
        reactor.registerHandler(this, SelectionKey.OP_READ);
        interoperabilityService.tagToConnected(belongedGatewayModel.getUuid());
        interoperabilityService.startUpdateSensorData(belongedGatewayModel.getUuid());
        active();
        Log.d(TAG, "I-isOpened");
    }



    @Override
    public void handleEvent() throws IOException {
        read();
    }

    private void read() throws IOException {

        ByteBuffer byteLength =  ByteBuffer.wrap(readFullPacket(4));
        byteLength.rewind();
        int length = byteLength.getInt();
        Log.d(handlerName, "I-readPacket " + " [size=" + length + "]");
        byte[] input = readFullPacket(length);
        String readMessage = new String(input);
        Log.d(handlerName, "I-readPacket " + readMessage + " [size=" + readMessage.length() + "]");
        taskChain.handlePacketInfo(readMessage);

    }


    public void write(String sendMessage) {
        final String message = sendMessage;
        if(message == null){
            Log.d(TAG, "MT-sendMessge is null");
            return;
        }

        if(pseudoSocket==null){//prevent the connection has not been established completely
            Log.d(TAG,"Connection has not been established");
            return;
        }
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {

                Log.d(TAG, "I-send-Length " + message.length());
                Log.d(TAG, "I-send-Message " + message);
                ByteBuffer output = ByteBuffer.allocate(4 + message.length());
                output.clear();
                output.putInt(message.length());
                output.put(message.getBytes());
                output.flip();

                try {
                    pseudoSocket.getOutputStream().write(output.array());
                    pseudoSocket.getOutputStream().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();

    }

    private byte[] readFullPacket(int length) {
        int currentLength = 0;
//        ByteBuffer buf = ByteBuffer.allocate(length);
//        buf.clear();
        byte[] buf = new byte[length];
        while (currentLength != length) {
            try {
                currentLength += pseudoSocket.getInputStream().read(buf,currentLength,length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        buf.rewind();
        return buf;
    }

    @Override
    public void close() throws IOException {
        interoperabilityService.disconnectGateway(belongedGatewayModel.getUuid());
        if(pseudoSocket!=null) {
            pseudoSocket.close();
        }
    }

}
