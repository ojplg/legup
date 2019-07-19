package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.VoteSide;

import java.util.ArrayList;
import java.util.List;

public class VoteLists {

    private final List<Name> nays = new ArrayList<>();
    private final List<Name> yeas = new ArrayList<>();
    private final List<Name> presents = new ArrayList<>();
    private final List<Name> notVotings = new ArrayList<>();
    private final List<Name> excuseds = new ArrayList<>();
    private final List<Name> absents = new ArrayList<>();

    public void addVoteRecords(List<VoteRecord> voteRecords){
        for(VoteRecord vr : voteRecords){
            addVoteRecord(vr);
        }
    }

    public void addVoteRecord(VoteRecord voteRecord){
        switch (voteRecord.getVote().getCode()){
            case VoteSide.PresentCode :
                presents.add(voteRecord.getName());
                break;
            case VoteSide.NayCode :
                nays.add(voteRecord.getName());
                break;
            case VoteSide.YeaCode :
                yeas.add(voteRecord.getName());
                break;
            case VoteSide.NotVotingCode :
                notVotings.add(voteRecord.getName());
                break;
            case VoteSide.AbsentCode :
                absents.add(voteRecord.getName());
                break;
            case VoteSide.ExcusedCode :
                excuseds.add(voteRecord.getName());
                break;
            default :
                throw new RuntimeException("Unrecognized vote type in " + voteRecord);
        }
    }


    public List<Name> getNays() {
        return nays;
    }

    public List<Name> getYeas() {
        return yeas;
    }

    public List<Name> getPresents() {
        return presents;
    }

    public List<Name> getNotVotings() {
        return notVotings;
    }

    public List<Name> getExcuseds() {
        return excuseds;
    }

    public List<Name> getAbsents() {
        return absents;
    }

    public int getFullCount(){
        return nays.size()
                + yeas.size()
                + presents.size()
                + notVotings.size()
                + excuseds.size()
                + absents.size();
    }

    public void checkVoteCounts(ExpectedVoteCounts expectedVoteCounts){
        if (nays.size() != expectedVoteCounts.getExpectedNays()){
            throw new RuntimeException("Bad Nays count. Expected " + expectedVoteCounts.getExpectedNays() + " calculated " + nays.size());
        }
        if (yeas.size() != expectedVoteCounts.getExpectedYeas()){
            throw new RuntimeException("Bad Yeas count. Expected " + expectedVoteCounts.getExpectedYeas() + " calculated " + yeas.size());
        }
        if (presents.size() != expectedVoteCounts.getExpectedPresent()){
            throw new RuntimeException("Bad Present count. Expected " + expectedVoteCounts.getExpectedPresent() + " calculated " + presents.size());
        }
    }
}
