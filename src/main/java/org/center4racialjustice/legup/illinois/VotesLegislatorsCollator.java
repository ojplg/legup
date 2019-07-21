package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.VoteSide;
import org.center4racialjustice.legup.service.LegislativeStructure;

import java.util.ArrayList;
import java.util.List;

public class VotesLegislatorsCollator {

    private final LegislativeStructure legislativeStructure;
    private final BillVotes billVotes;

    private List<CollatedVote> yeas;
    private List<CollatedVote> nays;
    private List<CollatedVote> notVotings;
    private List<CollatedVote> presents;

    private final List<Name> uncollated;

    public VotesLegislatorsCollator(LegislativeStructure legislativeStructure, BillVotes billVotes) {
        this.legislativeStructure = legislativeStructure;
        this.billVotes = billVotes;
        this.uncollated = new ArrayList<>();
    }

    public void collate(){
        yeas = collate(VoteSide.Yea, billVotes.getYeas());
        nays = collate(VoteSide.Nay, billVotes.getNays());
        notVotings = collate(VoteSide.NotVoting, billVotes.getNotVotings());
        presents = collate(VoteSide.Present, billVotes.getPresents());

    }

    private List<CollatedVote> collate(VoteSide vote, List<Name> voters){
        List<CollatedVote> collated = new ArrayList<>();
        for(Name voter : voters){
            Legislator legislator = legislativeStructure.findByNameAndChamber(voter, billVotes.getVotingChamber());
            if( legislator != null ){
                CollatedVote collatedVote = new CollatedVote(vote, legislator, voter, null );
                collated.add(collatedVote);
            } else {
                uncollated.add(voter);
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

    public List<CollatedVote> getAllCollatedVotes(){
        List<CollatedVote> allVotes = new ArrayList<>();
        allVotes.addAll(yeas);
        allVotes.addAll(nays);
        allVotes.addAll(notVotings);
        allVotes.addAll(presents);
        return allVotes;
    }

    public List<Name> getUncollated() { return uncollated; }

    public Chamber getBillChamber(){
        return billVotes.getBillChamber();
    }

    public long getBillNumber(){
        return billVotes.getBillNumber();
    }

    public long getBillSession() { return billVotes.getSessionNumber(); }
}
