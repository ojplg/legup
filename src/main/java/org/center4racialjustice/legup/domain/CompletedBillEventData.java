package org.center4racialjustice.legup.domain;

import java.time.LocalDate;

public class CompletedBillEventData implements BillEventData {

    private final BillEvent billEventData;
    private final Legislator legislator;
    private final Committee committee;

    public CompletedBillEventData(BillEvent billEventData, Legislator legislator, Committee committee) {
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

    @Override
    public BillActionType getBillActionType() {
        return billEventData.getBillActionType();
    }

    @Override
    public String getRawData() {
        return billEventData.getRawContents();
    }

    @Override
    public LocalDate getDate() {
        return billEventData.getDate();
    }

    @Override
    public Chamber getChamber() {
        return billEventData.getChamber();
    }

    @Override
    public String getLink() {
        return billEventData.getLink();
    }

    @Override
    public BillEventKey generateEventKey() {
        return billEventData.generateEventKey();
    }

    @Override
    public boolean hasLegislator() {
        return billEventData.hasLegislator();
    }

    @Override
    public String getRawLegislatorName() {
        return billEventData.getRawLegislatorName();
    }

    @Override
    public Name getParsedLegislatorName() {
        return billEventData.getParsedLegislatorName();
    }

    @Override
    public String getLegislatorMemberID() {
        return billEventData.getLegislatorMemberID();
    }

    @Override
    public boolean isSponsorship() {
        return billEventData.getBillActionType() == BillActionType.SPONSOR;
    }

    @Override
    public boolean isChiefSponsorship() {

        return billEventData.getBillActionType() == BillActionType.CHIEF_SPONSOR;
    }

    @Override
    public boolean isVote() {
        return billEventData.getBillActionType() == BillActionType.VOTE;
    }

    @Override
    public boolean hasCommittee() {
        return billEventData.hasCommittee();
    }

    @Override
    public String getRawCommitteeName() {
        return billEventData.getRawCommitteeName();
    }

    @Override
    public String getCommitteeID() {
        return billEventData.getCommitteeID();
    }
}
