package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TestBillVotesListParser {

    public static final String VOTES_LIST_HTML_100_HOUSE_2771_FILE =
            "/html/illinois_house_bill_2771_votes_list.html";
    public static final String VOTES_LIST_HTML_100_HOUSE_2771_URL =
            "http://www.ilga.gov/legislation/votehistory.asp?DocNum=2771&GAID=14&DocTypeID=HB&LegId=104095&SessionID=91&GA=100";
    public static final String VOTES_LIST_HTML_100_HOUSE_3115_FILE =
            "/html/illinois_house_bill_3115_votes_list.html";
    public static final String VOTES_LIST_HTML_100_HOUSE_3115_URL =
            "http://www.ilga.gov/legislation/votehistory.asp?DocNum=3115&GAID=14&DocTypeID=HB&LegId=104721&SessionID=91&GA=100";

    public static final String VOTES_LIST_HTML_101_HOUSE_2040_FILE =
            "/html/illinois_101_house_2040_votes_list.html";
    public static final String VOTES_LIST_HTML_101_HOUSE_2040_URL =
            "http://www.ilga.gov/legislation/votehistory.asp?DocNum=2040&DocTypeID=HB&LegID=117547&GAID=15&SessionID=108&GA=101&SpecSess=";


    static {
        System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");
    }

    @Test
    public void findBillVotesUrls_House_2771() {
        InputStream inputStream = this.getClass().getResourceAsStream(VOTES_LIST_HTML_100_HOUSE_2771_FILE);
        BillVotesListParser billVotesListParser = new BillVotesListParser(inputStream, VOTES_LIST_HTML_100_HOUSE_2771_URL);
        Map<String, String> votePdfsUrls = billVotesListParser.grabVotesUrls();

        Assert.assertEquals(6, votePdfsUrls.size());
        Assert.assertTrue(votePdfsUrls.containsValue("http://www.ilga.gov/legislation/votehistory/100/house/10000HB2771sam001_05312018_028000C.pdf"));
    }


    @Test
    public void findBillVotesUrls_House_3115() {
        InputStream inputStream = this.getClass().getResourceAsStream(VOTES_LIST_HTML_100_HOUSE_3115_FILE);
        BillVotesListParser billVotesListParser = new BillVotesListParser(inputStream, VOTES_LIST_HTML_100_HOUSE_3115_URL);
        Map<String, String> votePdfsUrls = billVotesListParser.grabVotesUrls();

        Assert.assertNotNull(votePdfsUrls);
        Assert.assertTrue(votePdfsUrls.isEmpty());
    }

    @Test
    public void findBillVotesUrls_House_2771_Committee() {
        InputStream inputStream = this.getClass().getResourceAsStream(VOTES_LIST_HTML_100_HOUSE_2771_FILE);
        BillVotesListParser billVotesListParser = new BillVotesListParser(inputStream, VOTES_LIST_HTML_100_HOUSE_2771_URL);
        Map<String, String> votePdfsUrls = billVotesListParser.grabCommitteeVotesUrls();

        Assert.assertEquals(4, votePdfsUrls.size());
        Assert.assertTrue(votePdfsUrls.containsValue("http://www.ilga.gov/legislation/votehistory/100/senate/committeevotes/10000HB2771SFA2_19093.pdf"));
    }

    @Test
    public void testFullParse(){
        InputStream inputStream = this.getClass().getResourceAsStream(VOTES_LIST_HTML_100_HOUSE_2771_FILE);
        BillVotesListParser billVotesListParser = new BillVotesListParser(inputStream, VOTES_LIST_HTML_100_HOUSE_2771_URL);

        List<VoteLinkInfo> voteLinkInfos = billVotesListParser.parseVoteLinks();
        Assert.assertEquals(10, voteLinkInfos.size());
    }

    @Test
    public void testFirstVoteLinkInfo(){
        InputStream inputStream = this.getClass().getResourceAsStream(VOTES_LIST_HTML_100_HOUSE_2771_FILE);
        BillVotesListParser billVotesListParser = new BillVotesListParser(inputStream, VOTES_LIST_HTML_100_HOUSE_2771_URL);

        List<VoteLinkInfo> voteLinkInfos = billVotesListParser.parseVoteLinks();
        VoteLinkInfo firstInfo = voteLinkInfos.get(0);
        Assert.assertEquals("SCA0001", firstInfo.getCode());
        Assert.assertEquals(LocalDate.of(2018, 5, 31), firstInfo.getVoteDate());
        Assert.assertEquals("Concurrence", firstInfo.getVoteDescription());
        Assert.assertEquals(Chamber.House, firstInfo.getChamber());
        Assert.assertFalse(firstInfo.isCommittee());
        Assert.assertEquals("http://www.ilga.gov/legislation/votehistory/100/house/10000HB2771sam001_05312018_028000C.pdf",
                firstInfo.getPdfUrl());
    }

    @Test
    public void testFullParse_101_House_2040(){
        InputStream inputStream = this.getClass().getResourceAsStream(VOTES_LIST_HTML_101_HOUSE_2040_FILE);
        BillVotesListParser billVotesListParser = new BillVotesListParser(inputStream, VOTES_LIST_HTML_101_HOUSE_2040_URL);

        List<VoteLinkInfo> voteLinkInfos = billVotesListParser.parseVoteLinks();
        Assert.assertEquals(6, voteLinkInfos.size());
    }

}
