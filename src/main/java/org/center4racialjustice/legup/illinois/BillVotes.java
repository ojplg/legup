package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Name;

import java.util.ArrayList;
import java.util.List;


public class BillVotes {

    // FIXME: Privacy needed here now.

    String content;
    int billNumber;
    int expectedNays;
    int expectedYeas;
    int expectedPresent;
    int expectedNotVoting;
    List<Name> nays = new ArrayList<>();
    List<Name> yeas = new ArrayList<>();
    List<Name> presents = new ArrayList<>();
    List<Name> notVotings = new ArrayList<>();

    public void addVoteRecord(VoteRecord voteRecord){
        switch (voteRecord.getVote().getCode()){
            case Vote.PresentCode :
                presents.add(voteRecord.getName());
                break;
            case Vote.NayCode :
                nays.add(voteRecord.getName());
                break;
            case Vote.YeaCode :
                yeas.add(voteRecord.getName());
                break;
            case Vote.NotVotingCode :
                notVotings.add(voteRecord.getName());
                break;
            default :
                throw new RuntimeException("Unrecognized vote type");
        }
    }

    public List<Name> getNays(){
        return nays;
    }

    public List<Name> getYeas(){
        return yeas;

    }
    public List<Name> getPresents(){
        return presents;
    }

    public List<Name> getNotVotings(){
        return notVotings;
    }

    public int totalVotes(){
        return nays.size() + yeas.size() + presents.size() + notVotings.size();
    }

    public void checkVoteCounts(){
        if (nays.size() != expectedNays){
            throw new RuntimeException("Bad Nays count. Expected " + expectedNays + " calculated " + nays.size());
        }
        if (yeas.size() != expectedYeas){
            throw new RuntimeException("Bad Yeas count. Expected " + expectedYeas + " calculated " + yeas.size());
        }
        if (presents.size() != expectedPresent){
            throw new RuntimeException("Bad Present count. Expected " + expectedPresent + " calculated " + presents.size());
        }
//        if (notVotings.size() != expectedNotVoting){
//            throw new RuntimeException("Bad NotVoting count. Expected " + expectedNotVoting + " calculated " + notVotings.size());
//        }
    }
}
