package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.illinois.BillActionLoads;
import org.center4racialjustice.legup.service.BillActionCollator;

import java.util.List;

public class BillHistory {

    private final Bill bill;
    private final BillActionLoads loads;
    private final BillActionCollator actions;

    public BillHistory(Bill bill, List<BillActionLoad> loads, List<BillAction> actions) {
        this.bill = bill;
        this.loads = new BillActionLoads(loads);
        this.actions = new BillActionCollator(actions);
    }

    public Bill getBill(){
        return bill;
    }

    public List<Legislator> getSponsors(Chamber chamber){
        return actions.getSponsors(chamber);
    }

    public List<Legislator> getChiefSponsors(Chamber chamber){
        return actions.getChiefSponsors(chamber);
    }

    public List<DisplayAction> getVotes(Chamber chamber, VoteSide voteSide){
        return actions.getVotes(chamber, voteSide);
    }

    public int getVoteCount(Chamber chamber, VoteSide voteSide){
        return getVotes(chamber, voteSide).size();
    }

    public float getVotePercentage(Chamber chamber, VoteSide voteSide){
        float all = actions.getVotes(chamber).size();
        float count = getVoteCount(chamber, voteSide);
        return count/all;
    }
}