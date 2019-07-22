package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.util.Dates;

import java.time.Instant;
import java.time.LocalDate;

public class BillEvent {

    private final RawBillEvent rawBillEvent;
    private final BillActionType billActionType;
    private final BillEventLegislatorData billEventLegislatorData;
    private final BillEventCommitteeData billEventCommitteeData;

    public BillEvent(RawBillEvent rawBillEvent,
                     BillActionType billActionType,
                     BillEventLegislatorData billEventLegislatorData,
                     BillEventCommitteeData billEventCommitteeData) {
        this.rawBillEvent = rawBillEvent;
        this.billActionType = billActionType;
        this.billEventLegislatorData = billEventLegislatorData;
        this.billEventCommitteeData = billEventCommitteeData;
    }

    public LocalDate getDate(){
        return rawBillEvent.getDate();
    }

    public Chamber getChamber(){
        return rawBillEvent.getChamber();
    }

    public String getRawContents(){
        return rawBillEvent.getRawContents();
    }

    public String getLink(){
        return rawBillEvent.getLink();
    }

    public Instant getDateAsInstant(){
        return Dates.instantOf(rawBillEvent.getDate());
    }

    public BillEventKey generateEventKey(){
        return new BillEventKey(rawBillEvent.getDate(), rawBillEvent.getChamber(), rawBillEvent.getRawContents());
    }

    public BillActionType getBillActionType(){
        return billActionType;
    }

    public boolean hasLegislator() {
        return getRawLegislatorName() != null;
    }

    public String getRawLegislatorName(){
        return billEventLegislatorData.getRawLegislatorName();
    }

    public Name getParsedLegislatorName(){
        return billEventLegislatorData.getParsedLegislatorName();
    }

    public String getLegislatorMemberID(){
        return billEventLegislatorData.getLegislatorMemberId();
    }

    public boolean hasCommittee(){
        return getRawCommitteeName() != null;
    }

    public String getRawCommitteeName(){
        return billEventCommitteeData.getRawCommitteeName();
    }

    public String getCommitteeID(){
        return billEventCommitteeData.getCommitteeId();
    }

    public String legislatorToString(){
        return billEventLegislatorData.toString();
    }

    public String committeeToString(){
        return billEventCommitteeData.toString();
    }


    @Override
    public String toString() {
        return "BillEvent{" +
                "rawBillEvent=" + rawBillEvent +
                ", billActionType=" + billActionType +
                ", billEventLegislatorData=" + billEventLegislatorData +
                ", billEventCommitteeData=" + billEventCommitteeData +
                '}';
    }
}
