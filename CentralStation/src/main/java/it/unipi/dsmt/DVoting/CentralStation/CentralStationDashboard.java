package it.unipi.dsmt.DVoting.CentralStation;

import it.unipi.dsmt.DVoting.crypto.Crypto;
import it.unipi.dsmt.DVoting.network.Network;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CentralStationDashboard {

    private static Map<String, Integer> countVotes(DatabaseManager db, PrivateKey pk){
        Map<String,Integer> res=new HashMap<>();
        VotesIterator rs = db.getVotes();
        while(rs.hasNext()){
            String s =  rs.next();
            System.out.println(s);
            byte [] byteVote=Crypto.decrypt(pk, Base64.getDecoder().decode(s));
            if(byteVote == null)
                continue;
            String stringVote=new String(byteVote);
            if(res.containsKey(stringVote))
                res.put(stringVote,(res.get(stringVote)+1));
            else
                res.put(stringVote,1);
        }
        return res;

    }

    public static void main(String[] args) {
        try {
            String nodeId;
            String cookie;
            String mBox;
            if (args.length == 3){
                nodeId = args[0];
                cookie = args[1];
                mBox = args[2];
            } else {
                nodeId = "central@localhost";
                cookie = "abcde";
                mBox = "cs";
            }
            Network n;

            n = new Network(mBox, nodeId, cookie);

            System.out.println("The server " + nodeId + " is running.");
            System.out.println("cookie: " + cookie);
            System.out.println("TmBox: " + mBox);
            if (n.pingCS()) {
                System.out.println("server@localhost is up.");
            } else {
                System.out.println("server@localhost is down");
            }
            PrivateKey pk;
            InputStream filePath=CentralStationDaemon.class.getResourceAsStream("/cs_keys/cs_key.pem");
            System.out.println(filePath);
            //File privateKeyFile = new File(filePath); // private key file in PEM format
            pk= Crypto.getPrivateKey(filePath);
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
            CentralStationDaemon daemon=new CentralStationDaemon(n, db);
            System.out.println("Commands:");
            System.out.println("1. Start Daemon");
            System.out.println("2. Exit:");
            Scanner in = new Scanner(System.in);
            if (in.nextInt() == 1) {
                daemon.start();
            }
            else
                return;
            do {
                System.out.println("daemon running:");
                int t=db.getTurnout();
                System.out.println("turnout: "+t);
                System.out.println("press S to stop, ENTER to update turnout");
            }while(!in.nextLine().equals("S"));
            n.sendAtomToCentralStation("close_vote"); // TODO wait until cs has elaborated the preceding votes
            daemon.interrupt();
            System.out.println(countVotes(db,pk));
            db.disconnect();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
