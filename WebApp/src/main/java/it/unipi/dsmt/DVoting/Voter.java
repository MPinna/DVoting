package it.unipi.dsmt.DVoting;

import com.ericsson.otp.erlang.*;

import java.time.LocalDate;

public class Voter {


    public String getName() {
        return name;
    }


    public String getSurname() {
        return surname;
    }

    public LocalDate getDob() {
        return dob;
    }

    public Boolean getHasVoted() {
        return hasVoted;
    }

    public int getId() {   return id;    }

    String name;
    String surname;
    LocalDate dob;
    Boolean hasVoted;



    int id;

    public Voter(String name, String surname, LocalDate dob, Boolean hasVoted, int id) {
        this.name = name;
        this.surname = surname;
        this.dob = dob;
        this.hasVoted = hasVoted;
        this.id=id;
    }

    public Voter(OtpErlangTuple o) throws OtpErlangRangeException {
        this.id=((OtpErlangLong)o.elementAt(1)).intValue();
        this.name = ((OtpErlangString)o.elementAt(2)).stringValue();
        this.surname =  ((OtpErlangString)o.elementAt(3)).stringValue();
        this.dob = LocalDate.parse(((OtpErlangString)o.elementAt(4)).stringValue());
        this.hasVoted = ((OtpErlangAtom)o.elementAt(6)).booleanValue();
    }

}
