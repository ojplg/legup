package org.center4racialjustice.legup.illinois;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpectedVoteCounts {

    private final int expectedYeas;
    private final int expectedNays;
    private final int expectedPresent;
    private final int expectedNotVoting;

}
