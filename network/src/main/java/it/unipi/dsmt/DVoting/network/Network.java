package it.unipi.dsmt.DVoting.network;

import com.ericsson.otp.erlang.*;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * network class to communicate with otpNodes
 */
public class Network {

    protected String nodeId = "senderNode@localhost";
    protected String cookie = "abcde";
    protected static OtpNode otpNode;
    protected static OtpMbox otpMbox;

    protected int wait = 2000; // wait time on receive

    protected String centralStationMbox = "central_station_endpoint";

    String centralStationNode = "cs@studente75";

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


    /**
     * test connection with central station
     *
     * @return boolean
     */
    public boolean pingCS() {
        return otpNode.ping(centralStationNode, wait);
    }


    public void sendAtomToCentralStation(String message) {
        OtpErlangAtom msg = new OtpErlangAtom(message);
        OtpErlangTuple msgTuple = new OtpErlangTuple(
                new OtpErlangObject[]{otpMbox.self(), msg});
        otpMbox.send(centralStationMbox, centralStationNode, msgTuple);
    }


    public String receiveString() {

        try {
            OtpErlangObject message = otpMbox.receive(wait);
            //System.out.println("received something");
            OtpErlangTuple erlangTuple = (OtpErlangTuple) message;
            OtpErlangPid senderPID = (OtpErlangPid) erlangTuple.elementAt(0);
            //OtpErlangBinary payload = (OtpErlangBinary) erlangTuple.elementAt(1);
            OtpErlangString payload = (OtpErlangString) erlangTuple.elementAt(1);
            return payload.stringValue();

        } catch (OtpErlangDecodeException | OtpErlangExit ignored) {
        }
        return null;
    }


    public byte[] receiveBytes() {
        try {
            OtpErlangObject message = otpMbox.receive(wait);
            //System.out.println("received something");
            OtpErlangTuple erlangTuple = (OtpErlangTuple) message;
            OtpErlangBinary payload = (OtpErlangBinary) erlangTuple.elementAt(1);
            return payload.binaryValue();

        } catch (Exception ignored) {
        }
        return null;

    }

    public byte[] receiveBytesInfiniteWait() throws EOFException {
        try {
            OtpErlangObject message = otpMbox.receive();
            System.out.println("received something");
            OtpErlangTuple erlangTuple = (OtpErlangTuple) message;
            if (erlangTuple.elementAt(1) instanceof OtpErlangBinary) {
                OtpErlangBinary payload = (OtpErlangBinary) erlangTuple.elementAt(1);
                return payload.binaryValue();
            } else if (erlangTuple.elementAt(1) instanceof OtpErlangAtom) {
                OtpErlangAtom payload = (OtpErlangAtom) erlangTuple.elementAt(1);
                if (payload.atomValue().equals("vote_closed"))
                    throw new EOFException();
            }

        } catch (OtpErlangDecodeException | ClassCastException ignored) {

        } catch (OtpErlangExit e) {
            throw new RuntimeException(e);
        }
        return null;

    }

    /**
     * receive a signed message in binary format
     *
     * @return 2 arrays of bytes in a list, the 1st is the message, the 2nd is the sign
     */
    public List<byte[]> receiveSigned() {

        try {
            OtpErlangObject message = otpMbox.receive(wait);
            //System.out.println("received something");
            OtpErlangTuple erlangTuple = (OtpErlangTuple) message;
            OtpErlangBinary msg = (OtpErlangBinary) erlangTuple.elementAt(1);
            OtpErlangBinary s = (OtpErlangBinary) erlangTuple.elementAt(2);
            List<byte[]> res = new ArrayList<>(2);
            res.add(msg.binaryValue());
            res.add(s.binaryValue());
            return res;

        } catch (OtpErlangDecodeException | OtpErlangExit | ClassCastException ignored) {
        }
        return null;

    }
}
