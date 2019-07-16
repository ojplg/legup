package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.DisplayAction;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.LegislatorBillAction;
import org.center4racialjustice.legup.domain.LegislatorBillActionType;
import org.center4racialjustice.legup.domain.Vote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BillActionCollator {

    private final List<DisplayAction> votes;
    private final List<DisplayAction> sponsorships;
    private final List<DisplayAction> chiefSponsorships;

    public BillActionCollator(List<BillAction> actions){
        this(actions, null);
    }

    public BillActionCollator(List<BillAction> actions, Legislator legislator){
        List<DisplayAction> votes = new ArrayList<>();
        List<DisplayAction> sponsorships = new ArrayList<>();
        List<DisplayAction> chiefSponsorships = new ArrayList<>();

        for(BillAction action : actions){
            for( LegislatorBillAction legislatorBillAction : action.getLegislatorBillActions()) {
                if( legislator != null ){
                    if ( ! legislator.equals(legislatorBillAction.getLegislator()) ){
                        continue;
                    }
                }
                DisplayAction displayAction = new DisplayAction(action, legislatorBillAction);
                switch (legislatorBillAction.getLegislatorBillActionType().getCode()) {
                    case LegislatorBillActionType.VoteCode:
                        votes.add(displayAction);
                        break;
                    case LegislatorBillActionType.SponsorCode:
                        sponsorships.add(displayAction);
                        break;
                    case LegislatorBillActionType.ChiefSponsorCode:
                        chiefSponsorships.add(displayAction);
                        break;
                    default:
                        throw new RuntimeException("Unknown bill action type " + action.getBillActionType());
                }
            }
        }

        votes.sort(DisplayAction.ByBillComparator);
        sponsorships.sort(DisplayAction.ByBillComparator);
        chiefSponsorships.sort(DisplayAction.ByBillComparator);

        this.votes = Collections.unmodifiableList(votes);
        this.sponsorships = Collections.unmodifiableList(sponsorships);
        this.chiefSponsorships = Collections.unmodifiableList(chiefSponsorships);

    }

    public List<DisplayAction> getVotes() {
        return votes;
    }

    public List<DisplayAction> getSponsorships() {
        return sponsorships;
    }

    public List<DisplayAction> getChiefSponsorships() {
        return chiefSponsorships;
    }
}
