package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class TestMemberHtmlParser {

    @Test
    public void testParsingHouseMembers() {
        String url = "http://www.ilga.gov/house/default.asp";
        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_100_house.html");
        MemberHtmlParser parser = MemberHtmlParser.loadFromInputStream(inputStream, url);

        List<Legislator> names = parser.getLegislators();
        Assert.assertEquals(128, names.size());
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

    @Test
    public void testParseOutMemberId(){
        MemberHtmlParser parser = MemberHtmlParser.load("http://www.ilga.gov/house/default.asp");

        List<Legislator> legislators = parser.getLegislators();

        List<Legislator> davidsmeyers = legislators.stream().filter(leg -> leg.getLastName().equals("Davidsmeyer")).collect(Collectors.toList());
        Assert.assertEquals(1, davidsmeyers.size());
        Assert.assertEquals("2438", davidsmeyers.get(0).getMemberId());
    }

}
