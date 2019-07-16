package org.center4racialjustice.legup.domain;

import java.util.Comparator;

public class DisplayAction {

    public static final Comparator<DisplayAction> ByBillComparator = Comparator.comparing(DisplayAction::getBill);
    public static final Comparator<DisplayAction> ByLegislatorComparator = Comparator.comparing(DisplayAction::getLegislator);

    private final BillAction billAction;
    private final LegislatorBillAction legislatorBillAction;

    public DisplayAction(BillAction billAction, LegislatorBillAction legislatorBillAction) {
        this.billAction = billAction;
        this.legislatorBillAction = legislatorBillAction;
    }

    public Bill getBill(){
        return billAction.getBill();
    }

    public Legislator getLegislator(){
        return legislatorBillAction.getLegislator();
    }

    public boolean isVote(){
        return legislatorBillAction.isVote();
    }

    public VoteSide getVoteSide(){
        return legislatorBillAction.getVoteSide();
    }

    public String getLegislatorDisplayName(){
        return getLegislator().getDisplay();
    }

    public String getLegislatorParty(){
        return getLegislator().getParty();
    }

    public Chamber getBillChamber(){
        return getBill().getChamber();
    }

    public Long getBillNumber(){
        return getBill().getNumber();
    }

    public String getBillShortDescription(){
        return getBill().getShortDescription();
    }
}
