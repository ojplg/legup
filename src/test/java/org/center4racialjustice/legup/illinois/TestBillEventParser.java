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

    @Test
    public void testEventsFound(){
        InputStream inputStream = this.getClass().getResourceAsStream(TestBillHtmlParser.SenateBill889BaseFileName);
        BillHtmlParser parser = new BillHtmlParser(inputStream, TestBillHtmlParser.SenateBill889BaseUrl);

        List<BillEvent> events = parser.getBillEvents();

        Assert.assertEquals(59, events.size());

        BillEventParser billEventParser = new BillEventParser();
        for(BillEvent billEvent : events){
            BillEventData billEventData = billEventParser.parse(billEvent);
            if( billEventData.getBillActionType().equals(BillActionType.UNCLASSIFIED)) {
                System.out.println(billEvent.getRawContents());
            }
        }
    }

    @Test
    public void testAllSponsorEventsFound_HouseBill2771(){
        InputStream inputStream = this.getClass().getResourceAsStream(TestBillHtmlParser.HouseBill2771FileName);
        BillHtmlParser billHtmlParser = new BillHtmlParser(inputStream, TestBillHtmlParser.HouseBill2771BaseUrl);

        List<BillEvent> events = billHtmlParser.getBillEvents();

        List<String> sponsorNames = new ArrayList<>();
        BillEventParser billEventParser = new BillEventParser();
        for(BillEvent billEvent : events){
            BillEventData billEventData = billEventParser.parse(billEvent);
            if( billEventData == null){
                continue;
            }
            if( billEventData.isSponsorship() || billEventData.isChiefSponsorship() ){
                sponsorNames.add(billEventData.getRawLegislatorName());
            }
        }

        List<String> expectedNames = billHtmlParser.getSponsorNames().getAllRawNames();
        for(String expectedName : expectedNames){
            Assert.assertTrue("missing " + expectedName, sponsorNames.contains(expectedName));
        }
        Assert.assertEquals(expectedNames.size(), sponsorNames.size());
    }

    @Test
    public void testAllSponsorEventsFound_SenateBill889(){
        InputStream inputStream = this.getClass().getResourceAsStream(TestBillHtmlParser.SenateBill889BaseFileName);
        BillHtmlParser billHtmlParser = new BillHtmlParser(inputStream, TestBillHtmlParser.SenateBill889BaseUrl);

        List<BillEvent> events = billHtmlParser.getBillEvents();

        List<String> sponsorNames = new ArrayList<>();
        BillEventParser billEventParser = new BillEventParser();
        for(BillEvent billEvent : events){
            BillEventData billEventData = billEventParser.parse(billEvent);
            if( billEventData == null){
                continue;
            }
            if( billEventData.isSponsorship() || billEventData.isChiefSponsorship() ){
                sponsorNames.add(billEventData.getRawLegislatorName());
            }
        }

        List<String> expectedNames = billHtmlParser.getSponsorNames().getAllRawNames();
        for(String expectedName : expectedNames){
            // This is more complicated since she was removed as a sponsor and then re-added
            if( expectedName.equals("Juliana Stratton")){
                continue;
            }
            Assert.assertTrue("missing " + expectedName, sponsorNames.contains(expectedName));
        }
    }


    @Test
    public void testParsesChiefSponsorFromFiledWithClerkAction(){
        BillEvent billEvent = newBillEvent("Filed with the Clerk by Rep. Christian L. Mitchell");
        BillEventParser billEventParser = new BillEventParser();
        BillEventData billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertTrue(billEventData.isChiefSponsorship());
        Assert.assertEquals("Christian L. Mitchell", billEventData.getRawLegislatorName());
    }

    @Test
    public void testParsesChiefSponsorFromFiledWithClerkActionWorksForSenators(){
        BillEvent billEvent = newBillEvent("Filed with Secretary by Sen. Toi W. Hutchinson");
        BillEventParser billEventParser = new BillEventParser();
        BillEventData billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertTrue(billEventData.isChiefSponsorship());
        Assert.assertEquals("Toi W. Hutchinson", billEventData.getRawLegislatorName());
    }


    @Test
    public void testParsesChiefCosponsor(){
        BillEvent billEvent = newBillEvent("Added Chief Co-Sponsor Rep. Camille Y. Lilly");

        BillEventParser billEventParser = new BillEventParser();
        BillEventData billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertTrue(billEventData.isChiefSponsorship());
        Assert.assertEquals("Camille Y. Lilly", billEventData.getRawLegislatorName());
    }

    @Test
    public void testParsesAddedCosponsor(){
        BillEvent billEvent = newBillEvent("Added Co-Sponsor Rep. Barbara Flynn Currie");

        BillEventParser billEventParser = new BillEventParser();
        BillEventData billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertTrue(billEventData.isSponsorship());
        Assert.assertEquals("Barbara Flynn Currie", billEventData.getRawLegislatorName());
    }

    @Test
    public void testParsesChiefSenateSponsor(){
        BillEvent billEvent = newBillEvent("Chief Senate Sponsor Sen. Toi W. Hutchinson");

        BillEventParser billEventParser = new BillEventParser();
        BillEventData billEventData = billEventParser.parse(billEvent);

        Assert.assertTrue(billEventData.hasLegislator());
        Assert.assertTrue(billEventData.isChiefSponsorship());
        Assert.assertEquals("Toi W. Hutchinson", billEventData.getRawLegislatorName());
    }

    private BillEvent newBillEvent(String rawContents){
        return new BillEvent(LocalDate.now(), Chamber.House, rawContents, "");
    }
}
