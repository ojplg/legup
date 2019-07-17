package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.center4racialjustice.legup.domain.BillEventKey;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.VoteType;

import java.time.LocalDate;
import java.util.List;

@Data
public class BillVotesResults {

    private final List<CollatedVote> collatedVotes;
    private final List<Name> uncollatedNames;
    private final String url;
    private final long checksum;
    private final Chamber chamber;
    private final VoteType voteType;
    private final LocalDate actionDate;

    public int getCollatedCount(){
        return collatedVotes.size();
    }

    public String getVoteTypeSummary(){
        return voteType.getSummarizedType();
    }

    public String getRawData(){
        return voteType.getRawData();
    }

    public BillEventKey generateEventKey(){
        return new BillEventKey(actionDate, chamber, voteType.getRawData());
    }

}
