package ntu.selab.iot.interoperationapp.protocol.communication;

/**
 * Created by Uiling on 2015/9/9.
 */
public class GatewayConnectionInfo {
    private String uuid;
    private ConnectionInfo connectionInfo;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public void setConnectionInfo(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }
}
