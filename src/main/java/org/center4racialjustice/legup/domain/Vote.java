package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class Vote implements Identifiable {

    private Long id;
    private Bill bill;
    private Legislator legislator;
    private VoteSide voteSide;

}
