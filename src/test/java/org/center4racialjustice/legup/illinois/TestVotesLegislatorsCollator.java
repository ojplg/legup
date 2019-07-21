package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.VoteSide;
import org.center4racialjustice.legup.domain.VoteType;
import org.center4racialjustice.legup.service.LegislativeStructure;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class TestVotesLegislatorsCollator {

    static {
        System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");
    }

    @Test
    public void correctlyMatchesByLastName(){
        BillWebData billWebData = new BillWebData("url", "contents");

        VoteLists voteLists = new VoteLists();
        voteLists.addVoteRecord(
                new VoteRecord(new Name(null, null, "McGee","H", null), VoteSide.Nay)
        );
        BillVotes bv = new BillVotes(null, billWebData, null, voteLists, Chamber.Senate);

        Legislator legislator = new Legislator();
        legislator.setFirstName("Herbie");
        legislator.setLastName("McGee");
        legislator.setChamber(Chamber.Senate);

        LegislativeStructure legislativeStructure = new LegislativeStructure(Collections.singletonList(legislator), Collections.emptyList());
        VotesLegislatorsCollator collator = new VotesLegislatorsCollator(legislativeStructure, bv);
        collator.collate();

        List<CollatedVote> nays = collator.getNays();
        Assert.assertEquals(1, nays.size());
        Assert.assertEquals(legislator, nays.get(0).getLegislator());
        Assert.assertTrue(collator.getUncollated().isEmpty());
    }

    @Test
    public void reportsUncollatedVotes(){
        BillWebData billWebData = new BillWebData("url", "contents");
        VoteLists voteLists = new VoteLists();
        voteLists.addVoteRecord(
                new VoteRecord(new Name(null, null, "McGee","H", null), VoteSide.Nay)
        );
        voteLists.addVoteRecord(
                new VoteRecord(new Name(null, null, "McGee","H", null), VoteSide.Nay)
        );
        voteLists.addVoteRecord(
                new VoteRecord(new Name(null, null, "Henry","B", null), VoteSide.Nay)
        );
        BillVotes bv = new BillVotes(null, billWebData, null, voteLists, Chamber.Senate);

        Legislator legislator = new Legislator();
        legislator.setFirstName("Herbie");
        legislator.setLastName("McGee");
        legislator.setChamber(Chamber.Senate);

        LegislativeStructure legislativeStructure = new LegislativeStructure(Collections.singletonList(legislator), Collections.emptyList());
        VotesLegislatorsCollator collator = new VotesLegislatorsCollator(legislativeStructure, bv);
        collator.collate();

        List<Name> uncollated = collator.getUncollated();
        Assert.assertEquals(1, uncollated.size());
        Assert.assertEquals("Henry", uncollated.get(0).getLastName());
    }

}
