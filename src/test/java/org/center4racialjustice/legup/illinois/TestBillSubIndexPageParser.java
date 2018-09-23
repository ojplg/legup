package org.center4racialjustice.legup.illinois;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class TestBillSubIndexPageParser {

    @Test
    public void testFindSubIndexPage_Senate_123(){

        InputStream inputStream = this.getClass().getResourceAsStream("/html/senate_bills_101_200.html");

        BillSubIndexPageParser parser = new BillSubIndexPageParser(inputStream,
                "http://www.ilga.gov/legislation/grplist.asp?num1=101&num2=200&DocTypeID=SB&GA=100&SessionId=91");

        String url = parser.parseOutBillUrl(123);

        Assert.assertEquals("http://www.ilga.gov/legislation/BillStatus.asp?DocNum=123&GAID=14&DocTypeID=SB&LegId=100000&SessionID=91&GA=100", url);
    }

}
