package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventData;
import org.center4racialjustice.legup.domain.Chamber;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestBillVotesResults {

    private static List<String> VoteLinkTexts_101_House_2040 = Arrays.asList(
            "HB2040 - Third Reading - Thursday, May 16, 2019",
            "HB2040 - Third Reading - Wednesday, April 10, 2019",
            "HB2040 - Executive - Apr 30, 2019",
            "HFA0003 - Labor & Commerce - Apr 03, 2019",
            "HB2040 - Commerce and Innovation Subcommitte - Mar 20, 2019",
            "HB2040 - Labor & Commerce - Mar 20, 2019");

    private static List<String> VoteEventsDescriptions_101_House_2040 = Arrays.asList(
            "Recommends Do Pass Subcommittee/ Labor & Commerce Committee; 007-000-000",
            "Do Pass as Amended / Short Debate Labor & Commerce Committee; 018-010-000",
            "House Floor Amendment No. 3 Recommends Be Adopted Labor & Commerce Committee; 017-004-000",
            "Third Reading - Short Debate - Passed 085-026-000",
            "Do Pass Executive; 011-004-001",
            "Third Reading - Passed; 034-014-000");

    private static final BillEventParser EventParser = new BillEventParser();

    @Test
    public void testMatching(){
        VoteLinkInfo voteLinkInfo = VoteLinkInfo.create(VoteLinkTexts_101_House_2040.get(0),Chamber.House, true,"pdfUrl");
        BillEvent billEvent = new BillEvent(LocalDate.of(2019, 5, 16),
                Chamber.House, VoteEventsDescriptions_101_House_2040.get(0), "empty_link");
        BillEventData billEventData = EventParser.parse(billEvent);

        BillVotesResults billVotesResults = new BillVotesResults(voteLinkInfo,
                Collections.emptyList(),
                Collections.emptyList(),
                0L);

        Assert.assertTrue(billVotesResults.matches(billEventData));
    }


}
