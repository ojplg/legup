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

    public int score(VoteSide preferredSide){
        int scoreValue = LegislatorBillActionType.scoreValue(legislatorBillActionType);
        if( LegislatorBillActionType.VOTE.equals(legislatorBillActionType)){
            if( voteSide == VoteSide.NotVoting || voteSide == VoteSide.Present ){
                return 0;
            } else {
                return preferredSide == voteSide ? scoreValue : -scoreValue;
            }
        } else if (LegislatorBillActionType.SPONSOR.equals(legislatorBillActionType)
                || LegislatorBillActionType.CHIEF_SPONSOR.equals(legislatorBillActionType)){
            return VoteSide.Yea.equals(preferredSide) ? scoreValue : -scoreValue;
        }
        throw new RuntimeException("No way to score " + legislatorBillActionType);
    }

}
