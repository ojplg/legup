package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class SponsorSaveResults {

    private final Legislator chiefHouseSponsor;
    private final Legislator chiefSenateSponsor;
    private final int houseSponsorCount;
    private final int senateSponsorCount;

}
