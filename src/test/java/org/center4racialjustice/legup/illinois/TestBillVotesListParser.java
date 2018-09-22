package org.center4racialjustice.legup.illinois;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.Map;

public class TestBillVotesListParser {

    @Test
    public void findBillVotesUrls_House_2771(){

        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_house_bill_2771_votes_list.html");
        String votesPageUrl = "http://www.ilga.gov/legislation/votehistory.asp?DocNum=2771&GAID=14&DocTypeID=HB&LegId=104095&SessionID=91&GA=100";

        BillVotesListParser billVotesListParser = new BillVotesListParser(inputStream, votesPageUrl);

        Map<String, String> votePdfsUrls = billVotesListParser.grabVotesUrls();

        Assert.assertEquals(6, votePdfsUrls.size());
        Assert.assertTrue( votePdfsUrls.containsValue("http://www.ilga.gov/legislation/votehistory/100/house/10000HB2771sam001_05312018_028000C.pdf") );
    }

}
