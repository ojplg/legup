package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;

public class VoteBillEvent extends AbstractBillEvent implements VoteEventCounts {

    private final VoteEventCountExtractor voteCounts;

    public VoteBillEvent(BillEvent underlyingEvent) {
        super(underlyingEvent);
        this.voteCounts = new VoteEventCountExtractor(underlyingEvent.getRawContents());
    }

    @Override
    public BillActionType getBillActionType() {
        return BillActionType.VOTE;
    }

    @Override
    public int getYeaCount() {
        return voteCounts.getYeaCount();
    }

    @Override
    public int getNayCount() {
        return voteCounts.getNayCount();
    }

    @Override
    public int getOtherCount() {
        return voteCounts.getOtherCount();
    }
}
