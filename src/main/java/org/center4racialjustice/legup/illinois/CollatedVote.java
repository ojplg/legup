package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.VoteSide;

@Data
public class CollatedVote {
    private final VoteSide voteSide;
    private final Legislator legislator;
    private final Name name;

    public Vote asVote(Bill bill){
        Vote vote = new Vote();
        vote.setBill(bill);
        vote.setLegislator(legislator);
        vote.setVoteSide(voteSide);
        return vote;
    }
}
