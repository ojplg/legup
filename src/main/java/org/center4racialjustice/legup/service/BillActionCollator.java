package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.LegislatorBillAction;
import org.center4racialjustice.legup.domain.LegislatorBillActionType;
import org.center4racialjustice.legup.domain.Vote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BillActionCollator {

    private final List<LegislatorBillAction> votes;
    private final List<LegislatorBillAction> sponsorships;
    private final List<LegislatorBillAction> chiefSponsorships;

    public BillActionCollator(List<BillAction> actions){

        List<LegislatorBillAction> votes = new ArrayList<>();
        List<LegislatorBillAction> sponsorships = new ArrayList<>();
        List<LegislatorBillAction> chiefSponsorships = new ArrayList<>();

        for(BillAction action : actions){
            for( LegislatorBillAction legislatorBillAction : action.getLegislatorBillActions()) {
                switch (legislatorBillAction.getLegislatorBillActionType().getCode()) {
                    case LegislatorBillActionType.VoteCode:
                        votes.add(legislatorBillAction);
                        break;
                    case LegislatorBillActionType.SponsorCode:
                        sponsorships.add(legislatorBillAction);
                        break;
                    case LegislatorBillActionType.ChiefSponsorCode:
                        chiefSponsorships.add(legislatorBillAction);
                        break;
                    default:
                        throw new RuntimeException("Unknown bill action type " + action.getBillActionType());
                }
            }
        }

        // FIXME: Sorting was probably good ...
//        votes.sort(Vote.ByBillComparator);
//        sponsorships.sort(BillAction.ByBillComparator);
//        chiefSponsorships.sort(BillAction.ByBillComparator);

        this.votes = Collections.unmodifiableList(votes);
        this.sponsorships = Collections.unmodifiableList(sponsorships);
        this.chiefSponsorships = Collections.unmodifiableList(chiefSponsorships);
    }

    public List<LegislatorBillAction> getVotes() {
        return votes;
    }

    public List<LegislatorBillAction> getSponsorships() {
        return sponsorships;
    }

    public List<LegislatorBillAction> getChiefSponsorships() {
        return chiefSponsorships;
    }
}
