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
        int scoreValue = legislatorBillActionType.scoreValue();
        if( LegislatorBillActionType.VOTE.equals(legislatorBillActionType)){
            if( voteSide.isUncommittedVote() ){
                return 0;
            } else {
                return preferredSide == voteSide ? scoreValue : -scoreValue;
            }
        } else {
            return VoteSide.Yea.equals(preferredSide) ? scoreValue : -scoreValue;
        }
    }

}
