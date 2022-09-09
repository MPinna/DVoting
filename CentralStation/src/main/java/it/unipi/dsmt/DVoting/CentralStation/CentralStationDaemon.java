package it.unipi.dsmt.DVoting.CentralStation;

import it.unipi.dsmt.DVoting.network.Network;

import java.io.EOFException;
import java.util.Base64;


/**
 * listener for receive and store encrypted votes
 */
public class CentralStationDaemon extends Thread{
    private final Network n;
    private final DatabaseManager db;

    public CentralStationDaemon(Network n, DatabaseManager db) {
        this.n = n;
        this.db=db;
    }

    public void run() {
        while(true) {
           byte[] payload;
           try {
               payload = n.receiveBytesInfiniteWait();
           }catch (RuntimeException e){
               e.printStackTrace(); // error happened
               break;
           } catch (EOFException e) {
               System.out.println("vote_closed received"); // voting has been closed
               break;
           }
            if(payload==null){
               continue;
           }
            String encoded=Base64.getEncoder().encodeToString(payload);
           System.out.println("vote received: " + encoded);
           db.addVote(encoded);
        }
        System.out.println("daemon terminated");

    }
}
