package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class TestBillIndexPageParser {

    @Test
    public void test_findSubIndexUrl_Senate_3622(){

        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_legislation_index.html");

        BillIndexPageParser parser = new BillIndexPageParser(inputStream,
                "http://www.ilga.gov/legislation/default.asp");

        String url = parser.findSubIndexUrl(Chamber.Senate, 3622L);

        Assert.assertEquals("http://www.ilga.gov/legislation/grplist.asp?num1=3601&num2=3627&DocTypeID=SB&GA=100&SessionId=91", url);
    }

    @Test
    public void test_findSubIndexUrl_House_155(){

        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_legislation_index.html");

        BillIndexPageParser parser = new BillIndexPageParser(inputStream,
                "http://www.ilga.gov/legislation/default.asp");

        String url = parser.findSubIndexUrl(Chamber.House, 155L);

        Assert.assertEquals("http://www.ilga.gov/legislation/grplist.asp?num1=101&num2=200&DocTypeID=HB&GA=100&SessionId=91", url);
    }

}
