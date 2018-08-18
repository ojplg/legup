package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.domain.VoteSide;

@Data
public class CollatedVote {
    private final VoteSide voteSide;
    private final Legislator legislator;
    private final Name name;

    public Vote asVote(long billId){
        Vote vote = new Vote();
        vote.setBillId(billId);
        vote.setLegislatorId(legislator.getId());
        vote.setVoteSide(voteSide.getCode());
        return vote;
    }
}
