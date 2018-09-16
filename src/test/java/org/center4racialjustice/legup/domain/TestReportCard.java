package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.util.LookupTable;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestReportCard {

    @Test
    public void testSimplestCalculation(){

        Bill bill = newBill(Chamber.Senate, 1L);
        Legislator legislator = newLegislator(Chamber.House, "Smith");
        ReportFactor factor = newFactor(bill, VoteSide.Yea);
        ReportCard card = newReportCard( factor );

        BillAction action = newVote(bill, legislator, VoteSide.Yea);

        LookupTable<Legislator,Bill,Integer> table = card.calculateScores(Collections.singletonList(action));

        Assert.assertEquals(1, (int) table.get(legislator, bill));
    }

    @Test
    public void testCalculationComplicatedIncludingNulls(){

        Bill b1 = newBill(Chamber.Senate, 1L);
        Bill b2 = newBill(Chamber.House, 2L);
        Bill b3 = newBill(Chamber.House, 3L);

        Legislator l1 = newLegislator(Chamber.House, "Apple");
        Legislator l2 = newLegislator(Chamber.House, "Banana");
        Legislator l3 = newLegislator(Chamber.House, "Cherry");

        BillAction ab11 = newChiefSponsor(b1, l1);
        BillAction ab12 = newSponsor(b1, l2);
        BillAction ab13 = newVote(b1, l1, VoteSide.Yea);
        BillAction ab14 = newVote(b1, l2, VoteSide.Yea);
        BillAction ab15 = newVote(b1, l3, VoteSide.Yea);

        BillAction ab21 = newChiefSponsor(b2, l1);
        BillAction ab22 = newVote(b2, l1, VoteSide.Yea);
        BillAction ab23 = newVote(b2, l2, VoteSide.Nay);

        BillAction ab31 = newChiefSponsor(b3, l2);
        BillAction ab32 = newSponsor(b3, l3);
        BillAction ab33 = newVote(b3, l1, VoteSide.Nay);
        BillAction ab34 = newVote(b3, l2, VoteSide.Yea);
        BillAction ab35 = newVote(b3, l3, VoteSide.Yea);

        ReportFactor f1 = newFactor(b1, VoteSide.Yea);
        ReportFactor f2 = newFactor(b2, VoteSide.Nay);
        ReportFactor f3 = newFactor(b3, VoteSide.Yea);

        ReportCard card = newReportCard(f1, f2, f3);

        List<BillAction> actionList = Arrays.asList(
                ab11, ab12, ab13, ab14, ab15,
                ab21, ab22, ab23,
                ab31, ab32, ab33, ab34, ab35
        );

        LookupTable<Legislator,Bill,Integer> table = card.calculateScores(actionList);
        Assert.assertEquals(4, (int) table.get(l1, b1));
        Assert.assertEquals(-4, (int) table.get(l1, b2));
        Assert.assertEquals(-1, (int) table.get(l1, b3));

        Assert.assertEquals(3, (int) table.get(l2, b1));
        Assert.assertEquals(1, (int) table.get(l2, b2));
        Assert.assertEquals(4, (int) table.get(l2, b3));

        Assert.assertEquals(1, (int) table.get(l3, b1));
        Assert.assertEquals(0, (int) table.get(l3, b2));
        Assert.assertEquals(3, (int) table.get(l3, b3));
    }

    private static long id = 1;

    private static BillAction newSponsor(Bill bill, Legislator legislator){
        BillAction action = new BillAction();
        action.setBillActionType(BillActionType.SPONSOR);
        action.setBill(bill);
        action.setLegislator(legislator);
        action.setId(nextId());
        return action;
    }

    private static BillAction newChiefSponsor(Bill bill, Legislator legislator){
        BillAction action = new BillAction();
        action.setBillActionType(BillActionType.CHIEF_SPONSOR);
        action.setBill(bill);
        action.setLegislator(legislator);
        action.setId(nextId());
        return action;
    }


    private static BillAction newVote(Bill bill, Legislator legislator, VoteSide voteSide){
        BillAction action = new BillAction();
        action.setBillActionType(BillActionType.VOTE);
        action.setBill(bill);
        action.setLegislator(legislator);
        action.setBillActionDetail(voteSide.getCode());
        action.setId(nextId());
        return action;
    }

    private static ReportCard newReportCard(ReportFactor ... factors){
        ReportCard card = new ReportCard();
        card.setReportFactors(Arrays.asList(factors));
        card.setId(nextId());
        return card;
    }

    private static ReportFactor newFactor(Bill bill, VoteSide voteSide){
        ReportFactor factor = new ReportFactor();
        factor.setBill(bill);
        factor.setVoteSide(voteSide);
        factor.setId(nextId());
        return factor;
    }

    private static Bill newBill(Chamber chamber, Long number){
        Bill bill = new Bill();
        bill.setNumber(number);
        bill.setChamber(chamber);
        bill.setShortDescription(chamber + "." + number);
        bill.setId(nextId());
        return bill;
    }

    private static Legislator newLegislator(Chamber chamber, String lastName){
        Legislator legislator = new Legislator();
        legislator.setId(nextId());
        legislator.setChamber(chamber);
        legislator.setLastName(lastName);
        return legislator;
    }

    private static long nextId(){
        return id++;
    }
}
