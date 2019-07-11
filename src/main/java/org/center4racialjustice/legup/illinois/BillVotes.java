package org.center4racialjustice.legup.illinois;


import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.VoteType;

import java.util.List;

public class BillVotes {

    private final BillIdentity billIdentity;
    private final BillWebData billWebData;
    private final ExpectedVoteCounts expectedVoteCounts;
    private final VoteLists voteLists;
    private final Chamber votingChamber;

    public BillVotes(BillIdentity billIdentity, BillWebData billWebData, ExpectedVoteCounts expectedVoteCounts, VoteLists voteLists, Chamber votingChamber) {
        this.billIdentity = billIdentity;
        this.billWebData = billWebData;
        this.expectedVoteCounts = expectedVoteCounts;
        this.voteLists = voteLists;
        this.votingChamber = votingChamber;
    }

    public void checkVoteCounts(){
        voteLists.checkVoteCounts(expectedVoteCounts);
    }

    public List<Name> getNays() {
        return voteLists.getNays();
    }

    public List<Name> getYeas() {
        return voteLists.getYeas();
    }

    public List<Name> getPresents() {
        return voteLists.getPresents();
    }

    public List<Name> getNotVotings() {
        return voteLists.getNotVotings();
    }

    public List<Name> getExcuseds() {
        return voteLists.getExcuseds();
    }

    public List<Name> getAbsents() {
        return voteLists.getAbsents();
    }

    public Chamber getVotingChamber() {
        return votingChamber;
    }

    public Chamber getBillChamber() {
        return billIdentity.getChamber();
    }

    public long getSessionNumber(){
        return billIdentity.getSession();
    }

    public long getBillNumber(){
        return billIdentity.getNumber();
    }

    public VoteType getVoteType(){
        return billWebData.getVoteType();
    }

    public long getChecksum(){
        return billWebData.getChecksum();
    }

    public long getSession(){
        return billIdentity.getSession();
    }

    public int getExpectedYeas(){
        return expectedVoteCounts.getExpectedYeas();
    }

    public int getExpectedNays(){
        return expectedVoteCounts.getExpectedNays();
    }

    public int getExpectedPresent(){
        return expectedVoteCounts.getExpectedPresent();
    }



    public int getExpectedNotVoting(){
        return expectedVoteCounts.getExpectedNotVoting();
    }
}
