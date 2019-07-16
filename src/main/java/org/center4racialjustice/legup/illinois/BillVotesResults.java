package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.VoteType;

import java.time.Instant;
import java.util.List;

@Data
public class BillVotesResults {

    private final List<CollatedVote> collatedVotes;
    private final List<Name> uncollatedNames;
    private final String url;
    private final long checksum;
    private final Chamber chamber;
    private final VoteType voteType;
    private final Instant actionDate;

    public String generateKey(Bill bill){
        return BillActionLoad.formKey(bill, url, true);
    }

    public int getCollatedCount(){
        return collatedVotes.size();
    }

    public String getVoteTypeSummary(){
        return voteType.getSummarizedType();
    }

    public String getRawData(){
        return voteType.getRawData();
    }
}
