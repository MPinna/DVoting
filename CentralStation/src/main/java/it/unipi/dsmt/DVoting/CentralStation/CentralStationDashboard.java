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
      db.getVotes().forEachRemaining(
              o -> {
                String s=(String) o[0];
                byte [] byteVote=Crypto.decrypt(pk, Base64.getDecoder().decode(s));
                if(byteVote == null)
                  return;
                String stringVote=new String(byteVote);
                if(res.containsKey(stringVote))
                  res.put(stringVote,(res.get(stringVote)+1));
                else
                  res.put(stringVote,0);
              }
      );
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
      if (n.test()) {
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
      do // TODO add turnout request (fulfill it checking the votes database)
        System.out.println("daemon running, press S to stop:");
      while(!in.nextLine().equals("S\n"));
      daemon.interrupt();
      n.sendAtomToCentralStation("close_vote");

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
