package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.RawBillEvent;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestBillEventParser {

    private RawBillEvent newRawBillEvent(String rawContents) {
        return new RawBillEvent(LocalDate.now(), Chamber.House, rawContents, "");
    }

    @Test
    public void testEventsFound_Senate889() {
        InputStream inputStream = this.getClass().getResourceAsStream(TestBillHtmlParser.SenateBill889BaseFileName);
        BillHtmlParser parser = new BillHtmlParser(inputStream, TestBillHtmlParser.SenateBill889BaseUrl);

        List<RawBillEvent> events = parser.getRawBillEvents();

        Assert.assertEquals(59, events.size());

        int missing = 0;
        BillEventParser billEventParser = new BillEventParser();
        for (RawBillEvent billEvent : events) {
            BillEvent billEventData = billEventParser.parse(billEvent);
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

        List<RawBillEvent> events = parser.getRawBillEvents();

        Assert.assertEquals(137, events.size());

        int missing = 0;
        BillEventParser billEventParser = new BillEventParser();
        for (RawBillEvent billEvent : events) {
            BillEvent billEventData = billEventParser.parse(billEvent);
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

        List<RawBillEvent> events = billHtmlParser.getRawBillEvents();

        List<String> sponsorNames = new ArrayList<>();
        BillEventParser billEventParser = new BillEventParser();
        for (RawBillEvent billEvent : events) {
            BillEvent billEventData = billEventParser.parse(billEvent);
            if (billEventData == null) {
                continue;
            }
            if (BillActionType.isSponsoringEvent(billEventData.getBillActionType())) {
                sponsorNames.add(billEventData.getRawLegislatorName());
            }
        }

        List<String> expectedNames = billHtmlParser.getSponsorNames().getAllRawNames();
        for (String expectedName : expectedNames) {
            Assert.assertTrue("A missing " + expectedName, sponsorNames.contains(expectedName));
        }
        for (String sponsorName : sponsorNames) {
            Assert.assertTrue("B missing " + sponsorName, expectedNames.contains(sponsorName));
        }
        Assert.assertEquals(expectedNames.size(), sponsorNames.size());
    }

    @Test
    public void testAllSponsorEventsFound_SenateBill889() {
        InputStream inputStream = this.getClass().getResourceAsStream(TestBillHtmlParser.SenateBill889BaseFileName);
        BillHtmlParser billHtmlParser = new BillHtmlParser(inputStream, TestBillHtmlParser.SenateBill889BaseUrl);

        List<RawBillEvent> events = billHtmlParser.getRawBillEvents();

        List<String> sponsorNames = new ArrayList<>();
        BillEventParser billEventParser = new BillEventParser();
        for (RawBillEvent billEvent : events) {
            BillEvent billEventData = billEventParser.parse(billEvent);
            if (billEventData == null) {
                continue;
            }
            if (BillActionType.isSponsoringEvent(billEventData.getBillActionType())) {
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
        RawBillEvent billEvent = newRawBillEvent("Referred to Assignments");
        BillEventParser billEventParser = new BillEventParser();
        BillEvent billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasCommittee());
        Assert.assertEquals("Assignments", billEventData.getRawCommitteeName());
        Assert.assertEquals(BillActionType.COMMITTEE_REFERRAL, billEventData.getBillActionType());
    }

    @Test
    public void testParsesCommitteeNamesWithHyphens() {
        RawBillEvent billEvent = newRawBillEvent("Assigned to Judiciary - Civil Committee");
        BillEventParser billEventParser = new BillEventParser();
        BillEvent billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasCommittee());
        Assert.assertEquals("Judiciary - Civil Committee", billEventData.getRawCommitteeName());
        Assert.assertEquals(BillActionType.COMMITTEE_ASSIGNMENT, billEventData.getBillActionType());
    }


    @Test
    public void testParsesIntroducerFromFiledWithClerkAction() {
        RawBillEvent billEvent = newRawBillEvent("Filed with the Clerk by Rep. Christian L. Mitchell");
        BillEventParser billEventParser = new BillEventParser();
        BillEvent billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertTrue(billEventData.getBillActionType().equals(BillActionType.INTRODUCE));
        Assert.assertEquals("Christian L. Mitchell", billEventData.getRawLegislatorName());
    }

    @Test
    public void testParsesIntroducerFromFiledWithClerkActionWorksForSenators() {
        RawBillEvent billEvent = newRawBillEvent("Filed with Secretary by Sen. Toi W. Hutchinson");
        BillEventParser billEventParser = new BillEventParser();
        BillEvent billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertTrue(billEventData.getBillActionType().equals(BillActionType.INTRODUCE));
        Assert.assertEquals("Toi W. Hutchinson", billEventData.getRawLegislatorName());
    }

    @Test
    public void testParsesChiefCosponsor() {
        RawBillEvent billEvent = newRawBillEvent("Added Chief Co-Sponsor Rep. Camille Y. Lilly");

        BillEventParser billEventParser = new BillEventParser();
        BillEvent billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertTrue(billEventData.getBillActionType().equals(BillActionType.CHIEF_SPONSOR));
        Assert.assertEquals("Camille Y. Lilly", billEventData.getRawLegislatorName());
    }

    @Test
    public void testParsesAddedCosponsor() {
        RawBillEvent billEvent = newRawBillEvent("Added Co-Sponsor Rep. Barbara Flynn Currie");

        BillEventParser billEventParser = new BillEventParser();
        BillEvent billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertTrue(billEventData.getBillActionType().equals(BillActionType.SPONSOR));
        Assert.assertEquals("Barbara Flynn Currie", billEventData.getRawLegislatorName());
    }

    @Test
    public void testParsesChiefSenateSponsor() {
        RawBillEvent billEvent = newRawBillEvent("Chief Senate Sponsor Sen. Toi W. Hutchinson");

        BillEventParser billEventParser = new BillEventParser();
        BillEvent billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertTrue(billEventData.getBillActionType().equals(BillActionType.CHIEF_SPONSOR));
        Assert.assertEquals("Toi W. Hutchinson", billEventData.getRawLegislatorName());
    }

    @Test
    public void testParsesCommitteeAmendment() {
        RawBillEvent billEvent = newRawBillEvent("Senate Committee Amendment No. 1 Filed with Secretary by Sen. Toi W. Hutchinson");

        BillEventParser billEventParser = new BillEventParser();
        BillEvent billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertFalse(billEventData.hasCommittee());
        Assert.assertEquals("Toi W. Hutchinson", billEventData.getRawLegislatorName());
    }

    @Test
    public void testParsesCommitteeAmendment_WorksForHouse() {
        RawBillEvent billEvent = newRawBillEvent("House Committee Amendment No. 1 Filed with Clerk by Rep. Christian L. Mitchell");

        BillEventParser billEventParser = new BillEventParser();
        BillEvent billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEvent.getRawContents(), billEventData.hasLegislator());
        Assert.assertFalse(billEventData.hasCommittee());
        Assert.assertEquals("Christian L. Mitchell", billEventData.getRawLegislatorName());
    }

    @Test
    public void testRecognizesVoteEvents_Senate889() {
        InputStream inputStream = this.getClass().getResourceAsStream(TestBillHtmlParser.SenateBill889BaseFileName);
        BillHtmlParser parser = new BillHtmlParser(inputStream, TestBillHtmlParser.SenateBill889BaseUrl);

        List<RawBillEvent> events = parser.getRawBillEvents();

        Assert.assertEquals(59, events.size());

        int voteEventCount = 0;
        BillEventParser billEventParser = new BillEventParser();
        for (RawBillEvent billEvent : events) {
            BillEvent billEventData = billEventParser.parse(billEvent);
            if (billEventData.getBillActionType().equals(BillActionType.VOTE)) {
                voteEventCount++;
            }
        }
        Assert.assertEquals(6, voteEventCount);
    }


    @Test
    public void testRecognizesCommitteeVote() {
        RawBillEvent billEvent = newRawBillEvent("Recommends Do Pass Subcommittee/ Judiciary - Civil Committee; 003-000-000");
        BillEventParser billEventParser = new BillEventParser();
        BillEvent billEventData = billEventParser.parse(billEvent);

        Assert.assertEquals(BillActionType.VOTE, billEventData.getBillActionType());
        Assert.assertTrue(billEventData.hasCommittee());
        Assert.assertEquals("Subcommittee/ Judiciary - Civil Committee", billEventData.getRawCommitteeName());
    }

    @Test
    public void testRecognizesCommitteeVote_ExampleTwo() {
        RawBillEvent billEvent = newRawBillEvent("Do Pass as Amended / Short Debate Labor & Commerce Committee; 018-010-000");
        BillEventParser billEventParser = new BillEventParser();
        BillEvent billEventData = billEventParser.parse(billEvent);

        Assert.assertEquals(BillActionType.VOTE, billEventData.getBillActionType());
        Assert.assertTrue(billEventData.hasCommittee());
        Assert.assertEquals("Labor & Commerce Committee", billEventData.getRawCommitteeName());
    }


    @Test
    public void testRecognizesCommitteeVote_ExampleThree() {
        RawBillEvent billEvent = newRawBillEvent("House Floor Amendment No. 3 Recommends Be Adopted Labor & Commerce Committee; 017-004-000");
        BillEventParser billEventParser = new BillEventParser();
        BillEvent billEventData = billEventParser.parse(billEvent);

        Assert.assertEquals(BillActionType.VOTE, billEventData.getBillActionType());
        Assert.assertTrue(billEventData.hasCommittee());
        Assert.assertEquals("Labor & Commerce Committee", billEventData.getRawCommitteeName());
    }


    @Test
    public void testParsesSponsorshipRemoval(){
        RawBillEvent billEvent = newRawBillEvent("Removed Co-Sponsor Rep. Jerry Costello, II");
        BillEventParser billEventParser = new BillEventParser();
        BillEvent billEventData = billEventParser.parse(billEvent);

        Assert.assertEquals(BillActionType.REMOVE_SPONSOR, billEventData.getBillActionType());
        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertEquals("Jerry Costello, II", billEventData.getRawLegislatorName());
    }
}
