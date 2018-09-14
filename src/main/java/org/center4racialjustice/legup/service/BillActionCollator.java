package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.Vote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BillActionCollator {

    private final List<Vote> votes;
    private final List<BillAction> sponsorships;
    private final List<BillAction> chiefSponsorships;

    public BillActionCollator(List<BillAction> actions){

        List<Vote> votes = new ArrayList<>();
        List<BillAction> sponsorships = new ArrayList<>();
        List<BillAction> chiefSponsorships = new ArrayList<>();

        for(BillAction action : actions){
            switch(action.getBillActionType().getCode()){
                case BillActionType.VoteCode :
                    votes.add(action.asVote());
                    break;
                case BillActionType.SponsorCode :
                    sponsorships.add(action);
                    break;
                case BillActionType.ChiefSponsorCode :
                    chiefSponsorships.add(action);
                    break;
                default : throw new RuntimeException("Unknown bill action type " + action.getBillActionType());
            }
        }

        Collections.sort(votes, Vote.ByBillComparator);
        Collections.sort(sponsorships, BillAction.ByBillComparator);
        Collections.sort(chiefSponsorships, BillAction.ByBillComparator);

        this.votes = Collections.unmodifiableList(votes);
        this.sponsorships = Collections.unmodifiableList(sponsorships);
        this.chiefSponsorships = Collections.unmodifiableList(chiefSponsorships);
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public List<BillAction> getSponsorships() {
        return sponsorships;
    }

    public List<BillAction> getChiefSponsorships() {
        return chiefSponsorships;
    }
}
