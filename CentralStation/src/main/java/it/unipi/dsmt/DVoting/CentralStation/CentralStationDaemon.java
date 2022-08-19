package it.unipi.dsmt.DVoting.CentralStation;

import it.unipi.dsmt.DVoting.crypto.Crypto;
import it.unipi.dsmt.DVoting.network.Network;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class CentralStationDaemon {

    public static void main(String[] args) {
        String nodeId = null;
        String cookie = null;
        String mBox = null;
        if (args.length == 3){
            nodeId = args[0];
            cookie = args[1];
            mBox = args[2];
        } else {
            nodeId = "central@localhost";
            cookie = "abcde";
            mBox = "cs";
        }
        Network n=new Network(mBox, nodeId, cookie);
        System.out.println("The server " + nodeId + " is running.");
        System.out.println("cookie: " + cookie);
        System.out.println("TmBox: " + mBox);
        if (n.test()) {
            System.out.println("server@localhost is up.");
        } else {
            System.out.println("server@localhost is down");
        }
        PrivateKey pk;
        try {
            String filePath=CentralStationDaemon.class.
                    getResource("/cs_keys/cs_key.pem").getPath();
            System.out.println(filePath);
            //File privateKeyFile = new File(filePath); // private key file in PEM format
            pk=Crypto.getPrivateKey(filePath);
            if(pk==null)
                System.out.println("pk null");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        while(true) {

            byte[] payload=n.receiveBytes();
            System.out.println("raw: "+payload.toString());
            String message= new String(Crypto.decrypt(pk, payload));
            //String message=n.receiveString();
            System.out.println("message " + message);
            //n.send("ok");
        }
    }
}
