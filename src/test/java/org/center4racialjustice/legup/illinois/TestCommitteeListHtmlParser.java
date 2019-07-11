package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.util.Triple;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class TestCommitteeListHtmlParser {

    public static final String HouseCommitteeListUrl = "http://www.ilga.gov/house/committees/default.asp";
    public static final String HouseCommitteeListHtmlFileName = "/html/illinois_house_101_committee_list.html";

    private CommitteeListHtmlParser parserFromResourceFile(){
        InputStream inputStream = this.getClass().getResourceAsStream(HouseCommitteeListHtmlFileName);
        CommitteeListHtmlParser parser = CommitteeListHtmlParser.loadFromInputStream(inputStream, HouseCommitteeListUrl);
        return parser;
    }

    @Test
    public void testReadCommitteeNamesLinksAndCodes_Size(){
        CommitteeListHtmlParser parser = parserFromResourceFile();
        List<Triple<String, String, String>> links = parser.parseCommitteeLinks();
        Assert.assertEquals(97, links.size());
    }

    @Test
    public void testReadCommitteeNamesLinksAndCodes_FirstElement(){
        CommitteeListHtmlParser parser = parserFromResourceFile();
        Triple<String, String, String> firstCommittee = parser.parseCommitteeLinks().get(0);

        Assert.assertEquals("Adoption & Child Welfare", firstCommittee.getFirst());
        Assert.assertEquals("HACW", firstCommittee.getSecond());
        Assert.assertEquals("http://www.ilga.gov/house/committees/members.asp?CommitteeID=2549&GA=101", firstCommittee.getThird());
    }

}
