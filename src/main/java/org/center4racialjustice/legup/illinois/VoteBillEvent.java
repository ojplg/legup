package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;

public class VoteBillEvent extends AbstractBillEvent {

    public VoteBillEvent(BillEvent underlyingEvent) {
        super(underlyingEvent);
    }

    @Override
    public BillActionType getBillActionType() {
        return BillActionType.VOTE;
    }
}
