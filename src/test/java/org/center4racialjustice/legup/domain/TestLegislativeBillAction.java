package org.center4racialjustice.legup.domain;

import org.junit.Assert;
import org.junit.Test;

public class TestLegislativeBillAction {

    @Test
    public void testScoreCorrectVote(){
        LegislatorBillAction action = new LegislatorBillAction();
        action.setVoteSide(VoteSide.Yea);
        action.setLegislatorBillActionType(LegislatorBillActionType.VOTE);

        int score = action.score(VoteSide.Yea);

        Assert.assertEquals(1, score);
    }

    @Test
    public void testScoreCorrectVoteNo(){
        LegislatorBillAction action = new LegislatorBillAction();
        action.setVoteSide(VoteSide.Nay);
        action.setLegislatorBillActionType(LegislatorBillActionType.VOTE);

        int score = action.score(VoteSide.Nay);

        Assert.assertEquals(1, score);
    }


    @Test
    public void testScoreWrongVote(){
        LegislatorBillAction action = new LegislatorBillAction();
        action.setVoteSide(VoteSide.Yea);
        action.setLegislatorBillActionType(LegislatorBillActionType.VOTE);

        int score = action.score(VoteSide.Nay);

        Assert.assertEquals(-1, score);
    }

    @Test
    public void testScoreWrongVote_Yea(){
        LegislatorBillAction action = new LegislatorBillAction();
        action.setVoteSide(VoteSide.Nay);
        action.setLegislatorBillActionType(LegislatorBillActionType.VOTE);

        int score = action.score(VoteSide.Yea);

        Assert.assertEquals(-1, score);
    }

    @Test
    public void testScoreNotVoting(){
        LegislatorBillAction action = new LegislatorBillAction();
        action.setVoteSide(VoteSide.NotVoting);
        action.setLegislatorBillActionType(LegislatorBillActionType.VOTE);

        int score = action.score(VoteSide.Yea);

        Assert.assertEquals(0, score);
    }

    @Test
    public void testScorePresent(){
        LegislatorBillAction action = new LegislatorBillAction();
        action.setVoteSide(VoteSide.Present);
        action.setLegislatorBillActionType(LegislatorBillActionType.VOTE);

        int score = action.score(VoteSide.Yea);

        Assert.assertEquals(0, score);
    }

}
