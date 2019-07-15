package org.center4racialjustice.legup.domain;

import org.junit.Assert;
import org.junit.Test;

public class TestBillAction {

    @Test
    public void testScoreCorrectVote(){
        BillAction action = new BillAction();
//        action.setBillActionDetail(VoteSide.Yea.getCode());
        action.setBillActionType(BillActionType.VOTE);

        int score = action.score(VoteSide.Yea);

        Assert.assertEquals(1, score);
    }

    @Test
    public void testScoreCorrectVoteNo(){
        BillAction action = new BillAction();
//        action.setBillActionDetail(VoteSide.Nay.getCode());
        action.setBillActionType(BillActionType.VOTE);

        int score = action.score(VoteSide.Nay);

        Assert.assertEquals(1, score);
    }


    @Test
    public void testScoreWrongVote(){
        BillAction action = new BillAction();
//        action.setBillActionDetail(VoteSide.Yea.getCode());
        action.setBillActionType(BillActionType.VOTE);

        int score = action.score(VoteSide.Nay);

        Assert.assertEquals(-1, score);
    }

    @Test
    public void testScoreWrongVote_Yea(){
        BillAction action = new BillAction();
//        action.setBillActionDetail(VoteSide.Nay.getCode());
        action.setBillActionType(BillActionType.VOTE);

        int score = action.score(VoteSide.Yea);

        Assert.assertEquals(-1, score);
    }

    @Test
    public void testScoreNotVoting(){
        BillAction action = new BillAction();
//        action.setBillActionDetail(VoteSide.NotVoting.getCode());
        action.setBillActionType(BillActionType.VOTE);

        int score = action.score(VoteSide.Yea);

        Assert.assertEquals(0, score);
    }

    @Test
    public void testScorePresent(){
        BillAction action = new BillAction();
//        action.setBillActionDetail(VoteSide.Present.getCode());
        action.setBillActionType(BillActionType.VOTE);

        int score = action.score(VoteSide.Yea);

        Assert.assertEquals(0, score);
    }

}
