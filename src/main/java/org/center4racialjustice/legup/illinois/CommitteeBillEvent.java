package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;

public class CommitteeBillEvent extends AbstractBillEvent {

    public static CommitteeBillEvent referral(BillEvent underlyingEvent, String rawCommitteeName){
        return new CommitteeBillEvent(underlyingEvent, BillActionType.COMMITTEE_REFERRAL, rawCommitteeName);
    }

    public static CommitteeBillEvent assignment(BillEvent underlyingEvent, String rawCommitteeName){
        return new CommitteeBillEvent(underlyingEvent, BillActionType.COMMITTEE_ASSIGNMENT, rawCommitteeName);
    }

    public static CommitteeBillEvent postponement(BillEvent underlyingEvent, String rawCommitteeName){
        return new CommitteeBillEvent(underlyingEvent, BillActionType.COMMITTEE_POSTPONEMENT, rawCommitteeName);
    }

    public static CommitteeBillEvent vote(BillEvent underlyingEvent, String rawCommitteeName){
        return new CommitteeBillEvent(underlyingEvent, BillActionType.VOTE, rawCommitteeName);
    }

    private final BillActionType billActionType;
    private final String rawCommitteeName;

    public CommitteeBillEvent(BillEvent underlyingEvent, BillActionType billActionType, String rawCommitteeName) {
        super(underlyingEvent);
        this.billActionType = billActionType;
        this.rawCommitteeName = rawCommitteeName;
    }

    @Override
    public BillActionType getBillActionType() {
        return billActionType;
    }

    @Override
    public String getRawCommitteeName(){
        return rawCommitteeName;
    }
}
