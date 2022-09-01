package it.unipi.dsmt.DVoting.CentralStation;

import it.unipi.dsmt.DVoting.crypto.Crypto;
import it.unipi.dsmt.DVoting.network.Network;

import java.io.InputStream;
import java.security.PrivateKey;


public class CentralStationDaemon {

    public static void main(String[] args) {

        try {
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
        Network n= null;

            n = new Network(mBox, nodeId, cookie);

        System.out.println("The server " + nodeId + " is running.");
        System.out.println("cookie: " + cookie);
        System.out.println("TmBox: " + mBox);
        if (n.test()) {
            System.out.println("server@localhost is up.");
        } else {
            System.out.println("server@localhost is down");
        }
        PrivateKey pk;
        InputStream filePath=CentralStationDaemon.class.getResourceAsStream("/cs_keys/cs_key.pem");
        System.out.println(filePath);
        //File privateKeyFile = new File(filePath); // private key file in PEM format
        pk=Crypto.getPrivateKey(filePath);
        if(pk==null){
            System.out.println("pk null");
            return;
        }

        String DATABASE_NAME = "encVotes.db";
        DatabaseManager db = new DatabaseManager(DATABASE_NAME);
        if(!db.connect() || !db.createVotesTable()){
            System.out.println("Could not open connection to " + DATABASE_NAME);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
