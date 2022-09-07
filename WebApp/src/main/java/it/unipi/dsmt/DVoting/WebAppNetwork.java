package it.unipi.dsmt.DVoting;

import com.ericsson.otp.erlang.*;
import it.unipi.dsmt.DVoting.network.Network;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WebAppNetwork extends Network {
    static String pollingStationNode = null;
    protected String pollingStationMbox = "polling_station_endpoint";
    public WebAppNetwork(String name) throws IOException {
        super(name);
        if (pollingStationNode == null) {
            InputStream is = AdminServlet.class.getResourceAsStream("/admin.json");
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject obj = (JSONObject) jsonParser.parse(new InputStreamReader(is));
                pollingStationNode=(String)obj.get("pollingStationServer");
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public WebAppNetwork(String name, String pollingStationNode) throws IOException {
        super(name);
        WebAppNetwork.pollingStationNode = pollingStationNode;

    }

    public boolean pingPS() {
        //System.out.println(otpMbox.getName() +".: "+otpNode.whereis(otpMbox.getName()));
        //otpMbox.whereis(pollingStationNode);
        return otpNode.ping(pollingStationNode, 2000);
    }


    public void sendStringToPollingStation(String message) {
        OtpErlangString msg = new OtpErlangString(message);
        OtpErlangTuple msgTuple = new OtpErlangTuple(
                new OtpErlangObject[]{otpMbox.self(), msg});
        otpMbox.send(pollingStationMbox, pollingStationNode, msgTuple);
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

    public void sendSigned(byte[] message, String signerID,byte[] sign, String action) {
        OtpErlangBinary msg = new OtpErlangBinary(message);
        OtpErlangBinary s = new OtpErlangBinary(sign);
        OtpErlangString id= new OtpErlangString(signerID);
        OtpErlangAtom atom= new OtpErlangAtom(action);
        OtpErlangTuple payload = new OtpErlangTuple(
                new OtpErlangObject[]{msg,id, s});
        OtpErlangTuple msgTuple = new OtpErlangTuple(
                new OtpErlangObject[]{otpMbox.self(),payload, atom});
        otpMbox.send("polling_station_endpoint", pollingStationNode, msgTuple);

    }

    public void sendAtomToPollingStation(String message) {
        OtpErlangAtom msg = new OtpErlangAtom(message);
        OtpErlangTuple msgTuple = new OtpErlangTuple(
                new OtpErlangObject[]{otpMbox.self(), msg});
        //OtpErlangPid psPid = otpNode.whereis(pollingStationMbox);
        //otpMbox.send(psPid, msgTuple);
        otpMbox.send(pollingStationMbox, pollingStationNode, msgTuple);
    }

    public int receiveInt() {
        int res;
        while (true) {
            try {
                OtpErlangObject message = otpMbox.receive();
                System.out.println("received something");
                OtpErlangTuple erlangTuple= (OtpErlangTuple) message;
                OtpErlangPid senderPID = (OtpErlangPid) erlangTuple.elementAt(0);
                //OtpErlangBinary payload = (OtpErlangBinary) erlangTuple.elementAt(1);
                OtpErlangLong payload = (OtpErlangLong) erlangTuple.elementAt(1);
                res = payload.intValue();
                break;

            } catch (OtpErlangDecodeException | OtpErlangExit | OtpErlangRangeException ignored) {
            }
        }
        return res;
    }

    public Object[] receiveList() {
        Object[] res;
        while (true) {
            try {
                OtpErlangObject message = otpMbox.receive();
                System.out.println("received something");
                OtpErlangTuple erlangTuple= (OtpErlangTuple) message;
                OtpErlangPid senderPID = (OtpErlangPid) erlangTuple.elementAt(0);
                //OtpErlangBinary payload = (OtpErlangBinary) erlangTuple.elementAt(1);
                OtpErlangList payload = (OtpErlangList) erlangTuple.elementAt(1);
                res=new Object[payload.elements().length];
                for (int i = 0; i < payload.elements().length; i++) {
                    res[i]=payload.elementAt(i);
                }
                break;

            } catch (OtpErlangDecodeException | OtpErlangExit ignored) {
            }
        }
        return res;
    }

}