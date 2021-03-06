package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameOverrides;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.util.Triple;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class TestCommitteeMemberParser {

    public static final String HouseCommitteeListUrl = "http://www.ilga.gov/house/committees/members.asp?CommitteeID=2549&GA=101";
    public static final String HouseCommitteeListHtmlFileName = "/html/illinois_committee_members.html";
    public static final String EmptySenateCommitteeFileName = "/html/illinois_senate_empty_committee.html";
    public static final String EmptySenateCommittee2FileName = "/html/illinois_senate_empty_committee_2.html";

    private CommitteeMemberParser parserFromResourceFile(){
        InputStream inputStream = this.getClass().getResourceAsStream(HouseCommitteeListHtmlFileName);
        NameOverrides nameOverrides =  NameOverrides.load("conf/name.overrides");
        NameParser nameParser = new NameParser(nameOverrides.getOverrides());
        CommitteeMemberParser parser = CommitteeMemberParser.loadFromInputStream(inputStream, HouseCommitteeListUrl, nameParser);
        return parser;
    }

    @Test
    public void testParseMembers_Size(){
        CommitteeMemberParser parser = parserFromResourceFile();
        List<Triple<String, Name, String>> links = parser.parseMembers();
        Assert.assertEquals(13, links.size());
    }

    @Test
    public void testParseMembers_FirstMember(){
        CommitteeMemberParser parser = parserFromResourceFile();
        Triple<String, Name, String> member = parser.parseMembers().get(0);
        Assert.assertEquals("Chairperson", member.getFirst());
        Assert.assertEquals("Feigenholtz", member.getSecond().getLastName());
        Assert.assertEquals("Sara", member.getSecond().getFirstName());
        Assert.assertEquals("2525",  member.getThird());
    }

    @Test
    public void testEmptyCommitteeParsing(){
        InputStream inputStream = this.getClass().getResourceAsStream(EmptySenateCommitteeFileName);
        NameOverrides nameOverrides =  NameOverrides.load("conf/name.overrides");
        NameParser nameParser = new NameParser(nameOverrides.getOverrides());
        CommitteeMemberParser parser = CommitteeMemberParser.loadFromInputStream(inputStream, HouseCommitteeListUrl, nameParser);
        List<Triple<String,Name,String>> members = parser.parseMembers();
        Assert.assertTrue(members.isEmpty());
    }

    @Test
    public void testEmptyCommitteeParsing2(){
        InputStream inputStream = this.getClass().getResourceAsStream(EmptySenateCommittee2FileName);
        NameOverrides nameOverrides =  NameOverrides.load("conf/name.overrides");
        NameParser nameParser = new NameParser(nameOverrides.getOverrides());
        CommitteeMemberParser parser = CommitteeMemberParser.loadFromInputStream(inputStream, HouseCommitteeListUrl, nameParser);
        List<Triple<String,Name,String>> members = parser.parseMembers();
        Assert.assertTrue(members.isEmpty());
    }
}
