package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.LegislatorBillAction;
import org.center4racialjustice.legup.domain.LegislatorBillActionType;
import org.center4racialjustice.legup.util.Dates;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestBillActionCollator {

    @Test
    public void testCorrectlyReportsSponsor(){

        Legislator legislator = new Legislator();
        legislator.setLastName("Brown");
        legislator.setFirstName("Charlie");
        legislator.setChamber(Chamber.House);

        LegislatorBillAction legislatorBillAction = new LegislatorBillAction();
        legislatorBillAction.setLegislatorBillActionType(LegislatorBillActionType.SPONSOR);
        legislatorBillAction.setLegislator(legislator);

        BillAction billAction = new BillAction();
        billAction.setBillActionType(BillActionType.SPONSOR);
        billAction.setLegislatorBillActions(Collections.singletonList(legislatorBillAction));
        billAction.setChamber(Chamber.House);
        billAction.setActionDate(Instant.now());

        BillActionCollator collator = new BillActionCollator(Collections.singletonList(billAction));

        List<Legislator> sponsors = collator.getSponsors(Chamber.House);

        Assert.assertEquals(1, sponsors.size());
        Assert.assertEquals("Brown", sponsors.get(0).getLastName());
    }

    @Test
    public void testCorrectlyHandlesRemovedSponsor(){

        Bill bill = new Bill();
        bill.setNumber(123L);
        bill.setChamber(Chamber.House);
        bill.setSession(100L);
        bill.setLegislationSubType("Bill");

        Legislator legislator = new Legislator();
        legislator.setLastName("Brown");
        legislator.setFirstName("Charlie");
        legislator.setChamber(Chamber.House);

        LegislatorBillAction sponsorLegislatorAction = new LegislatorBillAction();
        sponsorLegislatorAction.setLegislatorBillActionType(LegislatorBillActionType.SPONSOR);
        sponsorLegislatorAction.setLegislator(legislator);

        BillAction sponsorAction = new BillAction();
        sponsorAction.setBillActionType(BillActionType.SPONSOR);
        sponsorAction.setLegislatorBillActions(Collections.singletonList(sponsorLegislatorAction));
        sponsorAction.setChamber(Chamber.House);
        sponsorAction.setActionDate(Dates.instantOf(LocalDate.of(2019, 5, 23)));
        sponsorAction.setBill(bill);

        LegislatorBillAction sponsorRemoveLegislatorAction = new LegislatorBillAction();
        sponsorRemoveLegislatorAction.setLegislatorBillActionType(LegislatorBillActionType.REMOVE_SPONSOR);
        sponsorRemoveLegislatorAction.setLegislator(legislator);

        BillAction sponsorRemoveAction = new BillAction();
        sponsorRemoveAction.setBillActionType(BillActionType.REMOVE_SPONSOR);
        sponsorRemoveAction.setLegislatorBillActions(Collections.singletonList(sponsorLegislatorAction));
        sponsorRemoveAction.setChamber(Chamber.House);
        sponsorRemoveAction.setActionDate(Dates.instantOf(LocalDate.of(2019, 5, 28)));
        sponsorRemoveAction.setBill(bill);

        List<BillAction> actions = Arrays.asList(sponsorAction, sponsorRemoveAction);

        BillActionCollator collator = new BillActionCollator(actions);

        List<Legislator> sponsors = collator.getSponsors(Chamber.House);

        Assert.assertEquals(0, sponsors.size());
    }


}
