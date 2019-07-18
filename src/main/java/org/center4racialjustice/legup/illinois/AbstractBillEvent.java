package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEventData;

public abstract class AbstractBillEvent implements BillEventData {

    private final String rawData;

    public AbstractBillEvent(String rawData){
        this.rawData = rawData;
    }

    @Override
    public String getRawData() {
        return rawData;
    }

    @Override
    public boolean hasLegislator() {
        return getRawLegislatorName() != null;
    }

    @Override
    public boolean isSponsorship() {
        return BillActionType.SPONSOR.equals(getBillActionType());
    }

    @Override
    public boolean isChiefSponsorship() {
        return BillActionType.CHIEF_SPONSOR.equals(getBillActionType());
    }

    @Override
    public boolean isVote() {
        return BillActionType.VOTE.equals(getBillActionType());
    }

    @Override
    public boolean hasCommittee() {
        return getRawCommitteeName() != null;
    }

    @Override
    public String getRawCommitteeName() {
        return null;
    }

    @Override
    public String getRawLegislatorName() {
        return null;
    }
}
