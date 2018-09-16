package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.util.LookupTable;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestGradeCalculator {

    @Test
    public void testSimplestCalculation(){

        Bill bill = newBill(Chamber.Senate, 1L);
        Legislator legislator = newLegislator(Chamber.House, "Smith");
        ReportFactor factor = newFactor(bill, VoteSide.Yea);
        ReportCard card = newReportCard(new ReportFactor[] { factor });

        BillAction action = new BillAction();
        action.setBillActionType(BillActionType.VOTE);
        action.setBill(bill);
        action.setLegislator(legislator);
        action.setBillActionDetail("Y");

        GradeCalculator calculator = new GradeCalculator(card, Collections.singletonList(legislator));

        Map<Bill, List<BillAction>> actionMap = new HashMap<>();
        actionMap.put(bill, Collections.singletonList(action));
        LookupTable<Legislator,Bill,Integer> table = calculator.calculate(actionMap);

        Assert.assertEquals(1, (int) table.get(legislator, bill));
    }

    private static long id = 1;

    private static ReportCard newReportCard(ReportFactor[] factors){
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
