package org.center4racialjustice.legup.domain;

public interface BillEventData {

    BillActionType getBillActionType();
    String getRawData();

    boolean hasLegislator();
    String getRawLegislatorName();

    boolean isSponsorship();
    boolean isChiefSponsorship();
    boolean isVote();

}
