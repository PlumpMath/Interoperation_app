package ntu.selab.iot.interoperationapp.connection;

import android.util.Log;

import org.ice4j.ice.Agent;
import org.ice4j.ice.CandidatePair;
import org.ice4j.ice.IceProcessingState;

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
public class ICE_UDPConnector extends Connector implements PropertyChangeListener{
    private final String TAG = "ICE_UDPConnector";
    private Agent agent;
    private DatagramSocket datagramSocket=null;
    private InteroperabilityService interoperabilityService;
    private ConnectionInfo connectionInfo;
    private ServiceHandler serviceHandler;

    public ICE_UDPConnector( Agent agent, Reactor reactor, Handle handle, ServiceHandler serviceHandler, InteroperabilityService interoperabilityService){
        super(handle);
        this.agent = agent;
//        this.reactor = reactor;
        this.interoperabilityService = interoperabilityService;
//        this.connectionInfo = connectionInfo;
        this.serviceHandler = serviceHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object iceProcessingState = evt.getNewValue();
        if(iceProcessingState == IceProcessingState.TERMINATED){
            Log.d(TAG,"ice_UDP processing complete");
            CandidatePair selectedPair = agent.getStream("data").getComponents().get(0).getSelectedPair();
            Log.d(TAG,"ice_UDP_local:\t" + selectedPair.getLocalCandidate());
            Log.d(TAG,"ice_UDP_remote:\t" + selectedPair.getRemoteCandidate());
            datagramSocket = selectedPair.getDatagramSocket();



//            Log.d(TAG,"ICE_UDPConnector start setTimeout");
//            try {
////                datagramSocket.setSoTimeout(2000);
//                Log.d(TAG,"ICE_UDPConnector complete setTimeout "+datagramSocket.getSoTimeout());
//            } catch (SocketException e) {
//                Log.e(TAG,"ICE_UDPConnector setTimoutFuck");
//                e.printStackTrace();
//            }

            ((UDPSocketHandle)handle).setDatagramSocket(datagramSocket);
            activateSvcHandler(serviceHandler, handle);

        }
        else if(iceProcessingState == IceProcessingState.FAILED){
            Log.d(TAG,"ice processing end");

        }

    }

    @Override
    public boolean connect(InetAddress remoteAddress, int port, String mode) throws IOException {
        return false;
    }
}
