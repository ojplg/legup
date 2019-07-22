package org.center4racialjustice.legup.domain;

import java.time.LocalDate;

public class CompletedBillEvent {

    private final BillEvent billEventData;
    private final Legislator legislator;
    private final Committee committee;

    public CompletedBillEvent(BillEvent billEventData, Legislator legislator, Committee committee) {
        this.billEventData = billEventData;
        this.legislator = legislator;
        this.committee = committee;
    }

    public BillEvent getBillEventData() {
        return billEventData;
    }

    public Legislator getLegislator() {
        return legislator;
    }

    public Committee getCommittee() {
        return committee;
    }

    public BillActionType getBillActionType() {
        return billEventData.getBillActionType();
    }

    public String getRawData() {
        return billEventData.getRawContents();
    }

    public LocalDate getDate() {
        return billEventData.getDate();
    }

    public Chamber getChamber() {
        return billEventData.getChamber();
    }

    public String getLink() {
        return billEventData.getLink();
    }

    public BillEventKey generateEventKey() {
        return billEventData.generateEventKey();
    }

    public boolean hasLegislator() {
        return billEventData.hasLegislator();
    }

    public String getRawLegislatorName() {
        return billEventData.getRawLegislatorName();
    }

    public Name getParsedLegislatorName() {
        return billEventData.getParsedLegislatorName();
    }

    public String getLegislatorMemberID() {
        return billEventData.getLegislatorMemberID();
    }

    public boolean isSponsorship() {
        return billEventData.getBillActionType() == BillActionType.SPONSOR;
    }

    public boolean isChiefSponsorship() {
        return billEventData.getBillActionType() == BillActionType.CHIEF_SPONSOR;
    }

    public boolean isVote() {
        return billEventData.getBillActionType() == BillActionType.VOTE;
    }

    public boolean hasCommittee() {
        return billEventData.hasCommittee();
    }

    public String getRawCommitteeName() {
        return billEventData.getRawCommitteeName();
    }

    public String getCommitteeID() {
        return billEventData.getCommitteeID();
    }
}
