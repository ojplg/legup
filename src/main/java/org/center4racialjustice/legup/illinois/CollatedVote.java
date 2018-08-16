package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;

@Data
public class CollatedVote {
    private final Vote vote;
    private final Legislator legislator;
    private final Name name;
}
