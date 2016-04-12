package ntu.selab.iot.interoperationapp.serviceHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import android.util.Log;

import ntu.selab.iot.interoperationapp.connection.TCPSocketChannelHandle;
import ntu.selab.iot.interoperationapp.reactor.Reactor;

public class GatewayDiscoverySvcHandler extends ServiceHandler {
    private final static String TAG = "GatewayDiscoverySvcHandler";
    private SocketChannel socketChannel;
    private String sendMessage=null,readMessage=null;
    static final int READING=1, WRITING=2;
    int event;

    public GatewayDiscoverySvcHandler(Reactor reactor, int START_EVENT) {
        handlerName = TAG;
        event=START_EVENT;
        this.reactor = reactor;
    }

    @Override
    public void open() throws ClosedChannelException {
        TCPSocketChannelHandle socketHandle = (TCPSocketChannelHandle) this.handle;
        socketChannel = socketHandle.getSocketChannel();

        if(event==1){
            reactor.registerHandler(this, SelectionKey.OP_READ);
        }else if(event==2){
            reactor.registerHandler(this, SelectionKey.OP_WRITE);
        }else{
            Log.e(TAG,"event is wrong:"+event);
        }
        Log.d(TAG, "I-isOpened");
        active();
    }



    @Override
    public void handleEvent() throws IOException {

        if(event==READING){
            read();
        }else if(event==WRITING){
            write();
        }else{
            Log.e(TAG,"Event is wrong: "+event);
        }
    }

    public String getMessage(){
        return readMessage;
    }

    public void sendMessage(String message){
        sendMessage=message;
        Log.d(TAG,"(sendMessge)="+message);

    }

    private void read() throws IOException {
        int length = readFullPacket(4).getInt();
        Log.d(handlerName, "I-readPacket " + " [size=" + length + "]");
        ByteBuffer input = readFullPacket(length);
        String readMessage = new String(input.array()).trim();
        Log.d(handlerName, "I-readPacket " + readMessage + " [size=" + readMessage.length() + "]");
        sendMessage=null;
        event=WRITING;
        reactor.registerHandler(this,  SelectionKey.OP_WRITE);
        reactor.getSelector().wakeup();
        taskChain.handlePacketInfo(readMessage);

    }

    private void write() throws IOException {
        if(sendMessage!=null){
            readMessage=null;
            int length = sendMessage.length();
            Log.d(TAG,"I-(write)Length:"+length);

            ByteBuffer output = ByteBuffer.allocate(4+length);
            output.clear();
            output.putInt(length);
            output.put(sendMessage.getBytes());
            output.flip();
            socketChannel.write(output);
            Log.d(TAG, "I-(write) " + new String(output.array()).trim());
            event=READING;
            reactor.registerHandler(this,  SelectionKey.OP_READ);
//			Log.d(TAG,"I-(write)State"+event);
            reactor.getSelector().wakeup();
            sendMessage=null;
        }
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
