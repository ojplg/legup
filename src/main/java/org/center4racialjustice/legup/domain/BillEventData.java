package org.center4racialjustice.legup.domain;

import java.time.LocalDate;

public interface BillEventData {

    BillActionType getBillActionType();
    String getRawData();
    LocalDate getDate();
    Chamber getChamber();
    String getLink();
    BillEventKey generateEventKey();


    boolean hasLegislator();
    String getRawLegislatorName();
    Name getParsedLegislatorName();

    boolean isSponsorship();
    boolean isChiefSponsorship();
    boolean isVote();

    boolean hasCommittee();
    String getRawCommitteeName();
}
