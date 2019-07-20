package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventData;
import org.center4racialjustice.legup.domain.BillEventKey;
import org.center4racialjustice.legup.domain.Chamber;

import java.time.LocalDate;

public abstract class AbstractBillEvent implements BillEventData {

    private final BillEvent underlyingEvent;

    public AbstractBillEvent(BillEvent underlyingEvent){
        this.underlyingEvent = underlyingEvent;
    }

    @Override
    public LocalDate getDate() {
        return underlyingEvent.getDate();
    }

    @Override
    public Chamber getChamber() {
        return underlyingEvent.getChamber();
    }

    @Override
    public String getLink() {
        return underlyingEvent.getLink();
    }

    @Override
    public String getRawData() {
        return underlyingEvent.getRawContents();
    }

    @Override
    public BillEventKey generateEventKey() {
        return underlyingEvent.generateEventKey();
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

    @Override
    public String toString() {
        return "AbstractBillEvent{" +
                "underlyingEvent=" + underlyingEvent +
                '}';
    }
}
