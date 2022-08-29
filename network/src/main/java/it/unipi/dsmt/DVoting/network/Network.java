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
    String centralStationMbox = "central_station_endpoint";
    String pollingStationNode = "server@localhost";
    String centralStationNode = "server@localhost";

    /**
     * Network class constructor
     *
     * @param name the Mbox name (must be unique for each Mbox)
     * @throws IOException if network can't be created
     */
    public Network(String name) throws IOException {

        if (otpNode == null) {
            otpNode = new OtpNode(nodeId, cookie);
            otpMbox = otpNode.createMbox(name);
            System.out.println(otpMbox.getName());
        }

    }

    /**
     * Network class constructor
     *
     * @param name   the Mbox name (must be unique for each Mbox)
     * @param nodeId id od the current node
     * @param cookie the cookie for the Mbox
     * @throws IOException if network can't be created
     */
    public Network(String name, String nodeId, String cookie) throws IOException {
        this.nodeId = nodeId;
        this.cookie = cookie;
        if (otpNode == null) {
            otpNode = new OtpNode(nodeId, cookie);
            otpMbox = otpNode.createMbox(name);
        }
    }

    public boolean test() {
        //System.out.println(otpMbox.getName() +".: "+otpNode.whereis(otpMbox.getName()));
        //otpMbox.whereis(pollingStationNode);
        return otpNode.ping(pollingStationNode, 2000);
    }

    public void sendString(String message) {
        OtpErlangString msg = new OtpErlangString(message);
        OtpErlangTuple msgTuple = new OtpErlangTuple(
                new OtpErlangObject[]{otpMbox.self(), msg});
        otpMbox.send(pollingStationMbox, pollingStationNode, msgTuple);
    }

    public void sendStringToCentralStation(String message) {
        OtpErlangString msg = new OtpErlangString(message);
        OtpErlangTuple msgTuple = new OtpErlangTuple(
                new OtpErlangObject[]{otpMbox.self(), msg});
        otpMbox.send(centralStationMbox, centralStationNode, msgTuple);
    }

    public void sendAtomToCentralStation(String message) {
        OtpErlangAtom msg = new OtpErlangAtom(message);
        OtpErlangTuple msgTuple = new OtpErlangTuple(
                new OtpErlangObject[]{otpMbox.self(), msg});
        otpMbox.send(centralStationMbox, centralStationNode, msgTuple);
    }


    public void sendBytes(byte[] message) {
        OtpErlangBinary msg = new OtpErlangBinary(message);
        OtpErlangTuple msgTuple = new OtpErlangTuple(
                new OtpErlangObject[]{otpMbox.self(), msg});
        otpMbox.send("polling_station_endpoint", pollingStationNode, msgTuple);
    }

    public void sendSigned(byte[] message, byte[] sign) {
        OtpErlangBinary msg = new OtpErlangBinary(message);
        OtpErlangBinary s = new OtpErlangBinary(sign);
        OtpErlangTuple msgTuple = new OtpErlangTuple(
                new OtpErlangObject[]{otpMbox.self(), msg, s});
        otpMbox.send("polling_station_endpoint", pollingStationNode, msgTuple);
    }

    public void sendSigned(byte[] message, String signerID,byte[] sign) {
        OtpErlangBinary msg = new OtpErlangBinary(message);
        OtpErlangBinary s = new OtpErlangBinary(sign);
        OtpErlangString id= new OtpErlangString(signerID);
        OtpErlangTuple payload = new OtpErlangTuple(
                new OtpErlangObject[]{msg,id, s});
        OtpErlangTuple msgTuple = new OtpErlangTuple(
                new OtpErlangObject[]{otpMbox.self(),payload});
        otpMbox.send("polling_station_endpoint", pollingStationNode, msgTuple);

    }

    public String receiveString() {
        String res = null;
        while (true) {
            try {
                OtpErlangObject message = otpMbox.receive();
                System.out.println("received something");
                OtpErlangTuple erlangTuple= (OtpErlangTuple) message;
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
                OtpErlangTuple erlangTuple= (OtpErlangTuple) message;
                OtpErlangBinary payload = (OtpErlangBinary) erlangTuple.elementAt(1);
                return payload.binaryValue();

            } catch (OtpErlangDecodeException | OtpErlangExit | ClassCastException ignored) {
            }
        }

    }

    /**
     * receive a signed message in binary format
     *
     * @return 2 arrays of bytes in a list, the 1st is the message, the 2nd is the sign
     */
    public List<byte[]> receiveSigned() {
        List<byte[]> res = new ArrayList<>(2);
        while (true) {
            try {
                OtpErlangObject message = otpMbox.receive();
                System.out.println("received something");
                OtpErlangTuple erlangTuple= (OtpErlangTuple) message;
                OtpErlangBinary msg = (OtpErlangBinary) erlangTuple.elementAt(1);
                OtpErlangBinary s = (OtpErlangBinary) erlangTuple.elementAt(2);
                res.add(msg.binaryValue());
                res.add(s.binaryValue());
                return res;

            } catch (OtpErlangDecodeException | OtpErlangExit | ClassCastException ignored) {
            }
        }

    }

    public boolean receiveAck() {
        try {
            OtpErlangObject message = otpMbox.receive();
            System.out.println("received something");
            OtpErlangTuple erlangTuple= (OtpErlangTuple) message;
            OtpErlangPid senderPID = (OtpErlangPid) erlangTuple.elementAt(0);
            OtpErlangAtom payload = (OtpErlangAtom) erlangTuple.elementAt(1);
            if (payload.toString().equals("ok"))
                return true;
        } catch (OtpErlangDecodeException | OtpErlangExit ignored) {
            return false;
        }
        return false;
    }
}
