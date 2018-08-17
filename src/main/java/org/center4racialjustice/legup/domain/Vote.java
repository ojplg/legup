package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class Vote implements Identifiable {
    private Long id;
    //TODO: Figure out joins and all, but not yet
    private long billId;
    private long legislatorId;
    private String voteSide;
}
