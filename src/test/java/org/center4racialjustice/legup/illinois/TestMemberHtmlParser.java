package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestMemberHtmlParser {

    @Test
    public void testParsingHouseMembers() {

        MemberHtmlParser parser = MemberHtmlParser.load("http://www.ilga.gov/house/default.asp");

        List<Legislator> names = parser.getLegislators();

        Assert.assertNotNull(names);
    }

    @Test
    public void testGetAssembly(){
        MemberHtmlParser parser = MemberHtmlParser.load("http://www.ilga.gov/senate/default.asp");

        Chamber chamber = parser.getAssembly();
        Assert.assertEquals(Chamber.Senate, chamber);
    }


    @Test
    public void testGetSession(){
        MemberHtmlParser parser = MemberHtmlParser.load("http://www.ilga.gov/house/default.asp");

        long session = parser.getSessionNumber();
        Assert.assertEquals(100L, session);
    }

}
