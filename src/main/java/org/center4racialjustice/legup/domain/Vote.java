package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class Vote implements Identifiable, Comparable<Vote> {

    private Long id;
    private Bill bill;
    private Legislator legislator;
    private VoteSide voteSide;
    private BillActionLoad billActionLoad;

    @Override
    public int compareTo(Vote that) {
        return this.legislator.compareTo(that.legislator);
    }
}
