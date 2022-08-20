package it.unipi.dsmt.DVoting.network;

import com.ericsson.otp.erlang.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Network {
    String nodeId = "senderNode@localhost";
    String cookie = "abcde";
    static OtpNode otpNode;
    static OtpMbox otpMbox;
    String pollingStationMbox = "polling_station_endpoint";
    String pollingStationNode = "server@localhost";

    public Network(String name) {
        try {
            if(otpNode==null) {
                otpNode = new OtpNode(nodeId, cookie);
                otpMbox = otpNode.createMbox(name);
                System.out.println(otpMbox.getName());
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Network(String name, String nodeId, String cookie) {
        this.nodeId = nodeId;
        this.cookie = cookie;
        try {

            if(otpNode==null) {
                otpNode = new OtpNode(nodeId, cookie);
                otpMbox = otpNode.createMbox(name);
            }




        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean test() {
        //System.out.println(otpMbox.getName() +".: "+otpNode.whereis(otpMbox.getName()));
        //otpMbox.whereis(pollingStationNode);
        return otpNode.ping(pollingStationNode, 2000);
    }

    public void send(String message) {
        OtpErlangString msg = new OtpErlangString(message);
        OtpErlangTuple msgTuple = new OtpErlangTuple(
                new OtpErlangObject[]{otpMbox.self(), msg});
        otpMbox.send(pollingStationMbox, pollingStationNode, msgTuple);
    }

    public String receiveString() {
        String res = null;
        while (true) {
            try {
                OtpErlangObject message = otpMbox.receive();
                System.out.println("received something");
                if (!(message instanceof OtpErlangTuple erlangTuple))
                    continue;
                OtpErlangPid senderPID = (OtpErlangPid) erlangTuple.elementAt(0);
                //OtpErlangBinary payload = (OtpErlangBinary) erlangTuple.elementAt(1);
                OtpErlangString payload = (OtpErlangString) erlangTuple.elementAt(1);
                res = payload.stringValue();
                break;

            } catch (OtpErlangDecodeException | OtpErlangExit ignored) {
            }
        }
        return res;
    }

    public byte[] receiveBytes() {
        while (true) {
            try {
                OtpErlangObject message = otpMbox.receive();
                System.out.println("received something");
                if (!(message instanceof OtpErlangTuple erlangTuple))
                    continue;
                OtpErlangBinary payload = (OtpErlangBinary) erlangTuple.elementAt(1);
               return payload.binaryValue();

            } catch (OtpErlangDecodeException | OtpErlangExit | ClassCastException ignored) {
            }
        }

    }

    public List<byte[]> receiveSigned() {
        List<byte[]> res=new ArrayList<>(2);
        while (true) {
            try {
                OtpErlangObject message = otpMbox.receive();
                System.out.println("received something");
                if (!(message instanceof OtpErlangTuple erlangTuple))
                    continue;
                OtpErlangBinary msg = (OtpErlangBinary) erlangTuple.elementAt(1);
                OtpErlangBinary s = (OtpErlangBinary) erlangTuple.elementAt(2);
                res.set(1,msg.binaryValue());
                res.set(2,s.binaryValue());
                return res;

            } catch (OtpErlangDecodeException | OtpErlangExit | ClassCastException ignored) {
            }
        }

    }

    public void sendBytes(byte[] message) {
        OtpErlangBinary msg = new OtpErlangBinary(message);
        OtpErlangTuple msgTuple = new OtpErlangTuple(
                new OtpErlangObject[]{otpMbox.self(), msg});
        otpMbox.send("polling_station_endpoint", pollingStationNode,  msgTuple);
    }

    public void sendSigned(byte[] message, byte[] sign) {
        OtpErlangBinary msg = new OtpErlangBinary(message);
        OtpErlangBinary s = new OtpErlangBinary(sign);
        OtpErlangTuple msgTuple = new OtpErlangTuple(
                new OtpErlangObject[]{otpMbox.self(), msg, s});
        otpMbox.send("polling_station_endpoint", pollingStationNode,  msgTuple);
    }
}

