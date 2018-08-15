package org.center4racialjustice.legup.illinois;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestJsoup {

    @Test
    public void testParsingHouseMembers() {

        MemberHtmlParser parser = MemberHtmlParser.load("http://www.ilga.gov/house/default.asp");

        List<String> names = parser.getNames();

        System.out.println(names);

        Assert.assertNotNull(names);

    }

}
