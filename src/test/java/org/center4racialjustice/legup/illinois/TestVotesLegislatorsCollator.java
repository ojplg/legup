package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.VoteSide;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class TestVotesLegislatorsCollator {

    @Test
    public void correctlyMatchesByLastName(){
        BillVotes bv = new BillVotes();
        bv.addVoteRecord(
                new VoteRecord(new Name(null, null, "McGee","H", null), VoteSide.Nay)
        );
        bv.setVotingChamber(Chamber.Senate);

        Legislator legislator = new Legislator();
        legislator.setFirstName("Herbie");
        legislator.setLastName("McGee");
        legislator.setChamber(Chamber.Senate);

        VotesLegislatorsCollator collator = new VotesLegislatorsCollator(Collections.singletonList(legislator), bv);
        collator.collate();

        List<CollatedVote> nays = collator.getNays();
        Assert.assertEquals(1, nays.size());
        Assert.assertEquals(legislator, nays.get(0).getLegislator());
        Assert.assertTrue(collator.getUncollated().isEmpty());
    }

    @Test
    public void reportsUncollatedVotes(){
        BillVotes bv = new BillVotes();
        bv.addVoteRecord(
                new VoteRecord(new Name(null, null, "McGee","H", null), VoteSide.Nay)
        );
        bv.addVoteRecord(
                new VoteRecord(new Name(null, null, "Henry","B", null), VoteSide.Nay)
        );
        bv.setVotingChamber(Chamber.Senate);

        Legislator legislator = new Legislator();
        legislator.setFirstName("Herbie");
        legislator.setLastName("McGee");
        legislator.setChamber(Chamber.Senate);

        VotesLegislatorsCollator collator = new VotesLegislatorsCollator(Collections.singletonList(legislator), bv);
        collator.collate();

        List<Name> uncollated = collator.getUncollated();
        Assert.assertEquals(1, uncollated.size());
        Assert.assertEquals("Henry", uncollated.get(0).getLastName());
    }

}
