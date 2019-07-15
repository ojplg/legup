package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class LegislatorBillAction {

    private Long id;
    private Legislator legislator;
    private LegislatorBillActionType legislatorBillActionType;
    private VoteSide voteSide;

    public boolean isVote(){
        return voteSide != null;
    }

}
