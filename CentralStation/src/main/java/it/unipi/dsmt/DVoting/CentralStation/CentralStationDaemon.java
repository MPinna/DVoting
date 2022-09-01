package it.unipi.dsmt.DVoting.CentralStation;

import it.unipi.dsmt.DVoting.network.Network;

import java.util.Base64;


public class CentralStationDaemon extends Thread{
    private final Network n;
    private final DatabaseManager db;

    public CentralStationDaemon(Network n, DatabaseManager db) {
        this.n = n;
        this.db=db;
    }






    public void run() {
        while(true) {
            byte[] payload=n.receiveBytes();
            System.out.println("message " + new String(payload));
            db.addVote(Base64.getEncoder().encodeToString(payload));
        }
    }
}
