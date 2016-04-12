package ntu.selab.iot.interoperationapp.protocol.communication;

import java.util.ArrayList;

/**
 * Created by Uiling on 2015/9/8.
 */
public class ConnectionInfo {
    private String uuid;
    private String ufrag;
    private String password;
    private ArrayList<CandidateInfo> candidates;
    public ConnectionInfo(){
        candidates = new ArrayList<CandidateInfo>();
    }

    public String getUfrag() {
        return ufrag;
    }

    public void setUfrag(String ufrag) {
        this.ufrag = ufrag;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<CandidateInfo> getCandidates() {
        return candidates;
    }

    public void setCandidates(ArrayList<CandidateInfo> candidates) {
        this.candidates = candidates;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
