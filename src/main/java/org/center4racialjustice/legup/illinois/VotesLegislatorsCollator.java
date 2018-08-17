package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;

import java.util.ArrayList;
import java.util.List;

public class VotesLegislatorsCollator {

    private final List<Legislator> legislators;
    private final BillVotes billVotes;

    private List<CollatedVote> yeas;
    private List<CollatedVote> nays;
    private List<CollatedVote> notVotings;
    private List<CollatedVote> presents;

    public VotesLegislatorsCollator(List<Legislator> legislators, BillVotes billVotes) {
        this.legislators = new ArrayList<>(legislators);
        this.billVotes = billVotes;
    }

    public void collate(){
        yeas = collate(Vote.Yea, billVotes.getYeas());
        nays = collate(Vote.Nay, billVotes.getNays());
        notVotings = collate(Vote.NotVoting, billVotes.getNotVotings());
        presents = collate(Vote.Present, billVotes.getPresents());
    }

    private List<CollatedVote> collate(Vote vote, List<Name> voters){
        List<CollatedVote> collated = new ArrayList<>();
        for(Name voter : voters){
            for(Legislator legislator : legislators){
                if( legislator.getName().matches(voter)
                        && legislator.getChamber().equals(billVotes.getVotingChamber())){
                    CollatedVote collatedVote =
                            new CollatedVote(vote, legislator, voter);
                    collated.add(collatedVote);
                }
            }
        }
        return collated;
    }

    public List<CollatedVote> getYeas() {
        return yeas;
    }

    public List<CollatedVote> getNays() {
        return nays;
    }

    public List<CollatedVote> getNotVotings() {
        return notVotings;
    }

    public List<CollatedVote> getPresents() {
        return presents;
    }
}
