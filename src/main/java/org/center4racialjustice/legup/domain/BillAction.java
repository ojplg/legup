package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.util.Lists;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class BillAction {

//    public static final Comparator<BillAction> ByLegislatorComparator = Comparator.comparing(BillAction::getLegislator);
    public static final Comparator<BillAction> ByBillComparator = Comparator.comparing(BillAction::getBill);

    private Long id;
    private Bill bill;
//    private Legislator legislator;
    private BillActionType billActionType;
//    private String billActionDetail;
    private String rawActionData;
    private Instant actionDate;
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

}
