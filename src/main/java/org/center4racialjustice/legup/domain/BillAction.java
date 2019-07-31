package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.util.Dates;
import org.center4racialjustice.legup.util.Lists;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Data
public class BillAction implements Comparable<BillAction> {

    public static final Comparator<BillAction> ByBillComparator = Comparator.comparing(BillAction::getBill);

    private Long id;
    private Bill bill;
    private BillActionType billActionType;
    private String rawActionData;
    private Instant actionDate;
    private Chamber chamber;
    private BillActionLoad billActionLoad;
    private Long committeeId;
    private List<LegislatorBillAction> legislatorBillActions;

    public boolean isScoreable(){
        if ( billActionType.equals(BillActionType.VOTE) ){
            return rawActionData.contains("Third Reading");
        } else {
            return BillActionType.isSponsoringRelated(billActionType);
        }
    }

    public boolean isVote(){
        return BillActionType.VOTE.equals(billActionType);
    }

    public boolean isSponsorship(){
        return BillActionType.SPONSOR.equals(billActionType);
    }

    public boolean isChiefSponsorship(){
        return BillActionType.CHIEF_SPONSOR.equals(billActionType);
    }

    public LegislatorBillAction getLegislatorAction(Legislator legislator){
        return Lists.findfirst(legislatorBillActions, legislatorBillAction -> legislator.equals(legislatorBillAction.getLegislator()));
    }

    public boolean matchesEvent(CompletedBillEvent billEvent){
        return billEvent.generateEventKey().equals(generateBillEventKey());
    }

    public BillEventKey generateBillEventKey(){
        return new BillEventKey(getActionDateAsLocalDate(), chamber, rawActionData);
    }

    public LocalDate getActionDateAsLocalDate(){
        return Dates.localDateOf(actionDate);
    }

    @Override
    public int compareTo(BillAction o) {
        return this.actionDate.compareTo(o.actionDate);
    }

    public Legislator getSingleLegislator(){
        if ( legislatorBillActions.size() != 1 ){
            throw new RuntimeException("Wrong number of legislative actions " + legislatorBillActions.size() + " in "
                    + id + " for " + bill + " with type " + billActionType);
        }
        return legislatorBillActions.get(0).getLegislator();
    }
}

