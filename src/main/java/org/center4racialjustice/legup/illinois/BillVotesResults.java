package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.center4racialjustice.legup.domain.Name;

import java.util.Collections;
import java.util.List;

@Data
public class BillVotesResults {

    public static final BillVotesResults NO_RESULTS = new BillVotesResults(
            Collections.emptyList(), Collections.emptyList(), "", 0);

    private final List<CollatedVote> collatedVotes;
    private final List<Name> uncollatedNames;
    private final String url;
    private final long checksum;

}
