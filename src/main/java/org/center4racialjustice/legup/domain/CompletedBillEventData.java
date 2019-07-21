package org.center4racialjustice.legup.domain;

import java.time.LocalDate;

public class CompletedBillEventData implements BillEventData {

    private final BillEventData billEventData;
    private final Legislator legislator;
    private final Committee committee;

    public CompletedBillEventData(BillEventData billEventData, Legislator legislator, Committee committee) {
        this.billEventData = billEventData;
        this.legislator = legislator;
        this.committee = committee;
    }

    public BillEventData getBillEventData() {
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
        return billEventData.getRawData();
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
        return billEventData.isSponsorship();
    }

    @Override
    public boolean isChiefSponsorship() {
        return billEventData.isChiefSponsorship();
    }

    @Override
    public boolean isVote() {
        return billEventData.isVote();
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
