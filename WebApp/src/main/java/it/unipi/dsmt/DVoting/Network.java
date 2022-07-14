package it.unipi.dsmt.DVoting;

import com.ericsson.otp.erlang.*;

import java.io.IOException;

public class Network {
     String nodeId = "senderNode@localhost";
     String cookie = "abcde";
     String mBox = "javaMboxS";
     static OtpNode otpNode;
     static OtpMbox otpMbox ;
     String pollingStationMbox="pollingStation";
     String pollingStationNode="server@localhost";
     public  Network(String name){
        try {
            if(otpNode==null) {
                otpNode = new OtpNode(nodeId, cookie);
                otpMbox = otpNode.createMbox(name);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean test(){
        if (otpNode.ping(pollingStationNode, 2000)) {
            return true;
        }
        return false;
    }
     public void send(String message){
        OtpErlangString msg = new OtpErlangString(message);
        OtpErlangTuple msgTuple = new OtpErlangTuple(
                new OtpErlangObject[]{otpMbox.self(), msg});
        otpMbox.send(pollingStationMbox, pollingStationNode,msgTuple);
    }
}

