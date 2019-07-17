package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.util.Dates;
import org.center4racialjustice.legup.util.Lists;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Data
public class BillAction {

    public static final Comparator<BillAction> ByBillComparator = Comparator.comparing(BillAction::getBill);

    private Long id;
    private Bill bill;
    private BillActionType billActionType;
    private String rawActionData;
    private Instant actionDate;
    private Chamber chamber;
    private BillActionLoad billActionLoad;

    private List<LegislatorBillAction> legislatorBillActions;

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

    public boolean matchesEvent(BillEvent billEvent){
        return billEvent.generateEventKey().equals(generateBillEventKey());
    }

    public BillEventKey generateBillEventKey(){
        return new BillEventKey(getActionDateAsLocalDate(), chamber, rawActionData);
    }

    public LocalDate getActionDateAsLocalDate(){
        return Dates.localDateOf(actionDate);
    }

}
