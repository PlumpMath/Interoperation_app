package ntu.selab.iot.interoperationapp.serviceHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;

import ntu.selab.iot.interoperationapp.connection.PseudoTCPSocketHandle;
import ntu.selab.iot.interoperationapp.model.GatewayModel;
import ntu.selab.iot.interoperationapp.model.ScenarioModel;
import ntu.selab.iot.interoperationapp.protocol.rtp.RtcpSession;
import ntu.selab.iot.interoperationapp.reactor.Reactor;
import ntu.selab.iot.interoperationapp.serviceHandler.p2p.IPCameraStreamingHandler;

import android.util.Log;

import org.ice4j.pseudotcp.PseudoTcpSocket;

public class IPCameraControlHandler extends ServiceHandler {
	private final static String TAG = "IPCameraControlHandler";
//	private SocketChannel socketChannel;
    private PseudoTcpSocket pseudoSocket;
	private ByteBuffer output;
	private String sendMessage=null,readMessage=null;
	static final int READING=1, WRITING=2;
	int event=0;
	private State state=null;
	private ScenarioModel model;
	private RtcpSession rtcpSession;
	private String cameraName;
	private String uuid;
	private int remotePort;
	private MediaInfo mediaInfo;
	private boolean start=false;
	private int localPort;
	
	public IPCameraControlHandler(Reactor reactor,ScenarioModel model, String camera, String uuid){
		handlerName=TAG;
		this.reactor=reactor; 
		this.model = model;
		cameraName=camera;
		this.uuid=uuid;
		mediaInfo=new MediaInfo();
	}
	
	
	@Override
	public void open() throws ClosedChannelException {



		rtcpSession = new RtcpSession(false, 16000);
        /*For socketChannel
		TCPSocketHandle socketHandle= (TCPSocketHandle)this.handle;
		socketChannel = socketHandle.getSocketChannel();
        */
        PseudoTCPSocketHandle socketHandle = (PseudoTCPSocketHandle) this.handle;
        pseudoSocket = socketHandle.getPseudoTcpSocket();
		reactor.registerHandler(this,  SelectionKey.OP_WRITE);
		event=WRITING;
		state=new DescribeState(this);
		Log.d(TAG,"I-(open)");

        try {
            write();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

	@Override
	public void handleEvent() throws IOException {
		
		if(event==READING){
			read();
		}else if(event==WRITING){
			write();
		}
	}

	protected void read() throws IOException{
        int length = readFullPacket(4).length;
        Log.d(handlerName, "I-readPacket " + " [size=" + length + "]");
        byte[] input = readFullPacket(length);
        readMessage = new String(input);
        Log.d(handlerName, "I-readPacket " + readMessage + " [size=" + readMessage.length() + "]");
        sendMessage=null;
        event=WRITING;
        reactor.registerHandler(this,  SelectionKey.OP_WRITE);
        Log.d(TAG,"I-(read)"+readMessage);
        Log.d(TAG,"I-(read)state");
        if(state!=null){
            state.read();
        }
/*  for socketChannel
		input = ByteBuffer.allocate(4);
		input.clear();
		socketChannel.read(input);
		input.rewind();
		int length = input.getInt();
		input = ByteBuffer.allocate(length);
		input.clear();
		socketChannel.read(input);
		input.rewind();
		readMessage = new String(input.array());
		sendMessage=null;
		event=WRITING;
		reactor.registerHandler(this,  SelectionKey.OP_WRITE);
		reactor.getSelector().wakeup();
		Log.d(TAG,"I-(read)"+readMessage);
		Log.d(TAG,"I-(read)state");
		if(state!=null){
			state.read();
		}
*/
	}

    public void write() throws IOException{
		
		if(state!=null){
			state.write(cameraName, uuid);
		}
		if(sendMessage!=null){
			readMessage=null;
			int length = sendMessage.length();
			Log.d(TAG,"I-(write)Length:"+length);

            ByteBuffer output = ByteBuffer.allocate(4 + length);
            output.clear();
            output.putInt(length);
            output.put(sendMessage.getBytes());
            output.flip();
            pseudoSocket.getOutputStream().write(output.array());
            pseudoSocket.getOutputStream().flush();
			Log.d(TAG, "I-(write) " + sendMessage);
			event=READING;
			reactor.registerHandler(this,  SelectionKey.OP_READ);
			sendMessage=null;
		}
	}
	

//	public int getEvent(){
//		return event;
//	}
	
	
	public String getMessage(){
		return readMessage;
	}
	
	public void sendMessage(String message){
		sendMessage=message;
		Log.d(TAG,"(sendMessge)="+message);

	}
	
	public void setCameraName(String name){
		cameraName=name;
	}
	
	public String getCameraName(){
		return cameraName;
	}
	
	public void setCameraUuid(String uuid){
		this.uuid=uuid;
	}
	
	public String getCameraUuid(){
		return this.uuid;
	}
	
	public void setNextState(State state){
		this.state=state;
	}
	
	
	public void setRemotePort(int port){
		remotePort=port;
	}
	
	public ScenarioModel getScenarioModel(){
		return model;
	}
	
//	public RtcpSession getRtcpSession(){
//		return rtcpSession;
//	}
	
	public void setMediaInfo(MediaInfo info){
		this.mediaInfo=info;
	}
	
	public MediaInfo getMediaInfo(){
		return mediaInfo;
	}

	public IPCameraStreamingHandler createIPCameraStreamingHandler() throws IOException{
		GatewayModel gatewayModel = (GatewayModel)model;
		return gatewayModel.createIPCameraStreamingHandler(uuid, rtcpSession, mediaInfo);
	}
	
	public IPCameraQosHandler createIPCameraQosHandler() throws IOException{
		GatewayModel gatewayModel = (GatewayModel)model;
		return gatewayModel.createIPCameraQosHandler(uuid,remotePort+1, rtcpSession);
	}
	public ScenarioModel getModel(){
		return model;
	}

	public void startSend(){
		this.start=true;
	}

    public void stopSend(){
        this.start=false;
    }
	public boolean isStartSend(){
		return start;
	}
	
	public void setLocalPort(int port){
		this.localPort=port;
	}
	
	public int getLocalPort(){
		return localPort;
	}
	
	public State getState(){
		return state;
	}

    public void close() throws IOException {
        pseudoSocket.close();
    }

    private byte[] readFullPacket(int length) {
        int currentLength = 0;
//        ByteBuffer buf = ByteBuffer.allocate(length);
//        buf.clear();
        byte[] buf = new byte[4];
        while (currentLength != length) {
            try {
                currentLength += pseudoSocket.getInputStream().read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        buf.rewind();
        return buf;
    }
}
