package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventData;
import org.center4racialjustice.legup.domain.Chamber;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collections;

public class TestBillVotesResults {


    private static BillVotesResults[] VoteBillResults_101_House_2040 = {
            fromLinkText("HB2040 - Commerce and Innovation Subcommitte - Mar 20, 2019",Chamber.House, true),
            fromLinkText("HB2040 - Labor & Commerce - Mar 20, 2019", Chamber.House, true),
            fromLinkText("HFA0003 - Labor & Commerce - Apr 03, 2019", Chamber.House, true),
            fromLinkText("HB2040 - Third Reading - Wednesday, April 10, 2019",Chamber.House, false),
            fromLinkText("HB2040 - Executive - Apr 30, 2019",Chamber.Senate, true),
            fromLinkText("HB2040 - Third Reading - Thursday, May 16, 2019", Chamber.Senate, false)
    };

    private static BillEventData[] VoteEvents_101_House_2040 = {
            fromVoteEventDescription("Recommends Do Pass Subcommittee/ Labor & Commerce Committee; 007-000-000",
                    LocalDate.of(2019,3,20), Chamber.House),
            fromVoteEventDescription("Do Pass as Amended / Short Debate Labor & Commerce Committee; 018-010-000",
                    LocalDate.of(2019,3,20), Chamber.House),
            fromVoteEventDescription( "House Floor Amendment No. 3 Recommends Be Adopted Labor & Commerce Committee; 017-004-000",
                    LocalDate.of(2019,4,3), Chamber.House),
            fromVoteEventDescription("Third Reading - Short Debate - Passed 085-026-000",
                    LocalDate.of(2019,4,10), Chamber.House),
            fromVoteEventDescription("Do Pass Executive; 011-004-001",
                    LocalDate.of(2019,5,1), Chamber.Senate),
            fromVoteEventDescription("Third Reading - Passed; 034-014-000",
                    LocalDate.of(2019,5,16), Chamber.Senate)
    };

    private static BillVotesResults fromLinkText(String linkText, Chamber chamber, boolean committee){
        VoteLinkInfo voteLinkInfo = VoteLinkInfo.create(linkText,chamber, committee,"pdfUrl");

        BillVotesResults billVotesResults = new BillVotesResults(voteLinkInfo,
                Collections.emptyList(),
                Collections.emptyList(),
                0L);

        return billVotesResults;
    }

    private static BillEventData fromVoteEventDescription(String voteEventDescription, LocalDate localDate, Chamber chamber){
        BillEvent billEvent = new BillEvent(
                localDate, chamber, voteEventDescription, "empty_link");
        BillEventData billEventData = new BillEventParser().parse(billEvent);
        return billEventData;
    }

    @Test
    public void testMatchesWork_101_House_2040(){
        for(int idx=0; idx<6; idx++){
            BillVotesResults billVotesResults = VoteBillResults_101_House_2040[idx];
            BillEventData billEventData = VoteEvents_101_House_2040[idx];

            Assert.assertTrue("Not matching: " + idx, billVotesResults.matches(billEventData));
        }
    }


    @Test
    public void testMisMatchesDoNotWork_101_House_2040(){
        for(int idx=0; idx<6; idx++){
            for(int jdx=0; jdx<6; jdx++) {
                if( idx != jdx ) {
                    BillVotesResults billVotesResults = VoteBillResults_101_House_2040[idx];
                    BillEventData billEventData = VoteEvents_101_House_2040[jdx];

//                    Assert.assertFalse("Incorrect match: " + idx + ", " + jdx, billVotesResults.matches(billEventData));
                }
            }
        }
    }

}
