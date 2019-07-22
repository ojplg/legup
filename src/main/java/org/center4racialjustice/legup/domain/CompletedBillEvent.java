package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.service.PersistableAction;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class CompletedBillEvent implements PersistableAction {

    private final BillEvent billEvent;
    private final Legislator legislator;
    private final Committee committee;

    private final String error;

    public static CompletedBillEvent forLegislator(BillEvent billEvent, Legislator legislator){
        if( legislator == null ){
            return new CompletedBillEvent(billEvent, "Could not match: " + billEvent.legislatorToString());
        } else {
            return new CompletedBillEvent(billEvent, legislator);
        }
    }

    public static CompletedBillEvent forCommittee(BillEvent billEvent, Committee committee){
        if( committee == null ){
            return new CompletedBillEvent(billEvent, "Could not match: " + billEvent.committeeToString());
        } else {
            return new CompletedBillEvent(billEvent, committee);
        }
    }

    public static CompletedBillEvent forStandalone(BillEvent billEvent){
        return new CompletedBillEvent(billEvent);
    }

    private CompletedBillEvent(BillEvent billEvent){
        this.billEvent = billEvent;
        this.legislator = null;
        this.committee = null;
        this.error = null;
    }

    private CompletedBillEvent(BillEvent billEvent, String error){
        this.billEvent = billEvent;
        this.error = error;
        this.legislator = null;
        this.committee = null;
    }

    private CompletedBillEvent(BillEvent billEvent, Legislator legislator) {
        this.billEvent = billEvent;
        this.legislator = legislator;
        this.committee = null;
        this.error = null;
    }

    private CompletedBillEvent(BillEvent billEvent, Committee committee) {
        this.billEvent = billEvent;
        this.legislator = null;
        this.committee = committee;
        this.error = null;
    }

    public BillEvent getBillEvent() {
        return billEvent;
    }

    public Legislator getLegislator() {
        return legislator;
    }

    public Committee getCommittee() {
        return committee;
    }

    public BillActionType getBillActionType() {
        return billEvent.getBillActionType();
    }

    public String getRawData() {
        return billEvent.getRawContents();
    }

    public LocalDate getDate() {
        return billEvent.getDate();
    }

    public Chamber getChamber() {
        return billEvent.getChamber();
    }

    public String getLink() {
        return billEvent.getLink();
    }

    public BillEventKey generateEventKey() {
        return billEvent.generateEventKey();
    }

    public boolean hasLegislator() {
        return billEvent.hasLegislator();
    }

    public String getRawLegislatorName() {
        return billEvent.getRawLegislatorName();
    }

    public Name getParsedLegislatorName() {
        return billEvent.getParsedLegislatorName();
    }

    public String getLegislatorMemberID() {
        return billEvent.getLegislatorMemberID();
    }

    public boolean isSponsorship() {
        return billEvent.getBillActionType() == BillActionType.SPONSOR;
    }

    public boolean isChiefSponsorship() {
        return billEvent.getBillActionType() == BillActionType.CHIEF_SPONSOR;
    }

    public boolean isVote() {
        return billEvent.getBillActionType() == BillActionType.VOTE;
    }

    public boolean hasCommittee() {
        return billEvent.hasCommittee();
    }

    public String getRawCommitteeName() {
        return billEvent.getRawCommitteeName();
    }

    public String getCommitteeID() {
        return billEvent.getCommitteeID();
    }

    @Override
    public String getDisplay() {
        return toString();
    }

    @Override
    public List<String> getErrors() {
        if ( error == null ){
            return Collections.emptyList();
        } else {
            return Collections.singletonList(error);
        }
    }

    @Override
    public String toString() {
        return "CompletedBillEvent{" +
                "billEvent=" + billEvent +
                ", legislator=" + legislator +
                ", committee=" + committee +
                '}';
    }
}
