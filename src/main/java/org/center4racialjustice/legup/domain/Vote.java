package org.center4racialjustice.legup.domain;

import lombok.Data;

import java.util.Comparator;

@Data
public class Vote {

    public static final Comparator<Vote> ByLegislatorComparator = Comparator.comparing(Vote::getLegislator);
    public static final Comparator<Vote> ByBillComparator = Comparator.comparing(Vote::getBill);

    private Long id;
    private Bill bill;
    private Legislator legislator;
    private VoteSide voteSide;
    private BillActionLoad billActionLoad;

    public boolean matches(Chamber chamber, VoteSide voteSide){
        return voteSide.equals(this.voteSide) && chamber.equals(legislator.getChamber());
    }
}
