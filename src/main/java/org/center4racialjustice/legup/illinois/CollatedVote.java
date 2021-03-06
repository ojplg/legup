package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.LegislatorBillAction;
import org.center4racialjustice.legup.domain.LegislatorBillActionType;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.domain.VoteSide;

@Data
public class CollatedVote {
    private final VoteSide voteSide;
    private final Legislator legislator;
    private final Name name;

    public Vote asVote(BillActionLoad billActionLoad){
        Vote vote = new Vote();
        vote.setBill(billActionLoad.getBill());
        vote.setBillActionLoad(billActionLoad);
        vote.setLegislator(legislator);
        vote.setVoteSide(voteSide);
        return vote;
    }

    public LegislatorBillAction asLegislatorBillAction(){
        LegislatorBillAction legislatorBillAction = new LegislatorBillAction();
        legislatorBillAction.setLegislator(getLegislator());
        legislatorBillAction.setVoteSide(getVoteSide());
        legislatorBillAction.setLegislatorBillActionType(LegislatorBillActionType.VOTE);
        return legislatorBillAction;
    }
}
