package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.junit.Assert;
import org.junit.Test;

public class TestBillSearcher {

    static {
        System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");
    }

    @Test
    public void testSearchForBaseUrl_Senate_123(){

        BillSearcher searcher = new BillSearcher();

        String url = searcher.searchForBaseUrl(LegislationType.SENATE_BILL, 123L);

        Assert.assertEquals("http://www.ilga.gov/legislation/BillStatus.asp?DocNum=123&GAID=15&DocTypeID=SB&LegId=115219&SessionID=108&GA=101", url);
    }

}

