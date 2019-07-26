package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.Chamber;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestBillHtmlParser {

    static {
        System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");
    }

    public static String HouseBill2771BaseUrl =
            "http://www.ilga.gov/legislation/BillStatus.asp?DocNum=2771&GAID=14&DocTypeID=HB&LegId=104095&SessionID=91&GA=100";

    public static String HouseBill2771FileName =
            "/html/illinois_house_bill_2771.html";

    public static String SenateBill889BaseUrl =
            "http://www.ilga.gov/legislation/BillStatus.asp?DocNum=889&GAID=14&DocTypeID=SB&LegId=102981&SessionID=91&GA=100";

    public static String SenateBill889BaseFileName =
            "/html/illinois_senate_bill_889.html";

    public static String Bill_101House2040_FileName = "/html/illinois_101_house_2040.html";

    public static String Bill_101House2040_BaseUrl =
            "http://www.ilga.gov/legislation/BillStatus.asp?DocNum=2040&GAID=15&DocTypeID=HB&LegId=117547&SessionID=108&GA=101";

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

        List<String> sponsors = sponsorNames.getHouseSponsors().stream().map(SponsorName::getRawName).collect(Collectors.toList());

        Assert.assertEquals(46, sponsors.size());
        Assert.assertEquals("Christian L. Mitchell", sponsors.get(0));
        Assert.assertTrue(sponsors.contains("Barbara Flynn Currie"));
        Assert.assertEquals("Al Riley", sponsors.get(45));
    }

    @Test
    public void testFindSenateSponsors(){
        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_house_bill_2771.html");
        BillHtmlParser parser = new BillHtmlParser(inputStream, HouseBill2771BaseUrl);

        SponsorNames sponsorNames = parser.getSponsorNames();

        List<SponsorName> sponsorTuples = sponsorNames.getSenateSponsors();
        List<String> sponsors = sponsorTuples.stream().map(SponsorName::getRawName).collect(Collectors.toList());

        Assert.assertEquals(20, sponsors.size());
        Assert.assertTrue(sponsors.contains("Iris Y. Martinez"));
        Assert.assertTrue(sponsors.contains("Daniel Biss"));
    }

    @Test
    public void testFindBillEvents_HouseBill2771(){
        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_house_bill_2771.html");
        BillHtmlParser parser = new BillHtmlParser(inputStream, HouseBill2771BaseUrl);

        List<BillEvent> events = parser.getBillEvents();

        Assert.assertEquals(137, events.size());

    }

    @Test
    public void testFindBillEvents_SenateBill889(){
        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_senate_bill_889.html");
        BillHtmlParser parser = new BillHtmlParser(inputStream, SenateBill889BaseUrl);

        List<BillEvent> events = parser.getBillEvents();

        Assert.assertEquals(59, events.size());

    }


    @Test
    public void testHouseBill2771Parsing(){
        checkBill(houseBill2771(), HouseBill2771FileName, HouseBill2771BaseUrl);
    }

    @Test
    public void testSenateBill889Parsing(){
        checkBill(senateBill889(), SenateBill889BaseFileName, SenateBill889BaseUrl);
    }

    @Test
    public void testFindVoteEvents(){
        InputStream inputStream = this.getClass().getResourceAsStream(Bill_101House2040_FileName);
        BillHtmlParser parser = new BillHtmlParser(inputStream, Bill_101House2040_BaseUrl);

        List<BillEvent> voteEvents = new ArrayList<>();
        for(BillEvent event : parser.getBillEvents()){
            if( event.getBillActionType().equals(BillActionType.VOTE) ){
                voteEvents.add(event);
            }
        }

        Assert.assertEquals(6, voteEvents.size());
    }


    private String Raw_Html =
            "<html>"
            + "<body>"
            + "<table>"
            + "<tr>"
            + "<td class=\"content\" width=\"13%\" align=\"right\" valign=\"top\">&nbsp;&nbsp;2/19/2019</td>"
            + "</tr>"
            + "</table>"
            + "</body>"
            + "<html>";


    @Test
    public void testUnescaping(){

        Document document = Jsoup.parse(Raw_Html);

        Element cell = document.select("td").first();

        System.out.println(cell);

        System.out.println(cell.text());

        System.out.println(Parser.unescapeEntities(cell.text(), false));
    }
}
