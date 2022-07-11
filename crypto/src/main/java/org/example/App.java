package org.example;

import com.ericsson.otp.erlang.*;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, OtpErlangDecodeException, OtpErlangExit {

        String nodeId = null;
        String cookie = null;
        String mBox = null;
        if (args.length == 3){
            nodeId = args[0];
            cookie = args[1];
            mBox = args[2];
        } else {
            nodeId = "helloNode@localhost";
            cookie = "abcde";
            mBox = "hello";
        }
        OtpNode otpNode = new OtpNode(nodeId, cookie);
        OtpMbox otpMbox = otpNode.createMbox(mBox);
        System.out.println("The server " + nodeId + " is running.");
        System.out.println("cookie: " + cookie);
        System.out.println("TmBox: " + mBox);
        OtpErlangObject message = otpMbox.receive();
        System.out.println("message " + message);
        if (message instanceof OtpErlangTuple){
            OtpErlangTuple erlangTuple = (OtpErlangTuple) message;
            OtpErlangPid senderPID = (OtpErlangPid) erlangTuple.elementAt(0);
            OtpErlangBinary payload = (OtpErlangBinary) erlangTuple.elementAt(1);
            // frobincate
            // ..

            OtpErlangString okErlangMsg = new OtpErlangString("ok");
            OtpErlangTuple responseTuple = new OtpErlangTuple(new OtpErlangObject[]{otpMbox.self(), okErlangMsg});
            otpMbox.send(senderPID, responseTuple);
        }
    }
}
