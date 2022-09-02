package it.unipi.dsmt.DVoting;

import it.unipi.dsmt.DVoting.crypto.Crypto;
import it.unipi.dsmt.DVoting.network.Network;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Candidates {

    private static List<String> candidates=null;

    public static List<String> getCandidates(String mBoxName){
        if(candidates!=null)
            return candidates;
        Network n;
        try {
            n=new Network(mBoxName, "ps@studente76"); // TODO use actual polling station address
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        n.sendAtomToCentralStation("request_candidates");
        List<byte[]> signed_msg= n.receiveSigned();

        if(!Crypto.verifyCs(signed_msg.get(0), signed_msg.get(1)))
            return null;
        String list= new String(signed_msg.get(1));
        candidates=Arrays.asList(list.split("_"));
        return candidates;

    }
}
