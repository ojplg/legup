package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;

public class CommitteeBillEvent extends AbstractBillEvent {

    public static CommitteeBillEvent referral(String rawData, String rawCommitteeName){
        return new CommitteeBillEvent(rawData, BillActionType.COMMITTEE_REFERRAL, rawCommitteeName);
    }

    public static CommitteeBillEvent assignment(String rawData, String rawCommitteeName){
        return new CommitteeBillEvent(rawData, BillActionType.COMMITTEE_ASSIGNMENT, rawCommitteeName);
    }

    public static CommitteeBillEvent postponement(String rawData, String rawCommitteeName){
        return new CommitteeBillEvent(rawData, BillActionType.COMMITTEE_POSTPONEMENT, rawCommitteeName);
    }

    private final BillActionType billActionType;
    private final String rawCommitteeName;

    public CommitteeBillEvent(String rawData, BillActionType billActionType, String rawCommitteeName) {
        super(rawData);
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
