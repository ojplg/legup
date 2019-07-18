package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;

public class VoteBillEvent extends AbstractBillEvent {

    public VoteBillEvent(String rawData) {
        super(rawData);
    }

    @Override
    public BillActionType getBillActionType() {
        return BillActionType.VOTE;
    }
}
