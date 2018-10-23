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

        String url = searcher.searchForBaseUrl(Chamber.Senate, 123L);

        Assert.assertEquals("http://www.ilga.gov/legislation/BillStatus.asp?DocNum=123&GAID=14&DocTypeID=SB&LegId=100000&SessionID=91&GA=100", url);
    }

}

