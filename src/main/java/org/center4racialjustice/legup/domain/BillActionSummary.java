package org.center4racialjustice.legup.domain;

import java.util.List;
import java.util.stream.Collectors;

public class BillActionSummary {

    private final List<BillAction> billActions;

    public BillActionSummary(List<BillAction> billActions){
        this.billActions = billActions;
    }

    public List<Vote> getVotes(Chamber chamber, VoteSide voteSide){
        List<Vote> votes = BillAction.filterAndConvertToVotes(billActions);
        List<Vote> filtered = votes.stream().filter(v -> v.matches(chamber, voteSide)).collect(Collectors.toList());
        filtered.sort(Vote::compareTo);
        return filtered;
    }

    public int yeaCount(Chamber chamber){
        return getVotes(chamber, VoteSide.Yea).size();
    }

    public int nayCount(Chamber chamber){
        return getVotes(chamber, VoteSide.Nay).size();
    }

    public int presentCount(Chamber chamber){
        return getVotes(chamber, VoteSide.Present).size();
    }

    public int notVotingCount(Chamber chamber){
        return getVotes(chamber, VoteSide.NotVoting).size();
    }

    public float yeaPercentage(Chamber chamber){
        float yeaCount = (float) yeaCount(chamber);
        float nayCount = (float) nayCount(chamber);
        return yeaCount / (yeaCount + nayCount);
    }
}
