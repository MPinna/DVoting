package it.unipi.dsmt.DVoting.CentralStation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class VotesIterator implements Iterator<String> {

    private final ResultSet rs;
    private boolean hasNextChecked, hasNext;
    public VotesIterator(ResultSet rs){
        this.rs = rs;
    }
    public boolean hasNext() {
        if (hasNextChecked)
            return hasNext;
        try {
            hasNext = rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        hasNextChecked = true;
        return hasNext;
    }

    public String next() {
        if (!hasNext())
            throw new NoSuchElementException();

        try {
            String res = rs.getString(2);
            hasNextChecked = false;
            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
