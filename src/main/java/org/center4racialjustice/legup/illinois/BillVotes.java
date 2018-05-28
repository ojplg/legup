package org.center4racialjustice.legup.illinois;

import java.util.ArrayList;
import java.util.List;


public class BillVotes {
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
}
