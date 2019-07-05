package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.Chamber;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class TestBillHtmlParser {

    static {
        System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");
    }

    private static String HouseBill2771BaseUrl =
            "http://www.ilga.gov/legislation/BillStatus.asp?DocNum=2771&GAID=14&DocTypeID=HB&LegId=104095&SessionID=91&GA=100";

    private static String HouseBill2771FileName =
            "/html/illinois_house_bill_2771.html";

    private static String SenateBill889BaseUrl =
            "http://www.ilga.gov/legislation/BillStatus.asp?DocNum=889&GAID=14&DocTypeID=SB&LegId=102981&SessionID=91&GA=100";

    private static String SenateBill889BaseFileName =
            "/html/illinois_senate_bill_889.html";

    private static Bill houseBill2771(){
        Bill bill = new Bill();
        bill.setShortDescription("HEALTHY WORKPLACE ACT");
        bill.setChamber(Chamber.House);
        bill.setNumber(2771L);
        bill.setSession(100L);
        return bill;
    }

    private static Bill senateBill889(){
        Bill bill = new Bill();
        bill.setShortDescription("JURIES-UNLAWFUL DISCRIMINATION");
        bill.setChamber(Chamber.Senate);
        bill.setNumber(889L);
        bill.setSession(100L);
        return bill;
    }

    private BillHtmlParser fromFileName(String fileName, String url){
        InputStream htmlStream = this.getClass().getResourceAsStream(fileName);
        return new BillHtmlParser(htmlStream, url);
    }

    private void checkBill(Bill expected, String fileName, String url){
        BillHtmlParser parser = fromFileName(fileName, url);
        Assert.assertEquals("Unmatched numbers", expected.getNumber(), parser.getNumber());
        Assert.assertEquals("Unmatched chambers", expected.getChamber(), parser.getChamber());
        Assert.assertEquals("Unmatched sessions", expected.getSession(), parser.getSession());
        Assert.assertEquals("Unmatched short description", expected.getShortDescription(), parser.getShortDescription());
    }

    @Test
    public void testFindHouseSponsors(){
        BillHtmlParser parser = fromFileName(HouseBill2771FileName, HouseBill2771BaseUrl);

        SponsorNames sponsorNames = parser.getSponsorNames();
        Assert.assertEquals("Christian L. Mitchell", sponsorNames.getChiefHouseSponsor().getRawName());

        List<String> sponsors = sponsorNames.getHouseSponsors().stream().map(SponsorName::getRawName).collect(Collectors.toList());

        Assert.assertEquals(45, sponsors.size());
        Assert.assertEquals("Camille Y. Lilly", sponsors.get(0));
        Assert.assertTrue(sponsors.contains("Barbara Flynn Currie"));
        Assert.assertEquals("Al Riley", sponsors.get(44));
    }

    @Test
    public void testFindSenateSponsors(){
        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_house_bill_2771.html");
        BillHtmlParser parser = new BillHtmlParser(inputStream, HouseBill2771BaseUrl);

        SponsorNames sponsorNames = parser.getSponsorNames();

        Assert.assertEquals("Toi W. Hutchinson", sponsorNames.getChiefSenateSponsor().getRawName());

        List<SponsorName> sponsorTuples = sponsorNames.getSenateSponsors();
        List<String> sponsors = sponsorTuples.stream().map(SponsorName::getRawName).collect(Collectors.toList());

        Assert.assertEquals(19, sponsors.size());
        Assert.assertEquals("Iris Y. Martinez", sponsors.get(0));
        Assert.assertTrue(sponsors.contains("Daniel Biss"));
        Assert.assertEquals("Cristina Castro", sponsors.get(18));
    }

    @Test
    public void testFindBillEvents(){
        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_house_bill_2771.html");
        BillHtmlParser parser = new BillHtmlParser(inputStream, HouseBill2771BaseUrl);

        List<BillEvent> events = parser.getBillEvents();

        Assert.assertEquals(137, events.size());

        int uncategorizedCount = 0;

        for(BillEvent event : events){
            BillEventParser billEventParser = new BillEventParser();
            BillActionType actionType = billEventParser.readActionType(event);

            if( actionType == null ){
                System.out.println("   " + event.getRawContents() );
                uncategorizedCount++;
            }

        }

        System.out.println("UNCATEGORIZED " + uncategorizedCount);
    }

    @Test
    public void testHouseBill2771Parsing(){
        checkBill(houseBill2771(), HouseBill2771FileName, HouseBill2771BaseUrl);
    }

    @Test
    public void testSenateBill889Parsing(){
        checkBill(senateBill889(), SenateBill889BaseFileName, SenateBill889BaseUrl);
    }
}
