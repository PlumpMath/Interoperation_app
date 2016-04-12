package ntu.selab.iot.interoperationapp.connection;

import android.util.Log;

import org.ice4j.ice.Agent;
import org.ice4j.ice.CandidatePair;
import org.ice4j.ice.IceProcessingState;
import org.ice4j.pseudotcp.PseudoTcpSocket;
import org.ice4j.pseudotcp.PseudoTcpSocketFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import ntu.selab.iot.interoperationapp.reactor.Reactor;
import ntu.selab.iot.interoperationapp.service.InteroperabilityService;
import ntu.selab.iot.interoperationapp.serviceHandler.ServiceHandler;
import ntu.selab.iot.interoperationapp.protocol.communication.ConnectionInfo;

/**
 * Created by Uiling on 2015/9/7.
 */
public class ICE_TCPConnector extends Connector implements PropertyChangeListener{
    private final String TAG = "ICE_TCPConnector";
    private Agent agent;
    private PseudoTcpSocket pseudoTcpsocket=null;
//    private InteroperabilityService interoperabilityService;
//    private ConnectionInfo connectionInfo;
    private ServiceHandler serviceHandler;
    private boolean isconnected = false;
    private boolean isActiving = false;

    public ICE_TCPConnector( Agent agent, Reactor reactor, Handle handle, ServiceHandler sensorOperationHandler){
        super(handle);
        this.agent = agent;
        this.reactor = reactor;
//        this.interoperabilityService = interoperabilityService;
//        this.connectionInfo = connectionInfo;
        this.serviceHandler = sensorOperationHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object iceProcessingState = evt.getNewValue();
        if(iceProcessingState == IceProcessingState.TERMINATED){
            Log.d(TAG,"ice processing complete");
            CandidatePair selectedPair = agent.getStream("data").getComponents().get(0).getSelectedPair();
            Log.d(TAG,"local:\t" + selectedPair.getLocalCandidate());
            Log.d(TAG,"remote:\t" + selectedPair.getRemoteCandidate());
            DatagramSocket datagramSocket = selectedPair.getDatagramSocket();
                try {
                    pseudoTcpsocket = buildSocket(datagramSocket);
                    Log.d(TAG, "prepare_socket.connect(selectedPair.getRemoteCandidate().getTransportAddress(), 10000)");
//                    Log.e(TAG,"before: "+socket.getState());
                    pseudoTcpsocket.connect(selectedPair.getRemoteCandidate().getTransportAddress(), 10000);
                    ((PseudoTCPSocketHandle)handle).setPseudoTcpSocket(pseudoTcpsocket);
                    activateSvcHandler(serviceHandler, handle);//Tag to connected gateway
                    isconnected = true;
                } catch (IOException e) {
                    Log.e(TAG,"exception: "+pseudoTcpsocket.getState());
                    e.printStackTrace();
                    isconnected = false;
                }
                setActiving(false);
        } else if(iceProcessingState == IceProcessingState.FAILED){
            setActiving(false);
            unactiveSvcHandler();

            Log.d(TAG,"ice processing failed");
        }
    }

    private PseudoTcpSocket buildSocket(DatagramSocket datagramSocket) throws SocketException {
        PseudoTcpSocket socket= new PseudoTcpSocketFactory().createSocket(datagramSocket);
        socket.setConversationID(1905120102);
        socket.setMTU(1500);
        socket.setDebugName("L");
        return socket;
    }

    private void unactiveSvcHandler(){
        try {
            serviceHandler.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean connect(InetAddress remoteAddress, int port, String mode) throws IOException {
        return false;
    }

    public boolean isActiving() {
        return isActiving;
    }

    public void setActiving(boolean isActiving) {
        this.isActiving = isActiving;
    }
}
