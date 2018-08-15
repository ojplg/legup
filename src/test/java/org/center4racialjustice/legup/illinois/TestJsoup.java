package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Legislator;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestJsoup {

    @Test
    public void testParsingHouseMembers() {

        MemberHtmlParser parser = MemberHtmlParser.load("http://www.ilga.gov/house/default.asp");

        List<Legislator> names = parser.getNames();

        System.out.println(names);

        Assert.assertNotNull(names);

    }

}
