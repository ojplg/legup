package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.util.Lists;

import java.util.List;
import java.util.stream.Collectors;

// TODO: Get rid of this class. Replace with BillHistory.
public class BillActionSummary {

    private final List<BillAction> billActions;

    public BillActionSummary(List<BillAction> billActions){
        this.billActions = billActions;
    }

    public List<Legislator> getVotes(Chamber chamber, VoteSide voteSide){
        return billActions.stream()
                .filter(action -> action.getBillActionType().equals(BillActionType.VOTE))
                .flatMap(action -> action.getLegislatorBillActions().stream())
                .filter(action->action.getVoteSide().equals(voteSide))
                .filter(legAction -> legAction.getLegislator().getChamber().equals(chamber))
                .map(LegislatorBillAction::getLegislator)
                .collect(Collectors.toList());
    }

    public List<Legislator> getSponsors(Chamber chamber){
        return billActions.stream()
            .filter(action -> action.getBillActionType().equals(BillActionType.SPONSOR))
            .flatMap(action -> action.getLegislatorBillActions().stream())
            .map(LegislatorBillAction::getLegislator)
            .filter(legislator -> legislator.getChamber().equals(chamber))
            .collect(Collectors.toList());
    }

    public Legislator getChiefSponsor(Chamber chamber){
        List<Legislator> chiefs = billActions.stream()
                .filter(action -> action.getBillActionType().equals(BillActionType.CHIEF_SPONSOR))
                .flatMap(action -> action.getLegislatorBillActions().stream())
                .map(LegislatorBillAction::getLegislator)
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
