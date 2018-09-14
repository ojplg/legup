package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.util.Lists;

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
        filtered.sort(Vote.ByLegislatorComparator);
        return filtered;
    }

    public List<Legislator> getSponsors(Chamber chamber){
        return billActions.stream()
            .filter(action -> action.getBillActionType().equals(BillActionType.SPONSOR))
            .map(action -> action.getLegislator())
            .filter(legislator -> legislator.getChamber().equals(chamber))
            .collect(Collectors.toList());
    }

    public Legislator getChiefSponsor(Chamber chamber){
        List<Legislator> chiefs = billActions.stream()
                .filter(action -> action.getBillActionType().equals(BillActionType.CHIEF_SPONSOR))
                .map(action -> action.getLegislator())
                .collect(Collectors.toList());
        return Lists.findfirst(chiefs, legislator -> legislator.getChamber().equals(chamber));
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
