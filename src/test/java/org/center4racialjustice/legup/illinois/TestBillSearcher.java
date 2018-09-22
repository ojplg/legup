package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class TestBillSearcher {

    /*
    Senate 1
    http://www.ilga.gov/legislation/BillStatus.asp?DocNum=1&GAID=14&DocTypeID=SB&LegId=98844&SessionID=91&GA=100

    Senate 2
    http://www.ilga.gov/legislation/BillStatus.asp?DocNum=2&GAID=14&DocTypeID=SB&LegId=98845&SessionID=91&GA=100

    Senate 3
    http://www.ilga.gov/legislation/BillStatus.asp?DocNum=3&GAID=14&DocTypeID=SB&LegId=98846&SessionID=91&GA=100

    Senate 123
    http://www.ilga.gov/legislation/BillStatus.asp?DocNum=123&GAID=14&DocTypeID=SB&LegId=100000&SessionID=91&GA=100

    Senate 1710
    http://www.ilga.gov/legislation/BillStatus.asp?DocNum=1710&GAID=14&DocTypeID=SB&LegId=104566&SessionID=91&GA=100

    House 1109
    http://www.ilga.gov/legislation/BillStatus.asp?DocNum=1109&GAID=14&DocTypeID=HB&LegId=101586&SessionID=91&GA=100

   http://www.ilga.gov/legislation/votehistory.asp?DocNum=123&DocTypeID=SB&LegID=100000&GAID=14&SessionID=91&GA=100&SpecSess=

     */

    @Test
    public void findBillSubIndexPage_Senate_123(){

        BillSearcher searcher = new BillSearcher();

        String url = searcher.subIndexPageUrl(Chamber.Senate, 123L);

        Assert.assertEquals("http://www.ilga.gov/legislation/grplist.asp?num1=101&num2=200&DocTypeID=SB&GA=100&SessionId=91", url);
    }

}

