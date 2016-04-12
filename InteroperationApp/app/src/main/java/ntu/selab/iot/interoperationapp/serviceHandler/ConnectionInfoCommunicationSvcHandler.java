package ntu.selab.iot.interoperationapp.serviceHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import android.os.AsyncTask;
import android.util.Log;

import ntu.selab.iot.interoperationapp.connection.TCPSocketChannelHandle;
import ntu.selab.iot.interoperationapp.reactor.Reactor;

public class ConnectionInfoCommunicationSvcHandler extends ServiceHandler {
    private final static String TAG = "ConnectionInfoCommunicationSvcHandler";
    private SocketChannel socketChannel;


    public ConnectionInfoCommunicationSvcHandler(Reactor reactor) {
        handlerName = TAG;
        this.reactor = reactor;
    }

    @Override
    public void open() throws ClosedChannelException {
        TCPSocketChannelHandle socketHandle = (TCPSocketChannelHandle) this.handle;
        socketChannel = socketHandle.getSocketChannel();
        reactor.registerHandler(this, SelectionKey.OP_READ);
        active();
        Log.d(TAG, "I-isOpened");
    }



    @Override
    public void handleEvent() throws IOException {
        read();
    }

    private void read() throws IOException {
        int length = readFullPacket(4).getInt();
        Log.d(handlerName, "I-readPacket " + " [size=" + length + "]");
        ByteBuffer input = readFullPacket(length);
        String readMessage = new String(input.array()).trim();
        Log.d(handlerName, "I-readPacket " + readMessage + " [size=" + readMessage.length() + "]");
        taskChain.handlePacketInfo(readMessage);

    }


    public void write(String sendMessage) {
        final String message = sendMessage;
        if(message == null){
            Log.d(TAG, "MT-sendMessge is null");
            return;
        }

        if(socketChannel==null){//prevent the connection has not been established completely
            Log.d(TAG,"Connection has not been established");
            return;
        }
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                protected Void doInBackground(Void... params) {
                    int length = message.length();
                    Log.d("Send Message Length", "I-send-Length " + length);
                    ByteBuffer output = ByteBuffer.allocate(4 + length);
                    output.clear();
                    output.putInt(length);
                    output.put(message.getBytes());
                    output.flip();
                    try {
                        socketChannel.write(output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(handlerName, "I-send " + new String(output.array()));
                    return null;
                }
            };
            task.execute();

    }


    private ByteBuffer readFullPacket(int length) {
        int currentLength = 0;
        ByteBuffer buf = ByteBuffer.allocate(length);
        buf.clear();
        while (currentLength != length) {
            try {
                currentLength += socketChannel.read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        buf.rewind();
        return buf;
    }


    @Override
    public void close() throws IOException {
        socketChannel.close();
    }



}
