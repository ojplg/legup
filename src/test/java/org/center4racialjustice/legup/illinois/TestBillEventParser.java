package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventData;
import org.center4racialjustice.legup.domain.Chamber;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestBillEventParser {

    private BillEvent newBillEvent(String rawContents) {
        return new BillEvent(LocalDate.now(), Chamber.House, rawContents, "");
    }

    @Test
    public void testEventsFound_Senate889() {
        InputStream inputStream = this.getClass().getResourceAsStream(TestBillHtmlParser.SenateBill889BaseFileName);
        BillHtmlParser parser = new BillHtmlParser(inputStream, TestBillHtmlParser.SenateBill889BaseUrl);

        List<BillEvent> events = parser.getBillEvents();

        Assert.assertEquals(59, events.size());

        int missing = 0;
        BillEventParser billEventParser = new BillEventParser();
        for (BillEvent billEvent : events) {
            BillEventData billEventData = billEventParser.parse(billEvent);
            if (billEventData.getBillActionType().equals(BillActionType.UNCLASSIFIED)) {
                System.out.println(billEvent.getRawContents());
                missing++;
            }
        }
        Assert.assertTrue(missing < 30);
    }


    @Test
    public void testEventsFound_House2771() {
        InputStream inputStream = this.getClass().getResourceAsStream(TestBillHtmlParser.HouseBill2771FileName);
        BillHtmlParser parser = new BillHtmlParser(inputStream, TestBillHtmlParser.HouseBill2771BaseUrl);

        List<BillEvent> events = parser.getBillEvents();

        Assert.assertEquals(137, events.size());

        int missing = 0;
        BillEventParser billEventParser = new BillEventParser();
        for (BillEvent billEvent : events) {
            BillEventData billEventData = billEventParser.parse(billEvent);
            if (billEventData.getBillActionType().equals(BillActionType.UNCLASSIFIED)) {
                System.out.println(billEvent.getRawContents());
                missing++;
            }
        }
        Assert.assertTrue(missing < 75);
    }


    @Test
    public void testAllSponsorEventsFound_HouseBill2771() {
        InputStream inputStream = this.getClass().getResourceAsStream(TestBillHtmlParser.HouseBill2771FileName);
        BillHtmlParser billHtmlParser = new BillHtmlParser(inputStream, TestBillHtmlParser.HouseBill2771BaseUrl);

        List<BillEvent> events = billHtmlParser.getBillEvents();

        List<String> sponsorNames = new ArrayList<>();
        BillEventParser billEventParser = new BillEventParser();
        for (BillEvent billEvent : events) {
            BillEventData billEventData = billEventParser.parse(billEvent);
            if (billEventData == null) {
                continue;
            }
            if (billEventData.isSponsorship() || billEventData.isChiefSponsorship()) {
                sponsorNames.add(billEventData.getRawLegislatorName());
            }
        }

        List<String> expectedNames = billHtmlParser.getSponsorNames().getAllRawNames();
        for (String expectedName : expectedNames) {
            Assert.assertTrue("missing " + expectedName, sponsorNames.contains(expectedName));
        }
        Assert.assertEquals(expectedNames.size(), sponsorNames.size());
    }

    @Test
    public void testAllSponsorEventsFound_SenateBill889() {
        InputStream inputStream = this.getClass().getResourceAsStream(TestBillHtmlParser.SenateBill889BaseFileName);
        BillHtmlParser billHtmlParser = new BillHtmlParser(inputStream, TestBillHtmlParser.SenateBill889BaseUrl);

        List<BillEvent> events = billHtmlParser.getBillEvents();

        List<String> sponsorNames = new ArrayList<>();
        BillEventParser billEventParser = new BillEventParser();
        for (BillEvent billEvent : events) {
            BillEventData billEventData = billEventParser.parse(billEvent);
            if (billEventData == null) {
                continue;
            }
            if (billEventData.isSponsorship() || billEventData.isChiefSponsorship()) {
                sponsorNames.add(billEventData.getRawLegislatorName());
            }
        }

        List<String> expectedNames = billHtmlParser.getSponsorNames().getAllRawNames();
        for (String expectedName : expectedNames) {
            // This is more complicated since she was removed as a sponsor and then re-added
            if (expectedName.equals("Juliana Stratton")) {
                continue;
            }
            Assert.assertTrue("missing " + expectedName, sponsorNames.contains(expectedName));
        }
    }

    @Test
    public void testParsesCommitteeReferral() {
        BillEvent billEvent = newBillEvent("Referred to Assignments");
        BillEventParser billEventParser = new BillEventParser();
        BillEventData billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasCommittee());
        Assert.assertEquals("Assignments", billEventData.getRawCommitteeName());
        Assert.assertEquals(BillActionType.COMMITTEE_REFERRAL, billEventData.getBillActionType());
    }

    @Test
    public void testParsesCommitteeNamesWithHyphens() {
        BillEvent billEvent = newBillEvent("Assigned to Judiciary - Civil Committee");
        BillEventParser billEventParser = new BillEventParser();
        BillEventData billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasCommittee());
        Assert.assertEquals("Judiciary - Civil Committee", billEventData.getRawCommitteeName());
        Assert.assertEquals(BillActionType.COMMITTEE_ASSIGNMENT, billEventData.getBillActionType());
    }


    @Test
    public void testParsesChiefSponsorFromFiledWithClerkAction() {
        BillEvent billEvent = newBillEvent("Filed with the Clerk by Rep. Christian L. Mitchell");
        BillEventParser billEventParser = new BillEventParser();
        BillEventData billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertTrue(billEventData.isChiefSponsorship());
        Assert.assertEquals("Christian L. Mitchell", billEventData.getRawLegislatorName());
    }

    @Test
    public void testParsesChiefSponsorFromFiledWithClerkActionWorksForSenators() {
        BillEvent billEvent = newBillEvent("Filed with Secretary by Sen. Toi W. Hutchinson");
        BillEventParser billEventParser = new BillEventParser();
        BillEventData billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertTrue(billEventData.isChiefSponsorship());
        Assert.assertEquals("Toi W. Hutchinson", billEventData.getRawLegislatorName());
    }

    @Test
    public void testParsesChiefCosponsor() {
        BillEvent billEvent = newBillEvent("Added Chief Co-Sponsor Rep. Camille Y. Lilly");

        BillEventParser billEventParser = new BillEventParser();
        BillEventData billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertTrue(billEventData.isChiefSponsorship());
        Assert.assertEquals("Camille Y. Lilly", billEventData.getRawLegislatorName());
    }

    @Test
    public void testParsesAddedCosponsor() {
        BillEvent billEvent = newBillEvent("Added Co-Sponsor Rep. Barbara Flynn Currie");

        BillEventParser billEventParser = new BillEventParser();
        BillEventData billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertTrue(billEventData.isSponsorship());
        Assert.assertEquals("Barbara Flynn Currie", billEventData.getRawLegislatorName());
    }

    @Test
    public void testParsesChiefSenateSponsor() {
        BillEvent billEvent = newBillEvent("Chief Senate Sponsor Sen. Toi W. Hutchinson");

        BillEventParser billEventParser = new BillEventParser();
        BillEventData billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertTrue(billEventData.isChiefSponsorship());
        Assert.assertEquals("Toi W. Hutchinson", billEventData.getRawLegislatorName());
    }

    @Test
    public void testParsesCommitteeAmendment() {
        BillEvent billEvent = newBillEvent("Senate Committee Amendment No. 1 Filed with Secretary by Sen. Toi W. Hutchinson");

        BillEventParser billEventParser = new BillEventParser();
        BillEventData billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertFalse(billEventData.hasCommittee());
        Assert.assertEquals("Toi W. Hutchinson", billEventData.getRawLegislatorName());
    }

    @Test
    public void testParsesCommitteeAmendment_WorksForHouse() {
        BillEvent billEvent = newBillEvent("House Committee Amendment No. 1 Filed with Clerk by Rep. Christian L. Mitchell");

        BillEventParser billEventParser = new BillEventParser();
        BillEventData billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEvent.getRawContents(), billEventData.hasLegislator());
        Assert.assertFalse(billEventData.hasCommittee());
        Assert.assertEquals("Christian L. Mitchell", billEventData.getRawLegislatorName());
    }

    @Test
    public void testRecognizesVoteEvents_Senate889() {
        InputStream inputStream = this.getClass().getResourceAsStream(TestBillHtmlParser.SenateBill889BaseFileName);
        BillHtmlParser parser = new BillHtmlParser(inputStream, TestBillHtmlParser.SenateBill889BaseUrl);

        List<BillEvent> events = parser.getBillEvents();

        Assert.assertEquals(59, events.size());

        int voteEventCount = 0;
        BillEventParser billEventParser = new BillEventParser();
        for (BillEvent billEvent : events) {
            BillEventData billEventData = billEventParser.parse(billEvent);
            if (billEventData.isVote()) {
                System.out.println(billEventData.getRawData());
                voteEventCount++;
            }
        }
        Assert.assertEquals(6, voteEventCount);
    }


    @Test
    public void testRecognizesCommitteeVote() {
        BillEvent billEvent = newBillEvent("Recommends Do Pass Subcommittee/ Judiciary - Civil Committee; 003-000-000");
        BillEventParser billEventParser = new BillEventParser();
        BillEventData billEventData = billEventParser.parse(billEvent);

        Assert.assertEquals(BillActionType.VOTE, billEventData.getBillActionType());
        Assert.assertTrue(billEventData.hasCommittee());
        Assert.assertEquals("Subcommittee/ Judiciary - Civil Committee", billEventData.getRawCommitteeName());
    }
}
