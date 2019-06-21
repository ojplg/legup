package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class SponsorSaveResults {

    public static final SponsorSaveResults EMPTY = new SponsorSaveResults(null, null, 0, 0);

    private final Legislator chiefHouseSponsor;
    private final Legislator chiefSenateSponsor;
    private final int houseSponsorCount;
    private final int senateSponsorCount;

}
