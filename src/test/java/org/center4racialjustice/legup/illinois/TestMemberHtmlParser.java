package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameOverrides;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class TestMemberHtmlParser {

    static {
        System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");
    }

    private static Map<String, Name> loadOverrides(){
        NameOverrides nameOverrides =  NameOverrides.load("conf/name.overrides");
        return nameOverrides.getOverrides();
    }

    public static final String HouseMemberUrl = "http://www.ilga.gov/house/default.asp";

    @Test
    public void loadAndreNameWithAccent(){
        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_100_house.html");
        MemberHtmlParser parser = MemberHtmlParser.loadFromInputStream(inputStream, HouseMemberUrl, loadOverrides());
        List<Legislator> legislators = parser.getLegislators();

        List<Legislator> filtered = Legislator.findByLastName(legislators, "Thapedi");
        Legislator andreThapedi = filtered.get(0);

        Assert.assertEquals("André", andreThapedi.getFirstName());
    }

    @Test
    public void testParsingHouseMembers() {
        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_100_house.html");
        MemberHtmlParser parser = MemberHtmlParser.loadFromInputStream(inputStream, HouseMemberUrl, loadOverrides());

        List<Legislator> names = parser.getLegislators();
        Assert.assertEquals(128, names.size());
    }

    @Test
    public void testGetAssembly(){
        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_100_house.html");
        MemberHtmlParser parser = MemberHtmlParser.loadFromInputStream(inputStream, HouseMemberUrl, loadOverrides());

        Chamber chamber = parser.getAssembly();
        Assert.assertEquals(Chamber.House, chamber);
    }


    @Test
    public void testGetSession(){
        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_100_house.html");
        MemberHtmlParser parser = MemberHtmlParser.loadFromInputStream(inputStream, HouseMemberUrl, loadOverrides());

        long session = parser.getSessionNumber();
        Assert.assertEquals(100L, session);
    }

    @Test
    public void testParseOutMemberId(){
        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_100_house.html");
        MemberHtmlParser parser = MemberHtmlParser.loadFromInputStream(inputStream, HouseMemberUrl, loadOverrides());

        List<Legislator> legislators = parser.getLegislators();

        List<Legislator> davidsmeyers = Legislator.findByLastName(legislators, "Davidsmeyer");
        Assert.assertEquals(1, davidsmeyers.size());
        Assert.assertEquals("2438", davidsmeyers.get(0).getMemberId());
    }

    @Test
    public void testMembersInSecondTableShouldNotBeCompleteTerm(){
        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_100_house.html");
        MemberHtmlParser parser = MemberHtmlParser.loadFromInputStream(inputStream, HouseMemberUrl, loadOverrides());

        List<Legislator> legislators = parser.getLegislators();

        Legislator nekritz = Legislator.findByLastName(legislators, "Nekritz").get(0);

        Assert.assertFalse(nekritz.getCompleteTerm());
    }

    @Test
    public void testMembersInSameDistrictAsThoseInSecondTableShouldNotBeCompleteTerm(){
        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_100_house.html");
        MemberHtmlParser parser = MemberHtmlParser.loadFromInputStream(inputStream, HouseMemberUrl, loadOverrides());

        List<Legislator> legislators = parser.getLegislators();

        Legislator bristow = Legislator.findByLastName(legislators, "Bristow").get(0);

        Assert.assertFalse(bristow.getCompleteTerm());
    }

    @Test
    public void testDistrictsMentionedOnceAreCompleteTerm(){
        InputStream inputStream = this.getClass().getResourceAsStream("/html/illinois_100_house.html");
        MemberHtmlParser parser = MemberHtmlParser.loadFromInputStream(inputStream, HouseMemberUrl, loadOverrides());

        List<Legislator> legislators = parser.getLegislators();

        Legislator andrade = Legislator.findByLastName(legislators, "Andrade").get(0);

        Assert.assertTrue(andrade.getCompleteTerm());

    }

}
