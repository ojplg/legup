package org.center4racialjustice.legup.domain;

import com.google.common.collect.Multimap;
import org.center4racialjustice.legup.illinois.BillActionLoads;
import org.center4racialjustice.legup.service.BillActionCollator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BillHistory {

    public static final BillHistory EMPTY = new BillHistory();

    private final Bill bill;
    private final BillActionLoads loads;
    private final BillActionCollator actionCollator;

    private BillHistory(){
        this.bill = null;
        this.loads = new BillActionLoads();
        this.actionCollator = new BillActionCollator(Collections.emptyList());
    }

    public BillHistory(Bill bill, List<BillActionLoad> loads, List<BillAction> actions) {
        this.bill = bill;
        this.loads = new BillActionLoads(loads);
        this.actionCollator = new BillActionCollator(actions);
    }

    public boolean isKnownBill(){
        return bill != null;
    }

    public List<BillActionLoad> getPriorLoads(){
        return loads.getAllLoads();
    }

    public Multimap<LocalDate,BillAction> getActionsByDate(){
        return actionCollator.getActionsByDate();
    }

    public Bill getBill(){
        return bill;
    }

    public List<Legislator> getSponsors(Chamber chamber){
        return actionCollator.getSponsors(chamber);
    }

    public List<Legislator> getChiefSponsors(Chamber chamber){
        return actionCollator.getChiefSponsors(chamber);
    }

    public List<Legislator> getIntroductions(Chamber chamber){
        return actionCollator.getIntroductions(chamber);
    }


    public List<String> getVoteDescriptions(){
        return actionCollator.getVoteDescriptions();
    }

    public Chamber getActionChamber(String rawActionData){
        BillAction billAction = actionCollator.getActionFromRawData(rawActionData);
        return billAction.getChamber();
    }

    public Collection<DisplayAction> getVotes(String description, Chamber chamber, VoteSide voteSide){
        return actionCollator.getVotes(description, chamber, voteSide);
    }

    public boolean recognizedEvent(CompletedBillEvent billEvent){
        return actionCollator.getMatchingAction(billEvent) != null;
    }

    public boolean unrecognizedEvent(CompletedBillEvent billEvent){
        return actionCollator.getMatchingAction(billEvent) == null;
    }

}
