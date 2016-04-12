package ntu.selab.iot.interoperationapp.protocol.communication;

/**
 * Created by Uiling on 2015/9/8.
 */
public class CandidateInfo {
    //transport address
    private String ip;
    private int port;
    private String protocol;
    //Candidate type
    private String type;
    //others
    private String foundation;
    private long piority;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFoundation() {
        return foundation;
    }

    public void setFoundation(String foundation) {
        this.foundation = foundation;
    }

    public long getPiority() {
        return piority;
    }

    public void setPiority(long piority) {
        this.piority = piority;
    }
}
